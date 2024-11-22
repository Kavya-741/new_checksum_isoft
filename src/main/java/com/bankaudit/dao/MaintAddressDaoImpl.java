package com.bankaudit.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.bankaudit.model.MaintAddress;


@Repository("maintAddressDao")
public class MaintAddressDaoImpl extends AbstractDao implements MaintAddressDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAddress> getAllAddress(Integer legalEntityCode) {
		Session session=getSession();
		return (List<MaintAddress>)session.createQuery(" from MaintAddress ma ").list();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAddress> getAllCountryCode(Integer legalEntityCode) {
		
		Session session=getSession();
		
		return (List<MaintAddress>)session.createQuery("select distinct ma.countryAlpha3Code as countryAlpha3Code , "
			    + " ma.countryNumericCode as countryNumericCode ,"
			    + " ma.countryName as countryName  , "
			    + " ma.ccyCd as ccyCd , "
			    + " ma.isdCode as isdCode , "
			    + " ma.gmtPlusMinus as gmtPlusMinus , "
			    + " ma.gmtOffsetHrs as gmtOffsetHrs , "
			    + " ma.gmtOffsetMins as gmtOffsetMins from MaintAddress ma where ma.legalEntityCode =:legalEntityCode")
				.setParameter("legalEntityCode", legalEntityCode)
			    .setResultTransformer(Transformers.aliasToBean(MaintAddress.class)).list();
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAddress> getStatesbyCountry(Integer legalEntityCode,String countryAlpha3Code) {
		Session session=getSession();
		
		
		return (List<MaintAddress>)session.createQuery("select distinct ma.countryAlpha3Code as countryAlpha3Code, "
			    + " ma.countryName as countryName  , "
			    + " ma.stateCode as stateCode , "
			    + " ma.stateIsoCode as stateIsoCode , "
			    + " ma.stateName as stateName "
			     + " from MaintAddress ma where ma.legalEntityCode =:legalEntityCode and ma.countryAlpha3Code =:countryAlpha3Code "
			    + " order by ma.stateName ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("countryAlpha3Code", countryAlpha3Code).setResultTransformer(Transformers.aliasToBean(MaintAddress.class))
				.list();
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAddress> getDistrictsByCountryState(Integer legalEntityCode,String countryAlpha3Code, String stateCode) {
		Session session=getSession();
		
		return (List<MaintAddress>)session.createQuery("select distinct ma.countryAlpha3Code as countryAlpha3Code, "
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
				.setResultTransformer(Transformers.aliasToBean(MaintAddress.class)).list();
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAddress> getCitiesByCountryStateDist(Integer legalEntityCode,String countryAlpha3Code, String stateCode,
			String districtCode) {
		Session session=getSession();
		
		return (List<MaintAddress>)session.createQuery("from MaintAddress ma "
				+ " where ma.legalEntityCode =:legalEntityCode and ma.countryAlpha3Code =:countryAlpha3Code "
				+ " and ma.stateCode =:stateCode"
				+ " and ma.districtCode =:districtCode"
				+ " order by ma.cityName")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("countryAlpha3Code", countryAlpha3Code)
		.setParameter("stateCode", stateCode)
		.setParameter("districtCode", districtCode).list();
	}
	
}
