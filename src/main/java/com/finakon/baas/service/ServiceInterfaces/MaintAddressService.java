package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import com.finakon.baas.dto.Request.MaintAddressRequest;
import com.finakon.baas.entities.MaintAddress;

public interface MaintAddressService {

	/**
	 * This method is use to Creates the maint address.
	 *
	 * @param maintAuditActivity
	 *                           specify the maint audit activity
	 */
	void createMaintAddress(MaintAddressRequest maintAuditActivity);

	/**
	 * This method is use to Update maint address.
	 *
	 * @param maintAuditActivity
	 *                           specify the maint audit activity
	 */
	void updateMaintAddress(MaintAddressRequest maintAuditActivity);

	/**
	 * This method is use to Gets the all address.
	 *
	 * @param legalEntityCode
	 *                        specify the legal entity code
	 * @return the list .
	 */
	List<MaintAddress> getAllAddress(Integer legalEntityCode);

	/**
	 * This method is use to Gets the all country code.
	 *
	 * @param legalEntityCode
	 *                        specify the legal entity code
	 * @return the list .
	 */
	List<MaintAddress> getAllCountryCode(Integer legalEntityCode);

	/**
	 * This method is use to Gets the statesby country.
	 *
	 * @param legalEntityCode
	 *                          specify the legal entity code
	 * @param countryAlpha3Code
	 *                          specify the country alpha 3 code
	 * @return the list .
	 */
	List<MaintAddress> getStatesbyCountry(Integer legalEntityCode, String countryAlpha3Code);

	/**
	 * This method is use to Gets the districts by country state.
	 *
	 * @param legalEntityCode
	 *                          specify the legal entity code
	 * @param countryAlpha3Code
	 *                          specify the country alpha 3 code
	 * @param stateCode
	 *                          specify the state code
	 * @return the list .
	 */
	List<MaintAddress> getDistrictsByCountryState(Integer legalEntityCode, String countryAlpha3Code, String stateCode);

	/**
	 * This method is use to Gets the cities by country state dist.
	 *
	 * @param legalEntityCode
	 *                          specify the legal entity code
	 * @param countryAlpha3Code
	 *                          specify the country alpha 3 code
	 * @param stateCode
	 *                          specify the state code
	 * @param districtCode
	 *                          specify the district code
	 * @return the list .
	 */
	List<MaintAddress> getCitiesByCountryStateDist(Integer legalEntityCode, String countryAlpha3Code, String stateCode,
			String districtCode);

}
