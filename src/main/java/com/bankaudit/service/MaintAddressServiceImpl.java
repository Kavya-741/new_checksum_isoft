package com.bankaudit.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankaudit.dao.MaintAddressDao;
import com.bankaudit.model.MaintAddress;

@Service
@Transactional
public class MaintAddressServiceImpl implements MaintAddressService {

	/**
	 * The maint address dao is autowired and make methods available from dao
	 * layer .
	 */
	@Autowired
	MaintAddressDao maintAddressDao;
	
	
	/* (non-Javadoc)
	 * @see com.bankaudit.service.MaintAddressService#createMaintAddress(com.bankaudit.model.MaintAddress)
	 */
	@Override
	public void createMaintAddress(MaintAddress maintaddress) {
		maintAddressDao.save(maintaddress);
	}

	/* (non-Javadoc)
	 * @see com.bankaudit.service.MaintAddressService#updateMaintAddress(com.bankaudit.model.MaintAddress)
	 */
	@Override
	public void updateMaintAddress(MaintAddress maintaddress) {
		maintAddressDao.update(maintaddress);
		
	}
	
	/* (non-Javadoc)
	 * @see com.bankaudit.service.MaintAddressService#getAllAddress(java.lang.Integer)
	 */
	@Override
	public List<MaintAddress> getAllAddress(Integer legalEntityCode) {
		System.out.println("Inside getAllAddress MaintAddressServiceImpl.... ");
		return maintAddressDao.getAllAddress(legalEntityCode);
	}
	
	/* (non-Javadoc)
	 * @see com.bankaudit.service.MaintAddressService#getAllCountryCode(java.lang.Integer)
	 */
	@Override
	public List<MaintAddress> getAllCountryCode(Integer legalEntityCode) {
		System.out.println("Inside getAllCountryCode MaintAddressServiceImpl.... ");
		return maintAddressDao.getAllCountryCode(legalEntityCode);
	}

	/* (non-Javadoc)
	 * @see com.bankaudit.service.MaintAddressService#getStatesbyCountry(java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<MaintAddress> getStatesbyCountry(Integer legalEntityCode,String countryAlpha3Code) {
		System.out.println("Inside getStatesbyCountry MaintAddressServiceImpl.... "+ countryAlpha3Code);
		return maintAddressDao.getStatesbyCountry(legalEntityCode,countryAlpha3Code);
	}

	/* (non-Javadoc)
	 * @see com.bankaudit.service.MaintAddressService#getDistrictsByCountryState(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MaintAddress> getDistrictsByCountryState(Integer legalEntityCode,String countryAlpha3Code, String stateCode) {
		System.out.println("Inside getDistrictsByCountryState MaintAddressServiceImpl....:: "+ countryAlpha3Code+ " sateCode:: "+ stateCode);
		return maintAddressDao.getDistrictsByCountryState(legalEntityCode,countryAlpha3Code,stateCode);
	}

	/* (non-Javadoc)
	 * @see com.bankaudit.service.MaintAddressService#getCitiesByCountryStateDist(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MaintAddress> getCitiesByCountryStateDist(Integer legalEntityCode,String countryAlpha3Code, String stateCode,
			String districtCode) {
		System.out.println("Inside getCitiesByCountryStateDist MaintAddressServiceImpl....:: "+ countryAlpha3Code+ " sateCode:: "+ stateCode+ "distCode:: "+districtCode);
		return maintAddressDao.getCitiesByCountryStateDist(legalEntityCode,countryAlpha3Code, stateCode, districtCode);
	}
}
