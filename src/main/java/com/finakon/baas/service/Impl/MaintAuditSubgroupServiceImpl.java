package com.finakon.baas.service.Impl;

import com.finakon.baas.entities.MaintAuditSubgroup;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.repository.JPARepositories.MaintAuditSubgroupRepository;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditSubgroupService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("transactionManager")
public class MaintAuditSubgroupServiceImpl implements MaintAuditSubgroupService {

	@Autowired
	MaintAuditSubgroupRepository maintAuditSubgroupRepository;
	
	@Override
	public List<MaintAuditSubgroup> getMaintAuditSubgroupes(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode) {
		return maintAuditSubgroupRepository.findByMatchingProperties(legalEntityCode, auditTypeCode, auditGroupCode, auditSubGroupCode, BankAuditConstant.STATUS_ACTIVE);
	}
}
