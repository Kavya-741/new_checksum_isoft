package com.bankaudit.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintCriticality;


@Repository("maintCriticalityDao")
public class MaintCriticalityDaoImpl extends AbstractDao implements MaintCriticalityDao {

	/** The Constant logger is used to specify the . */
	static final Logger logger = Logger.getLogger(MaintCriticalityDaoImpl.class);
	
	@Override
	public DataTableResponse getAllMaintCriticality(String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size,Integer legalEntityCode) {
		
		
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintCriticality> maintCriticalities = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		StringBuilder queryString=new StringBuilder(
				
				         " SELECT "
						 + "  "
						 + " legal_entity_code, "
						 + " criticality_code, "
						 + " criticality_of_type,  "
						 + " criticality_desc, "
						 + " score, "
						 + " auth_rej_remarks, "

				         + " (select concat(gp.value , '-' , gp.description) from general_parameter gp where"
				         + " gp.key_1='ENTITY_STATUS' and gp.key_2='ENTITY_STATUS' and gp.value=c.entity_status "
				         + " and gp.legal_entity_code=c.legal_entity_code ) as entity_status ,  "
						
						 + " (select concat(gp.value , '-' , gp.description)"
						 + " from general_parameter gp where "
						 + " gp.key_1='STATUS' and gp.value=c.status "
						 + " and gp.legal_entity_code=c.legal_entity_code ) as status, "
						 
						 + "( select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						 + " from user u where "
						 + " u.user_id=c.maker "
						 + " and u.legal_entity_code=c.legal_entity_code ) as maker ,  "
						 
						 + " maker_timestamp, "
						 + " checker, "
						 + " checker_timestamp "
						 + "       "
						 + "     FROM "
						 + "         ( SELECT "
						 + "             *  "
						 + "         FROM "
						 + "             maint_criticality "
						 + "         WHERE "
						 + "             legal_entity_code =:legal_entity_code "
						 + "         UNION ALL "
						 + "         SELECT "
						 + "             *  "
						 + "         FROM "
						 + "             maint_criticality_wrk  "
						 + "         WHERE "
						 + "             legal_entity_code =:legal_entity_code "
						 + "              "
						 + "     ) AS c  "
						 + " WHERE "
						 + "    legal_entity_code =:legal_entity_code ");
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( c.criticality_code like :search "
					+ " or  c.criticality_of_type like :search  "
					+ " or  c.criticality_desc like :search  "
					+ " or  c.maker like :search  "
					+ " or  c.status IN (SELECT value FROM general_parameter WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  c.score  like :search  "
					+ " or  c.criticality_of_type IN (SELECT value FROM general_parameter  WHERE legal_entity_code =:legal_entity_code and key_1='CRITICALITY_OF_TYPE' and  description LIKE :search ) "  
                    + " or  c.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
                    + " or  c.maker IN ( "
					+ " select u.user_id "
						 + " from user u where "
						 + " (concat(u.first_name , ' ' , u.last_name) like :search  ) "
						 + " and u.legal_entity_code =:legal_entity_code ) "
					+ " ) "
                    
					);
		}
		
	
		String[] columns={
				"c.criticality_code",
				"c.criticality_of_type",
				"c.criticality_desc",
				"c.score",
				"c.maker",
				"c.entity_status",
				"c.status"};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		@SuppressWarnings("deprecation")
        Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						
						MaintCriticality maintCriticality=null;
						
						if(tuple!=null){
							
							maintCriticality=new MaintCriticality();
							
							if(tuple[0]!=null){
								maintCriticality.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								maintCriticality.setCriticalityCode(tuple[1].toString());
							}
							if(tuple[2]!=null){
								maintCriticality.setCriticalityOfType(tuple[2].toString());
							}
							if(tuple[3]!=null){
								maintCriticality.setCriticalityDesc(tuple[3].toString());
							}
							if(tuple[4]!=null){
								maintCriticality.setScore(tuple[4].toString());
							}
							if(tuple[5]!=null){
								maintCriticality.setAuthRejRemarks(tuple[5].toString());
							}
							if(tuple[6]!=null){
								maintCriticality.setEntityStatus(tuple[6].toString());
							}
							if(tuple[7]!=null){
								maintCriticality.setStatus(tuple[7].toString());
							}
							if(tuple[8]!=null){
								maintCriticality.setMaker(tuple[8].toString());
							}
							if(tuple[9]!=null){
								maintCriticality.setMakerTimestamp((Date)tuple[9]);
							}
							if(tuple[10]!=null){
								maintCriticality.setChecker(tuple[10].toString());
							}
							if(tuple[11]!=null){
								maintCriticality.setCheckerTimestamp((Date)tuple[11]);
							}
							
						}
						
