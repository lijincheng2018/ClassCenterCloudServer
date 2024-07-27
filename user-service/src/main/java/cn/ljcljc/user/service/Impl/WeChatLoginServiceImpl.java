package cn.ljcljc.user.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.domain.entity.AllUser;
import cn.ljcljc.user.domain.entity.WeChatMPQrLogin;
import cn.ljcljc.user.repository.AllUserRepository;
import cn.ljcljc.user.repository.WeChatMPQrLoginRepository;
import cn.ljcljc.user.service.WeChatLoginService;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.MD5Util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WeChatLoginServiceImpl implements WeChatLoginService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final WeChatMPQrLoginRepository weChatMPQrLoginRepository;

    private final AllUserRepository allUserRepository;

    @Autowired
    public WeChatLoginServiceImpl(RedisTemplate<String, Object> redisTemplate, WeChatMPQrLoginRepository sceneRepository, AllUserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.weChatMPQrLoginRepository = sceneRepository;
        this.allUserRepository = userRepository;
    }

    @Override
    @Transactional
    public Result createQrcode() {
        try {
            String token = getToken();
            String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token;
            String scene = MD5Util.md5Hash(UUID.randomUUID().toString()).substring(0, 12);
            String json = "{\"scene\":\"" + scene + "\", \"env_version\":\"release\"}";

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(url);
                post.setEntity(new StringEntity(json));
                try (CloseableHttpResponse response = client.execute(post)) {
                    byte[] result = EntityUtils.toByteArray(response.getEntity());
                    String filePath = scene + ".png";
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        fos.write(result);
                    }

                    // 将二维码的scene写入数据库
                    saveSceneToDatabase(scene);

                    String base64Image = Base64.getEncoder().encodeToString(result);
                    String qrcodeData = "data:image/png;base64," + base64Image;


                    File file = new File(filePath);
                    if (file.delete()) {
                        log.info("文件" + filePath + "已删除");
                    } else {
                        log.error("文件" + filePath + "已删除");
                    }


                    Map<String, String> responses = new HashMap<>();
                    responses.put("scene", scene);
                    responses.put("qrcode", qrcodeData);
                    return Result.success(responses);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.error("错误");
        }
    }

    @Override
    public Result updateSceneStatus(String scene) {
        WeChatMPQrLogin existingScene = weChatMPQrLoginRepository.findByScene(scene);

        if (existingScene == null) {
            return Result.error("无效的二维码");
        }

        if ("3".equals(existingScene.getStatus())) {
            return Result.success("二维码已过期");
        } else {
            existingScene.setStatus("2");
            weChatMPQrLoginRepository.save(existingScene);
            return Result.success();
        }
    }

    @Override
    @Transactional
    public Result loginWithCode(String code, String scene) {
        String api = "https://api.weixin.qq.com/sns/jscode2session?appid=" + Constant.WECHAT_APP_ID + "&secret=" + Constant.WECHAT_APP_SECRET + "&js_code=" + code + "&grant_type=authorization_code";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(api);
            try (CloseableHttpResponse response = client.execute(get)) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(result);
                String openid = jsonObject.getString("openid");

                if (openid == null) {
                    return Result.error("获取OpenID失败");
                }

                AllUser user = allUserRepository.findByWxId(openid);
                if (user == null) {
                    return Result.error("没有绑定");
                }

                WeChatMPQrLogin existingScene = weChatMPQrLoginRepository.findByScene(scene);
                if (existingScene == null) {
                    return Result.error("无效的二维码");
                }

                existingScene.setOpenid(openid);
                existingScene.setStatus("3");
                weChatMPQrLoginRepository.save(existingScene);

                return Result.success(openid);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    private String getToken() throws IOException {
        String token = (String) redisTemplate.opsForValue().get(Constant.WECHAT_ACCESS_TOKEN);
        if (token == null) {
            token = getNewToken();
            log.info("微信access_token已生成：" + token);
            redisTemplate.opsForValue().set(Constant.WECHAT_ACCESS_TOKEN, token, 7000, TimeUnit.SECONDS);
        }
        return token;
    }

    private String getNewToken() throws IOException {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + Constant.WECHAT_APP_ID + "&secret=" + Constant.WECHAT_APP_SECRET;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(get)) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(result);
                return jsonObject.getString(Constant.WECHAT_RESPONSE_TOKEN_NAME);
            }
        }
    }

    private void saveSceneToDatabase(String scene) {
        WeChatMPQrLogin newScene = new WeChatMPQrLogin();
        newScene.setScene(scene);
        newScene.setStatus("1");
        newScene.setCreateTime(Timestamp.from(Instant.now()));
        weChatMPQrLoginRepository.save(newScene);
    }
}
