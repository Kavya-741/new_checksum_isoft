package com.bankaudit.rest.process.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.bankaudit.dao.Dao;
import com.bankaudit.dto.process.ComplianceTrackingReportDto;
import com.bankaudit.dto.process.LettersAuditDto;

public interface ReportDao extends Dao {

	boolean isDeptAsRMD(Integer legalEntityCode, String role, String userId);

	List<Date> getFinancialYearEndDate(Integer legalEntityCode);

	List<String> getAuditScheduleUnitIdsList(Integer legalEntityCode, List<String> unitList, LocalDate financialYear,
			String auditTypeCode);

	List<ComplianceTrackingReportDto> getAuditScheduleReportList(Integer legalEntityCode, List<String> auditList,
			LocalDate financialYear, String auditTypeCode);

	List<LettersAuditDto> getAuditDetail(Integer legalEntityCode, LocalDate financialYear, String auditTypeCode,
			List<String> unitList);

}
