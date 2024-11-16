package com.finakon.baas.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private String status;
	private String message;
	private Object result;
	private String apiKey;
}
