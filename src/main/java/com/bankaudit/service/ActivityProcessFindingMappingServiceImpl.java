package com.bankaudit.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.ActivityProcessFindingMappingDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.ActivityProcessFindingMapping;
import com.bankaudit.model.ActivityProcessFindingMappingHst;
import com.bankaudit.model.ActivityProcessFindingMappingWrk;

@Service("activityProcessFindingMappingService")
@Transactional("transactionManager")
public class ActivityProcessFindingMappingServiceImpl implements ActivityProcessFindingMappingService {

	@Autowired
	ActivityProcessFindingMappingDao activityProcessFindingMappingDao;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	@Override
	public void createActivityProcessFindingMapping(ActivityProcessFindingMapping activityProcessFindingMapping)
			throws Exception {

		if (activityProcessFindingMapping.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)) {

			ActivityProcessFindingMappingWrk activityProcessFindingMappingWrk = new ActivityProcessFindingMappingWrk();
			BeanUtils.copyProperties(activityProcessFindingMapping, activityProcessFindingMappingWrk);
			activityProcessFindingMappingDao.save(activityProcessFindingMappingWrk);

		} else if (activityProcessFindingMapping.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			activityProcessFindingMappingDao.save(activityProcessFindingMapping);
		} else {
			throw new Exception("invalid status");
		}
	}

	@Override
	public DataTableResponse getActivityProcessFindingMappings(Integer legalEntityCode, String search,
			Integer orderColumn, String orderDirection, Integer page, Integer size) {
		return activityProcessFindingMappingDao.getActivityProcessFindingMappings(legalEntityCode, search,
				orderColumn, orderDirection, page, size);
	}

	@Override
	public Boolean isActivityProcessFindingMapping(int parseInt, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode, String activityCode, String processCode, String findingCode, String mappingId,
			String riskBelongsTo) {
		return activityProcessFindingMappingDao.isActivityProcessFindingMapping(parseInt, auditTypeCode, auditGroupCode,
				auditSubGroupCode, activityCode, processCode, findingCode, mappingId, riskBelongsTo);
	}

	@Override
	public void updateActivityProcessFindingMapping(ActivityProcessFindingMapping activityProcessFindingMapping) {

		if (!activityProcessFindingMapping.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {

			if (activityProcessFindingMapping.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| activityProcessFindingMapping.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)) {
				activityProcessFindingMapping.setMakerTimestamp(new Date());
			}

			// get Db object and save into the history
			ActivityProcessFindingMapping activityProcessFindingMappingDb = getActivityProcessFindingMapping(
					activityProcessFindingMapping.getLegalEntityCode(), activityProcessFindingMapping.getActivityId(),
					BankAuditConstant.STATUS_AUTH);
			if (activityProcessFindingMappingDb != null) {
				ActivityProcessFindingMappingHst maintEntityHst = new ActivityProcessFindingMappingHst();
				BeanUtils.copyProperties(activityProcessFindingMappingDb, maintEntityHst);
				activityProcessFindingMappingDao.save(maintEntityHst);
			}

			ActivityProcessFindingMappingWrk activityProcessFindingMappingWrk = new ActivityProcessFindingMappingWrk();
			BeanUtils.copyProperties(activityProcessFindingMapping, activityProcessFindingMappingWrk);
			activityProcessFindingMappingDao.saveOrUpdate(activityProcessFindingMappingWrk);

		} else {

			// get Db object and save into the history
			ActivityProcessFindingMapping activityProcessFindingMappingDb = getActivityProcessFindingMapping(
					activityProcessFindingMapping.getLegalEntityCode(), activityProcessFindingMapping.getActivityId(),
					BankAuditConstant.STATUS_AUTH);
			if (activityProcessFindingMappingDb != null) {
				activityProcessFindingMappingDb.setEntityStatus(BankAuditConstant.STATUS_EXPIRE);
				activityProcessFindingMappingDao.update(activityProcessFindingMappingDb);
			}

			// delete from work
			activityProcessFindingMappingDao.deleteActivityProcessFindingMapping(
					activityProcessFindingMapping.getLegalEntityCode(), activityProcessFindingMapping.getMappingId(),
					BankAuditConstant.STATUS_UNAUTH);
			// save again
			String mappingId = activityProcessFindingMapping.getMappingId();

			activityProcessFindingMapping.setMappingId(sequenceAppenderService.getAutoSequenceId());
			activityProcessFindingMappingDao.save(activityProcessFindingMapping);

			activityProcessFindingMappingDao.updateStatusforAuthModify_E(
					activityProcessFindingMapping.getLegalEntityCode(), mappingId, BankAuditConstant.STATUS_AUTH);

		}

	}

	@Override
	public ActivityProcessFindingMapping getActivityProcessFindingMapping(Integer legalEntityCode, String mappingId,
			String status) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("mappingId", mappingId);
		if (!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)) {
			ActivityProcessFindingMapping activityProcessFindingMapping = null;
			ActivityProcessFindingMappingWrk activityProcessFindingMappingWrk = (ActivityProcessFindingMappingWrk) activityProcessFindingMappingDao
					.getUniqueEntityByMatchingProperties(ActivityProcessFindingMappingWrk.class, properties);
			if (activityProcessFindingMappingWrk != null) {
				activityProcessFindingMapping = new ActivityProcessFindingMapping();
				BeanUtils.copyProperties(activityProcessFindingMappingWrk, activityProcessFindingMapping);
			}
			return activityProcessFindingMapping;
		} else {
			return (ActivityProcessFindingMapping) activityProcessFindingMappingDao
					.getUniqueEntityByMatchingProperties(ActivityProcessFindingMapping.class, properties);
		}

	}

	public void deleteActivityProcessFindingMapping(Integer legalEntityCode, String mappingId, String statusUnauth) {
		activityProcessFindingMappingDao.deleteActivityProcessFindingMapping(legalEntityCode, mappingId, statusUnauth);
	}
}
