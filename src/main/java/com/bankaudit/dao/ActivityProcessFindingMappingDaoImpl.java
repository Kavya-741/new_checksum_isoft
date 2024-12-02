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
import com.bankaudit.dto.SequenceDto;
import com.bankaudit.model.ActivityProcessFindingMapping;
import com.bankaudit.model.User;
import com.bankaudit.util.BankAuditUtil;

@Repository("activityProcessFindingMappingDao")
public class ActivityProcessFindingMappingDaoImpl extends AbstractDao implements ActivityProcessFindingMappingDao {

	static final Logger logger = Logger.getLogger(ActivityProcessFindingMappingDaoImpl.class);
	
	@Override
	public DataTableResponse getActivityProcessFindingMappings(Integer legalEntityCode, String search,
			Integer orderColumn, String orderDirection, Integer page, Integer size) {
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<ActivityProcessFindingMapping> activityProcessFindingMappings = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		
		StringBuilder queryString=new StringBuilder(
				
				 "  SELECT "
						 + "  legal_entity_code, "
						 + "  mapping_id, "
						 + "  mapping_code, "
						
						 + "(select concat(atd.audit_type_code , '-' , atd.audit_type_desc)"
						    + " from maint_audit_type_desc atd where "
						    + " atd.audit_type_code=apfm.audit_type_code "
						    + " and atd.legal_entity_code=apfm.legal_entity_code ) as audit_type_code , "
						 
						 + "(select concat(ag.audit_group_code , '-' , ag.audit_group_name)"
							+ " from maint_audit_group ag where "
							+ " ag.audit_group_code=apfm.audit_group_code "
							+ " and ag.audit_type_code=apfm.audit_type_code "
							+ " and ag.legal_entity_code=apfm.legal_entity_code ) as audit_group_code , "
						
						 + "(select concat(asg.audit_sub_group_code , '-' , asg.audit_sub_group_name)"
							+ " from maint_audit_subgroup asg where "
							+ " asg.audit_sub_group_code=apfm.audit_sub_group_code "
							+ " and asg.audit_group_code=apfm.audit_group_code "
							+ " and asg.audit_type_code=apfm.audit_type_code "
							+ " and asg.legal_entity_code=apfm.legal_entity_code ) as audit_sub_group_code , "
						 
			             + "(select concat(aa.activity_id , '-' , aa.activity_name)"
							+ " from maint_audit_activity aa where "
							+ " aa.activity_id=apfm.activity_id "
							+ " and aa.legal_entity_code=apfm.legal_entity_code ) as activity_id , "
							
					     + "(select concat(ap.process_id , '-' , ap.process_name)"
							+ " from maint_audit_process ap where "
							+ " ap.process_id=apfm.process_id "
							+ " and ap.legal_entity_code=apfm.legal_entity_code ) as process_id , "
							
						 + "(select concat(af.finding_id , '-' , af.finding_name , '-' , af.f_criticality) "   // to get the Loss Event type.
							+ " from maint_audit_finding af where "
							+ " af.finding_id=apfm.finding_id "
							+ " and af.legal_entity_code=apfm.legal_entity_code ) as finding_id , "	
						
						 + "  auth_rej_remarks, "
						  + " (select concat(gp.value , '-' , gp.description) from general_parameter gp where "
							 + " gp.key_1='ENTITY_STATUS' and gp.key_2='ENTITY_STATUS' and gp.value=apfm.entity_status "
							 + " and gp.legal_entity_code=apfm.legal_entity_code ) as entity_status , " 
							 
						  + " (select concat(gp.value , '-' , gp.description)"
				    		 + " from general_parameter gp where "
				    		 + " gp.key_1='STATUS' and gp.value=apfm.status "
				    		 + " and gp.legal_entity_code=apfm.legal_entity_code ) as status , "
						
						 + "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						 + " from user u where "
						 + " u.user_id=apfm.maker "
						 + " and u.legal_entity_code=apfm.legal_entity_code ) as maker ,  "
						 
						 + "  maker_timestamp, "
						 + "  checker, "
						 + "  checker_timestamp, "
						 + "  activity_code , "
						 + "  process_code , "
						 + "  finding_code ,  "
						 +" (select concat(c.criticality_code,'-',c.criticality_desc) from maint_criticality c, maint_audit_finding af "
						 + "  where af.finding_id=apfm.finding_id AND  af.legal_entity_code=apfm.legal_entity_code AND c.legal_entity_code=af.legal_entity_code "  
						 +"		AND c.criticality_of_type='Finding' AND c.criticality_code=af.f_criticality ) as f_criticality, "
						 + " (select  gp.description "
			    		 + " from general_parameter gp where "
			    		 + " gp.key_1='RCSA_SETUP' and gp.key_2='RISK_BELONGS_TO' and gp.value=apfm.risk_belongs_to "
			    		 + " and gp.legal_entity_code=apfm.legal_entity_code ) as risk_belongs_to "		// risk_belongs_to added as part of RCSA Setup				 
						
						 + "  FROM "
						 + "  ( SELECT * "
						 + "  FROM "
						 + "  activity_process_finding_mapping "
						 + "  WHERE "
						 + "  legal_entity_code =:legal_entity_code "
						 + "  and entity_status !='E' "
						 + "  UNION ALL "
						 + "  SELECT * "
						 + "  FROM "
						 + "  activity_process_finding_mapping_wrk "
						 + "  WHERE "
						 + "  legal_entity_code =:legal_entity_code "
						 + "  ) AS apfm "
						 + "  WHERE "
						 + "  legal_entity_code =:legal_entity_code "

				);
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( apfm.audit_type_code IN ("
					+ " select atd.audit_type_code "
			    + " from maint_audit_type_desc atd where "
			    + " atd.audit_type_desc LIKE :search "
			    + " and atd.legal_entity_code=apfm.legal_entity_code  "
					+ " ) "
					+ " or  apfm.audit_type_code LIKE :search "
					+ " or  apfm.audit_group_code IN ( "
					+ "select ag.audit_group_code "
				    + " from maint_audit_group ag where "
				    + " ag.audit_group_name LIKE :search "
				    + " and ag.legal_entity_code=apfm.legal_entity_code "
					+ " ) "
					+ " or  apfm.audit_group_code LIKE :search "
					+ " or  apfm.audit_sub_group_code IN ( "
					+ " select asg.audit_sub_group_code"
				+ " from maint_audit_subgroup asg where "
				+ " asg.audit_sub_group_name LIKE :search "
				+ " and asg.legal_entity_code=apfm.legal_entity_code "
					+ " ) "
					+ " or  apfm.audit_sub_group_code LIKE :search "
					+ " or  apfm.activity_id IN ( "
					+ " select aa.activity_id "
				+ " from maint_audit_activity aa where "
				+ " aa.activity_name LIKE :search "
				+ " and aa.legal_entity_code=apfm.legal_entity_code  "
					+ " )"
					+ " or  apfm.activity_code LIKE :search "
					+ " or  apfm.process_id IN ( "
					+ " select ap.process_id"
				+ " from maint_audit_process ap where "
				+ " ap.process_name LIKE :search "
				+ " and ap.legal_entity_code=apfm.legal_entity_code "
					+ " )"
					+ " or  apfm.process_code LIKE :search "
					+ " or  apfm.maker like :search"
                    + " or  apfm.maker in ( "
					+ " select u.user_id "
					+ " from user u where "
					+ " (concat(u.first_name , ' ' , u.last_name) like :search ) "
					+ " and u.legal_entity_code=apfm.legal_entity_code ) "
					+ " or  apfm.status IN (SELECT value FROM general_parameter  WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  apfm.finding_id IN ( "
					+ " select af.finding_id "
				+ " from maint_audit_finding af where "
				+ " af.finding_name LIKE :search "
				+ " and af.legal_entity_code=apfm.legal_entity_code "
					+ " ) "
					+ " or  apfm.finding_code LIKE :search "
					+ " or  apfm.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
					+ " or  apfm.risk_belongs_to IN (SELECT value FROM general_parameter  WHERE key_1='RCSA_SETUP' and key_2='RISK_BELONGS_TO' and description LIKE :search ) "
					+ " ) ");
			
		}
	
		String[] columns={
				"apfm.audit_type_code",
				"apfm.audit_group_code",
				"apfm.audit_sub_group_code",
				/*"apfm.activity_code",*/
				" CAST(apfm.activity_code AS UNSIGNED), apfm.activity_code",
				"apfm.process_code",
				"apfm.finding_code",
				"f_criticality",
				"apfm.maker",
				"apfm.entity_status",
				"apfm.status"};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null
				&& !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						ActivityProcessFindingMapping activityProcessFindingMapping=null;
						if(tuple!=null){
							
							activityProcessFindingMapping=new ActivityProcessFindingMapping();
							
							if(tuple[0]!=null){
								activityProcessFindingMapping.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){                   
								activityProcessFindingMapping.setMappingId(tuple[1].toString());
							}
							if(tuple[2]!=null){
								activityProcessFindingMapping.setMappingCode(tuple[2].toString());
							}
							if(tuple[3]!=null){
								activityProcessFindingMapping.setAuditTypeCode(tuple[3].toString());
							}
							if(tuple[4]!=null){
								activityProcessFindingMapping.setAuditGroupCode(tuple[4].toString());
							}
							if(tuple[5]!=null){
								activityProcessFindingMapping.setAuditSubGroupCode(tuple[5].toString());
							}
							if(tuple[6]!=null){
								activityProcessFindingMapping.setActivityId(tuple[6].toString());
							}
							if(tuple[7]!=null){
								activityProcessFindingMapping.setProcessId(tuple[7].toString());
							}
							if(tuple[8]!=null){
								activityProcessFindingMapping.setFindingId(tuple[8].toString());
							}
							if(tuple[9]!=null){
								activityProcessFindingMapping.setAuthRejRemarks(tuple[9].toString());
							}
							if(tuple[10]!=null){
								activityProcessFindingMapping.setEntityStatus(tuple[10].toString());
							}
							if(tuple[11]!=null){
								activityProcessFindingMapping.setStatus(tuple[11].toString());
							}
							if(tuple[12]!=null){
								activityProcessFindingMapping.setMaker(tuple[12].toString());
							}
							if(tuple[13]!=null){
								activityProcessFindingMapping.setMakerTimestamp((Date)tuple[13]);
							}
							if(tuple[14]!=null){
								activityProcessFindingMapping.setChecker(tuple[14].toString());
							}
							if(tuple[15]!=null){
								activityProcessFindingMapping.setCheckerTimestamp((Date)tuple[15]);
							}
							
							if(tuple[16]!=null){
								activityProcessFindingMapping.setActivityCode(tuple[16].toString());
							}
							
							if(tuple[17]!=null){
								activityProcessFindingMapping.setProcessCode(tuple[17].toString());
							}
							
							if(tuple[18]!=null){
								activityProcessFindingMapping.setFindingCode(tuple[18].toString());
							}
							if(tuple[19]!=null){
								activityProcessFindingMapping.setFCriticality(tuple[19].toString());
							}
							if(tuple[20]!=null){
								activityProcessFindingMapping.setRiskBelongsTo(tuple[20].toString());
							}
						}
						return activityProcessFindingMapping;
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
			
			activityProcessFindingMappings= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				activityProcessFindingMappings.add((ActivityProcessFindingMapping) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(activityProcessFindingMappings);
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
	public List<String> getActivityProcessFindingMappingValues(String legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode, String activityId, String processId,
			String findingId,String createOrUpdate) {
		
		Session session=getSession();
		
		StringBuilder queryStr=new StringBuilder();
		queryStr.append(" From ActivityProcessFindingMapping map  "
				+ " where map.legalEntityCode =:legalEntityCode "
				+ " and   map.auditTypeCode =:auditTypeCode ");
		
		if(createOrUpdate.equalsIgnoreCase("C")){
			queryStr.append(" and  map.entityStatus ='"+BankAuditConstant.STATUS_ACTIVE+"'  ");
		}
		
		if(!BankAuditUtil.isEmptyString(findingId)){
			queryStr.insert( 0," select map.mappingId  ");
			queryStr.append(" and map.findingId =:findingId "
					+ " and map.processId =:processId "
					+ " and map.activityId =:activityId "
					+ " and map.auditSubGroupCode =:auditSubGroupCode "
					+ " and map.auditGroupCode =:auditGroupCode "
					+ " ");
			
		}else if (!BankAuditUtil.isEmptyString(processId)) {
			
			queryStr.insert(0," select distinct  "
					+ "( select distinct  concat(f.findingName , '-' , f.findingId  , '-' ,f.fCriticality )  "
            	    + " from  MaintAuditFinding f where "
            	   // + " f.entityStatus= '"+BankAuditConstant.STATUS_ACTIVE+"' and"
            	    + " f.findingId=map.findingId  "
	                + " and f.legalEntityCode=map.legalEntityCode ) as findingId ");
			
			queryStr.append(" "
					+ " and map.processId =:processId "
					+ " and map.activityId =:activityId "
					+ " and map.auditSubGroupCode =:auditSubGroupCode "
					+ " and map.auditGroupCode =:auditGroupCode "
					+ " ");
			
		}else if (!BankAuditUtil.isEmptyString(activityId)) {
			
			queryStr.insert(0," select distinct "
					+ "( select distinct  concat( f.processName , '-' , f.processId )  "
            	    + " from  MaintAuditProcess f where "
            	   // + " f.entityStatus= '"+BankAuditConstant.STATUS_ACTIVE+"' and"
            	    + "  f.processId=map.processId  "
            	    + " and f.legalEntityCode=map.legalEntityCode ) as processId ");
			
			queryStr.append(" "
					+ " and map.activityId =:activityId "
					+ " and map.auditSubGroupCode =:auditSubGroupCode "
					+ " and map.auditGroupCode =:auditGroupCode "
					+ " ");
			
		}else if (!BankAuditUtil.isEmptyString(auditSubGroupCode)) {
		
			queryStr.insert(0," select distinct "
					+ "( select distinct  concat( f.activityName , '-' , f.activityId )  "
            	    + " from  MaintAuditActivity f where "
            	  //  + " f.entityStatus= '"+BankAuditConstant.STATUS_ACTIVE+"' and "
            	    + " f.activityId=map.activityId "
            	    + " and f.legalEntityCode=map.legalEntityCode ) as activityId ");
			
			queryStr.append(" "
					+ " and map.auditSubGroupCode =:auditSubGroupCode "
					+ " and map.auditGroupCode =:auditGroupCode "
					+ " ");
		}
		else if (!BankAuditUtil.isEmptyString(auditGroupCode)) {
		
			queryStr.insert(0," select distinct  "
					+ "( select distinct  concat( f.auditSubGroupName , '-' , f.auditSubGroupCode )  "
            	    + " from  MaintAuditSubgroup f where "
            	   // + " f.entityStatus= '"+BankAuditConstant.STATUS_ACTIVE+"' and "
            	    + " f.auditSubGroupCode=map.auditSubGroupCode  "
            	    + " and f.auditGroupCode=map.auditGroupCode  "
            	    + " and f.auditTypeCode=map.auditTypeCode  "
            	    + " and f.legalEntityCode=map.legalEntityCode ) as auditSubGroupCode ");
			
			queryStr.append(" "
					+ " and map.auditGroupCode =:auditGroupCode "
					+ " ");
		}
		else {
			queryStr.insert(0," select distinct  "
					+ "( select distinct  concat(f.auditGroupName  , '-' , f.auditGroupCode )  "
            	    + " from  MaintAuditGroup f where "
            	    + "  f.auditGroupCode=map.auditGroupCode  "
            	    + " and f.auditTypeCode=map.auditTypeCode  "
            	    + " and f.legalEntityCode=map.legalEntityCode ) as auditGroupCode ");
			
		}
		
		Query query = session.createQuery(queryStr.toString())
				.setParameter("legalEntityCode", Integer.parseInt(legalEntityCode))
				.setParameter("auditTypeCode", auditTypeCode);
		
		if(!BankAuditUtil.isEmptyString( auditGroupCode)){
			query.setParameter("auditGroupCode", auditGroupCode);
		}
		
		if(!BankAuditUtil.isEmptyString( auditSubGroupCode)){
			query.setParameter("auditSubGroupCode", auditSubGroupCode);
		}
		
		if(!BankAuditUtil.isEmptyString( activityId)){
			query.setParameter("activityId", activityId);
		}
		
		if(!BankAuditUtil.isEmptyString( processId)){
			query.setParameter("processId", processId);
		}
		
		if(!BankAuditUtil.isEmptyString( findingId)){
			query.setParameter("findingId", findingId);
		}
		
		return query.list();
	}

	@Override
	public void deleteActivityProcessFindingMapping(Integer legalEntityCode, String mappingId, String statusUnauth) {
		Session session=getSession();
		if(!BankAuditConstant.STATUS_AUTH.equals(statusUnauth)){
			session.createQuery("delete from ActivityProcessFindingMappingWrk  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  mappingId =:mappingId  ").setParameter("mappingId", mappingId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}else {
			session.createQuery("delete from ActivityProcessFindingMapping  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  mappingId =:mappingId ").setParameter("mappingId", mappingId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate() ;
		}
	}

	@Override
	public Boolean isActivityProcessFindingMapping(int legalEntityCode, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode, String activityCode, String processCode, String findingCode,String mappingId, String riskBelongsTo) {
		Session session=getSession();
	
		logger.info("Inside isActivityProcessFindingMapping LE.."+legalEntityCode+" auditTypeCode.."+auditTypeCode+" auditGroupCode.."+auditGroupCode+" auditSubGroupCode.."+auditSubGroupCode);
		logger.info("activityCode.."+activityCode+" processCode.."+processCode+" findingCode.."+findingCode+" mappingId.."+mappingId +" riskBelongsTo.."+riskBelongsTo);
		
		if(BankAuditUtil.isEmptyString(mappingId)){
			Long count=(Long)session.createQuery(
					" select count(*) "
					+ " from ActivityProcessFindingMapping   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode =:auditTypeCode  "
					+ " and auditGroupCode =:auditGroupCode  "
					+ " and auditSubGroupCode =:auditSubGroupCode  "
					+ " and activityCode =:activityCode  "
					+ " and processCode =:processCode  "
					+ " and findingCode =:findingCode  "
					+ " and entityStatus !='E'"
					+ " and riskBelongsTo =:riskBelongsTo ")
					.setParameter("legalEntityCode", legalEntityCode)
					.setParameter("auditTypeCode", auditTypeCode)
					.setParameter("auditGroupCode", auditGroupCode)
					.setParameter("auditSubGroupCode", auditSubGroupCode)
					.setParameter("activityCode", activityCode)
					.setParameter("processCode", processCode)
					.setParameter("findingCode", findingCode)
					.setParameter("riskBelongsTo", riskBelongsTo+"")  // riskBelongsTo added to handle an additional parameter in  RCSA Setup
			.uniqueResult();
			
			Long count1=(Long)session.createQuery(
			
					
					" select count(*) "
					+ " from ActivityProcessFindingMappingWrk   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode =:auditTypeCode  "
					+ " and auditGroupCode =:auditGroupCode  "
					+ " and auditSubGroupCode =:auditSubGroupCode  "
					+ " and activityCode =:activityCode  "
					+ " and processCode =:processCode  "
					+ " and findingCode =:findingCode  "
					+ " and entityStatus !='E'"
					+ " and riskBelongsTo =:riskBelongsTo ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("auditGroupCode", auditGroupCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode)
			.setParameter("activityCode", activityCode)
			.setParameter("processCode", processCode)
			.setParameter("findingCode", findingCode)
			.setParameter("riskBelongsTo", riskBelongsTo+"")
			.uniqueResult();
			
			logger.info("Inside mappingId null ....MST ::"+ count +" WRK::" +count1);
			Boolean flag=false;
			if((count1!=null && count1 >0)||(count!=null && count >0)){
				flag=true;
			}else {
			    flag=false;
			}
			logger.info("flag ::"+flag);
			return flag;
		}else{
			Long count=(Long)session.createQuery(
					" select count(*) "
					+ " from ActivityProcessFindingMapping   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode =:auditTypeCode  "
					+ " and auditGroupCode =:auditGroupCode  "
					+ " and auditSubGroupCode =:auditSubGroupCode  "
					+ " and activityCode =:activityCode  "
					+ " and processCode =:processCode  "
					+ " and findingCode =:findingCode  "
					+ " and entityStatus !='E' "
					+ " and riskBelongsTo =:riskBelongsTo "
					//+ " and mappingId !=:mappingId " -- Commented by Debasish on 19 Nov 18, as mapping Id condition not required
					)
					.setParameter("legalEntityCode", legalEntityCode)
					.setParameter("auditTypeCode", auditTypeCode)
					.setParameter("auditGroupCode", auditGroupCode)
					.setParameter("auditSubGroupCode", auditSubGroupCode)
					.setParameter("activityCode", activityCode)
					.setParameter("processCode", processCode)
					.setParameter("findingCode", findingCode)
					.setParameter("riskBelongsTo", riskBelongsTo+"")
					//.setParameter("mappingId", mappingId)
			.uniqueResult();
			
			Long count1=(Long)session.createQuery(
			
					
					" select count(*) "
					+ " from ActivityProcessFindingMappingWrk   "
					+ " where "
					+ " legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode =:auditTypeCode  "
					+ " and auditGroupCode =:auditGroupCode  "
					+ " and auditSubGroupCode =:auditSubGroupCode  "
					+ " and activityCode =:activityCode  "
					+ " and processCode =:processCode  "
					+ " and findingCode =:findingCode  "
					+ " and entityStatus !='E'"
					//+ " and mappingId !=:mappingId " -- Commented by Debasish on 19 Nov 18
					)
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("auditGroupCode", auditGroupCode)
			.setParameter("auditSubGroupCode", auditSubGroupCode)
			.setParameter("activityCode", activityCode)
			.setParameter("processCode", processCode)
			.setParameter("findingCode", findingCode)
			//.setParameter("mappingId", mappingId)
			.uniqueResult();
			
			logger.info("Inside mappingId not null ...MST ::"+ count +" WRK::" +count1);
			Boolean flag=false;
			if((count1!=null && count1 >0)||(count!=null && count >0)){
				flag=true;
			}else {
			    flag=false;
			}
			logger.info("flag ::"+flag);
			return flag;			
		}
		
	}
	
	@Override
	public void updateStatusforAuthModify_E(Integer legalEntityCode, String mappingId, String statusUnauth) {
		Session session=getSession(); 
		logger.info("Inside updateStatusforAuthModify_E:: LE.. "+legalEntityCode+" mapingId.."+mappingId+" status.."+statusUnauth);
		try{
			session.createNativeQuery("update activity_process_finding_mapping  "
					+ " set entity_status='E' where legal_entity_code =:legalEntityCode "
					+ " and  mapping_id =:mappingId  ").setParameter("mappingId", mappingId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<SequenceDto> getDynamicValuesForSequence(String legalEntityCode, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode, String activityId, String processId) {
		Session session = getSession();
		StringBuilder queryStr = new StringBuilder(" SELECT  DISTINCT maa.finding_id, maa.finding_name FROM maint_audit_finding maa "
				+ "INNER JOIN activity_process_finding_mapping apfm ON maa.legal_entity_code = apfm.legal_entity_code AND "
				+ "maa.finding_id = apfm.finding_code WHERE maa.legal_entity_code=:legalEntityCode AND apfm.audit_type_code=:auditTypeCode "
				+ "AND apfm.audit_group_code=:auditGroupCode AND apfm.audit_sub_group_code=:auditSubGroupCode AND apfm.activity_code=:activityId "
				+ "AND apfm.process_code=:processId");

		Query query = session.createNativeQuery(queryStr.toString()).setParameter("legalEntityCode", Integer.parseInt(legalEntityCode))
				.setParameter("auditTypeCode", auditTypeCode).setParameter("auditGroupCode", auditGroupCode)
				.setParameter("auditSubGroupCode", auditSubGroupCode).setParameter("activityId", activityId).setParameter("processId", processId);

		query.setResultTransformer(new ResultTransformer() {
			@Override
			public Object transformTuple(Object[] tuple, String[] aliases) {
				SequenceDto sequenceDto = null;
				if (tuple != null) {
					sequenceDto = new SequenceDto();
					if (tuple[1] != null) {
						sequenceDto.setCode((String) tuple[0]);
					}
					if (tuple[1] != null) {
						sequenceDto.setDiscription((String) tuple[1]);
					}
				}
				return sequenceDto;
			}
			@Override
			public List transformList(List collection) {
				return collection;
			}
		});
		return query.list();
	}
	
	@Override
	public List<ActivityProcessFindingMapping> getAnyOneAuditGrpAndSubGrpForAuditType(Integer legalEntityCode, String auditTypeCode, String status) {

		logger.info("Inside getAnyOneAuditGrpAndSubGrpForAuditType .. "+legalEntityCode +" auditTypeCode.."+auditTypeCode +" status.."+status );
		Session session=getSession();
		return session.createQuery(" from ActivityProcessFindingMapping map "
					+ " WHERE map.legalEntityCode =:legalEntityCode AND map.auditTypeCode=:auditTypeCode "
					+ " AND map.status ='A' AND map.entityStatus='A' "
					+ " ORDER BY map.auditGroupCode,map.auditSubGroupCode ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("auditTypeCode", auditTypeCode).list();
	}

}
