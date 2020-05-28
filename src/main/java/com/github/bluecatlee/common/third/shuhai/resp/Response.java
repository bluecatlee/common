package com.github.bluecatlee.common.third.shuhai.resp;

import lombok.Data;

@Data
public class Response<T> {
	
	private Integer status;
	private String message;
	private Long timestamp;
	private T data;

}
