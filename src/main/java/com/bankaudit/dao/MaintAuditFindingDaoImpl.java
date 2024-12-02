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
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditFinding;
import com.bankaudit.util.BankAuditUtil;


@Repository("maintAuditFindingDao")
public class MaintAuditFindingDaoImpl extends AbstractDao implements MaintAuditFindingDao {

	@Override
	public Boolean isMaintAuditFinding(Integer legalEntityCode, String findingCode) {
		Session session=getSession();
		Long count=(Long)session.createQuery(
				" select count(*) "
				+ " from MaintAuditFinding   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and entityStatus !='E'"
				+ " and findingCode =:findingCode  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("findingCode", findingCode).uniqueResult();
		
		Long count1=(Long)session.createQuery(
		
				
				" select count(*) "
				+ " from MaintAuditFindingWrk   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and findingCode =:findingCode  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("findingCode", findingCode).uniqueResult();
		Boolean flag=false;
		if((count1!=null && count1 >0)||(count!=null && count >0)){
			flag=true;
		}else {
			flag=false;
		}
		return flag;
	}

	@Override
	public void deleteMaintAuditFinding(Integer legalEntityCode, String findingId, String statusUnauth) {

		Session session=getSession();
		
		if(!BankAuditConstant.STATUS_AUTH.equals(statusUnauth)){
			
			session.createQuery("delete from MaintAuditFindingWrk  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  findingId =:findingId  ").setParameter("findingId", findingId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			
		}else {
			session.createQuery("delete from MaintAuditFinding  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and  findingId =:findingId ").setParameter("findingId", findingId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate() ;
		}
	}

	@Override
	public DataTableResponse getMaintAuditFindings(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {


		Session session = getSession();
		session.setDefaultReadOnly(true);
		List<MaintAuditFinding> activities = null;

		DataTableResponse dataTableResponse=new DataTableResponse();

		StringBuilder queryString=new StringBuilder(
				  " SELECT "
						  +" legal_entity_code, "
						  +" finding_id, "
						  +" finding_code, "
						  +" finding_name, "
						  // +" f_criticality   , "
						  +" (select concat(c.criticality_code,'-',c.criticality_desc) from maint_criticality c where c.criticality_of_type='Finding' "  
						  +"		AND c.criticality_code=aa.f_criticality AND c.legal_entity_code=aa.legal_entity_code) as f_criticality ,"
						  +" auth_rej_remarks, "
						  + " (select concat(gp.value , '-' , gp.description) from general_parameter gp where "
							 + " gp.key_1='ENTITY_STATUS' and gp.key_2='ENTITY_STATUS' and gp.value=aa.entity_status "
							 + " and gp.legal_entity_code=aa.legal_entity_code ) as entity_status , " 
							 
						  + " (select concat(gp.value , '-' , gp.description)"
				    		 + " from general_parameter gp where "
				    		 + " gp.key_1='STATUS' and gp.value=aa.status "
				    		 + " and gp.legal_entity_code=aa.legal_entity_code ) as status , "
						  
						 + "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						 + " from user u where "
						 + " u.user_id=aa.maker "
						 + " and u.legal_entity_code=aa.legal_entity_code ) as maker ,  "	 
						  
						  +" maker_timestamp, "
						  +" checker, "
						  +" checker_timestamp "
						  +"  FROM "
						  +"  ( SELECT * FROM maint_audit_finding WHERE legal_entity_code =:legal_entity_code "
						  + " and entity_status !='E' "
						  +" UNION ALL "
						  +" SELECT * FROM maint_audit_finding_wrk WHERE legal_entity_code =:legal_entity_code "
						/*  + " and (status !='DF' OR ( status ='DF' and maker =:user_id ) )  "	*/	
						  +"  "
						  +" ) AS aa WHERE legal_entity_code =:legal_entity_code "
				);

		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( aa.finding_code like :search "
					+ " or  aa.finding_name like :search  "
					+ " or  aa.maker like :search  "
					
					+ " or  aa.f_criticality like :search"
					+ " or  aa.f_criticality in (select c.criticality_code from maint_criticality c where c.criticality_of_type='Finding' "  
					+ "						 AND c.legal_entity_code=aa.legal_entity_code AND c.criticality_desc like :search )"					
					+ " or  aa.maker in ( "
					
					+ " select u.user_id "
					+ " from user u where "
					+ " (concat(u.first_name , ' ' , u.last_name) like :search  ) "
					+ " and u.legal_entity_code=aa.legal_entity_code ) "
					
					+ " or  aa.status IN (SELECT value FROM general_parameter  WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  aa.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
					+ " )"
					);
		}



		String[] columns={
				"aa.finding_code",
				"aa.finding_name",
				"aa.f_criticality",
				"aa.maker",
				"aa.entity_status",
				
				"aa.status"
				};

		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by FIELD(STATUS,'DF') DESC ,  ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}

		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {

					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						MaintAuditFinding maintAuditFinding=null;
						if(tuple!=null){
							
							maintAuditFinding=new MaintAuditFinding();
							
							if(tuple[0]!=null){
								maintAuditFinding.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								maintAuditFinding.setFindingId(tuple[1].toString());
							}
							if(tuple[2]!=null){
								maintAuditFinding.setFindingCode(tuple[2].toString());
							}
							if(tuple[3]!=null){
								maintAuditFinding.setFindingName(tuple[3].toString());
							}
							if(tuple[4]!=null){
								maintAuditFinding.setfCriticality(tuple[4].toString());
							}
							if(tuple[5]!=null){
								maintAuditFinding.setAuthRejRemarks(tuple[5].toString());
							}
							if(tuple[6]!=null){
								maintAuditFinding.setEntityStatus(tuple[6].toString());
							}
							if(tuple[7]!=null){
								maintAuditFinding.setStatus(tuple[7].toString());
							}
							if(tuple[8]!=null){
								maintAuditFinding.setMaker(tuple[8].toString());
							}
							if(tuple[9]!=null){
								maintAuditFinding.setMakerTimestamp((Date)tuple[9]);
							}
							if(tuple[10]!=null){
								maintAuditFinding.setChecker(tuple[10].toString());
							}
							if(tuple[11]!=null){
								maintAuditFinding.setCheckerTimestamp((Date)tuple[11]);
							}
							
						}
						
						return maintAuditFinding;
					}

					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legal_entity_code", legalEntityCode)
				/*.setParameter("user_id", userId)*/;



		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}


		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){

			activities= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				activities.add((MaintAuditFinding) resultScroll.get(0));
				if (!resultScroll.next())
					break;
			}
			dataTableResponse.setData(activities);
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


}
