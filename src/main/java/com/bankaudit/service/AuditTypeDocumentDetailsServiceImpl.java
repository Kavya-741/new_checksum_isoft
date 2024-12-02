package com.bankaudit.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.AuditTypeDocumentDetailsDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.process.model.AuditDocumentDetails;
import com.bankaudit.process.model.AuditDocumentDetailsWrk;
import com.bankaudit.util.BankAuditUtil;

@Service("auditTypeDocumentDetailsService")
@Transactional("transactionManager")
public class AuditTypeDocumentDetailsServiceImpl implements AuditTypeDocumentDetailsService {

	static final Logger logger = Logger.getLogger(AuditTypeDocumentDetailsServiceImpl.class);

	@Autowired
	AuditTypeDocumentDetailsDao auditTypeDocumentDetailsDao;

	@Override
	public DataTableResponse getAuditTypeDocumentDetails(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size, String documentType, String documentSubType) {
		return auditTypeDocumentDetailsDao.getAuditTypeDocumentDetails(legalEntityCode, search, orderColumn,
				orderDirection, page, size, documentType, documentSubType);
	}

	@Override
	public void updateAuditTypeDocumentDetails(AuditDocumentDetails auditTypeDocumentDetails) {
		if (!auditTypeDocumentDetails.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {

			if (auditTypeDocumentDetails.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| auditTypeDocumentDetails.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)) {
				auditTypeDocumentDetails.setMakerTimestamp(new Date());
			}

			AuditDocumentDetailsWrk auditTypeDocumentDetailsWrk = new AuditDocumentDetailsWrk();
			BeanUtils.copyProperties(auditTypeDocumentDetails, auditTypeDocumentDetailsWrk);
			auditTypeDocumentDetailsDao.flushSession();
			auditTypeDocumentDetailsDao.saveOrUpdate(auditTypeDocumentDetailsWrk);

		} else {

			// delete from work
			auditTypeDocumentDetailsDao.deleteAuditTypeDocumentDetails(auditTypeDocumentDetails.getLegalEntityCode(),
					auditTypeDocumentDetails.getId(),
					auditTypeDocumentDetails.getAuditTypeCode(), BankAuditConstant.STATUS_UNAUTH);

			// save again
			auditTypeDocumentDetailsDao.flushSession();
			auditTypeDocumentDetailsDao.save(auditTypeDocumentDetails);

		}

	}

	// New Code
	@Override
	public Map<String, Integer> uploadMultipleDocument(Integer legalEntityCode, String auditTypeCode,
			String documentType,
			String documentName, String maker, String userRoleId, CommonsMultipartFile[] files,
			String documentSubType) {

		Map<String, Integer> uplodedIds = new HashMap<String, Integer>();

		String referencePath = "";
		for (CommonsMultipartFile commonsMultipartFile : files) {
			Integer uploadId = 0;
			try {

				AuditDocumentDetailsWrk auditTypeDocumentDetails = new AuditDocumentDetailsWrk();

				String pattern = "yyyyMMddHHmmssSSS";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				String fileNameAppender = legalEntityCode.toString() + auditTypeCode + documentType;
				referencePath = BankAuditUtil.uploadMaterial(commonsMultipartFile, BankAuditConstant.DIR_DOC_UPLOAD,
						simpleDateFormat.format(new Date()) + "_" + fileNameAppender);

				auditTypeDocumentDetails.setLegalEntityCode(legalEntityCode);
				auditTypeDocumentDetails.setFileName(commonsMultipartFile.getOriginalFilename());
				auditTypeDocumentDetails.setReferencePath(referencePath);
				auditTypeDocumentDetails.setAuditTypeCode(auditTypeCode);
				auditTypeDocumentDetails.setDocumentType(documentType);
				auditTypeDocumentDetails.setDocumentSubType(documentSubType);
				auditTypeDocumentDetails.setDocumentName(documentName);
				auditTypeDocumentDetails.setMaker(maker);
				auditTypeDocumentDetails.setMakerTimestamp(new Date());

				auditTypeDocumentDetails.setStatus(BankAuditConstant.STATUS_UNAUTH);
				auditTypeDocumentDetails.setEntityStatus(BankAuditConstant.STATUS_NEW);
				auditTypeDocumentDetails.setRole(userRoleId);
				auditTypeDocumentDetailsDao.flushSession();
				auditTypeDocumentDetailsDao.saveOrUpdate(auditTypeDocumentDetails);
				uploadId = auditTypeDocumentDetails.getId();
			} catch (Exception e) {
				logger.error("Error .." + e.getMessage() + " :: " + e.getCause());
			}
			uplodedIds.put(commonsMultipartFile.getOriginalFilename(), uploadId);
		}

		// update maker
		auditTypeDocumentDetailsDao.flushSession();
		auditTypeDocumentDetailsDao.updateMaker(legalEntityCode, auditTypeCode, maker, documentType, documentSubType);

		return uplodedIds;
	}

	// To Authenticate or reject
	@Override
	// public String updateAuditTypeDocumentDetails(Integer legalEntityCode, String
	// auditTypeCode, String checker, String status, String documentType, String
	// authRejRemarks) {
	public String updateAuthorizeAuditTypeDocumentDetails(AuditDocumentDetails auditTypeDocumentDetails) {
		return auditTypeDocumentDetailsDao.updateAuthorizeAuditTypeDocumentDetails(auditTypeDocumentDetails);// updateAuditTypeDocumentDetails(legalEntityCode,
																												// auditTypeCode,
																												// checker,
																												// status,
																												// authRejRemarks);
	}

	@Override
	public String deleteAuditTypeDocumentFromUI(Integer legalEntityCode, Integer id, String auditTypeCode,
			String status, String entityStatus, String documentType, String documentSubType) {
		return auditTypeDocumentDetailsDao.deleteAuditTypeDocumentFromUI(legalEntityCode, id, auditTypeCode, status,
				entityStatus, documentType, documentSubType);
	}

	@Override
	public String deleteAllAuditTypeDocument(Integer legalEntityCode, String auditTypeCode, String documentType,
			String status, String documentSubType) {
		return auditTypeDocumentDetailsDao.deleteAllAuditTypeDocument(legalEntityCode, auditTypeCode, documentType,
				status, documentSubType);
	}

	@Override
	public AuditDocumentDetails getAuditTypeDocumentDetailByAuditType(Integer legalEntityCode, String auditTypeCode,
			String status, String documentType, String documentSubType) {
		return auditTypeDocumentDetailsDao.getAuditTypeDocumentDetailByAuditType(legalEntityCode, auditTypeCode, status,
				documentType, documentSubType);
	}

}
