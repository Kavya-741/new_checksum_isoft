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
import org.hibernate.transform.Transformers;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditSubgroup;
import com.bankaudit.util.BankAuditUtil;

@Repository("maintAuditSubgroupDao")
public class MaintAuditSubgroupDaoImpl extends AbstractDao implements MaintAuditSubgroupDao {
	
	static final Logger logger = Logger.getLogger(MaintAuditSubgroupDaoImpl.class);

	@SuppressWarnings("deprecation")
	@Override
	public DataTableResponse getMaintAuditSubgroupes(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		
		
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintAuditSubgroup> maintAuditSubgroups = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
	
		StringBuilder queryString=new StringBuilder(
				
				         "  SELECT  "  
						+ "  legal_entity_code,  "  
						
						+ "(select concat(atd.audit_type_code , '-' , atd.audit_type_desc)"
						+ " from maint_audit_type_desc atd where "
						+ " atd.audit_type_code=asg.audit_type_code "
						+ " and atd.legal_entity_code=asg.legal_entity_code ) as audit_type_code , "
						
						+ "(select concat(atd.audit_group_code , '-' , atd.audit_group_name)"
						+ " from maint_audit_group atd where "
						+ " atd.audit_type_code=asg.audit_type_code "
						+ " and atd.audit_group_code=asg.audit_group_code "
						+ " and atd.legal_entity_code=asg.legal_entity_code ) as audit_group_code , "
						
						+ "  audit_sub_group_code,  "  
						+ "  audit_sub_group_name,   "  
					
						+ " (select concat(gp.value , '-' , gp.description) from general_parameter gp where"
						+ " gp.key_1='ENTITY_STATUS' and gp.key_2='ENTITY_STATUS' and gp.value=asg.entity_status "
						+ " and gp.legal_entity_code=asg.legal_entity_code ) as entity_status ,  "
						
						+ "  auth_rej_remarks,  "  
						
						+ " (select concat(gp.value , '-' , gp.description)"
						+ " from general_parameter gp where "
						+ " gp.key_1='STATUS' and gp.value=asg.status "
						+ " and gp.legal_entity_code=asg.legal_entity_code ) as status, "
						
						+ "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						+ " from user u where "
						+ " u.user_id=asg.maker "
						+ " and u.legal_entity_code=asg.legal_entity_code ) as maker ,  "
						
						+ "  maker_timestamp,  "  
						+ "  checker,  "  
						+ "  checker_timestamp  "   
						+ "  FROM (  "  
						+ "  SELECT * FROM maint_audit_subgroup   "  
						+ "  WHERE legal_entity_code =:legal_entity_code  "  
						+ "  UNION ALL "  
						+ "  SELECT * FROM maint_audit_subgroup_wrk   "  
						+ "  WHERE legal_entity_code =:legal_entity_code )  "  
						+ "  AS asg WHERE legal_entity_code =:legal_entity_code  "  
			
				);
		
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( "
					
					+ "   asg.audit_sub_group_code like :search  "
					+ " or  asg.audit_sub_group_name like :search  "
					+ " or  asg.audit_type_code like :search  "
					+ " or  asg.audit_group_code like :search  "
					+ " or  asg.maker like :search  "
					
					+ " or  asg.status IN (SELECT value FROM general_parameter WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  asg.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
					+ " or  asg.maker IN ( "
					+ " select u.user_id "
							 + " from user u where "
							 + " (concat(u.first_name , ' ' , u.last_name) like :search   ) "
							 + " and u.legal_entity_code =:legal_entity_code ) "
					+ " or  asg.audit_type_code in (select matd.audit_type_code from maint_audit_type_desc matd where matd.audit_type_desc like :search )"

					
					+ " or  asg.audit_group_code in ( select asg.audit_group_code from maint_audit_group asg where asg.audit_group_name like :search ) "
					
					+ ") ");
		}

