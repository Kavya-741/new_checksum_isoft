package com.bankaudit.rest.process.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.process.ComplianceTrackingReportDto;
import com.bankaudit.dto.process.LettersAuditDto;
import com.bankaudit.rest.process.service.ReportService;
import com.bankaudit.util.BankAuditUtil;
import com.bankaudit.dto.ServiceStatus;

@RestController
@RequestMapping("/api/reportController")
public class ReportController {
	
	@Autowired
	ReportService reportService;

	static final Logger logger = Logger.getLogger(ReportController.class);

	@GetMapping("/financialYearList")
	public ServiceStatus getFinancialYearList(@RequestParam(value = "legalEntityCode") String legalEntityCode) {
		ServiceStatus serviceStatus = new ServiceStatus();
		logger.info("Inside updateDynamicStatements .. "
					+ legalEntityCode);
		if (!BankAuditUtil.isEmptyString(legalEntityCode)) {
			try {
				List<Date> list = reportService.getFinancialYearList(legalEntityCode);
				serviceStatus.setResult(list);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully retrieved");
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("Invalid payload");
		}
		return serviceStatus;
	}

	@GetMapping("/complianceTrackingReport")
	public ServiceStatus getComplianceTrackingReport(@RequestParam(required = true, value = "legalEntityCode") Integer legalEntityCode,
			@RequestParam(required = true, value = "financialYear") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate financialYear,
			@RequestParam(required = true, value = "userId") String userId, @RequestParam(required = false, value = "auditTypeCode") String auditTypeCode) {
		ServiceStatus serviceStatus = new ServiceStatus();
		logger.info("Inside updateDynamicStatements .. " + legalEntityCode);
		try {
			List<ComplianceTrackingReportDto> list = reportService.getComplianceTrackingReport(legalEntityCode, userId, financialYear, auditTypeCode);
			serviceStatus.setResult(list);
			serviceStatus.setStatus("success");
			serviceStatus.setMessage("successfully retrieved");
		} catch (Exception e) {
			e.printStackTrace();
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure");
		}
		return serviceStatus;
	}
	
	
	@GetMapping("/getInspectionsDetail")
	public ServiceStatus getInspectionsDetail(
			@RequestParam(required = true, value = "legalEntityCode") Integer legalEntityCode,
			@RequestParam(required = true, value = "financialYear") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate financialYear,
			@RequestParam(required = true, value = "userId") String userId, 
			@RequestParam(required = false, value = "auditTypeCode") String auditTypeCode) {
		ServiceStatus serviceStatus = new ServiceStatus();
		logger.info("Inside getInspectionsDetail .. " + legalEntityCode);
		try {
			List<LettersAuditDto> list = reportService.getAuditDetail(legalEntityCode, userId, financialYear, auditTypeCode);
			serviceStatus.setResult(list);
			serviceStatus.setStatus("success");
			serviceStatus.setMessage("successfully retrieved");
		 } catch (Exception e) {
			e.printStackTrace();
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure");
		}
		return serviceStatus;
	}
	
	
}
