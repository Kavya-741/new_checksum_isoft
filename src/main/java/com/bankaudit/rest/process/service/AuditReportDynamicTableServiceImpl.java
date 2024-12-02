package com.bankaudit.rest.process.service;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.rest.process.dao.AuditReportDynamicTableDao;

@Service("auditReportDynamicTableService")
@Transactional("transactionManager")
public class AuditReportDynamicTableServiceImpl implements AuditReportDynamicTableService{

	@Autowired
	AuditReportDynamicTableDao auditReportDynamicTableDao; 
	
	static final Logger logger = Logger.getLogger(AuditReportDynamicTableServiceImpl.class);
	
	@Override
	public DataTableResponse getDynamicTables(Integer legalEntityCode, String userId, String role, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size ) {
		return auditReportDynamicTableDao.getDynamicTables(legalEntityCode, userId, role, search, orderColumn,
				orderDirection, page, size ); 
	}
}
