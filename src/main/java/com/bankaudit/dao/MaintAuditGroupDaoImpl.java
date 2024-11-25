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
import com.bankaudit.model.MaintAuditGroup;
import com.bankaudit.helper.BankAuditUtil;

@Repository("maintAuditGroupDao")
public class MaintAuditGroupDaoImpl extends AbstractDao implements MaintAuditGroupDao{
	
	static final Logger logger = Logger.getLogger(MaintAuditGroupDaoImpl.class);

	@Override
	public DataTableResponse getMaintAuditGroups(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintAuditGroup> maintAuditGroups = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		
		StringBuilder queryString=new StringBuilder(
				
				          "  SELECT  "
						+ "  legal_entity_code, "
						+ "(select concat(atd.audit_type_code , '-' , atd.audit_type_desc)"
					    + " from maint_audit_type_desc atd where "
					    + " atd.audit_type_code=ag.audit_type_code "
					    + " and atd.legal_entity_code=ag.legal_entity_code ) as audit_type_code , "
						
					    + "  audit_group_code, "
						+ "  audit_group_name, "
						 
						+ " (select concat(gp.value , '-' , gp.description) from general_parameter gp where"
						+ " gp.key_1='ENTITY_STATUS' and gp.key_2='ENTITY_STATUS' and gp.value=ag.entity_status "
						+ " and gp.legal_entity_code=ag.legal_entity_code ) as entity_status ,  "
						
						+ "  auth_rej_remarks, "
						+ " (select concat(gp.value , '-' , gp.description)"
						+ " from general_parameter gp where "
						+ " gp.key_1='STATUS' and gp.value=ag.status "
						+ " and gp.legal_entity_code=ag.legal_entity_code ) as status, "
						
						+ "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						+ " from user u where "
						+ " u.user_id=ag.maker "
						+ " and u.legal_entity_code=ag.legal_entity_code ) as maker ,  "
						
						+ "  maker_timestamp, "
						+ "  checker, "
						+ "  checker_timestamp "
						+ "  FROM ( "
						+ "  SELECT * FROM maint_audit_group "
						+ "  WHERE legal_entity_code =:legal_entity_code "
						+ "  UNION ALL "
						+ "  SELECT * FROM maint_audit_group_wrk  "
						+ "  WHERE legal_entity_code =:legal_entity_code ) "
						+ "  AS ag WHERE legal_entity_code =:legal_entity_code "
				);
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( "
					+ "   ag.audit_group_code like :search  "
					+ " or  ag.audit_group_name like :search  "
					+ " or  ag.audit_type_code like :search  "
					+ " or  ag.maker like :search  "
					+ " or  ag.status IN (SELECT value FROM general_parameter WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  ag.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
	                + " or  ag.maker IN ( "
				    + " select u.user_id "
							 + " from user u where "
							 + " (concat(u.first_name , ' ' , u.last_name) like :search ) "
							 + " and u.legal_entity_code =:legal_entity_code ) "
					+ " or  ag.audit_type_code in (select matd.audit_type_code from maint_audit_type_desc matd where matd.audit_type_desc like :search )"
					+ " ) ");
		}
		        
		
		String[] columns={
				"ag.audit_type_code",
				"ag.audit_group_code",
				"ag.audit_group_name",
				"ag.maker",
				"ag.entity_status",
				"ag.status"};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null	&& !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						MaintAuditGroup maintAuditGroup=null;
						if(tuple!=null){
							maintAuditGroup=new MaintAuditGroup();
							if(tuple[0]!=null){
								maintAuditGroup.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								maintAuditGroup.setAuditTypeCode(tuple[1].toString());
							}
							if(tuple[2]!=null){
								maintAuditGroup.setAuditGroupCode(tuple[2].toString());
							}
							if(tuple[3]!=null){
								maintAuditGroup.setAuditGroupName(tuple[3].toString());
							}
							if(tuple[4]!=null){
								maintAuditGroup.setEntityStatus(tuple[4].toString());
							}
							if(tuple[5]!=null){
								maintAuditGroup.setAuthRejRemarks(tuple[5].toString());
							}
							if(tuple[6]!=null){
								maintAuditGroup.setStatus(tuple[6].toString());
							}
							if(tuple[7]!=null){
								maintAuditGroup.setMaker(tuple[7].toString());
							}
							if(tuple[8]!=null){
								maintAuditGroup.setMakerTimestamp((Date)tuple[8]);
							}
							if(tuple[9]!=null){
								maintAuditGroup.setChecker(tuple[9].toString());
							}
							if(tuple[10]!=null){
								maintAuditGroup.setCheckerTimestamp((Date)tuple[10]);
							}
						}
						return maintAuditGroup;
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
			
			maintAuditGroups= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintAuditGroups.add((MaintAuditGroup) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintAuditGroups);
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
