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

import com.bankaudit.dao.MaintAuditActivityDao;
import com.bankaudit.model.MaintAuditActivity;

@Service("maintAuditActivityservice")
@Transactional("transactionManager")
public class MaintAuditActivityServiceImpl implements MaintAuditActivityService{

	@Autowired
	MaintAuditActivityDao maintAuditActivityDao;
	

	@Autowired
	SequenceAppenderService sequenceAppenderService;



	@Override
	public List<MaintAuditActivity> getMaintAuditActivity(Integer legalEntityCode) {
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", "A");
		return maintAuditActivityDao.getEntitiesByMatchingProperties(MaintAuditActivity.class, properties);
	}

}
