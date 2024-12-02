package com.bankaudit.dao;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.bankaudit.model.GeneralParameter;
import com.bankaudit.util.BankAuditUtil;

@Repository("generalParameterDao")
public class GeneralParameterDaoImpl extends AbstractDao implements GeneralParameterDao{

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<GeneralParameter> getGeneralParameter(Integer legalEntityCode, String modCode, String language,
			String key1, String key2, String value, String maker) {
		
		Session session=getSession();
		
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
		
		
		
		Query query=session.createQuery(queryStr1.toString());
		
		if(legalEntityCode!=null){
			query.setParameter("legalEntityCode", legalEntityCode);
		}
		if(!BankAuditUtil.isEmptyString(modCode)){query.setParameter("modCode", modCode);}
		if(!BankAuditUtil.isEmptyString(language)){query.setParameter("language", language);}
		if(!BankAuditUtil.isEmptyString(key1)){query.setParameter("key1", key1);}
		if(!BankAuditUtil.isEmptyString(maker)){query.setParameter("maker",maker);}
		if(!BankAuditUtil.isEmptyString(key2)){query.setParameter("key2", key2);}
		
		return query.list();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<GeneralParameter> getObservationCriticality(Integer legalEntityCode, String modCode, String language,
			String key1, String key2, String value, String maker) {
		
		Session session=getSession();		
		StringBuilder queryStr  = new StringBuilder(" Select "
				+ " legal_entity_code as legalEntityCode, mod_code as modCode, language as language, "
				+ " key_1 as key1, key_2 as key2,  value, description, "
				+ " maker, maker_timestamp as  makerTimestamp, checker, checker_timestamp as checkerTimestamp"
				+ " from general_parameter gp "
				+ " Where gp.legal_entity_code = :legalEntityCode "); 	
	 
		if(!BankAuditUtil.isEmptyString(modCode)){
			queryStr .append(" and gp.mod_code=:modCode"); 
		}
		if(!BankAuditUtil.isEmptyString(language)){
			queryStr .append(" and gp.language=:language"); 
		}
		if(!BankAuditUtil.isEmptyString(key1)){
			queryStr .append(" and gp.key_1=:key1"); 
		}		 
		if(!BankAuditUtil.isEmptyString(maker)){
			queryStr.append(" and gp.maker=:maker"); 
		}  
		 		
		Query query=session.createNativeQuery(queryStr.toString()).setResultTransformer(Transformers.aliasToBean(GeneralParameter.class));			
		if(legalEntityCode!=null){query.setParameter("legalEntityCode", legalEntityCode);}
		if(!BankAuditUtil.isEmptyString(modCode)){query.setParameter("modCode", modCode);}
		if(!BankAuditUtil.isEmptyString(language)){query.setParameter("language", language);}
		if(!BankAuditUtil.isEmptyString(key1)){query.setParameter("key1", key1);}
		if(!BankAuditUtil.isEmptyString(maker)){query.setParameter("maker",maker);} 
		
		List<GeneralParameter> generalParameterList =  query.list();	
		List<GeneralParameter> crityCalityListMatched = new ArrayList<>();
		List<GeneralParameter> crityCalityListAll = new ArrayList<>();
		
		generalParameterList.stream().forEach(key2Val->{
			if(key2Val.getKey2().equalsIgnoreCase(key2)){
				crityCalityListMatched.add(key2Val);
			}else if(key2Val.getKey2().equalsIgnoreCase("ALL")){
				crityCalityListAll.add(key2Val);
			}
		});
		
		return crityCalityListMatched.size()>0?crityCalityListMatched:crityCalityListAll;
	}

	@SuppressWarnings("deprecation")
	@Override
	public GeneralParameter findByLegalEntityCodeAndKey1AndKey2(Integer legalEntityCode, String key1, String key2) {
		Session session=getSession();		
		StringBuilder queryStr  = new StringBuilder(" Select "
				+ " legal_entity_code as legalEntityCode, mod_code as modCode, language as language, "
				+ " key_1 as key1, key_2 as key2,  value, description, "
				+ " maker, maker_timestamp as  makerTimestamp, checker, checker_timestamp as checkerTimestamp "
				+ " from general_parameter gp "
				+ " Where gp.legal_entity_code = :legalEntityCode "); 	
	 
		if(!BankAuditUtil.isEmptyString(key1)){
			queryStr .append(" and gp.key_1=:key1"); 
		}

		if(!BankAuditUtil.isEmptyString(key2)){
			queryStr .append(" and gp.key_2=:key2"); 
		}	

		 		
		@SuppressWarnings("deprecation")
		Query query=session.createNativeQuery(queryStr.toString()).setResultTransformer(Transformers.aliasToBean(GeneralParameter.class));			
		if(legalEntityCode!=null){query.setParameter("legalEntityCode", legalEntityCode);}
		if(!BankAuditUtil.isEmptyString(key1)){query.setParameter("key1", key1);}
		if(!BankAuditUtil.isEmptyString(key2)){query.setParameter("key2", key2);}
		
		GeneralParameter generalParameter =  (GeneralParameter) query.getSingleResult();	
		return generalParameter;
	}
	
}
