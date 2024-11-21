package com.finakon.baas.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finakon.baas.entities.MaintEntity;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.repository.CustomRepositories.MaintEntityCustomRepository;
import com.finakon.baas.repository.JPARepositories.MaintEntityRepository;
import com.finakon.baas.service.ServiceInterfaces.MaintEntityService;

@Service("maintEntityService")
@Transactional("transactionManager")
public class MaintEntityServiceImpl implements MaintEntityService {

	@Autowired
	MaintEntityCustomRepository maintEntityCustomRepository;

	@Autowired
	MaintEntityRepository maintEntityRepository;

	public List<String> getSubBranchesByUserIdOrUnitId(Integer legalEntityCode, String type, String userIdOrUnitId) {
		return maintEntityCustomRepository.getSubBranchesByUserIdOrUnitId(legalEntityCode, type, userIdOrUnitId);
	}


	@Override
	public List<MaintEntity> getMaintEntityByLegalEntityCode(Integer legalEntityCode) {
		return maintEntityRepository.findByLegalEntityCodeAndEntityStatus(legalEntityCode, BankAuditConstant.STATUS_ACTIVE);
	}
}
