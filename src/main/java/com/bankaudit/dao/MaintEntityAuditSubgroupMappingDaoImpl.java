package com.bankaudit.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;
import com.bankaudit.util.BankAuditUtil;

@Repository("maintEntityAuditSubgroupMappingDao") 
public class MaintEntityAuditSubgroupMappingDaoImpl extends AbstractDao implements MaintEntityAuditSubgroupMappingDao {

	
	@Override
	public void deleteMaintEntityAuditSubgroupMapping(Integer legalEntityCode, String mappingType, String id) {


		Session session=getSession();
		
		session.createQuery("delete from MaintEntityAuditSubgroupMapping"
				+ " where legalEntityCode =:legalEntityCode and "
				+ " mappingType =:mappingType and "
				+ " id =:id  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("mappingType", mappingType)
		.setParameter("id", id)
		.executeUpdate();
		
	}

	@Override
	public void deleteMaintEntityAuditSubgroupMappingWrk(Integer legalEntityCode, String mappingType, String id) {


		Session session=getSession();
		
		session.createQuery("delete from MaintEntityAuditSubgroupMappingWrk"
				+ " where legalEntityCode =:legalEntityCode and "
				+ " mappingType =:mappingType and "
				+ " id =:id  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("mappingType", mappingType)
		.setParameter("id", id)
		.executeUpdate();
		
	}
	
	@Override
	public DataTableResponse getmaintEntityAuditSubgroupMapping(Integer legalEntityCode, String userId, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		
		Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintEntityAuditSubgroupMapping> maintEntityAuditSubgroupMappings = null;
		DataTableResponse dataTableResponse=new DataTableResponse();
	
		StringBuilder queryString=new StringBuilder(
				          "  SELECT  distinct "  
						+ "  legal_entity_code,  " 
				        		  
						+ " (SELECT concat(unit_code , '-' , unit_name) FROM maint_entity me WHERE "
						+ " id= me.unit_code AND me.legal_entity_code =:legal_entity_code) as id , " // This piece of code by considering only Entity not for User
				        		  
						+ " (select concat(gp.value , '-' , gp.description)"
						+ " from general_parameter gp where "
						+ " gp.key_1='AUDIT_TYPE_MAPPING' and gp.value=asg.mapping_type "
						+ " and gp.legal_entity_code=asg.legal_entity_code ) as mapping_type, "
						
						+ " (select concat(atd.audit_type_code , '-' , atd.audit_type_desc)"
						+ " from maint_audit_type_desc atd where "
						+ " atd.audit_type_code=asg.audit_type_code "
						+ " and atd.legal_entity_code=asg.legal_entity_code ) as audit_type_code , "
						
						/*+ "(select concat(atd.audit_group_code , '-' , atd.audit_group_name)"
						+ " from maint_audit_group atd where "
						+ " atd.audit_type_code=asg.audit_type_code "
						+ " and atd.audit_group_code=asg.audit_group_code "
						+ " and atd.legal_entity_code=asg.legal_entity_code ) as audit_group_code , "
						
						+ "(select concat(atd.audit_sub_group_code , '-' , atd.audit_sub_group_name)"
						+ " from maint_audit_subgroup atd where "
						+ " atd.audit_type_code=asg.audit_type_code "
						+ " and atd.audit_group_code=asg.audit_group_code "
						+ " and atd.audit_sub_group_code=asg.audit_sub_group_code "
						+ " and atd.legal_entity_code=asg.legal_entity_code ) as audit_sub_group_code , "*/
						
						/*+ " (select concat(gp.value , '-' , gp.description) from general_parameter gp where"
						+ " gp.key_1='ENTITY_STATUS' and gp.key_2='ENTITY_STATUS' and gp.value=asg.entity_status "
						+ " and gp.legal_entity_code=asg.legal_entity_code ) as entity_status ,  "*/
						
						+ " (select concat(gp.value , '-' , gp.description)"
						+ " from general_parameter gp where "
						+ " gp.key_1='STATUS' and gp.value=asg.status "
						+ " and gp.legal_entity_code=asg.legal_entity_code ) as status, " 
						
						+ "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						+ " from user u where "
						+ " u.user_id=asg.maker "
						+ " and u.legal_entity_code=asg.legal_entity_code ) as maker ,  "
						
						+ "	 asg.auth_unique_id as authUniqueId,	"

						+ "  maker_timestamp,  "  
						+ "  checker,  "  
						+ "  checker_timestamp ,  " 
						+ "	 asg.status as status_sort " // added to sort by Status for predefined Sorting						
						
						
						
						+ "  FROM (  "  
						+ "  SELECT * FROM maint_entity_audit_subgroup_mapping   "  
						+ "  WHERE legal_entity_code =:legal_entity_code  "  
						+ "  UNION ALL "  
						+ "  SELECT * FROM maint_entity_audit_subgroup_mapping_wrk   "  
						+ "  WHERE legal_entity_code =:legal_entity_code )  "  
						+ "  AS asg WHERE legal_entity_code =:legal_entity_code "
						+ "  and (SELECT concat(unit_code , '-' , unit_name)  "
						+ "  FROM maint_entity me WHERE  id= me.unit_code AND me.legal_entity_code =:legal_entity_code) is not null " 
						
						/*+ "(select concat(atd.audit_group_code , '-' , atd.audit_group_name)"
						+ " from maint_audit_group atd where "
						+ " atd.audit_type_code=asg.audit_type_code "
						+ " and atd.audit_group_code=asg.audit_group_code "
						+ " and atd.legal_entity_code=asg.legal_entity_code ) as audit_group_code , "
						
						+ "(select concat(atd.audit_sub_group_code , '-' , atd.audit_sub_group_name)"
						+ " from maint_audit_subgroup atd where "
						+ " atd.audit_type_code=asg.audit_type_code "
						+ " and atd.audit_group_code=asg.audit_group_code "
						+ " and atd.audit_sub_group_code=asg.audit_sub_group_code "
						+ " and atd.legal_entity_code=asg.legal_entity_code ) as audit_sub_group_code , "*/
						
						
				);
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( "
					+ "   asg.audit_type_code like :search  "
					+ " or asg.id like :search "
					//+ " or  asg.audit_sub_group_code like :search  "
					//+ " or  asg.audit_sub_group_name like :search  "
					//+ " or  asg.audit_group_code like :search  "
					+ " or  asg.maker like :search  "
					+ " or  asg.status IN (SELECT value FROM general_parameter WHERE key_1='STATUS' and  description LIKE :search ) "
					//+ " or  asg.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
					
					+ " or  asg.id IN (SELECT  me_s.unit_code   "
							+ "	FROM maint_entity me_s WHERE "
							+ " (me_s.unit_code like :search or me_s.unit_name like :search ) "
							+ " AND me_s.legal_entity_code =:legal_entity_code )   "
					+ " or  asg.id IN (SELECT  concat(me_s.unit_code , '-' , me_s.unit_name)   "
							+ "	FROM maint_entity me_s WHERE "
							+ " (me_s.unit_code like :search or me_s.unit_name like :search ) "
							+ " AND me_s.legal_entity_code =:legal_entity_code )   "		
					
					+ " or  asg.maker IN ( "
						+ " select u.user_id "
							 + " from user u where "
							 + " (u.first_name like :search OR  u.last_name like :search)  "
							 + " and u.legal_entity_code =:legal_entity_code ) "
					 + " or  asg.maker IN ( "
						+ " select concat(u.user_id , '-' ,u.first_name,' ',u.last_name) "
							 + " from user u where "
							 + " (u.first_name like :search OR  u.last_name like :search)  "
							 + " and u.legal_entity_code =:legal_entity_code ) "	
							 
					+ " or  asg.audit_type_code in (select  matd.audit_type_code from maint_audit_type_desc matd "
							+ " where matd.audit_type_desc like :search )"
					
					+ "  or  asg.mapping_type IN (select  gp.value  "
							+ " from general_parameter gp where  gp.description  like :search )  "
					
					//+ " or  asg.audit_group_code in ( select asg.audit_group_code from maint_audit_group asg where asg.audit_group_name like :search ) "
					
					+ ") ");
		}

		String[] columns={
				"audit_type_code",
				"mapping_type",
				"id",
				"audit_type_code",//"asg.audit_group_code",
				"audit_type_code",//"asg.audit_sub_group_code",
				"maker",
				//"asg.entity_status",
				"status"};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(status_sort,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						MaintEntityAuditSubgroupMapping maintEntASGMapping=null;
						if(tuple!=null){
							maintEntASGMapping=new MaintEntityAuditSubgroupMapping();
							
							if(tuple[0]!=null)	maintEntASGMapping.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							if(tuple[1]!=null)	maintEntASGMapping.setId(tuple[1].toString());
							if(tuple[2]!=null)	maintEntASGMapping.setMappingType(tuple[2].toString());
							if(tuple[3]!=null)	maintEntASGMapping.setAuditTypeCode(tuple[3].toString());
							
							//if(tuple[4]!=null) 	maintEntASGMapping.setAuditGroupCode(tuple[2].toString());
							//if(tuple[5]!=null) 	maintAuditSubgroup.setAuditSubGroupCode(tuple[3].toString());
							
							if(tuple[4]!=null)	maintEntASGMapping.setStatus(tuple[4].toString());
							if(tuple[5]!=null) 	maintEntASGMapping.setMaker(tuple[5].toString());
							if(tuple[6]!=null) 	maintEntASGMapping.setAuthUniqueId(tuple[6].toString());
							
						}
						return maintEntASGMapping;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				}).setParameter("legal_entity_code", legalEntityCode);
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			maintEntityAuditSubgroupMappings= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintEntityAuditSubgroupMappings.add((MaintEntityAuditSubgroupMapping) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintEntityAuditSubgroupMappings);
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
	public void deleteMaintEntityAuditSubgroupMappingWrkByIds(Integer legalEntityCode, String mappingType, String id, String auditTypeCode, String authUniqueId ){
		
		Session session=getSession();
		
		StringBuffer str =new StringBuffer("delete from MaintEntityAuditSubgroupMappingWrk where ");
				if(!BankAuditUtil.isEmptyString(authUniqueId)){
					str.append(" authUniqueId =:authUniqueId");
				}else{
					str.append(" legalEntityCode =:legalEntityCode "
							+ " and mappingType =:mappingType  "
							+ " and id =:id  "
							+ " and auditTypeCode =:auditTypeCode  ");
				}
		
		Query qry = session.createQuery(str.toString());
		if(!BankAuditUtil.isEmptyString(authUniqueId)){
			qry.setParameter("authUniqueId", authUniqueId);
		}
		else{
			 qry.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("mappingType", mappingType)
				.setParameter("id", id)
				.setParameter("auditTypeCode", auditTypeCode);
		}		
		qry.executeUpdate();
		
	}
	
	 

	@SuppressWarnings("deprecation")
	@Override
	public void deleteMaintEntityAuditSubgroupMappingByIds(Integer legalEntityCode, String mappingType, String id, String auditTypeCode, String authUniqueId  ) {


		Session session=getSession();
		
		StringBuffer str =new StringBuffer("delete from MaintEntityAuditSubgroupMapping where ");
		if(!BankAuditUtil.isEmptyString(authUniqueId)){
			str.append(" authUniqueId =:authUniqueId");
		}else{
			str.append(" legalEntityCode =:legalEntityCode "
					+ " and mappingType =:mappingType  "
					+ " and id =:id  "
					+ " and auditTypeCode =:auditTypeCode  ");
		}
			
		Query qry = session.createQuery(str.toString());
		if(!BankAuditUtil.isEmptyString(authUniqueId)){
			qry.setParameter("authUniqueId", authUniqueId);
		}
		else{
			 qry.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("mappingType", mappingType)
				.setParameter("id", id)
				.setParameter("auditTypeCode", auditTypeCode);
		}
		qry.executeUpdate();
	}

}
