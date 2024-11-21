/*
 * 
 */
package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import com.finakon.baas.entities.GeneralParameter;

public interface GeneralParameterService {

	/**
	 * This method is use to Gets the general parameter.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @param modCode
	 *            specify the mod code
	 * @param language
	 *            specify the language
	 * @param key1
	 *            specify the key 1
	 * @param key2
	 *            specify the key 2
	 * @param value
	 *            specify the value
	 * @param maker
	 *            specify the maker
	 * @return the list .
	 */
	List<GeneralParameter> getGeneralParameter(Integer legalEntityCode, String modCode, String language, String key1,
			String key2, String value, String maker);

}
