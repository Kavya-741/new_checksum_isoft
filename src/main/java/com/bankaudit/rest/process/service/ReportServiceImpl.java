package com.bankaudit.rest.process.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.dto.process.ComplianceTrackingReportDto;
import com.bankaudit.dto.process.LettersAuditDto;
import com.bankaudit.rest.process.dao.ReportDao;
import com.bankaudit.service.MaintEntityService;
import com.bankaudit.util.BankAuditUtil;

@Service
@Transactional("transactionManager")
public class ReportServiceImpl implements ReportService {

	@Autowired
	ReportDao reportDao;

	@Autowired
	MaintEntityService maintEntityService;

	static final Logger logger = Logger.getLogger(ReportServiceImpl.class);

	@Override
	public List<Date> getFinancialYearList(String legalEntityCode) {
		return reportDao.getFinancialYearEndDate(Integer.parseInt(legalEntityCode));
	}

	@Override
	public List<ComplianceTrackingReportDto> getComplianceTrackingReport(Integer legalEntityCode, String userId,
			LocalDate financialYear, String auditTypeCode) {
		List<String> unitList = maintEntityService.getSubBranchesGyUserIdOrUnitId(legalEntityCode, "user", userId);
		List<String> auditList = reportDao.getAuditScheduleUnitIdsList(legalEntityCode, unitList, financialYear,
				auditTypeCode);
		if (auditList != null && !auditList.isEmpty()) {
			return reportDao.getAuditScheduleReportList(legalEntityCode, auditList, financialYear, auditTypeCode);
		}
		return Collections.emptyList();
	}

	@Override
	public List<LettersAuditDto> getAuditDetail(Integer legalEntityCode, String userId, LocalDate financialYear,
			String auditTypeCode) {
		List<String> unitList = maintEntityService.getSubBranchesGyUserIdOrUnitId(legalEntityCode, "user", userId);
		return reportDao.getAuditDetail(legalEntityCode, financialYear, auditTypeCode, unitList);
	}

}