						return maintCriticality;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legal_entity_code", legalEntityCode);
		
	
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			maintCriticalities= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintCriticalities.add((MaintCriticality) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintCriticalities);
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
		return dataTableResponse;

	
		
	}

	@SuppressWarnings("deprecation")
    @Override
	public List<MaintCriticality> getByCriticalityScore(Integer legalEntityCode, String criticalityOfType,
			String score ,String criticalityCode) {

		Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintCriticality> maintCriticalities = null;

		StringBuilder queryString=new StringBuilder("select "
				+ " c as maintCriticality  "
				+ " from MaintCriticality c "
				+ " where c.legalEntityCode =:legalEntityCode and c.criticalityOfType =:criticalityOfType ");
		
		if(!BankAuditUtil.isEmptyString(score)){
			queryString=queryString.append(" and c.score =:score ");
		}
		if(!BankAuditUtil.isEmptyString(criticalityCode)){
			queryString=queryString.append(" and c.criticalityCode =:criticalityCode ");
		}
		
		@SuppressWarnings("deprecation")
        Query query=session.createQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						MaintCriticality maintCriticality=null;
						if(tuple[0]!=null){
							maintCriticality=(MaintCriticality)tuple[0];
							
						}
						return maintCriticality;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legalEntityCode", legalEntityCode).setParameter("criticalityOfType", criticalityOfType);
		if(!BankAuditUtil.isEmptyString(score)){
			query.setParameter("score", score);
		}
		if(!BankAuditUtil.isEmptyString(criticalityCode)){
			query.setParameter("criticalityCode", criticalityCode);
		}
		maintCriticalities = query.list();
		
		return maintCriticalities;
	}

	@Override
	public void deleteMaintCriticality(Integer legalEntityCode, String criticalityCode, String criticalityOfType,
			String statusAuth) {

		
		Session session=getSession();
			
		if(!BankAuditConstant.STATUS_AUTH.equals(statusAuth)){
			session.createQuery("delete from MaintCriticalityWrk where legalEntityCode =:legalEntityCode  and "
					+ " criticalityCode =:criticalityCode "
					+ " and criticalityOfType =:criticalityOfType  ")
			.setParameter("criticalityOfType", criticalityOfType)
			.setParameter("criticalityCode", criticalityCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			
		}else {

			session.createQuery("delete from MaintCriticality where legalEntityCode =:legalEntityCode  and "
					+ " criticalityCode =:criticalityCode "
					+ " and criticalityOfType =:criticalityOfType  ")
			.setParameter("criticalityOfType", criticalityOfType)
			.setParameter("criticalityCode", criticalityCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}
		
	}

	@Override
	public Boolean isMaintCriticality(Integer legalEntityCode, String criticalityCode, String criticalityOfType) {
		Session session=getSession();
		Long count=(Long)session.createQuery(
				" select count(*) "
				+ " from MaintCriticality   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode  "
				+ " and criticalityOfType =:criticalityOfType "
				+ " and criticalityCode =:criticalityCode  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("criticalityOfType", criticalityOfType)
		.setParameter("criticalityCode", criticalityCode).uniqueResult();
		
		Long count1=(Long)session.createQuery(
		
				
				" select count(*) "
				+ " from MaintCriticalityWrk   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and criticalityOfType =:criticalityOfType "
				+ " and criticalityCode =:criticalityCode  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("criticalityOfType", criticalityOfType)
		.setParameter("criticalityCode", criticalityCode).uniqueResult();
		Boolean flag=false;
		if((count1!=null && count1 >0)||(count!=null && count >0)){
			flag= true;
		}else {
			flag= false;
		}
		return flag;
	}
	
	@Override
	public Boolean validateScore(MaintCriticality maintCriticality) {
		Session session=getSession();
		logger.info("LE::"+maintCriticality.getLegalEntityCode()+" CriticalityOfType::"+maintCriticality.getCriticalityOfType()+" Score::"+maintCriticality.getScore()+" CriticalityCode::"+maintCriticality.getCriticalityCode());
		Long scoreMstCnt=(Long)session.createQuery(
				" select count(*) "
				+ " from MaintCriticality   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode  "
				+ " and criticalityOfType =:criticalityOfType "
				+ " and criticalityCode <>:criticalityCode "  // Condition added to ignore the score check for the same Criticality Code
				+ " and score =:score  ")
		.setParameter("legalEntityCode", maintCriticality.getLegalEntityCode())
		.setParameter("criticalityOfType", maintCriticality.getCriticalityOfType())
		.setParameter("criticalityCode", maintCriticality.getCriticalityCode())
		.setParameter("score", maintCriticality.getScore()).uniqueResult();
		
		Long ScoreWrkCnt=(Long)session.createQuery(
				" select count(*) "
				+ " from MaintCriticalityWrk   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and criticalityOfType =:criticalityOfType "
				+ " and criticalityCode <>:criticalityCode " // Condition added to ignore the score check for the same Criticality Code
				+ " and score =:score  ")
				.setParameter("legalEntityCode", maintCriticality.getLegalEntityCode())
				.setParameter("criticalityOfType", maintCriticality.getCriticalityOfType())
				.setParameter("criticalityCode", maintCriticality.getCriticalityCode())
				.setParameter("score", maintCriticality.getScore()).uniqueResult();
		
		logger.info("scoreMstCnt::"+scoreMstCnt +" ScoreWrkCnt::" +ScoreWrkCnt);
		Boolean flag=false;
		if((ScoreWrkCnt!=null && ScoreWrkCnt >0)||(scoreMstCnt!=null && scoreMstCnt >0)){
			flag= true;
		}
		return flag;
	}

}
