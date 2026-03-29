package com.example.demo.exception;

public class LoadRejectedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String code;

	public LoadRejectedException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}