package com.finakon.baas.service.Impl;

import com.finakon.baas.service.ServiceInterfaces.GeneralParameterService;
import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.repository.CustomRepositories.GeneralParameterCustomRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("transactionManager")
public class GeneralParameterServiceImpl implements GeneralParameterService {

	/**
	 * The general parameter dao is autowired and make methods available from
	 * dao layer .
	 */
	@Autowired
	GeneralParameterCustomRepository generalParameterCustomRepository;
	
	/* (non-Javadoc)
	 * @see com.bankaudit.service.GeneralParameterService#getGeneralParameter(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<GeneralParameter> getGeneralParameter(Integer legalEntityCode, String modCode, String language,
			String key1, String key2, String value, String maker) {
		return generalParameterCustomRepository.getGeneralParameter( legalEntityCode,  modCode,  language,
				 key1,  key2,  value,  maker);
	}
	
}
