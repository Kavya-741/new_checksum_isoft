package com.bankaudit.dao;

import java.util.List;

import com.bankaudit.model.MaintAddress;

public interface MaintAddressDao extends Dao{
	
	List<MaintAddress> getAllAddress(Integer legalEntityCode);
	
	List<MaintAddress> getAllCountryCode(Integer legalEntityCode);
	
	List<MaintAddress> getStatesbyCountry(Integer legalEntityCode,String countryAlpha3Code);
	
	List<MaintAddress> getDistrictsByCountryState(Integer legalEntityCode,String countryAlpha3Code, String stateCode);
	
	List<MaintAddress> getCitiesByCountryStateDist(Integer legalEntityCode,String countryAlpha3Code, String stateCode, String districtCode);
	

}
