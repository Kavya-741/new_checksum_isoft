package com.bankaudit.rest.process.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Date;

import com.bankaudit.dto.process.ComplianceTrackingReportDto;
import com.bankaudit.dto.process.LettersAuditDto;

public interface ReportService {

	List<Date> getFinancialYearList(String legalEntityCode);

	List<ComplianceTrackingReportDto> getComplianceTrackingReport(Integer legalEntityCode, String userId,
			LocalDate financialYear, String auditTypeCode);

	List<LettersAuditDto> getAuditDetail(Integer legalEntityCode, String userId, LocalDate financialYear,
			String auditTypeCode);
}
