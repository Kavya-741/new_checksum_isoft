package com.bankaudit.dto;

import lombok.Data;

@Data
public class ServiceStatus {
    private String status;
	private String message;
	private Object result;
	private String apiKey;
}
