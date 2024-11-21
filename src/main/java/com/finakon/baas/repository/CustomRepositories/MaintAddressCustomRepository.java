package com.finakon.baas.repository.CustomRepositories;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintAddress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class MaintAddressCustomRepository {

	@PersistenceContext
    private EntityManager entityManager;

	@SuppressWarnings({ "unchecked" })
	public List<MaintAddress> getAllAddress(Integer legalEntityCode) {
		return entityManager.createQuery(" from MaintAddress ma ")
		.getResultList();
		
	}
	
	@SuppressWarnings({ "unchecked" })
	public List<MaintAddress> getAllCountryCode(Integer legalEntityCode) {
		
		
		return entityManager.createQuery("select distinct ma.countryAlpha3Code as countryAlpha3Code , "
			    + " ma.countryNumericCode as countryNumericCode ,"
			    + " ma.countryName as countryName  , "
			    + " ma.ccyCd as ccyCd , "
			    + " ma.isdCode as isdCode , "
			    + " ma.gmtPlusMinus as gmtPlusMinus , "
			    + " ma.gmtOffsetHrs as gmtOffsetHrs , "
			    + " ma.gmtOffsetMins as gmtOffsetMins from MaintAddress ma where ma.legalEntityCode =:legalEntityCode")
				.setParameter("legalEntityCode", legalEntityCode)
				.getResultList();
		
		
	}

	@SuppressWarnings({ "unchecked" })
	public List<MaintAddress> getStatesbyCountry(Integer legalEntityCode,String countryAlpha3Code) {
		
		
		return entityManager.createQuery("select distinct ma.countryAlpha3Code as countryAlpha3Code, "
			    + " ma.countryName as countryName  , "
			    + " ma.stateCode as stateCode , "
			    + " ma.stateIsoCode as stateIsoCode , "
			    + " ma.stateName as stateName "
			     + " from MaintAddress ma where ma.legalEntityCode =:legalEntityCode and ma.countryAlpha3Code =:countryAlpha3Code "
			    + " order by ma.stateName ")
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("countryAlpha3Code", countryAlpha3Code)
				.getResultList();
		
		
	}


	@SuppressWarnings({ "unchecked" })
	public List<MaintAddress> getDistrictsByCountryState(Integer legalEntityCode,String countryAlpha3Code, String stateCode) {
		
		return entityManager.createQuery("select distinct ma.countryAlpha3Code as countryAlpha3Code, "
			    + " ma.countryName as countryName  , "
			    + " ma.stateCode as stateCode , "
			    + " ma.stateIsoCode as stateIsoCode , "
			    + " ma.stateName as stateName , "
			    + " ma.districtCode as districtCode , "
			    + " ma.districtName as districtName "
			    + "  from MaintAddress ma where ma.legalEntityCode =:legalEntityCode and ma.countryAlpha3Code =:countryAlpha3Code and ma.stateCode =:stateCode"
			    + " order by ma.districtName")
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("countryAlpha3Code", countryAlpha3Code)
				.setParameter("stateCode", stateCode)
				.getResultList();
		
		
	}


	@SuppressWarnings({ "unchecked" })
	public List<MaintAddress> getCitiesByCountryStateDist(Integer legalEntityCode,String countryAlpha3Code, String stateCode,
			String districtCode) {
		
		return entityManager.createQuery("from MaintAddress ma "
				+ " where ma.legalEntityCode =:legalEntityCode and ma.countryAlpha3Code =:countryAlpha3Code "
				+ " and ma.stateCode =:stateCode"
				+ " and ma.districtCode =:districtCode"
				+ " order by ma.cityName")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("countryAlpha3Code", countryAlpha3Code)
		.setParameter("stateCode", stateCode)
		.setParameter("districtCode", districtCode)
		.getResultList();
	}
	
}
