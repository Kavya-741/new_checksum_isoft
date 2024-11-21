package com.finakon.baas.service.Impl;

import com.finakon.baas.dto.Request.MaintAddressRequest;
import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.MaintAddress;
import com.finakon.baas.repository.CustomRepositories.MaintAddressCustomRepository;
import com.finakon.baas.repository.JPARepositories.MaintAddressRepository;
import com.finakon.baas.repository.JPARepositories.MaintAuditTypeDescRepository;
import com.finakon.baas.service.ServiceInterfaces.MaintAddressService;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditTypeDescService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("transactionManager")
public class MaintAddressServiceImpl implements MaintAddressService {

	/**
	 * The maint address dao is autowired and make methods available from dao
	 * layer .
	 */
	@Autowired
	MaintAddressCustomRepository maintAddressCustomRepository;

	@Autowired
	MaintAddressRepository maintAddressRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bankaudit.service.MaintAddressService#createMaintAddress(com.bankaudit.
	 * model.MaintAddress)
	 */
	@Override
	public void createMaintAddress(MaintAddressRequest maintAddressRequest) {
		MaintAddress maintaddress = new MaintAddress();

		maintaddress.setCcyCd(maintAddressRequest.getCcyCd());
		maintaddress.setChecker(maintAddressRequest.getChecker());
		maintaddress.setCheckerTimestamp(maintAddressRequest.getCheckerTimestamp());
		maintaddress.setCityCode(maintAddressRequest.getCityCode());
		maintaddress.setCityName(maintAddressRequest.getCityName());
		maintaddress.setCountryAlpha3Code(maintAddressRequest.getCountryAlpha3Code());
		maintaddress.setCountryName(maintAddressRequest.getCountryName());
		maintaddress.setCountryNumericCode(maintAddressRequest.getCountryNumericCode());
		maintaddress.setDistrictCode(maintAddressRequest.getDistrictCode());
		maintaddress.setDistrictName(maintAddressRequest.getDistrictName());
		maintaddress.setGmtOffsetHrs(maintAddressRequest.getGmtOffsetHrs());
		maintaddress.setGmtOffsetMins(maintAddressRequest.getGmtOffsetMins());
		maintaddress.setIsdCode(maintAddressRequest.getIsdCode());
		maintaddress.setLegalEntityCode(maintAddressRequest.getLegalEntityCode());
		maintaddress.setMaker(maintAddressRequest.getMaker());
		maintaddress.setMakerTimestamp(maintAddressRequest.getMakerTimestamp());
		maintaddress.setStateCode(maintAddressRequest.getStateCode());
		maintaddress.setStateIsoCode(maintAddressRequest.getStateIsoCode());
		maintaddress.setStateName(maintAddressRequest.getStateName());
		maintaddress.setStatus(maintAddressRequest.getStatus());
		maintAddressRepository.save(maintaddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bankaudit.service.MaintAddressService#updateMaintAddress(com.bankaudit.
	 * model.MaintAddress)
	 */
	@Override
	public void updateMaintAddress(MaintAddressRequest maintAddressRequest) {
		MaintAddress maintaddress = new MaintAddress();

		maintaddress.setCcyCd(maintAddressRequest.getCcyCd());
		maintaddress.setChecker(maintAddressRequest.getChecker());
		maintaddress.setCheckerTimestamp(maintAddressRequest.getCheckerTimestamp());
		maintaddress.setCityCode(maintAddressRequest.getCityCode());
		maintaddress.setCityName(maintAddressRequest.getCityName());
		maintaddress.setCountryAlpha3Code(maintAddressRequest.getCountryAlpha3Code());
		maintaddress.setCountryName(maintAddressRequest.getCountryName());
		maintaddress.setCountryNumericCode(maintAddressRequest.getCountryNumericCode());
		maintaddress.setDistrictCode(maintAddressRequest.getDistrictCode());
		maintaddress.setDistrictName(maintAddressRequest.getDistrictName());
		maintaddress.setGmtOffsetHrs(maintAddressRequest.getGmtOffsetHrs());
		maintaddress.setGmtOffsetMins(maintAddressRequest.getGmtOffsetMins());
		maintaddress.setIsdCode(maintAddressRequest.getIsdCode());
		maintaddress.setLegalEntityCode(maintAddressRequest.getLegalEntityCode());
		maintaddress.setMaker(maintAddressRequest.getMaker());
		maintaddress.setMakerTimestamp(maintAddressRequest.getMakerTimestamp());
		maintaddress.setStateCode(maintAddressRequest.getStateCode());
		maintaddress.setStateIsoCode(maintAddressRequest.getStateIsoCode());
		maintaddress.setStateName(maintAddressRequest.getStateName());
		maintaddress.setStatus(maintAddressRequest.getStatus());
		maintAddressRepository.save(maintaddress);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bankaudit.service.MaintAddressService#getAllAddress(java.lang.Integer)
	 */
	@Override
	public List<MaintAddress> getAllAddress(Integer legalEntityCode) {
		System.out.println("Inside getAllAddress MaintAddressServiceImpl.... ");
		return maintAddressCustomRepository.getAllAddress(legalEntityCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bankaudit.service.MaintAddressService#getAllCountryCode(java.lang.
	 * Integer)
	 */
	@Override
	public List<MaintAddress> getAllCountryCode(Integer legalEntityCode) {
		System.out.println("Inside getAllCountryCode MaintAddressServiceImpl.... ");
		return maintAddressCustomRepository.getAllCountryCode(legalEntityCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bankaudit.service.MaintAddressService#getStatesbyCountry(java.lang.
	 * Integer, java.lang.String)
	 */
	@Override
	public List<MaintAddress> getStatesbyCountry(Integer legalEntityCode, String countryAlpha3Code) {
		System.out.println("Inside getStatesbyCountry MaintAddressServiceImpl.... " + countryAlpha3Code);
		return maintAddressCustomRepository.getStatesbyCountry(legalEntityCode, countryAlpha3Code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bankaudit.service.MaintAddressService#getDistrictsByCountryState(java.
	 * lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MaintAddress> getDistrictsByCountryState(Integer legalEntityCode, String countryAlpha3Code,
			String stateCode) {
		System.out.println("Inside getDistrictsByCountryState MaintAddressServiceImpl....:: " + countryAlpha3Code
				+ " sateCode:: " + stateCode);
		return maintAddressCustomRepository.getDistrictsByCountryState(legalEntityCode, countryAlpha3Code, stateCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bankaudit.service.MaintAddressService#getCitiesByCountryStateDist(java.
	 * lang.Integer, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MaintAddress> getCitiesByCountryStateDist(Integer legalEntityCode, String countryAlpha3Code,
			String stateCode,
			String districtCode) {
		System.out.println("Inside getCitiesByCountryStateDist MaintAddressServiceImpl....:: " + countryAlpha3Code
				+ " sateCode:: " + stateCode + "distCode:: " + districtCode);
		return maintAddressCustomRepository.getCitiesByCountryStateDist(legalEntityCode, countryAlpha3Code, stateCode,
				districtCode);
	}
}