		String[] columns={
				"asg.audit_type_code",
				"asg.audit_group_code",
				"asg.audit_sub_group_code",
				"asg.audit_sub_group_name",
				"asg.maker",
				"asg.entity_status",
				"asg.status"};
		
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
						MaintAuditSubgroup maintAuditSubgroup=null;
						if(tuple!=null){
							maintAuditSubgroup=new MaintAuditSubgroup();
							
							if(tuple[0]!=null){
								maintAuditSubgroup.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								maintAuditSubgroup.setAuditTypeCode(tuple[1].toString());
							}
							if(tuple[2]!=null){
								maintAuditSubgroup.setAuditGroupCode(tuple[2].toString());
							}
							if(tuple[3]!=null){
								maintAuditSubgroup.setAuditSubGroupCode(tuple[3].toString());
							}
							if(tuple[4]!=null){
								maintAuditSubgroup.setAuditSubGroupName(tuple[4].toString());
							}
							if(tuple[5]!=null){
								maintAuditSubgroup.setEntityStatus(tuple[5].toString());
							}
							if(tuple[6]!=null){
								maintAuditSubgroup.setAuthRejRemarks(tuple[6].toString());
							}
							if(tuple[7]!=null){
								maintAuditSubgroup.setStatus(tuple[7].toString());
							}
							if(tuple[8]!=null){
								maintAuditSubgroup.setMaker(tuple[8].toString());
							}
							if(tuple[9]!=null){
								maintAuditSubgroup.setMakerTimestamp((Date)tuple[9]);
							}
							if(tuple[10]!=null){
								maintAuditSubgroup.setChecker(tuple[10].toString());
							}
							if(tuple[11]!=null){
								maintAuditSubgroup.setCheckerTimestamp((Date)tuple[11]);
							}
							
						}
						return maintAuditSubgroup;
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
			
			maintAuditSubgroups= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintAuditSubgroups.add((MaintAuditSubgroup) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintAuditSubgroups);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditSubgroup> getMaintAuditSubgroupe(Integer legalEntityCode) {
		
		Session session=getSession();
		

		
		/*return (List<MaintAuditSubgroup>)session.createSQLQuery(" SELECT DISTINCT `audit_group_code` as auditGroupCode ,`audit_sub_group_code` as auditSubGroupCode , "
				+ " (SELECT audit_sub_group_name FROM maint_audit_subgroup "
				+ "  WHERE audit_sub_group_code=sb.audit_sub_group_code "
				+ " and entity_status ='"+BankAuditConstant.STATUS_ACTIVE+"' "
				+ "  AND legal_entity_code =:legalEntityCode LIMIT 1)AS  auditSubGroupName, "
				
				+ " (SELECT audit_group_name FROM maint_audit_group WHERE audit_group_code = sb.audit_group_code "
				+ " and entity_status ='"+BankAuditConstant.STATUS_ACTIVE+"' "
				+ "  AND legal_entity_code =:legalEntityCode LIMIT 1) AS auditGroupName "
				+ " FROM maint_audit_subgroup sb WHERE  legal_entity_code =:legalEntityCode ")
				.setParameter("legalEntityCode",legalEntityCode)
				.setResultTransformer(Transformers.aliasToBean(MaintAuditSubgroup.class))
				.list(); */ // Code commented as not returning the correct result
		
		/*return (List<MaintAuditSubgroup>)session.createNativeQuery(" SELECT"
				+ "		(select concat(at.audit_type_code,'-',at.audit_type_desc) from maint_audit_type_desc at " 
				+ "    		where at.legal_entity_code=:legalEntityCode and at.legal_entity_code=ag.legal_entity_code " 
				+ "			and ag.audit_type_code=at.audit_type_code) AS auditTypeCode, "
				+ "		ag.audit_group_code AS auditGroupCode, asg.audit_sub_group_code AS auditSubGroupCode,"
				+ "		asg.audit_sub_group_name AS auditSubGroupName, ag.audit_group_name AS auditGroupName"
				+ " FROM maint_audit_subgroup asg, maint_audit_group ag "
				+ " 	 ,( select max(audit_sub_group_code) as audit_sub_group_code ,audit_type_code, legal_entity_code  "
				+ "			from maint_audit_subgroup where legal_entity_code=:legalEntityCode group by audit_type_code ) asg1 "
				+ " 	WHERE ag.legal_entity_code=asg.legal_entity_code AND ag.legal_entity_code =:legalEntityCode "
				+ " 		AND ag.audit_group_code=asg.audit_group_code "
				+ "			AND ag.entity_status ='"+BankAuditConstant.STATUS_ACTIVE+"' AND asg.entity_status='"+BankAuditConstant.STATUS_ACTIVE+"' "
				+ "     	AND asg1.audit_sub_group_code=asg.audit_sub_group_code and asg.legal_entity_code=asg1.legal_entity_code "
				+ " 		AND asg1.audit_type_code = asg.audit_type_code and ag.legal_entity_code=asg1.legal_entity_code ")
				.setParameter("legalEntityCode",legalEntityCode)
				.setResultTransformer(Transformers.aliasToBean(MaintAuditSubgroup.class))
				.list();*/
		
		//Condition modified to get only one record for each Audit_sub_group to show Audit type wise in User and Entity.
		return (List<MaintAuditSubgroup>)session.createNativeQuery(" "
				+ " SELECT  legal_entity_code as legalEntityCode, "
				+ " 	    (SELECT CONCAT(at.audit_type_code,'-',at.audit_type_desc) FROM maint_audit_type_desc at   "
				+ " 		      WHERE at.legal_entity_code=:legalEntityCode AND at.legal_entity_code=asg.legal_entity_code   "
				+ " 					and asg.audit_type_code=at.audit_type_code) AS auditTypeCode,  "
				+ " 		MAX(audit_group_code) as auditGroupCode,audit_sub_group_code as auditSubGroupCode FROM "
				+ "   (SELECT asg1.legal_entity_code,asg1.audit_type_code,asg1.audit_group_code,asg1.audit_sub_group_code  "
				+ " 	  FROM maint_audit_subgroup asg1, "
				+ " 		   (SELECT MAX(audit_sub_group_code) as audit_sub_group_code, audit_type_code, legal_entity_code  "
				+ " 				 FROM maint_audit_subgroup WHERE legal_entity_code=:legalEntityCode GROUP BY audit_type_code ) as asg2 "
				+ "    WHERE asg1.legal_entity_code=asg2.legal_entity_code AND asg1.audit_sub_group_code=asg2.audit_sub_group_code  "
				+ " 		 AND asg1.audit_type_code =asg2.audit_type_code) as asg "
				+ " WHERE legal_entity_code=:legalEntityCode GROUP BY audit_type_code,audit_sub_group_code ")
				.setParameter("legalEntityCode",legalEntityCode)
				.setResultTransformer(Transformers.aliasToBean(MaintAuditSubgroup.class))
				.list();
	
		}

