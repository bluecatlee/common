package com.github.bluecatlee.common.exception;

public enum CommonExceptionEnum {

    /** 操作成功 */
    SUCCESS(200, "操作成功"),

    /** 系统错误 */
    SYSTEM_FAILURE(500, "系统错误"),

    /** 参数为空 */
    NULL_ARGUMENT(400, "参数为空"),

    /** 参数不正确 */
    ILLEGAL_ARGUMENT(400, "参数不正确"),

    /** 页面未找到 */
    NOT_FOUND(404, "页面未找到"),

    /** 没有权限 **/
    AUTHORITY_DENIED(401, "访问拒绝"),

    /** 会话超时 */
    SESSION_TIMEOUT(401, "会话超时");

    /** 枚举值 */
    private int value;

    /** 枚举信息 */
    private String message;

    private CommonExceptionEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    /**
     * 根据枚举值获取枚举对象，如果找不到对应的枚举返回<code>null</code>
     *
     * @param value 枚举值
     * @return 枚举对象
     */
    public static CommonExceptionEnum getEnumByValue(int value) {
        for (CommonExceptionEnum resultCode : CommonExceptionEnum.values()) {
            if (resultCode.getValue()==value) {
                return resultCode;
            }
        }
        return null;
    }

    /*
     * getter
     */
    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
