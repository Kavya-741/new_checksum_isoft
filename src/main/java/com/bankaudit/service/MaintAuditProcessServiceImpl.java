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
import com.bankaudit.dao.MaintAuditProcessDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditProcess;
import com.bankaudit.model.MaintAuditProcessWrk;

@Service("maintAuditProcessService")
@Transactional("transactionManager")
public class MaintAuditProcessServiceImpl implements MaintAuditProcessService {

	@Autowired
	MaintAuditProcessDao maintAuditProcessDao;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	@Override
	public List<MaintAuditProcess> getMaintAuditProcess(Integer legalEntityCode) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", "A");
		return maintAuditProcessDao.getEntitiesByMatchingProperties(MaintAuditProcess.class, properties);
	}

	@Override
	public Boolean isMaintAuditProcess(Integer legalEntityCode, String processCode) {
		return maintAuditProcessDao.isMaintAuditProcess(legalEntityCode, processCode);
	}

	@Override
	public void createMaintAuditProcess(MaintAuditProcess maintAuditProcess) throws Exception {

		if (maintAuditProcess.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)) {

			MaintAuditProcessWrk maintAuditProcessWrk = new MaintAuditProcessWrk();
			BeanUtils.copyProperties(maintAuditProcess, maintAuditProcessWrk);
			maintAuditProcessDao.save(maintAuditProcessWrk);

		} else if (maintAuditProcess.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditProcessDao.save(maintAuditProcess);
		} else {
			throw new Exception("invalid status");
		}
	}

	@Override
	public DataTableResponse getMaintAuditProcess(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {

		return maintAuditProcessDao.getMaintAuditProcess(legalEntityCode, search, orderColumn,
				orderDirection, page, size);
	}

}
