package com.bankaudit.dto;

import java.util.List;

import lombok.Data;

@Data
public class DataTableResponse {

	private Integer draw;
	private Long recordsTotal;
	private Long recordsFiltered;
	private List<? extends Object> data;
	private String error;

}
