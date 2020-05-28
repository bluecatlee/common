package com.github.bluecatlee.common.third.wechat.exception;

/**
 * WeChat 异常
 */
public class WeChatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WeChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeChatException(Throwable cause) {
        super(cause);
    }

    public WeChatException(String message) {
        super(message);
    }

}