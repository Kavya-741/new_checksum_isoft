package com.bankaudit.rest.process.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.process.model.AuditSchedule;
import com.bankaudit.rest.process.dao.MyAuditsDao;
import com.bankaudit.service.MaintEntityService;
import com.bankaudit.util.BankAuditUtil;

@Service("myAuditService")
@Transactional("transactionManager")
public class MyAuditsServiceImpl implements MyAuditsService {

	@Autowired(required = true)
	MyAuditsDao myAuditsDao;

	static final Logger logger = Logger.getLogger(MyAuditsServiceImpl.class);

	@Override
	public DataTableResponse getMuAuditsList(int legalEntityCode, String userId, String unitCode, String roleId,
			String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		DataTableResponse ds = myAuditsDao.getMuAuditsList(legalEntityCode, userId, unitCode, roleId, search,
				orderColumn,
				orderDirection, page, size);
		return ds;
	}

}
