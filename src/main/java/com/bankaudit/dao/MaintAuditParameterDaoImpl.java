package com.bankaudit.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditParameter;
import com.bankaudit.model.MaintAuditParameterDerived;
import com.bankaudit.model.MaintAuditParameterWrk;
import com.bankaudit.model.MaintAuditTypeDesc;
import com.bankaudit.util.BankAuditUtil;

@Repository("maintAuditParameterDao")
public class MaintAuditParameterDaoImpl extends AbstractDao implements MaintAuditParameterDao{

	static final Logger logger = Logger.getLogger(MaintAuditParameterDaoImpl.class);
	
	@SuppressWarnings("deprecation")
	@Override
	public DataTableResponse getAuditParameter(Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintAuditParameter> maintAuditParameters = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
				
		try {
		
		StringBuilder queryString=new StringBuilder(                "   SELECT      " + 
				"         map.legal_entity_code AS legal_entity_code ," + 
				"         (SELECT Concat(mc.criticality_code ,'-',mc.criticality_desc)            " + 
				"         FROM" + 
				"             maint_criticality mc              " + 
				"         WHERE" + 
				"             mc.criticality_of_type = 'ENTITY'                     " + 
				"             AND mc.criticality_code = map.u_criticality                     " + 
				"             AND mc.legal_entity_code = map.legal_entity_code ) AS u_criticality," + 
				"             map.audit_type_code AS audit_type_code ," + 
				"         (SELECT Concat ( gp.value, '-' , gp.description )              " + 
				"         FROM general_parameter gp              " + 
				"         WHERE gp.key_1 = 'UNIT_TYPE'                     " + 
				"             AND gp.value = map.unit_type                     " + 
				"             AND gp.legal_entity_code = map.legal_entity_code ) AS unit_type ,             " + 
				
				"         map.id AS id ," +				
			    //"         map.classification AS classification ," + 
				"         (SELECT Concat ( gp.value, '-' , gp.description )              " + 
				"         FROM general_parameter gp              " + 
				"         WHERE gp.key_1 = 'CLASSIFICATION' AND gp.key_2 = 'GEOGRAPHY'                     " + 
				"             AND gp.legal_entity_code = map.legal_entity_code AND gp.value = map.classification ) AS classification ,             " +				
				//"         map.rating AS rating ," +
				"			(SELECT CONCAT(rsp.risk_category_code,'-',rsp.risk_category_desc)  " +
				"			FROM `risk_score_percentage` rsp  " +
				"			WHERE rsp.risk_category_code = map.rating  " +
				"			AND rsp.legal_entity_code =  map.legal_entity_code " +
 				"			AND rsp.audit_type_code = map.audit_type_code) AS rating , " +				
				//"         map.revenue_leakage AS revenue_leakage ," + 
				"         (SELECT Concat (gp.value, '-' , gp.description)              " + 
				"         FROM general_parameter gp              " + 
				"         WHERE gp.key_1 = 'REVENUE_LEAK' AND gp.key_2 = 'REVENUE_LEAK' " + 
				"         AND gp.legal_entity_code = map.legal_entity_code AND gp.value = map.revenue_leakage) AS revenue_leakage ,             " + 				
				"         map.response_time AS response_time ," + 				
				"         map.frequency_type AS frequency_type ," + 
				"         map.frequency     AS frequency ," + 
				"         map.duration      AS duration ," + 
				"         map.man_days      AS man_days ," +
				"         map.closure_time    as closure_time, " +
				/*
				"         (SELECT Concat ( gp.value , '-' ,  gp.description )              " + 
				"         FROM general_parameter gp              " + 
				"         WHERE" + 
				"             gp.key_1 = 'STATUS'                     " + 
				"             AND gp.value = if(map.status,'M','U')  " + 
				"             AND gp.legal_entity_code = map.legal_entity_code ) AS status ," + 
				*/
				
				/*"         (SELECT Concat ( gp.value , '-' ,  gp.description )              " + 
				"         FROM general_parameter gp              " + 
				"         WHERE" + 
				"             gp.key_1 = 'ENTITY_STATUS'                     " + 
				"             AND gp.value = map.entity_status  " + 
				"             AND gp.legal_entity_code = map.legal_entity_code ) AS entity_status ," + */

				"         (SELECT Concat (  gp.value  , '-' ,  gp.description )              " + 
				"         FROM general_parameter gp              " + 
				"         WHERE gp.key_1 = 'STATUS'                     " + 
				"             AND gp.value = (case when map.status='M' then 'U' else map.status end)  " + 
				"             AND gp.legal_entity_code = map.legal_entity_code )  AS STATUS ," +
				
				"         map.auth_rej_remarks       as mat_auth_rej_remarks ," + 
				"         (SELECT DISTINCT CONCAT(map.maker,'-',first_name,last_name) FROM user usr WHERE usr.user_id = map.maker ANd usr.legal_entity_code=map.legal_entity_code ) AS maker ," +
				//"         map.maker                 AS maker ," + 
				"         map.maker_timestamp        AS maker_timestamp ," + 
				"        (SELECT DISTINCT CONCAT(map.checker,'-',first_name,last_name) FROM user usr WHERE usr.user_id = map.checker ANd usr.legal_entity_code=map.legal_entity_code ) AS checker," +
				//"         map.checker               AS checker ," + 
				"         map.checker_timestamp      AS checker_timestamp ," + 
				"         mat.legal_entity_code      as mat_legal_entity_code ," + 
				"         mat.audit_type_code        as mat_audit_type_code ," + 
				"         mat.audit_type_desc        as mat_audit_type_desc ," + 
				"         mat.auth_rej_remarks       as audit_desc_remarks," + 
				//"         mat.status                 as mat_STATUS ," +
				"         (SELECT Concat ((case when gp.value='U' then 'M' else mat.status end)  , '-' ,  gp.description )              " + 
				"         FROM general_parameter gp              " + 
				"         WHERE" + 
				"             gp.key_1 = 'STATUS'                     " + 
				"             AND gp.value = (case when mat.status='M' then 'U' else mat.status end)  " + 
				"             AND gp.legal_entity_code = mat.legal_entity_code  )  AS mat_STATUS ," + 
				
				"         (SELECT DISTINCT CONCAT(mat.maker,'-',first_name,last_name) FROM user usr WHERE usr.user_id = mat.maker ANd mat.legal_entity_code=usr.legal_entity_code ) as mat_maker ," +
				//"         mat.maker                  as mat_maker ," + 
				"         mat.maker_timestamp        as mat_maker_timestamp ," +
				"        (SELECT DISTINCT CONCAT(mat.checker,'-',first_name,last_name) FROM user usr WHERE usr.user_id = mat.checker ANd mat.legal_entity_code=usr.legal_entity_code ) as mat_checker," +
				//"         mat.checker                as mat_checker ," + 
				"         mat.checker_timestamp      as mat_checker_timestamp " + 				 
				"     FROM " + 
				"         ( (SELECT c1.* FROM" + 
				"             maint_audit_parameter c1              " + 
				"         WHERE" + 
				"             c1.legal_entity_code=:legalEntityCode )" + 
				"     UNION" + 
				"           (SELECT c2.* FROM" + 
				"             maint_audit_parameter_wrk c2                  " + 
				"         WHERE" + 
				"             c2.legal_entity_code=:legalEntityCode ) " + 
				"	) map " + 
				" right outer join" + 
				" ((SELECT c3.* FROM" + 
				"         maint_audit_type_desc c3                  " + 
				"     WHERE" + 
				"         c3.legal_entity_code=:legalEntityCode           " + 
				"   ) " + 
				" UNION" + 
				" (SELECT c4.* FROM" + 
				"     maint_audit_type_desc_wrk c4                  " + 
				" WHERE" + 
				"     c4.legal_entity_code=:legalEntityCode           " + 
				"  ) " + 
				"  ) mat " +
				" on (mat.legal_entity_code=map.legal_entity_code and map.audit_type_code = mat.audit_type_code ) " + 
				" where mat.legal_entity_code=:legalEntityCode ");

		if(!BankAuditUtil.isEmptyString(auditTypeCode) && !"null".equals(auditTypeCode)){
			queryString=queryString.append(" and map.audit_type_code = mat.audit_type_code ");					
			queryString=queryString.append(" and mat.audit_type_code=:auditTypeCode ");
		}
		
		if(entitlement!=null && !BankAuditUtil.isEmptyString(entitlement) && !"null".equals(entitlement) && "A".equals(entitlement)) {
			queryString=queryString.append(" and map.maker != '" +userName+"'");	
		}
		
		if(!BankAuditUtil.isEmptyString(search) && !"null".equals(search)){
			queryString=queryString.append(" and ( "
					+ " mat.audit_type_code like :search "
					+ " or  mat.audit_type_desc like :search  "
					+ " or  map.u_criticality IN ( "
					+ " select mc.criticality_code "
					+ " from maint_criticality mc where "
					+ " mc.criticality_of_type='ENTITY' and mc.criticality_desc LIKE :search "
					+ " and mc.legal_entity_code=map.legal_entity_code ANd map.legal_entity_code=:legalEntityCode "
					+ " ) "
	                + " or  map.unit_type In ( "
	                + " select gp.value "
				    + " from general_parameter gp where "
				    + " gp.key_1='UNIT_TYPE' and gp.description LIKE :search "
				    + " and gp.legal_entity_code=map.legal_entity_code  "
	                + " ) "
					//  classification
					+ " or  map.classification In ( "
					+ " select gp.value "
					+ " from general_parameter gp where "
					+ " gp.key_1='CLASSIFICATION' and gp.key_2='GEOGRAPHY' and gp.description LIKE :search "
					+ " and gp.legal_entity_code=map.legal_entity_code  "
					+ " ) "
                    //  rating
					+ " or  map.rating In ( SELECT rsp.risk_category_code  " 
					+ "	FROM `risk_score_percentage` rsp  " 
					+ "	WHERE rsp.legal_entity_code =  map.legal_entity_code " 
					+ " AND rsp.audit_type_code = map.audit_type_code "
					+ " AND rsp.risk_category_desc LIKE :search ) " 	
					// revenue_leakage
					+ " or  map.revenue_leakage In ( "
					+ " select gp.value "
					+ " from general_parameter gp where "
					+ " gp.key_1='REVENUE_LEAK' and gp.key_2='REVENUE_LEAK' and gp.description LIKE :search "
					+ " and gp.legal_entity_code=map.legal_entity_code  "
					+ " ) "
					// response_time
					+ " or map.response_time LIKE :search"
					+ " or map.frequency LIKE :search "
					+ " or map.duration LIKE :search "
					+ " or map.man_days LIKE :search "
					
					+ " or map.closure_time LIKE :search"
					
                    + " or map.status IN ( SELECT (case when gp1.value='U' or gp1.value='AU' then 'M' else mat.status end) as value FROM general_parameter gp1 WHERE "
                    + " gp1.key_1='STATUS' and gp1.description LIKE :search "
                    + " and gp1.legal_entity_code=map.legal_entity_code  ) "
                    
                    + " or map.maker in (SELECT DISTINCT usr.user_id FROM user usr WHERE CONCAT(usr.user_id,'-',usr.first_name,usr.last_name) like :search ANd usr.legal_entity_code=:legalEntityCode ) "
                    
					+ ") ");
		}
		
		String[] columns={
				" mat.audit_type_code",
				" mat.audit_type_desc",
			    " map.u_criticality",
			    " map.unit_type ",
			    " map.classification ",
			    " map.rating ",
			    " map.revenue_leakage ",
			    " map.frequency*1 ",
			    " map.duration*1 ",
			    " map.man_days*1 ",
			    " map.response_time*1 ",
			    " map.closure_time*1 ",
				" map.maker",
				" map.status"
		};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(map.STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		//Query query=session.createQuery(queryString.toString()).setResultTransformer(Transformers.aliasToBean(MaintAuditParameter.class))
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						MaintAuditParameter maintAuditParameters=null;
						MaintAuditTypeDesc maintAuditTypeDesc=null;
						int i=-1;
						if(tuple!=null){
							maintAuditParameters=new MaintAuditParameter();
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setLegalEntityCode(Integer.parseInt(tuple[i].toString()));
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setUCriticality(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setAuditTypeCode(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setUnitType(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setId(Integer.parseInt(tuple[i].toString()));
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setClassification(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setRating(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setRevenueLeakage(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setResponseTime(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setFrequencyType(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setFrequency(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setDuration(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setManDays(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setClosureTime(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setStatus(tuple[i].toString());
							}							
							
						    /*
						    i++;
						    if(tuple[i]!=null){
								maintAuditParameters.setEntityStatus(tuple[i].toString());
							}
							*/
							
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setAuthRejRemarks(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setMaker(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								try {
									maintAuditParameters.setMakerTimestamp((Date)tuple[i]);
									}catch (ClassCastException e) {
										maintAuditParameters.setMakerTimestamp(java.sql.Timestamp.valueOf(tuple[i].toString()));
									}
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setChecker(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								try {
									maintAuditParameters.setCheckerTimestamp((Date)tuple[i]);
									}catch (ClassCastException e) {
										maintAuditParameters.setCheckerTimestamp(java.sql.Timestamp.valueOf(tuple[i].toString()));
									}
							}
							
								maintAuditTypeDesc=new MaintAuditTypeDesc();
								i++;
								if(tuple[i]!=null) {
									maintAuditTypeDesc.setLegalEntityCode(Integer.parseInt(tuple[i].toString()));
								}	
								i++;
								if(tuple[i]!=null) {
									maintAuditTypeDesc.setAuditTypeCode(tuple[i].toString());
								}
								i++;
								if(tuple[i]!=null) {
									maintAuditTypeDesc.setAuditTypeDesc(tuple[i].toString());
								}
								i++; // value to be checked
								i++;
								if(tuple[i]!=null) {
									maintAuditTypeDesc.setStatus(tuple[i].toString());
								}
								i++;
								if(tuple[i]!=null){
									maintAuditTypeDesc.setMaker(tuple[i].toString());
								}
								i++;
								if(tuple[i]!=null){
									try {
									maintAuditTypeDesc.setMakerTimestamp((Date)tuple[i]);
									}catch (ClassCastException e) {
										maintAuditTypeDesc.setMakerTimestamp(java.sql.Timestamp.valueOf( tuple[i].toString()));
									}
								}
								i++;
								if(tuple[i]!=null){
									maintAuditTypeDesc.setChecker(tuple[i].toString());
								}
								i++;
								if(tuple[i]!=null){
									try {
										maintAuditTypeDesc.setCheckerTimestamp((Date)tuple[i]);
									}catch (ClassCastException e) {
										maintAuditTypeDesc.setCheckerTimestamp(java.sql.Timestamp.valueOf( tuple[i].toString()));
									}
								}
								
								maintAuditParameters.setMaintAuditTypeDesc(maintAuditTypeDesc);
							
						}
						return maintAuditParameters;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legalEntityCode", legalEntityCode);
		
		if(!BankAuditUtil.isEmptyString(auditTypeCode)){
			query.setParameter("auditTypeCode", auditTypeCode);
		}
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			maintAuditParameters= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintAuditParameters.add((MaintAuditParameter) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintAuditParameters);
			resultScroll.last();
			if(size!=0){
				dataTableResponse.setRecordsFiltered((resultScroll.getRowNumber()+1l/size)+1);
				dataTableResponse.setRecordsTotal((resultScroll.getRowNumber()+1l/size)+1);
			}else{
				dataTableResponse.setError("page size zero");
				dataTableResponse.setData(Collections.EMPTY_LIST);
				dataTableResponse.setRecordsTotal(0l);
				dataTableResponse.setRecordsFiltered(0l);
			}
			
		}else{
			dataTableResponse.setError(null);
			dataTableResponse.setData(Collections.EMPTY_LIST);
			dataTableResponse.setRecordsTotal(0l);
			dataTableResponse.setRecordsFiltered(0l);
		}
		
		
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return dataTableResponse;

	
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditParameter> getMaintAuditParameterbyAuditType(Integer legalEntityCode, String auditTypeCode) {
		
		Session session=getSession();
		
		
		
		String hql=" from MaintAuditParameter m where m.legalEntityCode =:legalEntityCode and m.auditTypeCode =:auditTypeCode "
				+ " UNION from MaintAuditParameterWrk m2 where m2.legalEntityCode =:legalEntityCode and m2.auditTypeCode =:auditTypeCode ";
		
		
		//return session.createQuery("  from MaintAuditParameter m where m.legalEntityCode =:legalEntityCode and m.auditTypeCode =:auditTypeCode ")
		return session.createQuery(hql)
				//.setResultTransformer(Transformers.aliasToBean(MaintAuditParameter.class))
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode).list();
	}
	
	
	
	@SuppressWarnings("deprecation")
	public Map<String,Object> isMaintAuditParameterAlreadyExist(MaintAuditParameterWrk maintAuditParameter) {

		Session session=getSession();
		List<MaintAuditParameter> map=new ArrayList<>() ;
		
		Map<String,Object> isExist=new HashMap<>();
		
		boolean flgAllowToCreateInWrk=false;
		String OperationType="DataExist";
		
					//checked the combination in work table
					String hql2=" select w.uCriticality,w.unitType,w.classification,w.revenueLeakage,w.rating from MaintAuditParameterWrk w where w.legalEntityCode =:legalEntityCode and w.auditTypeCode =:auditTypeCode ";
					if(maintAuditParameter.getUCriticality()==null)
						hql2+=" and w.uCriticality is null ";
					else
						hql2+=" and w.uCriticality =:uCriticality ";
					if(maintAuditParameter.getUnitType()==null)
						hql2+= "and w.unitType is null ";	
					else	
						hql2+= "and w.unitType=:unitType ";
					if(maintAuditParameter.getClassification()==null)
						hql2+= "and w.classification is null ";
					else
						hql2+= "and w.classification=:classification ";
					if(maintAuditParameter.getRevenueLeakage()==null)
						hql2+=" and w.revenueLeakage is null ";
					else	
						hql2+=" and w.revenueLeakage =:revenueLeakage ";
					if(maintAuditParameter.getRating()==null)
						hql2+= "and w.rating is null ";
					else	
						hql2+= "and w.rating=:rating ";

					if(maintAuditParameter.getId()==null)
					 hql2+= "and w.id!=:id ";

					Query query2= session.createQuery(hql2);
					query2.setParameter("legalEntityCode", maintAuditParameter.getLegalEntityCode()).setParameter("auditTypeCode", maintAuditParameter.getAuditTypeCode());

					if(maintAuditParameter.getUCriticality()!=null)
						query2.setParameter("uCriticality", maintAuditParameter.getUCriticality());

					if(maintAuditParameter.getUnitType()!=null)
						query2.setParameter("unitType", maintAuditParameter.getUnitType());

					if(maintAuditParameter.getClassification()!=null)
						query2.setParameter("classification", maintAuditParameter.getClassification());

					if(maintAuditParameter.getRevenueLeakage()!=null)
						query2.setParameter("revenueLeakage", maintAuditParameter.getRevenueLeakage());

					if(maintAuditParameter.getRating()!=null)
						query2.setParameter("rating", maintAuditParameter.getRating());
					
					 if(maintAuditParameter.getId()==null)
					 query2.setParameter("id", (maintAuditParameter.getId()==null)?new Integer(-1):maintAuditParameter.getId());

					map.addAll(query2.list());
					
					
					//Data checking 
					if(map.size()>0) {
						map = new ArrayList<>();
						
						
						hql2=" select w.uCriticality,w.unitType,w.classification,w.revenueLeakage,w.rating from MaintAuditParameterWrk w where w.legalEntityCode =:legalEntityCode and w.auditTypeCode =:auditTypeCode ";
						if(maintAuditParameter.getUCriticality()==null)
							hql2+=" and w.uCriticality is null ";
						else
							hql2+=" and w.uCriticality =:uCriticality ";
						if(maintAuditParameter.getUnitType()==null)
							hql2+= "and w.unitType is null ";	
						else	
							hql2+= "and w.unitType=:unitType ";
						if(maintAuditParameter.getClassification()==null)
							hql2+= "and w.classification is null ";
						else
							hql2+= "and w.classification=:classification ";
						if(maintAuditParameter.getRevenueLeakage()==null)
							hql2+=" and w.revenueLeakage is null ";
						else	
							hql2+=" and w.revenueLeakage =:revenueLeakage ";
						if(maintAuditParameter.getRating()==null)
							hql2+= "and w.rating is null ";
						else	
							hql2+= "and w.rating=:rating ";
						
						//Added to check data
						if(maintAuditParameter.getFrequency()==null)
							hql2+= "and w.frequency is null ";
						else
							hql2+= "and w.frequency=:frequency ";
						if(maintAuditParameter.getDuration()==null)
							hql2+= "and w.duration is null ";
						else
							hql2+= "and w.duration=:duration ";
						if(maintAuditParameter.getManDays()==null)
							hql2+= "and w.manDays is null ";
						else
							hql2+= "and w.manDays=:manDays ";
						if(maintAuditParameter.getResponseTime()==null)
							hql2+= "and w.responseTime is null ";
						else
							hql2+= "and w.responseTime=:responseTime ";
						
						if(maintAuditParameter.getId()==null)
						   hql2+= "and w.id!=:id ";

						query2= session.createQuery(hql2);
						query2.setParameter("legalEntityCode", maintAuditParameter.getLegalEntityCode()).setParameter("auditTypeCode", maintAuditParameter.getAuditTypeCode());

						if(maintAuditParameter.getUCriticality()!=null)
							query2.setParameter("uCriticality", maintAuditParameter.getUCriticality());

						if(maintAuditParameter.getUnitType()!=null)
							query2.setParameter("unitType", maintAuditParameter.getUnitType());

						if(maintAuditParameter.getClassification()!=null)
							query2.setParameter("classification", maintAuditParameter.getClassification());

						if(maintAuditParameter.getRevenueLeakage()!=null)
							query2.setParameter("revenueLeakage", maintAuditParameter.getRevenueLeakage());

						if(maintAuditParameter.getRating()!=null)
							query2.setParameter("rating", maintAuditParameter.getRating());
						
						//Added to check data
						if(maintAuditParameter.getFrequency()!=null)
							query2.setParameter("frequency", maintAuditParameter.getFrequency());
						
						if(maintAuditParameter.getDuration()!=null)
							query2.setParameter("duration", maintAuditParameter.getDuration());
						
						if(maintAuditParameter.getManDays()!=null)
							query2.setParameter("manDays", maintAuditParameter.getManDays());
						
						if(maintAuditParameter.getResponseTime()!=null)
							query2.setParameter("responseTime", maintAuditParameter.getResponseTime());
						
						if(maintAuditParameter.getId()==null)
						 query2.setParameter("id", (maintAuditParameter.getId()==null)?new Integer(-1):maintAuditParameter.getId());

						map.addAll(query2.list());
						
						if(map.size()>0) {
							//data already exist with the same values	
							flgAllowToCreateInWrk=false;
							OperationType="DataExist";
						}else {
							flgAllowToCreateInWrk=true;
							OperationType="UpdateWork";
						}
						
						
					}else {
						//no data exist in work the checking master 
						map=new ArrayList<>();
						
						String hql=" select m.uCriticality,m.unitType,m.classification,m.revenueLeakage,m.rating from MaintAuditParameter m where m.legalEntityCode =:legalEntityCode and m.auditTypeCode =:auditTypeCode ";

						if(maintAuditParameter.getUCriticality()==null)
							hql+=" and m.uCriticality is null ";
						else
							hql+=" and m.uCriticality =:uCriticality "; 
						if(maintAuditParameter.getUnitType()==null)
							hql+= "and m.unitType is null ";	
						else	
							hql+= "and m.unitType=:unitType ";
						if(maintAuditParameter.getClassification()==null)
							hql+= "and m.classification is null ";
						else
							hql+= "and m.classification=:classification ";
						if(maintAuditParameter.getRevenueLeakage()==null)
							hql+=" and m.revenueLeakage is null ";
						else	
							hql+=" and m.revenueLeakage =:revenueLeakage ";
						if(maintAuditParameter.getRating()==null)
							hql+= "and m.rating is null ";
						else	
							hql+= "and m.rating=:rating ";
						
						if(maintAuditParameter.getId()==null)
						    hql+= "and m.id!=:id ";
							
						Query query= session.createQuery(hql);
						query.setParameter("legalEntityCode", maintAuditParameter.getLegalEntityCode()).setParameter("auditTypeCode", maintAuditParameter.getAuditTypeCode());

						if(maintAuditParameter.getUCriticality()!=null)
							query.setParameter("uCriticality", maintAuditParameter.getUCriticality());

						if(maintAuditParameter.getUnitType()!=null)
							query.setParameter("unitType", maintAuditParameter.getUnitType());

						if(maintAuditParameter.getClassification()!=null)
							query.setParameter("classification", maintAuditParameter.getClassification());

						if(maintAuditParameter.getRevenueLeakage()!=null)
							query.setParameter("revenueLeakage", maintAuditParameter.getRevenueLeakage());

						if(maintAuditParameter.getRating()!=null)
							query.setParameter("rating", maintAuditParameter.getRating());
						
						if(maintAuditParameter.getId()==null)
						query.setParameter("id", (maintAuditParameter.getId()==null)?new Integer(-1):maintAuditParameter.getId());
						
						map=query.list();
						
						
						if(map.size()>0) {
							// if changes in data 
							map=new ArrayList<>();
							
							 hql=" select m.uCriticality,m.unitType,m.classification,m.revenueLeakage,m.rating from MaintAuditParameter m where m.legalEntityCode =:legalEntityCode and m.auditTypeCode =:auditTypeCode ";

							if(maintAuditParameter.getUCriticality()==null)
								hql+=" and m.uCriticality is null ";
							else
								hql+=" and m.uCriticality =:uCriticality "; 
							if(maintAuditParameter.getUnitType()==null)
								hql+= "and m.unitType is null ";	
							else	
								hql+= "and m.unitType=:unitType ";
							if(maintAuditParameter.getClassification()==null)
								hql+= "and m.classification is null ";
							else
								hql+= "and m.classification=:classification ";
							if(maintAuditParameter.getRevenueLeakage()==null)
								hql+=" and m.revenueLeakage is null ";
							else	
								hql+=" and m.revenueLeakage =:revenueLeakage ";
							if(maintAuditParameter.getRating()==null)
								hql+= "and m.rating is null ";
							else	
								hql+= "and m.rating=:rating ";
							
							//Added to check data
							if(maintAuditParameter.getFrequency()==null)
								hql+= "and m.frequency is null ";
							else
								hql+= "and m.frequency=:frequency ";
							if(maintAuditParameter.getDuration()==null)
								hql+= "and m.duration is null ";
							else
								hql+= "and m.duration=:duration ";
							if(maintAuditParameter.getManDays()==null)
								hql+= "and m.manDays is null ";
							else
								hql+= "and m.manDays=:manDays ";
							if(maintAuditParameter.getResponseTime()==null)
								hql+= "and m.responseTime is null ";
							else
								hql+= "and m.responseTime=:responseTime ";
							
							if(maintAuditParameter.getId()==null)
							 hql+= "and m.id!=:id ";
							
							query= session.createQuery(hql);
							query.setParameter("legalEntityCode", maintAuditParameter.getLegalEntityCode()).setParameter("auditTypeCode", maintAuditParameter.getAuditTypeCode());

							if(maintAuditParameter.getUCriticality()!=null)
								query.setParameter("uCriticality", maintAuditParameter.getUCriticality());

							if(maintAuditParameter.getUnitType()!=null)
								query.setParameter("unitType", maintAuditParameter.getUnitType());

							if(maintAuditParameter.getClassification()!=null)
								query.setParameter("classification", maintAuditParameter.getClassification());

							if(maintAuditParameter.getRevenueLeakage()!=null)
								query.setParameter("revenueLeakage", maintAuditParameter.getRevenueLeakage());

							if(maintAuditParameter.getRating()!=null)
								query.setParameter("rating", maintAuditParameter.getRating());
							
							//Added to check data
							if(maintAuditParameter.getFrequency()!=null)
								query.setParameter("frequency", maintAuditParameter.getFrequency());
							
							if(maintAuditParameter.getDuration()!=null)
								query.setParameter("duration", maintAuditParameter.getDuration());
							
							if(maintAuditParameter.getManDays()!=null)
								query.setParameter("manDays", maintAuditParameter.getManDays());
							
							if(maintAuditParameter.getResponseTime()!=null)
								query.setParameter("responseTime", maintAuditParameter.getResponseTime());

							if(maintAuditParameter.getId()==null)
							 query.setParameter("id", (maintAuditParameter.getId()==null)?new Integer(-1):maintAuditParameter.getId());
							
							map=query.list();
							
							if(map.size()>0) {
								//data already exist with the same values	
								flgAllowToCreateInWrk=false;
								OperationType="DataExist";
							}else {
								flgAllowToCreateInWrk=true;
								OperationType="CreateWork";
							}
							
						    
						}else {
							flgAllowToCreateInWrk=true;
							OperationType="CreateWork";
						}
						
					}
					isExist.put("isExist", flgAllowToCreateInWrk);
					isExist.put("OperationType", OperationType);
		return  isExist;
	}

	
	@Override
	public void deleteMaintAuditParameter(Integer legalEntityCode, String auditTypeCode, Integer id,
			String statusAuth) {
		
		Session session=getSession();
			
		if(BankAuditConstant.STATUS_AUTH.equals(statusAuth)){
			session.createQuery("delete from MaintAuditParameter where legalEntityCode =:legalEntityCode  and "
					+ " auditTypeCode =:auditTypeCode "
					+ " and id =:id ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("id", id)
			.executeUpdate();
			
		}else {

			session.createQuery("delete from MaintAuditParameterWrk  where legalEntityCode =:legalEntityCode  and "
					+ " auditTypeCode =:auditTypeCode "
					+ " and id =:id ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("id", id)
			.executeUpdate();
		}
		
	}

	@Override
	public boolean updateMaintAuditsParameters(List<MaintAuditParameter> maintAuditParameterList) {
		return false;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getMaintAuditParametersPreviousRatingLov(Integer legalEntityCode, String auditTypeCode) {
		
		Session session=getSession();
		
		
		
		/*String hql=" select concate(rsp.riskCategoryCode,'-',rsp.riskCategoryDesc) as PreviousRating  from RiskScorePercentage rsp where rsp.legalEntityCode =:legalEntityCode and rsp.auditTypeCode =:auditTypeCode "
				+ " order by rsp.minimunPer";*/
		
		String hql=" SELECT CONCAT(rsp.risk_category_code,'-',rsp.risk_category_desc) previous_rating FROM `risk_score_percentage` rsp " + 
				" WHERE 1=1 " + 
				" AND rsp.legal_entity_code =:legalEntityCode " + 
				" AND rsp.audit_type_code =:auditTypeCode " + 
				" ORDER BY minimun_per ASC " ;
				
		return session.createSQLQuery(hql)
				.addScalar("previous_rating", StandardBasicTypes.STRING)
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode).list();
	}

	@Override
	public boolean updateMaintAuditsParametersDerived(Integer legalEntityCode, String auditTypeCode) {
		
		Session session=getSession();
		List<String> aParams = new ArrayList<String>();
		List<String> criticality = new ArrayList<String>();
		List<String> unitType = new ArrayList<String>();List<String> classification = new ArrayList<String>();
		List<String> rating = new ArrayList<String>();List<String> revenueLeak = new ArrayList<String>();
		List<String> auditParam = new ArrayList<String>();
		boolean status=true;
		try {
			System.out.println("Inside daoImpl --> updateMaintAuditsParametersDerived() .. LE.. "+legalEntityCode +" AuditType.. "+auditTypeCode);
			StringBuilder queryString=new StringBuilder( "select  concat('C-',IFNULL(uCriticality,''),'||U-',IFNULL(unitType,''),'||G-',IFNULL(classification,''),'||R-',IFNULL(rating,''),'||L-',IFNULL(revenueLeakage,''),'||F-',IFNULL(frequency,''),'||D-',IFNULL(duration,''),'||M-',IFNULL(manDays,''),'||T-',IFNULL(responseTime,'')) as paramVal "
				+ " from MaintAuditParameter where  auditTypeCode =:auditTypeCode and legalEntityCode =:legalEntityCode");
			aParams=session.createQuery(queryString.toString()).setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode).list();
			 logger.info("aParams.... "+aParams);
		
			queryString=new StringBuilder( "select criticalityCode from MaintCriticality where criticalityOfType='Entity' and entityStatus='A' and legalEntityCode =:legalEntityCode");
			criticality=session.createQuery(queryString.toString()).setParameter("legalEntityCode", legalEntityCode).list();
			
			queryString=new StringBuilder( "select value from GeneralParameter where key1 = 'UNIT_TYPE' and legalEntityCode =:legalEntityCode");
			unitType=session.createQuery(queryString.toString()).setParameter("legalEntityCode", legalEntityCode).list();
			
			queryString=new StringBuilder( "select value from GeneralParameter where key1 = 'CLASSIFICATION' and legalEntityCode =:legalEntityCode");
			classification=session.createQuery(queryString.toString()).setParameter("legalEntityCode", legalEntityCode).list();	
			
			queryString=new StringBuilder( "select value from GeneralParameter where key1 = 'REVENUE_LEAK' and legalEntityCode =:legalEntityCode");
			revenueLeak=session.createQuery(queryString.toString()).setParameter("legalEntityCode", legalEntityCode).list();	
			
			queryString=new StringBuilder( "select riskCategoryCode from RiskScorePercentage where entityStatus='A' and legalEntityCode =:legalEntityCode and auditTypeCode =:auditTypeCode");
			rating=session.createQuery(queryString.toString()).setParameter("legalEntityCode", legalEntityCode).setParameter("auditTypeCode", auditTypeCode).list();	
			
			logger.info("criticality.... "+criticality + " uniType...."+unitType+" classification...."+classification+" revenueLeak...."+revenueLeak+" rating...."+rating );
			int cnt=0; int a=0; String paramF=null; int t=0;
			for(String crt:criticality){  // looping for each criticality from => MaintCriticality
				for(String unit:unitType){     // looping for each unittype from => GeneralParameter where key1 = 'UNIT_TYPE'
					for(String cls:classification){  // looping for each classification from => GeneralParameter where key1 = 'CLASSIFICATION'
						for(String leak:revenueLeak){    // looping for each revenue_leak from => GeneralParameter where key1 = 'REVENUE_LEAK'
							for(String rat:rating){			// looping for each rating from => RiskScorePercentage 
								int b=0; int p=0;paramF="";
								String adtPm = "C-"+crt+"||"+"U-"+unit+"||"+"G-"+cls+"||"+"L-"+leak+"||"+"R-"+rat;
								for(String aparam:aParams) //iterating the combinations in maint_audit_parameter
								{	a=0;t=0;
									try {
									//System.out.println(" aparam :"+aparam);
										if(aparam.indexOf("C-"+crt) != -1) a++; else if(aparam.indexOf("C-||") != -1) t++;
										if(aparam.indexOf("U-"+unit) != -1) a++; else if(aparam.indexOf("U-||") != -1) t++;
										if(aparam.indexOf("G-"+cls) != -1) a++; else if(aparam.indexOf("G-||") != -1) t++;
										if(aparam.indexOf("L-"+leak) != -1) a++; else if(aparam.indexOf("L-||") != -1) t++;
										if(aparam.indexOf("R-"+rat) != -1) a++; else if(aparam.indexOf("R-||") != -1) t++;
											
										
										// a value will provide the number of parameters matched
										// t value will provide the number of parameters are blank in maint_audit_parameter
										// b value indicates the first record comparison maint_audit_parameter to gen_parameters
										// p indicates the previous best match
										if(b == 0 && a+t == 5 ) {p=a; paramF =aparam;}  // for first record in maint_audit_parameter 
										//else if(a == p && a!=0 ) { p=a; paramF =aparam; }
										else if(a >= p && a+t ==5 && a!=0) {p=a; paramF =aparam; } // to check the 
										if(p==0 && aparam.contains("C-||U-||G-||R-||L-||")) {paramF =aparam;}
										b++;
									}catch (Exception e) {
									    //System.out.println(e.getMessage());
									}
								}
								adtPm += "||"+paramF;
								auditParam.add(adtPm);
								cnt++;
							}
						}
					}
				}
			}
			MaintAuditParameterDerived maintAuditParamDr = null;
			AbstractDao mAdtPmDaoImpl = new MaintAuditParameterDaoImpl();
			try {
				//logger.info("auditParamsDerived Insertion starts here  :: ");
				session.createQuery("delete from MaintAuditParameterDerived where legalEntityCode =:legalEntityCode and auditTypeCode =:auditTypeCode ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("auditTypeCode", auditTypeCode).executeUpdate();
				
				//logger.info("auditParams .."+auditParam.size() +" "+auditParam);
				for(String auditParams: auditParam) {
					maintAuditParamDr = new MaintAuditParameterDerived();
					//auditParams = auditParams.replaceAll("(.)-", ""); to replace "- and any character before -" with blank ""
					//logger.info("auditParams.. "+auditParams);
					String[] arrParams = auditParams.replaceAll("(.)-", "").split("\\|\\|");
					//logger.info("arrParams size.. "+arrParams.length);
					
					if(arrParams.length == 14) {
						maintAuditParamDr.setLegalEntityCode(legalEntityCode);
						maintAuditParamDr.setAuditTypeCode(auditTypeCode);
						maintAuditParamDr.setUCriticality(arrParams[0]);
						maintAuditParamDr.setUnitType(arrParams[1]);
						maintAuditParamDr.setClassification(arrParams[2]);
						maintAuditParamDr.setRevenueLeakage(arrParams[3]);
						maintAuditParamDr.setRating(arrParams[4]);
						//if(arrParams.length > 5) { // If there is at least one parameter is matching which is defined in Parameter setup table.
						maintAuditParamDr.setFrequency(arrParams[10]);
						maintAuditParamDr.setDuration(arrParams[11]);
						maintAuditParamDr.setManDays(arrParams[12]);
						maintAuditParamDr.setResponseTime(arrParams[13]);
						//}
							
						//logger.info("mAdtPmDaoImpl.. "+mAdtPmDaoImpl+" maintAuditParamDr.."+maintAuditParamDr);
						session.saveOrUpdate(maintAuditParamDr);
						session.flush();
						session.clear();
					}
				}logger.info("updated successfully .... ");
			}catch(Exception e) {
				status=false;
				e.printStackTrace(System.out);
			}
			//logger.info("auditParam .... "+auditParam.size() +" --> "+auditParam );
			
		}catch(Exception e) {
			status=false;
			e.printStackTrace();
		}return status;
	}
	
	
	// Method included to get the list of all the Derived Audit Parameter lists
	@SuppressWarnings("deprecation")
	@Override
	public DataTableResponse getAuditParameterDerived(Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintAuditParameterDerived> maintAuditParametersDerived = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
				
		try {
		
		StringBuilder queryString=new StringBuilder(                "   SELECT      " + 
				"         map.legal_entity_code AS legal_entity_code ," + 
				"         (SELECT Concat(mc.criticality_code ,'-',mc.criticality_desc)            " + 
				"         	FROM" + 
				"             maint_criticality mc              " + 
				"         	WHERE" + 
				"             mc.criticality_of_type = 'ENTITY'                     " + 
				"             AND mc.criticality_code = map.u_criticality                     " + 
				"             AND mc.legal_entity_code = map.legal_entity_code ) AS u_criticality," + 
				"         CONCAT(map.audit_type_code,'-',mat.audit_type_desc) AS audit_type_code ," + 
				"         (SELECT Concat ( gp.value, '-' , gp.description )              " + 
				"         	FROM general_parameter gp              " + 
				"         	WHERE gp.key_1 = 'UNIT_TYPE'                     " + 
				"             AND gp.value = map.unit_type                     " + 
				"             AND gp.legal_entity_code = map.legal_entity_code ) AS unit_type ,             " + 
				
				"         (SELECT Concat ( gp.value, '-' , gp.description )              " + 
				"         	FROM general_parameter gp              " + 
				"         	WHERE gp.key_1 = 'CLASSIFICATION' AND gp.key_2 = 'GEOGRAPHY'                     " + 
				"             AND gp.legal_entity_code = map.legal_entity_code AND gp.value = map.classification ) AS classification ,             " +				
				"		  (SELECT CONCAT(rsp.risk_category_code,'-',rsp.risk_category_desc)  " +
				"			FROM `risk_score_percentage` rsp  " +
				"			WHERE rsp.risk_category_code = map.rating  " +
				"				AND rsp.legal_entity_code =  map.legal_entity_code " +
 				"				AND rsp.audit_type_code = map.audit_type_code) AS rating , " +				
				"         (SELECT Concat (gp.value, '-' , gp.description)              " + 
				"         	FROM general_parameter gp              " + 
				"         	WHERE gp.key_1 = 'REVENUE_LEAK' AND gp.key_2 = 'REVENUE_LEAK' " + 
				"         		AND gp.legal_entity_code = map.legal_entity_code AND gp.value = map.revenue_leakage) AS revenue_leakage ,             " + 				
				"         map.response_time AS response_time ," + 				
				"         map.frequency     AS frequency ," + 
				"         map.duration      AS duration ," + 
				"         map.man_days      AS man_days " +
				
				"     FROM " + 
				"         maint_audit_parameter_derived map , maint_audit_type_desc mat " +
				" 	  WHERE mat.legal_entity_code=map.legal_entity_code and map.audit_type_code = mat.audit_type_code  " + 
				" 			AND mat.legal_entity_code=:legalEntityCode AND map.legal_entity_code=:legalEntityCode ");

		if(!BankAuditUtil.isEmptyString(auditTypeCode) && !"null".equals(auditTypeCode)){
			queryString=queryString.append(" and map.audit_type_code = mat.audit_type_code ");					
			queryString=queryString.append(" and mat.audit_type_code=:auditTypeCode ");
		}
		
		if(!BankAuditUtil.isEmptyString(search) && !"null".equals(search)){
			queryString=queryString.append(" and ( "
					+ " mat.audit_type_code like :search "
					+ " or  mat.audit_type_desc like :search  "
					+ " or  map.u_criticality IN ( "
					+ " 		select mc.criticality_code "
					+ " 		from maint_criticality mc  "
					+ " 		where	mc.criticality_of_type='ENTITY' and mc.criticality_desc LIKE :search "
					+ " 				and mc.legal_entity_code=map.legal_entity_code ANd map.legal_entity_code=:legalEntityCode "
					+ " ) "
	                + " or  map.unit_type In ( "
	                + " 		select gp.value "
				    + " 		from general_parameter gp "
				    + " 		where gp.key_1='UNIT_TYPE' and gp.description LIKE :search "
				    + " 			and gp.legal_entity_code=map.legal_entity_code  "
	                + " ) "
					//  classification
					+ " or  map.classification In ( "
					+ " 		select gp.value "
					+ " 		from general_parameter gp  "
					+ " 		where gp.key_1='CLASSIFICATION' and gp.key_1='GEOGRAPHY' and gp.description LIKE :search "
					+ " 			and gp.legal_entity_code=map.legal_entity_code  "
					+ " ) "
                    //  rating
					+ " or  map.rating In ( SELECT rsp.risk_category_code  " 
					+ "			FROM `risk_score_percentage` rsp  " 
					+ "			WHERE rsp.legal_entity_code =  map.legal_entity_code " 
					+ " 			AND rsp.audit_type_code = map.audit_type_code "
					+ " 			AND rsp.risk_category_desc LIKE :search ) " 	
					// revenue_leakage
					+ " or  map.revenue_leakage In ( "
					+ " 		select gp.value "
					+ " 		from general_parameter gp "
					+ " 		where gp.key_1='REVENUE_LEAK' and gp.key_1='REVENUE_LEAK' and gp.description LIKE :search "
					+ " 			and gp.legal_entity_code=map.legal_entity_code  "
					+ " ) "
					// response_time
					+ " or map.response_time LIKE :search"
					+ ") ");
		}
		
		String[] columns={
				" mat.audit_type_code ",
				" map.u_criticality ",
			    " map.unit_type ",
			    " map.classification ",
			    " map.rating ",
			    " map.revenue_leakage ",
			    " map.frequency*1 ",
			    " map.duration*1 ",
			    " map.man_days*1 ",
			    " map.response_time*1 "
		};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by audit_type_code ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		//Query query=session.createQuery(queryString.toString()).setResultTransformer(Transformers.aliasToBean(MaintAuditParameter.class))
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						MaintAuditParameterDerived maintAuditParameters=null;
						MaintAuditTypeDesc maintAuditTypeDesc=null;
						int i=-1;
						if(tuple!=null){
							maintAuditParameters=new MaintAuditParameterDerived();
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setLegalEntityCode(Integer.parseInt(tuple[i].toString()));
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setUCriticality(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setAuditTypeCode(tuple[i].toString());
								maintAuditParameters.setAuditTypeName(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setUnitType(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setClassification(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setRating(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setRevenueLeakage(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setResponseTime(tuple[i].toString());
							}
							i++;
							/*if(tuple[i]!=null){
								maintAuditParameters.setFrequencyType(tuple[i].toString());
							}
							i++;*/
							if(tuple[i]!=null){
								maintAuditParameters.setFrequency(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setDuration(tuple[i].toString());
							}
							i++;
							if(tuple[i]!=null){
								maintAuditParameters.setManDays(tuple[i].toString());
							}
							
						}
						return maintAuditParameters;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legalEntityCode", legalEntityCode);
		
		if(!BankAuditUtil.isEmptyString(auditTypeCode)){
			query.setParameter("auditTypeCode", auditTypeCode);
		}
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			maintAuditParametersDerived= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintAuditParametersDerived.add((MaintAuditParameterDerived) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintAuditParametersDerived);
			resultScroll.last();
			if(size!=0){
				dataTableResponse.setRecordsFiltered((resultScroll.getRowNumber()+1l/size)+1);
				dataTableResponse.setRecordsTotal((resultScroll.getRowNumber()+1l/size)+1);
			}else{
				dataTableResponse.setError("page size zero");
				dataTableResponse.setData(Collections.EMPTY_LIST);
				dataTableResponse.setRecordsTotal(0l);
				dataTableResponse.setRecordsFiltered(0l);
			}
			
		}else{
			dataTableResponse.setError(null);
			dataTableResponse.setData(Collections.EMPTY_LIST);
			dataTableResponse.setRecordsTotal(0l);
			dataTableResponse.setRecordsFiltered(0l);
		}
		
		
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return dataTableResponse;

	
		
	}

	
}