	@Override
	public void deleteMaintAuditSubgroup(Integer legalEntityCode, String auditSubGroupCode, String auditGroupCode,
			String auditTypeCode, String statusUnauth) {


		Session session=getSession();
		
		if(!BankAuditConstant.STATUS_AUTH.equals(statusUnauth)){
			session.createQuery("delete from MaintAuditSubgroupWrk where legalEntityCode =:legalEntityCode  and "
					+ " auditGroupCode =:auditGroupCode "
					+ " and auditSubGroupCode =:auditSubGroupCode "
					+ " and auditTypeCode =:auditTypeCode  ")
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode)
			.setParameter("auditGroupCode", auditGroupCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}else {
			session.createQuery("delete from MaintAuditSubgroup where legalEntityCode =:legalEntityCode  and "
					+ " auditGroupCode =:auditGroupCode "
					+ " and auditSubGroupCode =:auditSubGroupCode "
					+ " and auditTypeCode =:auditTypeCode  ")
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("auditGroupCode", auditGroupCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}
	
		
	}

	@Override
	public Boolean isMaintAuditSubgroup(Integer legalEntityCode, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode) {Session session=getSession();
			Long count=(Long)session.createQuery(
					" select count(*) "
					+ " from MaintAuditSubgroup   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditSubGroupCode =:auditSubGroupCode"
					+ " and auditGroupCode =:auditGroupCode"
					+ " and auditTypeCode =:auditTypeCode  ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode)
			.setParameter("auditGroupCode", auditGroupCode)
			.setParameter("auditTypeCode", auditTypeCode).uniqueResult();
			
			Long count1=(Long)session.createQuery(
			
					
					" select count(*) "
					+ " from MaintAuditSubgroupWrk   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditSubGroupCode =:auditSubGroupCode"
					+ " and auditGroupCode =:auditGroupCode"
					+ " and auditTypeCode =:auditTypeCode  ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode)
			.setParameter("auditGroupCode", auditGroupCode)
			.setParameter("auditTypeCode", auditTypeCode).uniqueResult();
			Boolean flag=false;
			if((count1!=null && count1 >0)||(count!=null && count >0)){
				flag= true;
			}else {
				flag= false;
			}
			return flag;
		}
	
	@Override
	public Boolean isMaintAuditSubgroupLE(Integer legalEntityCode, 	String auditSubGroupCode) {
		logger.info("Inside isMaintAuditSubgroupLE.."+ legalEntityCode+" :: "+auditSubGroupCode);		
		Session session=getSession();
			Long count=(Long)session.createQuery(
					" select count(*) "
					+ " from MaintAuditSubgroup   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditSubGroupCode =:auditSubGroupCode")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode).uniqueResult();
			
			Long count1=(Long)session.createQuery(
					" select count(*) "
					+ " from MaintAuditSubgroupWrk   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditSubGroupCode =:auditSubGroupCode")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode).uniqueResult();
			Boolean flag=false;
			if((count1!=null && count1 >0)||(count!=null && count >0)){
				flag= true;
			}else {
				flag= false;
			}
			return flag;
		}

}
