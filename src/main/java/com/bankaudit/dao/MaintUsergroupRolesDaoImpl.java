package com.bankaudit.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.bankaudit.model.MaintUsergroupRoles;
import com.bankaudit.model.User;
import com.bankaudit.model.UserRoleMapping;
import com.bankaudit.model.UserRoleMappingWrk;
import com.bankaudit.helper.BankAuditUtil;

@Repository("maintUsergroupRolesDao")
public class MaintUsergroupRolesDaoImpl extends AbstractDao implements MaintUsergroupRolesDao {

	static final Logger logger = Logger.getLogger(MaintUsergroupRolesDaoImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserRoleMapping> getMaintUsergroupRolesByLegalEntityCodeAndUserId(Integer legalEntityCode,
			String userId,String status) {
		
		Session session=getSession();
		session.setDefaultReadOnly(true);
		Class className=null;
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
		 className=UserRoleMappingWrk.class;	
		}else{
			className=UserRoleMapping.class;
		}
		
		return session.createQuery("select urm.maintUsergroupRoles as maintUsergroupRoles"
				+ "  from "+className.getSimpleName()+" urm "
				+ " where urm.legalEntityCode =:legalEntityCode "
				+ " and urm.userId =:userId "
				+ " order by urm.userRoleId ")
				.setParameter("userId",userId)
				.setParameter("legalEntityCode", legalEntityCode)
				.setResultTransformer(Transformers.aliasToBean(UserRoleMapping.class))
				.list();
	}

	@Override
	public DataTableResponse getMaintUsergroupRoles(Integer legalEntityCode,String userId, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		
		
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintUsergroupRoles> users = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		
		StringBuilder queryString=new StringBuilder(
				           " SELECT "
						 + " legal_entity_code, "
						 + " ug_role_code, "
						 + " ug_role_Name, "
						 + " ug_role_desc, "
						 + " auth_rej_remarks, "
						
						 + "(select concat(gp.value , '-' , gp.description)"
						 + " from general_parameter gp where "
						 + " gp.key_1='ENTITY_STATUS' and gp.value=ur.entity_status "
						 + " and gp.legal_entity_code=ur.legal_entity_code ) as entityStatus , "

						 + "(select concat(gp.value , '-' , gp.description)"
		    			 + " from general_parameter gp where "
		    			 + " gp.key_1='STATUS' and gp.value=ur.status "
		    			 + " and gp.legal_entity_code=ur.legal_entity_code ) as status , "
					    
						 + "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						 + " from user u where "
						 + " u.user_id=ur.maker "
						 + " and u.legal_entity_code=ur.legal_entity_code ) as maker ,  "
						 
						 + " maker_timestamp,      "
						 + " checker,         "
						 + " checker_timestamp     "
						 + "  "
						 + " FROM ( "
						 + " SELECT * FROM maint_usergroup_roles "
						 + " where legal_entity_code =:legal_entity_code "
						 + " AND ug_role_code != 'SADM' " //not to provide/select Super Admin Role for the Client, It is used only for internal BAAS purpose
						 + " UNION ALL "
						 + " SELECT * FROM maint_usergroup_roles_wrk "
					     + " where legal_entity_code =:legal_entity_code "
					     + " and (status !='DF' OR ( status ='DF' and maker =:user_id ) )  "
						 + " ) AS ur "
						 + " where legal_entity_code =:legal_entity_code "
						 //+ " order by ug_role_Name "
				);
		
		
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( ur.ug_role_code like :search "
					+ " or  ur.ug_role_name like :search  "
					+ " or  ur.ug_role_desc like :search  "
					+ " or  ur.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' AND description LIKE :search ) "
					+ " or  ur.status like :search  "
					+ " or  ur.status IN (SELECT value FROM general_parameter  WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  ur.maker IN ( "
					+ " select u.user_id "
						 + " from user u where "
						 + " (u.first_name like :search or u.last_name like :search ) "
						 + " and u.legal_entity_code=ur.legal_entity_code ) "
					+ " ) ");
		}

		String[] columns={
				"ur.ug_role_code",
				"ur.ug_role_name",
				"ur.ug_role_desc",
				"ur.maker",
				"ur.entity_status",
				"ur.status"};
		
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
						
