/*
 * 
 */
package com.bankaudit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.dao.MaintAuditProcessDao;
import com.bankaudit.model.MaintAuditProcess;

@Service("maintAuditProcessService")
@Transactional("transactionManager")
public class MaintAuditProcessServiceImpl implements MaintAuditProcessService {

	@Autowired
	MaintAuditProcessDao maintAuditProcessDao;
	
	@Autowired
	SequenceAppenderService sequenceAppenderService;
	

	@Override
	public List<MaintAuditProcess> getMaintAuditProcess(Integer legalEntityCode) {
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", "A");
		return maintAuditProcessDao.getEntitiesByMatchingProperties(MaintAuditProcess.class, properties);
	}

}
