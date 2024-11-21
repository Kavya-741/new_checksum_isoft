package com.finakon.baas.repository.CustomRepositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;

import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.entities.MaintAuditTypeDesc;
import com.finakon.baas.helper.BankAuditUtil;
import com.finakon.baas.repository.JPARepositories.MaintAuditTypeDescRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


@Repository
public class GeneralParameterCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<GeneralParameter> getGeneralParameter(Integer legalEntityCode, String modCode, String language,
			String key1, String key2, String value, String maker) {
		
		StringBuilder queryStr1 = new StringBuilder(" from GeneralParameter gp ");
		StringBuilder queryStr2 = new StringBuilder("");
		
		Boolean conditional=false;
		
		
		if(legalEntityCode!=null){
			queryStr2=queryStr2.append(" and gp.legalEntityCode=:legalEntityCode");
			conditional=true;
		}
		if(!BankAuditUtil.isEmptyString(modCode)){
			queryStr2=queryStr2.append(" and gp.modCode=:modCode");
			conditional=true;
		}
		if(!BankAuditUtil.isEmptyString(language)){
			queryStr2=queryStr2.append(" and gp.language=:language");
			conditional=true;
		}
		if(!BankAuditUtil.isEmptyString(key1)){
			queryStr2=queryStr2.append(" and gp.key1=:key1");
			conditional=true;
		}
		if(!BankAuditUtil.isEmptyString(key2)){
			queryStr2=queryStr2.append(" and gp.key2=:key2");
			conditional=true;
		}
		if(!BankAuditUtil.isEmptyString(maker)){
			queryStr2=queryStr2.append(" and gp.maker=:maker");
			conditional=true;
		}
		
		if(conditional){
			queryStr1=queryStr1.append(" where "+queryStr2.substring(4));
		}
		
		
		
		Query query=entityManager.createQuery(queryStr1.toString());
		
		if(legalEntityCode!=null){
			query.setParameter("legalEntityCode", legalEntityCode);
		}
		if(!BankAuditUtil.isEmptyString(modCode)){query.setParameter("modCode", modCode);}
		if(!BankAuditUtil.isEmptyString(language)){query.setParameter("language", language);}
		if(!BankAuditUtil.isEmptyString(key1)){query.setParameter("key1", key1);}
		if(!BankAuditUtil.isEmptyString(maker)){query.setParameter("maker",maker);}
		if(!BankAuditUtil.isEmptyString(key2)){query.setParameter("key2", key2);}
		
		return query.getResultList();
	}
	
}
