package cn.ljcljc.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCenterTaskFinishRateDTO {
    private String title;
    private String finish_rate;
    private String finish_jd;
}
