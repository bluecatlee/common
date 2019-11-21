package com.github.bluecatlee.common.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class RestResult<T>  {

    public static RestResultBuilder SUCCESS() {
        return RestResult.builder().code(200).message("操作成功");
    }

    public static RestResultBuilder ERROR_PARAMS() {
        return RestResult.builder().code(400).message("参数错误");
    }

    public static RestResultBuilder ERROR_SERVER() {
        return RestResult.builder().code(500).message("服务器错误");
    }

    public static RestResultBuilder NOT_FOUNT() {
        return RestResult.builder().code(404);
    }

    public static RestResultBuilder LOGIN() {
        return RestResult.builder().code(403).message("未登录");
    }

    public static RestResultBuilder FORBIDDEN() {
        return RestResult.builder().code(401).message("无权限，禁止访问");
    }

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T object;

    /**
     * sToken
     */
    private String sToken;

}
