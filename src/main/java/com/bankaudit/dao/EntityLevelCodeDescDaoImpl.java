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
import com.bankaudit.model.EntityLevelCodeDesc;
import com.bankaudit.service.EntityLevelCodeDescServiceImpl;
import com.bankaudit.helper.BankAuditUtil;

@Repository("entityLevelCodeDescDao")
public class EntityLevelCodeDescDaoImpl extends AbstractDao implements EntityLevelCodeDescDao {
	
	static final Logger logger = Logger.getLogger(EntityLevelCodeDescDaoImpl.class);

	@Override
	public Integer getEntityLevelCodeDescCount(Integer legalEntityCode) {
		
		Session session=getSession();
		return (int)(long)session.createQuery("select count(*) from EntityLevelCodeDesc where legalEntityCode =:legalEntityCode")
		.setParameter("legalEntityCode", legalEntityCode).uniqueResult();
	}

	
	@Override
	public DataTableResponse getEntityLevelCodeDesc(Integer legalEntityCode, String search,
			Integer orderColumn, String orderDirection, Integer page, Integer size) {
		
		
		
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<EntityLevelCodeDesc> entityLvlCodDesc = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		StringBuilder queryString=new StringBuilder(
				         "select  "
						+ "legal_entity_code, "
						+ "level_code, "
						+ "level_desc, "
						+ "auth_rej_remarks, "
						
						+ " (select concat(gp.value , '-' , gp.description) from general_parameter gp where "
						+ " gp.key_1='ENTITY_STATUS' and gp.key_2='ENTITY_STATUS' and gp.value=el.entity_status "
						+ " and gp.legal_entity_code=el.legal_entity_code ) as entity_status , " 
						 
						+ " (select concat(gp.value , '-' , gp.description)"
				    	+ " from general_parameter gp where "
				    	+ " gp.key_1='STATUS' and gp.value=el.status "
				    	+ " and gp.legal_entity_code=el.legal_entity_code ) as status , "
						
						 + "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						 + " from user u where "
						 + " u.user_id=el.maker "
						 + " and u.legal_entity_code=el.legal_entity_code ) as maker ,  "	 
						 
						+ "maker_timestamp, "
						+ "checker, "
						+ "checker_timestamp "
						+ "from ( "
						+ " "
						+ "select * from entity_level_code_desc  "
						+ "where legal_entity_code =:legal_entity_code "
						+ " "
						+ "union ALL "
						+ " "
						+ "select * from entity_level_code_desc_wrk  "
						+ "where legal_entity_code =:legal_entity_code ) "
						+ " "
						+ "as el where legal_entity_code =:legal_entity_code ");
	
		
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( el.level_code like :search "
					+ " or  el.level_desc like :search "
					+ " or  el.maker like :search "
                    + " or  el.maker in ( "
					
					+ " select u.user_id "
					+ " from user u where "
					+ " (concat(u.first_name , ' ' , u.last_name) like :search  ) "
					+ " and u.legal_entity_code=el.legal_entity_code ) "
					
					+ " or  el.status IN (SELECT value FROM general_parameter  WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  el.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
					
					+ " ) ");
		}

		String[] columns={
				"el.level_code",
				"el.level_desc",
				"el.maker",
				/*"el.entity_status",*/
				"el.status"
				};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						EntityLevelCodeDesc entityLevelCodeDesc=null;
						
						if(tuple!=null){
							entityLevelCodeDesc=new EntityLevelCodeDesc();
							if(tuple[0]!=null){
								entityLevelCodeDesc.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								entityLevelCodeDesc.setLevelCode(tuple[1].toString());
							}
							if(tuple[2]!=null){
								entityLevelCodeDesc.setLevelDesc(tuple[2].toString());
							}
							if(tuple[3]!=null){
								entityLevelCodeDesc.setAuthRejRemarks(tuple[3].toString());
							}
							if(tuple[4]!=null){
								entityLevelCodeDesc.setEntityStatus(tuple[4].toString());
							}
							if(tuple[5]!=null){
								entityLevelCodeDesc.setStatus(tuple[5].toString());
							}
							if(tuple[6]!=null){
								entityLevelCodeDesc.setMaker(tuple[6].toString());
							}
							if(tuple[7]!=null){
								entityLevelCodeDesc.setMakerTimestamp((Date)tuple[7]);
							}
							if(tuple[8]!=null){
								entityLevelCodeDesc.setChecker(tuple[8].toString());
							}
							if(tuple[9]!=null){
								entityLevelCodeDesc.setCheckerTimestamp((Date)tuple[9]);
							}
						}
						return entityLevelCodeDesc;
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
			
			entityLvlCodDesc= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				entityLvlCodDesc.add((EntityLevelCodeDesc) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(entityLvlCodDesc);
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

	@Override
	public void deleteEntityLevelCodeDesc(Integer legalEntityCode, String levelCode, String statusAuth) {
		Session session=getSession();
		if(!BankAuditConstant.STATUS_AUTH.equals(statusAuth)){
			session.createQuery("delete from EntityLevelCodeDescWrk  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  levelCode =:levelCode  ").setParameter("levelCode", levelCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}else {
			session.createQuery("delete from EntityLevelCodeDesc  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  levelCode =:levelCode ").setParameter("levelCode", levelCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate() ;
		}
	}
	
	@Override
	public List<EntityLevelCodeDesc> getEntityLevelCodesDescByUserID(Integer legalEntityCode, String userId){
		logger.info("Inside getEntityLevelCodesDescByUserID .. "+legalEntityCode +" userId.."+userId);
		Session session=getSession();
		return session.createQuery("SELECT l FROM User u "
					+ " INNER JOIN MaintEntity e ON u.unitCode=e.unitCode AND u.legalEntityCode=e.legalEntityCode "
					+ " INNER JOIN EntityLevelCodeDesc l ON u.legalEntityCode=l.legalEntityCode "
					+ "  WHERE u.userId=:userId AND u.legalEntityCode=:legalEntityCode AND "
					+ "  l.levelCode >= e.levelCode ORDER BY l.levelCode ")
					/*+ " 	l.levelCode > 	(CASE WHEN e.levelCode=1 THEN 0 "
					+ "			 				ELSE e.levelCode END) ORDER BY l.levelCode")*/
				.setParameter("legalEntityCode", legalEntityCode).setParameter("userId", userId).list();
	}

}
