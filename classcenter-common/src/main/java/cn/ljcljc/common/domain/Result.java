package cn.ljcljc.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code;
    private String msg;
    private Object data;

    public static Result success() {
        return new Result(200, "success", null);
    }
    public static Result success(Object data) {
        return new Result(200, "success", data);
    }
    public static Result error(String msg) {
        return new Result(100, msg, null);
    }
    public static Result error(int code, String msg, Object data) {
        return new Result(code, msg, data);
    }
}
