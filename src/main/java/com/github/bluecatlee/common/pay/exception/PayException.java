package com.github.bluecatlee.common.pay.exception;

public class PayException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PayException() {
		super();
	}

	public PayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PayException(String message, Throwable cause) {
		super(message, cause);
	}

	public PayException(String message) {
		super(message);
	}

	public PayException(Throwable cause) {
		super(cause);
	}

}
