package com.github.bluecatlee.common.third.wechat.enumeration;

/**
 * 下单来源
 */
public enum WeChatTradeType {
 
  APP("APP"), NATIVE("NATIVE"), JSAPI("JSAPI");
 
  private String val;
 
  private WeChatTradeType(String val) {
    this.val = val;
  }
 
  public String value() {
    return this.val;
  }
}