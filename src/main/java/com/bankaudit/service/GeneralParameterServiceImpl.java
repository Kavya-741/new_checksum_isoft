package com.bankaudit.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankaudit.dao.GeneralParameterDao;
import com.bankaudit.model.GeneralParameter;

@Service
@Transactional
public class GeneralParameterServiceImpl implements GeneralParameterService {

	/**
	 * The general parameter dao is autowired and make methods available from
	 * dao layer .
	 */
	@Autowired
	GeneralParameterDao generalParameterDao;
	
	/* (non-Javadoc)
	 * @see com.bankaudit.service.GeneralParameterService#getGeneralParameter(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<GeneralParameter> getGeneralParameter(Integer legalEntityCode, String modCode, String language,
			String key1, String key2, String value, String maker) {
		return generalParameterDao.getGeneralParameter( legalEntityCode,  modCode,  language,
				 key1,  key2,  value,  maker);
	}
	
}
