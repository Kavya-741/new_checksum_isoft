package com.bankaudit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.dao.MaintAuditTypeDescDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.helper.BankAuditConstant;
import com.bankaudit.model.MaintAuditTypeDesc;

@Service
@Transactional("transactionManager")
public class MaintAuditTypeDescServiceImpl implements MaintAuditTypeDescService {

	@Autowired
	MaintAuditTypeDescDao maintAuditTypeDescDao;


	public DataTableResponse getMaintAuditTypeDesc(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		return maintAuditTypeDescDao.getMaintAuditTypeDesc( legalEntityCode,  search,  orderColumn,
				 orderDirection,  page,  size);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditTypeDesc> getMaintAuditTypeDescByLegalEntityCode(Integer legalEntityCode) {
		Map<String , Object> properties=new HashMap<>();
		properties.put("legalEntityCode",legalEntityCode );
		properties.put("entityStatus",BankAuditConstant.STATUS_ACTIVE );
	
		return maintAuditTypeDescDao.getEntitiesByMatchingProperties(MaintAuditTypeDesc.class, properties);
	}

}
