/*
 * 
 */
package com.bankaudit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintAuditFindingDao;
import com.bankaudit.model.MaintAuditFinding;
import com.bankaudit.model.MaintAuditFindingWrk;

@Service("maintAuditFindingService")
@Transactional("transactionManager")
public class MaintAuditFindingServiceImpl implements MaintAuditFindingService {

	@Autowired
	MaintAuditFindingDao maintAuditFindingDao;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	@Override
	public List<MaintAuditFinding> getMaintAuditFinding(Integer legalEntityCode) {

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", "A");

		return maintAuditFindingDao.getEntitiesByMatchingProperties(MaintAuditFinding.class, properties);
	}

	@Override
	public Boolean isMaintAuditFinding(Integer legalEntityCode, String findingCode) {

		return maintAuditFindingDao.isMaintAuditFinding(legalEntityCode, findingCode);
	}

	@Override
	public void createMaintAuditFinding(MaintAuditFinding maintAuditFinding) throws Exception {

		if (maintAuditFinding.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)) {

			MaintAuditFindingWrk maintAuditFindingWrk = new MaintAuditFindingWrk();
			BeanUtils.copyProperties(maintAuditFinding, maintAuditFindingWrk);
			maintAuditFindingDao.save(maintAuditFindingWrk);

		} else if (maintAuditFinding.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditFindingDao.save(maintAuditFinding);
		} else {
			throw new Exception("invalid status");
		}
	}

	@Override
	public void deleteFindingByID(Integer legalEntityCode, String findingId, String userId) {
		maintAuditFindingDao.deleteMaintAuditFinding(legalEntityCode, findingId, BankAuditConstant.STATUS_REJ);
	}

}
