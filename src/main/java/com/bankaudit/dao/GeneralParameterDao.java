package com.bankaudit.dao;

import java.util.List;

import com.bankaudit.model.GeneralParameter;

public interface GeneralParameterDao extends Dao {

	List<GeneralParameter> getGeneralParameter(Integer legalEntityCode, String modCode, String language, String key1,
			String key2, String value, String maker);

	
	List<GeneralParameter> getObservationCriticality(Integer legalEntityCode, String modCode, String language,
			String key1, String key2, String value, String maker);

	GeneralParameter findByLegalEntityCodeAndKey1AndKey2(Integer legalEntityCode,String key1, String key2);
}
