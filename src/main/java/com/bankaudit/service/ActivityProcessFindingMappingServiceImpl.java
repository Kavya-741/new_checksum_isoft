package com.bankaudit.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.ActivityProcessFindingMappingDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.ActivityProcessFindingMapping;
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
			String auditSubGroupCode, String activityCode, String processCode, String findingCode,String mappingId, String riskBelongsTo) {
		return activityProcessFindingMappingDao.isActivityProcessFindingMapping( parseInt,  auditTypeCode,  auditGroupCode,
				 auditSubGroupCode,  activityCode,  processCode,  findingCode, mappingId, riskBelongsTo);
	}
}
