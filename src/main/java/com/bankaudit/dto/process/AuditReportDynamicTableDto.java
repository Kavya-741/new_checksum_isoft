package com.bankaudit.dto.process;

import java.util.List;

import com.bankaudit.process.model.AuditReportDynamicTable;

import lombok.Data;

@Data
public class AuditReportDynamicTableDto {
	
	private AuditReportDynamicTable ardTable;	  
	private List<AuditReportDynamicTable> ardTableSequenceList;
}
