package com.finakon.baas.service.Impl;

import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.MaintAuditTypeDesc;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.repository.CustomRepositories.MaintAuditTypeDescCustomRepository;
import com.finakon.baas.repository.JPARepositories.MaintAuditTypeDescRepository;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditTypeDescService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("transactionManager")
public class MaintAuditTypeDescServiceImpl implements MaintAuditTypeDescService {

	@Autowired
	MaintAuditTypeDescCustomRepository maintAuditTypeDescCustomRepository;

	@Autowired
	MaintAuditTypeDescRepository maintAuditTypeDescRepository;

	@Override
	public DataTableResponse getMaintAuditTypeDesc(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		return maintAuditTypeDescCustomRepository.getMaintAuditTypeDesc(legalEntityCode, search, orderColumn,
				orderDirection, page, size);
	}

	@Override
	public List<MaintAuditTypeDesc> getMaintAuditTypeDescByLegalEntityCode(Integer legalEntityCode) {
		return maintAuditTypeDescRepository.findByLegalEntityCodeAndEntityStatus(legalEntityCode, BankAuditConstant.STATUS_ACTIVE);
	}

}