						MaintUsergroupRoles maintUsergroupRoles=null;
								if(tuple!=null){
									maintUsergroupRoles=new MaintUsergroupRoles();
									
									if(tuple[0]!=null){
										maintUsergroupRoles.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
									}
							
									if(tuple[1]!=null){
										maintUsergroupRoles.setUgRoleCode(tuple[1].toString());
									}
									
									if(tuple[2]!=null){
										maintUsergroupRoles.setUgRoleName(tuple[2].toString());
									}
							
									if(tuple[3]!=null){
										maintUsergroupRoles.setUgRoleDesc(tuple[3].toString());
									}
									if(tuple[4]!=null){
										maintUsergroupRoles.setAuthRejRemarks(tuple[4].toString());
									}
							
									if(tuple[5]!=null){
										maintUsergroupRoles.setEntityStatus(tuple[5].toString());
									}
									if(tuple[6]!=null){
										maintUsergroupRoles.setStatus(tuple[6].toString());
									}
							
									if(tuple[7]!=null){
										maintUsergroupRoles.setMaker(tuple[7].toString());
									}
									if(tuple[8]!=null){
										maintUsergroupRoles.setMakerTimestamp((Date)tuple[8]);
									}
							
									if(tuple[9]!=null){
										maintUsergroupRoles.setChecker(tuple[9].toString());
									}
									if(tuple[10]!=null){
										maintUsergroupRoles.setCheckerTimestamp((Date)tuple[10]);
									}
									
								}
						
						
						return maintUsergroupRoles;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legal_entity_code", legalEntityCode)
				.setParameter("user_id", userId);
		
	
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			users= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				users.add((MaintUsergroupRoles) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(users);
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
	public void deleteRolesByUser(Integer legalEntityCode, String userId) {
		
		Session session=getSession();
		
		session.createQuery("delete from UserRoleMapping where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and userId =:userId "
				)
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("userId", userId).executeUpdate();
	}

	@Override
	public void deleteMaintUsergroupRoles(Integer legalEntityCode, String ugRoleCode,String status) {
		
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			getSession().createQuery("delete from MaintUsergroupRolesWrk "
					+ " where legalEntityCode =:legalEntityCode  "
					+ " and ugRoleCode =:ugRoleCode")
			.setParameter("ugRoleCode", ugRoleCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}else {
			
			getSession().createQuery("delete from MaintUsergroupRolesWrk "
					+ " where legalEntityCode =:legalEntityCode  "
					+ " and ugRoleCode =:ugRoleCode "
					+ " and entityStatus='A' ")
			.setParameter("ugRoleCode", ugRoleCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			
		}
		
	}

	@Override
	public void deleteUserRoleMapping(Integer legalEntityCode, String userId, String status) {
		
		Session session=getSession();
			
		if(!BankAuditConstant.STATUS_AUTH.equals(status)){
			session.createQuery("delete from UserRoleMappingWrk where legalEntityCode =:legalEntityCode  and "
					+ " userId =:userId ").setParameter("userId", userId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			
		}else {

			session.createQuery("delete from UserRoleMapping where legalEntityCode =:legalEntityCode  and "
					+ " userId =:userId ").setParameter("userId", userId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}
	}
	
	@Override
	public List<Map<String,Integer>> getTotalUsersPerRole(Integer legalEntityCode) {
		
		Session session=getSession();
	    session.setDefaultReadOnly(true);
	    List<Map<String, Integer>> userRoleList = null;
	    Map<String, Integer> userRoleMap = null;
		try {
			logger.info("getTotalUsersPerRole .. legalEntityCode:: "+ legalEntityCode);
			StringBuilder str = new StringBuilder(" "
					+ "	SELECT r.ug_role_code,r.ug_role_name,COUNT(m.user_role_id) "
					+ " 	FROM maint_usergroup_roles r LEFT JOIN " 
					+ " 		 user_role_mapping m ON "
					+ "			 	r.legal_entity_code=m.legal_entity_code  AND r.ug_role_code=m.user_role_id "
					+ "  	WHERE r.legal_entity_code=:legalEntityCode "
					+ " GROUP BY r.ug_role_code ORDER BY r.ug_role_name ");
					
				Query hqlQuery = session.createNativeQuery(str.toString())
						.setParameter("legalEntityCode", legalEntityCode);
							
				List lst=hqlQuery.list();
				userRoleList = new ArrayList();
				for ( Iterator iter = lst.iterator(); iter.hasNext(); ) {
					userRoleMap = new HashMap();
					Object[] usrObj= (Object[]) iter.next();
					userRoleMap.put(usrObj[1].toString(), Integer.parseInt(usrObj[2].toString()));
					userRoleList.add(userRoleMap);
				}
				logger.info("User Role details count .. "+ userRoleList);

		}catch (Exception e) {
			e.getStackTrace();
			throw e;
		}
		return  userRoleList;
	}


}
