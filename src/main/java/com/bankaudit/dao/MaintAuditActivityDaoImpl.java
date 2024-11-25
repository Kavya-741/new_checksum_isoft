package com.bankaudit.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditActivity;
import com.bankaudit.helper.BankAuditUtil;


@Repository("maintAuditActivityDao")
public class MaintAuditActivityDaoImpl extends AbstractDao implements MaintAuditActivityDao{

	
}
