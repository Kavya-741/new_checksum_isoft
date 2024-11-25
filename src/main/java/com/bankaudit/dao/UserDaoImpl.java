package com.bankaudit.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.GeneralParameter;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;
import com.bankaudit.model.User;
import com.bankaudit.model.UserWrk;
import com.bankaudit.helper.BankAuditUtil;

@Repository
public class UserDaoImpl extends AbstractDao implements UserDao {
	
	@Autowired(required = true)
	GeneralParameterDao generalParameterDao;
	
	static final Logger logger = Logger.getLogger(UserDaoImpl.class);
	
	@Override
	public void updateUserLoginTimestamp(Integer legalEntityCode, String userId) {
		
		logger.info("Inside updateUserLoginTimestamp .. "+ legalEntityCode+" :: "+userId);
		Session session=getSession();
		Date currentLogin=new Timestamp(System.currentTimeMillis());
		logger.info("currentLogin .. "+ currentLogin);
		session.createQuery("update User u set "
				+ " u.lastLogin=u.currentLogin , "
				+ " u.currentLogin =:currentLogin, "
				+ " u.unsuccessfulAttempts =0 "
				+ " where u.legalEntityCode =:legalEntityCode "
				+ " and u.userId =:userId" )
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("userId", userId)
		.setParameter("currentLogin", currentLogin).executeUpdate();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataTableResponse getUser(Integer legalEntityCode,String userId, String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size, List<String> unitList) {
		
		
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<User> users = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		logger.info("Inside UserDaoImpl getUser .."+ legalEntityCode +" userId::"+userId);
		logger.info("unitList .."+ unitList );
		try {
			StringBuilder queryString=new StringBuilder(
				
				"SELECT userId,legalEntityCode, "
				
						+ "   (SELECT CONCAT(atd.unit_code, '-', atd.unit_name) " 
						+ "    FROM maint_entity atd " 
						+ "    WHERE atd.unit_code =unitCode " 
						+ "      AND atd.legal_entity_code=legalEntityCode ) AS unitCode, " 
						+ "  " 
						+ "   (SELECT DISTINCT CONCAT(ma.country_alpha3_code, '-',ma.country_name) " 
						+ "    FROM maint_address ma " 
						+ "    WHERE ma.country_alpha3_code=country " 
						+ "      AND ma.legal_entity_code=legalEntityCode ) AS country, " 
						+ "  " 
						+ "   (SELECT DISTINCT CONCAT(ma.state_code, '-',ma.state_name) " 
						+ "    FROM maint_address ma " 
						+ "    WHERE ma.state_code = state " 
						+ "      AND ma.country_alpha3_code=country " 
						+ "      AND ma.legal_entity_code=legalEntityCode ) AS state, " 
						+ "  " 
						+ "   (SELECT DISTINCT CONCAT(ma.district_code, '-',ma.district_name) " 
						+ "    FROM maint_address ma " 
						+ "    WHERE ma.district_code=district " 
						+ "      AND ma.country_alpha3_code=country " 
						+ "      AND ma.state_code = state " 
						+ "      AND ma.legal_entity_code=legalEntityCode ) AS district, " 
						+ "  " 
						+ "   (SELECT DISTINCT CONCAT(ma.city_code, '-',ma.city_name) " 
						+ "    FROM maint_address ma " 
						+ "    WHERE ma.city_code=city " 
						+ "      AND ma.district_code=district " 
						+ "      AND ma.country_alpha3_code=country " 
						+ "      AND ma.state_code=state " 
						+ "      AND ma.legal_entity_code=legalEntityCode ) AS city, " 
						+ "  " 
						+ "   (SELECT CONCAT(gp.value, '-', gp.description) " 
						+ "    FROM general_parameter gp " 
						+ "    WHERE gp.key_1='STATUS' " 
						+ "      AND gp.value=status " 
						+ "      AND gp.legal_entity_code=legalEntityCode ) AS status, " 
						+ "  " 
						+ "   (SELECT CONCAT(gp.value, '-', gp.description) " 
						+ "    FROM general_parameter gp " 
						//+ "    WHERE gp.key_1='ENTITY_STATUS' " 
						+ "      WHERE gp.key_1='USER_ENTITY_STATUS' " 
						+ "      AND gp.value=entityStatus " 
						+ "      AND gp.legal_entity_code=legalEntityCode ) AS entityStatus, "
						
						
						+ " emailId,password,"
						+" createdDate,currentLogin,lastLogin,gender,salutation,"
						+" firstName,middleName,lastName,shortName,address1,"
						+" address2,address3,pincode,landline1,landline2,mobile1,"
						+" mobile2, "
				
						
              	+ "(select CONCAT(u.user_id , '-' ,u.first_name,' ',u.last_name )"
				+ " from user u where "
				+ " u.user_id=temp.maker "
				+ " and u.legal_entity_code=temp.legalEntityCode ) as maker ,  "
						
						+ " makerTimestamp,checker,checkerTimestamp,"
						+" authRejRemarks "
						+" ,roles "
						+" FROM("
				
				+" SELECT user_id AS userId, "                                         // legal_entity_code      
				+ "        legal_entity_code AS legalEntityCode, "                     // USer_id                
				+ "        unit_code  AS unitCode, "                                   // EMAil_id               
				+ "        country AS country, "                                       // PASSWOrD               
				+ "        state  AS state, "                                          // CREATEd_date           
				+ "        district  AS district, "                                    // current_login          
				+ "        city  AS city, "                                            // last_login             
				+ "        status AS status, "                                         // uNSuccessful_attemPTS  
				+ "        entity_status AS entityStatus, "                            // gender                 
				+ "        email_id AS emailId, "                                      // first_name             
		        + "        ' ' AS password, "      // middle_name            
				+ "        created_date AS createdDate, "                              // last_name              
				+ "        current_login AS currentLogin, "                            // short_name             
				+ "        last_login AS lastLogin, "                                  // address1               
				+ "        gender AS gender, "                                         // AdDRESS2               
				+ "        salutation AS salutation, "                                 // address3               
				+ "        first_name AS firstName, "                                  // city                   
				+ "        middle_name AS middleName, "                                // pincode                
				+ "        last_name AS lastName, "                                    // distriCT               
				+ "        short_name AS shortName, "                                  // state                  
				+ "        address1 AS address1, "                                     // COuNTRY                
				+ "        address2 AS address2, "                                     // lANDLINE1              
				+ "        address3 AS address3, "                                     // LANDLINe2              
				+ "        pincode AS pincode, "                                       // mobile1                
				+ "        landline1 AS landline1, "                                   // MOBIle2                
				+ "        landline2 AS landline2, "                                   // aUTH_REj_REMARkS       
				+ "        mobile1 AS mobile1, "                                       // ENTITY_status          
				+ "        mobile2 AS mobile2, "                                       // status                 
				+ "        maker AS maker, "                                           // MAKer                  
				+ "        maker_timestamp AS makerTimestamp, "                        // maKEr_timestamp        
				+ "        checker AS checker, "                                       // CHECKER                
				+ "        checker_timestamp AS checkerTimestamp, "                    // ChECKER_tIMESTAmP      
				+ "        auth_rej_remarks as authRejRemarks "                           // sysdate_timesTAMP   
				//+ " 	   (SELECT GROUP_CONCAT(concat(m.user_role_id,'-',r.ug_role_name)) FROM user_role_mapping m,maint_usergroup_roles r  "
				+ " 	   ,(SELECT GROUP_CONCAT(r.ug_role_name) FROM user_role_mapping m,maint_usergroup_roles r  "
				+ "				WHERE m.legal_entity_code=u.legal_entity_code AND m.user_id=u.user_id AND m.legal_entity_code=r.legal_entity_code AND m.user_role_id=r.ug_role_code "
				+ "				GROUP BY m.user_id) as roles "							// Roles - to get the Roles for each User separted by Comma
				+ " FROM user u " 
				+ " WHERE u.legal_entity_code =:legalEntityCode AND u.unit_code in (:unitList) " 
				+ " UNION ALL " 
				+ " SELECT user_id AS userId, " 
				+ "        legal_entity_code AS legalEntityCode , "                      
				+ "        unit_code  AS unitCode, " 
				+ "        country AS country, " 
				+ "        state  AS state, " 
				+ "        district  AS district, " 
				+ "        city  AS city, " 
				+ "        status AS status, " 
				+ "        entity_status AS entityStatus, " 
				+ "        email_id AS emailId, " 
				+ "        ' ' AS password, " 
				+ "        created_date AS createdDate, " 
				+ "        current_login AS currentLogin, " 
				+ "        last_login AS lastLogin, " 
				+ "        gender AS gender, " 
				+ "        salutation AS salutation, " 
				+ "        first_name AS firstName, " 
				+ "        middle_name AS middleName, " 
				+ "        last_name AS lastName, " 
				+ "        short_name AS shortName, " 
				+ "        address1 AS address1, " 
				+ "        address2 AS address2, " 
				+ "        address3 AS address3, " 
				+ "        pincode AS pincode, " 
				+ "        landline1 AS landline1, " 
				+ "        landline2 AS landline2, " 
				+ "        mobile1 AS mobile1, " 
				+ "        mobile2 AS mobile2, " 
				+ "        maker AS maker, " 
				+ "        maker_timestamp AS makerTimestamp, " 
				+ "        checker AS checker, " 
				+ "        checker_timestamp AS checkerTimestamp " 
				+ "        auth_rej_remarks as authRejRemarks " 
				//+ "		   ,(SELECT GROUP_CONCAT(concat(m.user_role_id,'-',r.ug_role_name)) FROM user_role_mapping_wrk m,maint_usergroup_roles r "
				+ "		   ,(SELECT GROUP_CONCAT(r.ug_role_name) FROM user_role_mapping_wrk m,maint_usergroup_roles r "
				+ "			 WHERE m.legal_entity_code=stg_u.legal_entity_code and m.user_id=stg_u.user_id and m.legal_entity_code=r.legal_entity_code and m.user_role_id=r.ug_role_code "
				+ "			 GROUP BY m.user_id) as roles "
				+ " FROM user_wrk stg_u " 
				+ " WHERE stg_u.legal_entity_code =:legalEntityCode AND stg_u.unit_code in (:unitList)" 
				+ " and (status !='DF' OR ( status ='DF' and maker =:userId ) )  "			
				+ " ) as temp where legalEntityCode =:legalEntityCode " );
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( userId like :search "
					+ " or unitCode like :search  "
					+ " or unitCode In ( select atd.unit_code from maint_entity atd where atd.legal_entity_code=legal_entity_code and atd.unit_code=unitCode and atd.unit_name LIKE :search )" // Code commented as resulting into extra records
					+ " or concat(firstName , ' ', middleName ,' ' , lastName) like :search "
					+ " or concat(firstName ,' ' , lastName) like :search "
					+ " or firstName  like :search "
					+ " or middleName  like :search "
					+ " or lastName  like :search "
					
					+ " or mobile1 like :search "
					+ " or emailId like :search "
					+ " or userId IN (select distinct m.user_id from user_role_mapping m,maint_usergroup_roles r "
					+ "					WHERE m.legal_entity_code=r.legal_entity_code and m.user_role_id =r.ug_role_code "
					+ "					and m.legal_entity_code=temp.legalEntityCode and r.ug_role_name LIKE :search) "
					
					+ " or maker like :search "
					+ " or temp.maker IN (select u.user_id from user u where (concat(u.first_name,' ',u.last_name) like :search ) and u.legal_entity_code=temp.legalEntityCode) "
					+ " or status like :search "
					+ " or status IN (SELECT value FROM general_parameter  WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or entityStatus IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' AND description LIKE :search ) "
					
					/*+ " or  country IN (SELECT country_alpha3_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_name LIKE :search )"
					+ " or  state IN (SELECT state_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_alpha3_code =country AND state_name LIKE :search ) "
					+ " or  district IN (SELECT district_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_alpha3_code =country"
					+ "  				AND state_code =state AND district_name LIKE :search ) "
					+ " or  city IN (SELECT city_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_alpha3_code =country "
					+ "  				AND state_code =state AND district_code =district AND city_name LIKE :search ) "*/
					+ " ) ");
		}

		String[] columns={
				"userId",
				"unitCode",
				"firstName",
				"mobile1",
				"emailId",
				"roles",
				"maker",
				"entityStatus",
				"status"
				};
		
		
/*
		FIELD(STATUS,'DF') DESC ,*/
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by   ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}else {
			queryString=queryString.append(" order by FIELD(STATUS,'DF') DESC");
		}
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						User user =new User();
						
						if(tuple[0]!=null){user.setUserId(tuple[0].toString());}
						if(tuple[1]!=null){user.setLegalEntityCode(Integer.parseInt(tuple[1].toString()));}
						if(tuple[2]!=null){user.setUnitCode(tuple[2].toString());}
						if(tuple[3]!=null){user.setCountry(tuple[3].toString());}
						if(tuple[4]!=null){user.setState(tuple[4].toString());}
						if(tuple[5]!=null){user.setDistrict(tuple[5].toString());}
						if(tuple[6]!=null){user.setCity(tuple[6].toString());}
						if(tuple[7]!=null){user.setStatus(tuple[7].toString());}
						if(tuple[8]!=null){user.setEntityStatus(tuple[8].toString());}
						if(tuple[9]!=null){user.setEmailId(tuple[9].toString());}
						if(tuple[10]!=null){user.setPassword(tuple[10].toString());}
						if(tuple[11]!=null){user.setCreatedDate((Date)tuple[11]);}
						if(tuple[12]!=null){user.setCurrentLogin((Date)tuple[12]);}
						if(tuple[13]!=null){user.setLastLogin((Date)tuple[13]);}
						if(tuple[14]!=null){user.setGender(tuple[14].toString());}
						if(tuple[15]!=null){user.setSalutation(tuple[15].toString());}
						if(tuple[16]!=null){user.setFirstName(tuple[16].toString());}
						if(tuple[17]!=null){user.setMiddleName(tuple[17].toString());}
						if(tuple[18]!=null){user.setLastName(tuple[18].toString());}
						if(tuple[19]!=null){user.setShortName(tuple[19].toString());}
						if(tuple[20]!=null){user.setAddress1(tuple[20].toString());}
						if(tuple[21]!=null){user.setAddress2(tuple[21].toString());}
						if(tuple[22]!=null){user.setAddress3(tuple[22].toString());}
						if(tuple[23]!=null){user.setPincode(Integer.parseInt(tuple[23].toString()));}
						if(tuple[24]!=null){user.setLandline1(tuple[24].toString());}
						if(tuple[25]!=null){user.setLandline2(tuple[25].toString());}
						if(tuple[26]!=null){user.setMobile1(tuple[26].toString());}
						if(tuple[27]!=null){user.setMobile2(tuple[27].toString());}
						if(tuple[28]!=null){user.setMaker(tuple[28].toString());}
						if(tuple[29]!=null){user.setMakerTimestamp((Date)tuple[29]);}
						if(tuple[30]!=null){user.setChecker(tuple[30].toString());}
						if(tuple[31]!=null){user.setCheckerTimestamp((Date)tuple[31]);}
						if(tuple[32]!=null){user.setAuthRejRemarks(tuple[32].toString());}
						if(tuple[33]!=null){user.setUserRoles(tuple[33].toString());}
						return user;
					}
					
					@Override
					public List transformList(List collection) {
						
						return collection;
					}
				})
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("userId", userId)
				.setParameter("unitList", unitList);
		
	
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			users= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				users.add((User) resultScroll.get(0));
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
				dataTableResponse.setData(Collections.emptyList());
				dataTableResponse.setRecordsTotal(0l);
				dataTableResponse.setRecordsFiltered(0l);
			}
			
		}else{
			dataTableResponse.setError(null);
			dataTableResponse.setData(Collections.emptyList());
			dataTableResponse.setRecordsTotal(0l);
			dataTableResponse.setRecordsFiltered(0l);
		}
		}catch(Exception e) {
			e.printStackTrace();
		}return dataTableResponse;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUser(Integer legalEntityCode, String unitCode ,String userId,String status) {

		
		Session session=getSession();
		
		Class className=null;
		
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			className=UserWrk.class;
		}else{
			className=User.class;
		}
		
		StringBuilder queryStr1=new StringBuilder(" select "
				+ " u.legalEntityCode as  legalEntityCode , "
				+ " u.userId as  userId , "
				+ " u.unitCode as  unitCode , "
				+ " u.emailId as   emailId , "
				+ " u.createdDate as   createdDate , "
				+ " u.currentLogin as currentLogin , "
				+ " u.lastLogin as lastLogin , "
				+ " u.typeOfUser as typeOfUser , "
				+ " u.gender as gender , "
				+ " u.salutation as salutation , "
				+ " u.firstName as firstName , "
				+ " u.middleName as middleName , "
				+ " u.lastName as lastName , "
				+ " u.shortName as shortName , "
				+ " u.grade as grade , "
				+ " u.designation as designation , "
				+ " u.department as department , "
				+ " u.address1 as address1 , "
				+ " u.address2 as address2 , "
				+ " u.address3 as address3 , "
				+ " u.city as city , "
				+ " u.pincode as pincode , "
				+ " u.district as district , "
				+ " u.state as state , "
				+ " u.country as country , "
				+ " u.landline1 as landline1 , "
				+ " u.landline2 as landline2 , "
				+ " u.mobile1 as mobile1 , "
				+ " u.mobile2 as mobile2 , "
				+ " u.entityStatus as entityStatus , "
				+ " u.status as status , "
				+ " u.maker as maker , "
				+ " u.makerTimestamp as makerTimestamp , "
				+ " u.checker as checker , "
				+ " u.checkerTimestamp as checkerTimestamp "
				+ " from "+className.getSimpleName()+" u "
				+ " where legalEntityCode =:legalEntityCode ");
			
		
		
		if(!BankAuditUtil.isEmptyString(unitCode)){
			queryStr1=queryStr1.append(" and ( u.unitCode =:unitCode or u.emailId =:unitCode ) ");
		}
		
		if(!BankAuditUtil.isEmptyString(userId)){
			queryStr1=queryStr1.append(" and u.userId =:userId");
		}
		
	    if(className.getSimpleName().equalsIgnoreCase("User")){
	    	// queryStr1=queryStr1.append(" and u.entityStatus ='"+BankAuditConstant.STATUS_ACTIVE+"'"); // Condition commented to modify the Inactive user
		}
		
		Query query=session.createQuery(queryStr1.toString())
				.setParameter("legalEntityCode", legalEntityCode)
				.setResultTransformer(Transformers.aliasToBean(User.class));
		
		if(!BankAuditUtil.isEmptyString(unitCode)){
		
			query.setParameter("unitCode", unitCode);
		}
		
		if(!BankAuditUtil.isEmptyString(userId)){
			
			query.setParameter("userId", userId);
		}
		
		
		return query.list();
		
		
	}

	@Override
	public void deleteUser(Integer legalEntityCode, String userId, String status) {
		
		Session session=getSession();
		
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			
			session.createQuery("delete from UserWrk where "
					+ " legalEntityCode =:legalEntityCode and "
					+ " userId =:userId ").setParameter("userId", userId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			
		}else {
			session.createQuery("delete from User where "
					+ " legalEntityCode =:legalEntityCode and "
					+ " userId =:userId ").setParameter("userId", userId)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}
		
	}

	@Override
	public Boolean isUser(Integer legalEntityCode, String userId) {		

		Session session=getSession();
		Long count=(Long)session.createQuery(
				" select count(*) "
				+ " from User   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and userId =:userId  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("userId", userId).uniqueResult();
		
		Long count1=(Long)session.createQuery(
				" select count(*) "
				+ " from UserWrk   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and userId =:userId  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("userId", userId).uniqueResult();
		Boolean flag;
		if((count1!=null && count1 >0)||(count!=null && count >0)){
			flag= true;
		}else {
			flag= false;
		}
	return flag;
	}

	@Override
	public List<User> getUserByRoleId(Integer legalEntityCode, String userRoleId) {

		
		Session session=getSession();
		session.setDefaultReadOnly(true);
		
		StringBuilder queryStr1=new StringBuilder(
				" SELECT                                        "
				+" u.legal_entity_code,                            "
				+" u.user_id,                                      "
				+" u.unit_code,                                    "
				+" u.email_id,                                     "
				+" u.created_date,                                 "
				+" u.current_login,                                "
				+" u.last_login,                                   "
				+" u.unsuccessful_attempts ,                       "
				+" u.gender,                                       "
				+" u.salutation,                                   "
				+" u.first_name,                                   "
				+" u.middle_name,                                  "
				+" u.last_name,                                    "
				+" u.short_name,                                   "
				+" u.address1,                                     "
				+" u.address2,                                     "
				+" u.address3,                                     "
				+" u.city,                                         "
				+" u.pincode,                                      "
				+" u.district,                                     "
				+" u.state,                                        "
				+" u.country,                                      "
				+" u.landline1,                                    "
				+" u.landline2,                                    "
				+" u.mobile1,                                      "
				+" u.mobile2,                                      "
				+" u.entity_status,                                "
				+" u.auth_rej_remarks,                             "
				+" u.status,                                       "
				+" u.maker,                                        "
				+" u.maker_timestamp,                              "
				+" u.checker,                                      "
				+" u.checker_timestamp                            "
				+" FROM                                          "
				+" user AS u,                                    "
				+" user_role_mapping AS urm                      "
				+" WHERE                                         "
				+" urm.legal_entity_code=:legal_entity_code      "
				+" AND urm.legal_entity_code=u.legal_entity_code "
				+" AND urm.user_id=u.user_id                     "
				+" AND urm.user_role_id=:user_role_id            "
				+" AND u.entity_status='A'                       "
				);                                               
			
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryStr1.toString())
				.setParameter("legal_entity_code", legalEntityCode)
				.setParameter("user_role_id", userRoleId)
				.setResultTransformer(new ResultTransformer() {
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						User user=null;
						if(tuple!=null){
							user=new User();
							if(tuple[0]!=null){ user.setLegalEntityCode(Integer.parseInt(tuple[0].toString())); }
							if(tuple[1]!=null){ user.setUserId(tuple[1].toString()); }
							if(tuple[2]!=null){ user.setUnitCode(tuple[2].toString()); }
							if(tuple[3]!=null){ user.setEmailId(tuple[3].toString()); }
							if(tuple[4]!=null){ user.setCreatedDate((Date)tuple[34]); }
							if(tuple[5]!=null){ user.setCurrentLogin((Date)tuple[34]); }
							if(tuple[6]!=null){ user.setLastLogin((Date)tuple[34]); }
							if(tuple[7]!=null){ user.setUnsuccessfulAttempts(Integer.parseInt(tuple[7].toString())); }
							if(tuple[8]!=null){ user.setGender(tuple[8].toString()); }
							if(tuple[9]!=null){ user.setSalutation(tuple[9].toString()); }
							if(tuple[10]!=null){ user.setFirstName(tuple[10].toString()); }
							if(tuple[11]!=null){ user.setMiddleName(tuple[11].toString()); }
							if(tuple[12]!=null){ user.setLastName(tuple[12].toString()); }
							//if(tuple[12]!=null){ user.setLastLogin((Date)tuple[34]); }
							if(tuple[13]!=null){ user.setShortName(tuple[13].toString()); }
							if(tuple[14]!=null){ user.setAddress1(tuple[14].toString()); }
							if(tuple[15]!=null){ user.setAddress2(tuple[15].toString()); }
							if(tuple[16]!=null){ user.setAddress3(tuple[16].toString()); }
							if(tuple[17]!=null){ user.setCity(tuple[17].toString()); }
							if(tuple[18]!=null){ user.setPincode(Integer.parseInt(tuple[18].toString())); }
							if(tuple[19]!=null){ user.setDistrict(tuple[19].toString()); }
							if(tuple[20]!=null){ user.setState(tuple[20].toString()); }
							if(tuple[21]!=null){ user.setCountry(tuple[21].toString()); }
							if(tuple[22]!=null){ user.setLandline1(tuple[22].toString()); }
							if(tuple[23]!=null){ user.setLandline2(tuple[23].toString()); }
							if(tuple[24]!=null){ user.setMobile1(tuple[24].toString()); }
							if(tuple[25]!=null){ user.setMobile2(tuple[25].toString()); }
							if(tuple[26]!=null){ user.setEntityStatus(tuple[26].toString()); }
							if(tuple[27]!=null){ user.setAuthRejRemarks(tuple[27].toString()); }
							if(tuple[28]!=null){ user.setStatus(tuple[28].toString()); }
							if(tuple[29]!=null){ user.setMaker(tuple[29].toString()); }
							if(tuple[30]!=null){ user.setMaker(tuple[30].toString()); }
							if(tuple[31]!=null){ user.setChecker(tuple[31].toString()); }
							if(tuple[32]!=null){ user.setChecker(tuple[32].toString()); }
						}
						return user;
					}
					
					@Override
					public List transformList(List collection) {
						return collection;
					}
				});
		
		
		return query.list();
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUploadTables(Integer legalEntityCode) {

		Session session=getSession();
		
		return session.createQuery("select aum.model from DataUploadModel aum "
				+ " where aum.legalEntityCode =:legalEntityCode ")
		.setParameter("legalEntityCode", legalEntityCode).list();

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAllActiveUsers(Integer legalEntityCode, String unitCode, String status) {

		logger.info("Inside getAllActiveUsers .. "+legalEntityCode +" unitCode.."+unitCode +" status.."+status );
		Session session=getSession();
		
		if(status != null && "".equals(status.trim()));
		else status="A";
		if(!BankAuditUtil.isEmptyString(unitCode)) {
			return session.createQuery(" from User "
					+ " where legalEntityCode =:legalEntityCode AND unitCode=:unitCode"
					+ " AND status =:status "
					+ " AND  entityStatus=:entityStatus " // new condition added
					+ " ORDER BY firstName,lastName ")
			.setParameter("legalEntityCode", legalEntityCode).setParameter("unitCode", unitCode)
			.setParameter("status", status)
			.setParameter("entityStatus", "A").list(); // new condition added
		}else {
			return session.createQuery(" from User "
					+ " where legalEntityCode =:legalEntityCode "
					+ " AND status =:status "
					+ " AND  entityStatus=:entityStatus " // new condition added
					+ " ORDER BY firstName,lastName ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("status", status)
			.setParameter("entityStatus", "A").list(); // new condition added
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean validateUserWithIdandMobile(Integer legalEntityCode, String userId, String mobile1, String emailId, String status) {

		logger.info("Inside validateUserWithIdandMobile .. "+legalEntityCode +" userId.."+userId +" mobile1.."+mobile1 );
		Session session=getSession();
		Boolean validUsr = false;
		try {
			if(status != null && "".equals(status.trim()));
			else status="A";
			List lst= session.createQuery(" from User "
					+ " where legalEntityCode =:legalEntityCode "
					+ " AND userId =:userId "
					+ " AND mobile1 =:mobile1 ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("userId", userId)
			.setParameter("mobile1", mobile1).list();
			if(lst.size() > 0) validUsr=true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return validUsr;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String validateUserOrgetUserStatus(Integer legalEntityCode, String userId, String status) {

		logger.info("Inside validateUserOrgetUserStatus .. "+legalEntityCode +" userId.."+userId +" status.."+status );
		Session session=getSession();
		String userStatus = null;
		try {
			userStatus= (String)session.createQuery("select entityStatus from User u"
					+ " where legalEntityCode =:legalEntityCode "
					+ " AND userId =:userId ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("userId", userId).uniqueResult();
			logger.info(" userStatus .. "+ userStatus);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return userStatus;
	}
	@SuppressWarnings("unchecked")
	@Override
	public String updateUserPassword(Integer legalEntityCode, String userId, String pwd) {

		logger.info("Inside updateUserPassword .. "+legalEntityCode +" userId.."+userId +" pwd.."+pwd );
		Session session=getSession();
		String updated = null;
		try {
			PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
			logger.info("pwd encode .."+passwordEncoder.encode(pwd));
			int updateCnt= session.createQuery(" UPDATE User set password=:passowrd "
					+ " , unsuccessfulAttempts = 0 "
					+ " where legalEntityCode =:legalEntityCode "
					+ " AND userId =:userId ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("userId", userId)
			.setParameter("passowrd", passwordEncoder.encode(pwd)).executeUpdate();
			if(updateCnt > 0) updated="SUCCESS";
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return updated;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getUnitIdforUser(Integer legalEntityCode, String userId) {

		logger.info("Inside getUnitIdforUser .. "+legalEntityCode +" userId.."+userId );
		Session session=getSession();
		String unitCode=null;
		try {
			unitCode= (String)session.createQuery("select unitCode from User "
					+ " where legalEntityCode =:legalEntityCode "
					+ " AND userId =:userId ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("userId", userId).uniqueResult();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return unitCode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getUnitLevelforUser(Integer legalEntityCode, String userId) {

		logger.info("Inside getUnitLevelforUser .. "+legalEntityCode +" userId.."+userId );
		Session session=getSession();
		String unitCode=null;
		try {
			unitCode= (String)session.createQuery("select e.levelCode from User u, MaintEntity e "
					+ " where u.unitCode=e.unitCode "
					+ " AND u.legalEntityCode =e.legalEntityCode "
					+ " AND u.legalEntityCode =:legalEntityCode AND u.userId =:userId ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("userId", userId).uniqueResult();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return unitCode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int getUnsuccessfullAttempts(Integer legalEntityCode, String userId) {

		logger.info("Inside getUnsuccessfullAttempts .. "+legalEntityCode +" userId.."+userId );
		Session session=getSession();
		int unSuccessfulAttempt=0;
		try {
			 
			BigInteger attempt =  (BigInteger) session.createSQLQuery("select ifnull(unsuccessful_attempts, 0) from user "
					+ " where legal_entity_code =:legalEntityCode "
					+ " AND user_id =:userId ")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("userId", userId).uniqueResult();
			 
			unSuccessfulAttempt = attempt==null?0:attempt.intValue(); 
			logger.info("unSuccessfulAttempt .. "+unSuccessfulAttempt);
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		return unSuccessfulAttempt;
	}
	
	@Override
	public void updateUnsuccessfullAttempts(Integer legalEntityCode, String userId) {
		
		Session session=getSession();
		int unSuccessfulAttempt=0;
		try {
			List<GeneralParameter> generalParameters=generalParameterDao.getGeneralParameter( legalEntityCode,  null,  null,
					 "UNSUCCESSFUL_ATTEMPT",  "LOCK",  null,  null);			
			if(!generalParameters.isEmpty()) unSuccessfulAttempt= Integer.parseInt(generalParameters.get(0).getValue().toString());
		}catch(Exception e) {
			unSuccessfulAttempt=10;
			e.printStackTrace();
		}
		logger.info("Inside updateUnsuccessfullAttempts .. "+legalEntityCode +" userId.."+userId );
		session.createQuery("update User u set "
				+ " u.unsuccessfulAttempts= ifnull(unsuccessfulAttempts,0) + 1 "
				+ " , u.entityStatus = CASE WHEN ifnull(unsuccessfulAttempts,0) >=:unSuccessfulAttempt  THEN 'L' ELSE 'A' END "
				+ " where u.legalEntityCode =:legalEntityCode "
				+ " and u.userId =:userId" )
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("userId", userId)
		.setParameter("unSuccessfulAttempt", unSuccessfulAttempt).executeUpdate();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersByLevel(Integer legalEntityCode, String levelCode, String status) {

		logger.info("Inside getUsersByLevel .. "+legalEntityCode +" levelCode.."+levelCode +" status.."+status );
		Session session=getSession();
		return session.createQuery("select u from User u, MaintEntity e"
					+ " where  u.legalEntityCode=e.legalEntityCode AND e.unitCode=u.unitCode "
					+ "  AND u.legalEntityCode =:legalEntityCode AND e.levelCode=:levelCode "
					+ " AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' "
					+ " ORDER BY u.firstName,u.lastName ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("levelCode", levelCode).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersByLevelAndNotInGrpEntityMapping(Integer legalEntityCode, String levelCode, String status) {

		logger.info("Inside getUsersByLevelAndNotInGrpEntityMapping .. "+legalEntityCode +" levelCode.."+levelCode +" status.."+status );
		Session session=getSession();
		return session.createQuery("select u from User u, MaintEntity e"
					+ " where  u.legalEntityCode=e.legalEntityCode AND e.unitCode=u.unitCode "
					+ "  AND u.legalEntityCode =:legalEntityCode AND e.levelCode=:levelCode "
					+ " AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' "
					+ " AND u.userId not in (select id from MaintEntityAuditSubgroupMapping m where m.legalEntityCode=u.legalEntityCode and m.mappingType='U' ) "
					+ " AND u.userId not in (select id from MaintEntityAuditSubgroupMappingWrk m where m.legalEntityCode=u.legalEntityCode and m.mappingType='U' ) "
					+ " ORDER BY u.firstName,u.lastName ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("levelCode", levelCode).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getReportingUsers(Integer legalEntityCode,String unitCode,String userId,String typeOfUser) {

		logger.info("Inside getReportingUsers .. "+legalEntityCode +" unitCode.."+unitCode +" userId.."+userId + " typeOfUser.. "+typeOfUser);
		Session session=getSession();
		return session.createQuery("select distinct u from User u, MaintEntity e, UserRoleMapping um "
					+ " where  u.legalEntityCode=e.legalEntityCode AND (e.parentUnitCode=u.unitCode OR u.unitCode='HO' OR u.unitCode='1')  "
					+ "  AND u.legalEntityCode =:legalEntityCode AND e.unitCode=:unitCode "
					+ "  AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' "
					+ "  AND u.legalEntityCode =um.legalEntityCode AND u.userId=um.userId AND um.status='A' AND um.userRoleId in ('G4','G7')"
					+ " ORDER BY u.firstName,u.lastName ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("unitCode", unitCode).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getReportingUsersFromParent(Integer legalEntityCode,String unitCode,String userId,String typeOfUser) {

		logger.info("Inside getReportingUsersFromParent .. "+legalEntityCode +" unitCode.."+unitCode +" userId.."+userId + " typeOfUser.. "+typeOfUser);
		Session session=getSession();
		return session.createQuery("select distinct u from User u, MaintEntity e, UserRoleMapping um "
					+ " where  u.legalEntityCode=e.legalEntityCode AND (e.parentUnitCode=u.unitCode )  "
					+ "  AND u.legalEntityCode =:legalEntityCode AND e.unitCode=:unitCode "
					+ "  AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' "
					+ "  AND u.legalEntityCode =um.legalEntityCode AND u.userId=um.userId AND um.status='A' AND um.userRoleId in ('G4','G7')"
					+ " ORDER BY u.firstName,u.lastName ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("unitCode", unitCode).list();
	}
	
	
	// This service is yet to complete
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getImmedieateReportingUsers(Integer legalEntityCode,String unitCode,String userId,String typeOfUser, Boolean onlyParent, Boolean inclusionHO) {

		logger.info("Inside getReportingUsersFromParent .. "+legalEntityCode +" unitCode.."+unitCode +" userId.."+userId + ":: "+typeOfUser +" :: "+onlyParent+" :: "+ inclusionHO);
		Session session=getSession();
		List<String> roles= new ArrayList<String>();
		return session.createQuery("select distinct u from User u, MaintEntity e, UserRoleMapping um "
					+ " where  u.legalEntityCode=e.legalEntityCode AND (e.parentUnitCode=u.unitCode )  "
					+ "  AND u.legalEntityCode =:legalEntityCode AND e.unitCode=:unitCode "
					+ "  AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' "
					+ "  AND u.legalEntityCode =um.legalEntityCode AND u.userId=um.userId AND um.status='A' AND um.userRoleId in ('G4','G7')"
					+ " ORDER BY u.firstName,u.lastName ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("unitCode", unitCode).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getReportReviewer(Integer legalEntityCode,String unitCode, Boolean onlyParent, Boolean inclusionHO) {

		logger.info("Inside getReportingUsersFromParent .. "+legalEntityCode +" unitCode.."+unitCode +" :: "+onlyParent+" :: "+ inclusionHO);
		Session session=getSession();
		List<String> levelCodes= new ArrayList<String>();
		if(inclusionHO) levelCodes.add("1");
		if(!onlyParent) levelCodes.add("2"); 
		
		return session.createQuery("select distinct u from User u, MaintEntity e, UserRoleMapping um "
					+ " where  u.legalEntityCode=e.legalEntityCode AND (e.parentUnitCode=u.unitCode  OR e.levelCode in (:levelCodes)) "
					+ "  AND e.levelCode not in ('3','4') AND u.legalEntityCode =:legalEntityCode  "
					+ "  AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' "
					+ "  AND u.legalEntityCode =um.legalEntityCode AND u.userId=um.userId AND um.status='A' AND um.userRoleId in ('G12')"
					+ " ORDER BY u.firstName,u.lastName ")
				.setParameter("legalEntityCode", legalEntityCode).setParameter("levelCodes", levelCodes).list(); //.setParameter("unitCode", unitCode)
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getReportReviewerApprover(Integer legalEntityCode,String unitCode, Boolean onlyRO, Boolean onlyHO) {

		logger.info("Inside getReportReviewerApprover .. "+legalEntityCode +" unitCode.."+unitCode +" :: "+onlyRO+" :: "+ onlyHO);
		Session session=getSession();
		List<String> levelCodes= new ArrayList<String>();
		
		StringBuilder queryString=new StringBuilder("SELECT DISTINCT u FROM User u, MaintEntity e, UserRoleMapping um WHERE u.legalEntityCode=e.legalEntityCode ");
			//	+ " where  u.legalEntityCode=e.legalEntityCode AND e.levelCode not in ('3','4') ");
		
		if(onlyRO) queryString.append(" AND e.parentUnitCode=u.unitCode AND e.unitCode=:unitCode ");
		else if(onlyHO) queryString.append(" AND u.unitCode='HO' AND u.unitCode=e.unitCode ");
		else queryString.append("  AND ((e.parentUnitCode=u.unitCode AND e.unitCode=:unitCode) OR (u.unitCode='HO' AND u.unitCode=e.unitCode) ) ");
		
		queryString.append(" AND u.legalEntityCode =:legalEntityCode AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' "
				+ "  AND u.legalEntityCode =um.legalEntityCode AND u.userId=um.userId AND um.status='A' AND um.userRoleId in ('G12')"
				+ " ORDER BY u.firstName,u.lastName ");
		
		Query qry=session.createQuery(queryString.toString()).setParameter("legalEntityCode", legalEntityCode);
		if(onlyRO || (!onlyRO && !onlyHO)) {
			qry.setParameter("unitCode", unitCode);
		}
		return qry.list();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getFYEOUser(Integer legalEntityCode,String unitCode, Boolean onlyParent, Boolean inclusionHO) {

		logger.info("Inside getFYEOUser .. "+legalEntityCode +" unitCode.."+unitCode +" :: "+onlyParent+" :: "+ inclusionHO);
		Session session=getSession();
		List<String> levelCodes= new ArrayList<String>();
		if(inclusionHO) levelCodes.add("1");
		if(!onlyParent) levelCodes.add("2"); 
		
		return session.createQuery("select distinct u from User u, MaintEntity e, UserRoleMapping um "
				+ " where  u.legalEntityCode=e.legalEntityCode "
				+ "   AND (e.parentUnitCode=u.unitCode  OR u.unitCode='HO' ) "
				+ "   AND u.legalEntityCode =:legalEntityCode  "  // AND e.levelCode not in ('3','4')
				+ "   AND u.status ='A' AND u.entityStatus='A' AND e.entityStatus='A' AND e.unitCode=:unitCode "
				+ "   AND u.legalEntityCode =um.legalEntityCode AND u.userId=um.userId AND um.status='A' AND um.userRoleId in ('G91')"
				+ " ORDER BY u.firstName,u.lastName ")
			.setParameter("legalEntityCode", legalEntityCode).setParameter("unitCode", unitCode).list(); //.setParameter("unitCode", unitCode)
	}
	
	@Override
	public String checkUserSession(Integer legalEntityCode, String userId){
		logger.info("Inside checkUserSession .. "+legalEntityCode +" userId.."+userId);
		Session session=getSession();
		 
		String queryStr = "select user_name from oauth_access_token where "
				+ " user_name=:userName ";		
		Object isLogedUser =  session.createNativeQuery(queryStr)
					.setParameter("userName", userId).uniqueResult();		
		return isLogedUser==null?null:"Yes";
		
	}
	
	@Override
	public String getUserPassword(Integer legalEntityCode, String userId) {
		
		Session session=getSession();
		logger.info("Inside getUserPassword .. "+legalEntityCode +" userId.."+userId );
		return (String)session.createQuery(" select password from User u "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and userId =:userId  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("userId", userId).uniqueResult();
		
	}
	
	@Override
	public Map<String, Integer> getUserGrades(int legalEntityCode, List<String> userIds)  {
		
		Session session=getSession();
	    session.setDefaultReadOnly(true);
	    Map<String, Integer> userGrades = null;
		try {
			logger.info("Inside UserDaoImpl -> getUserGrades .. legalEntityCode:: "+ legalEntityCode);
			logger.info(" userIds .. "+userIds );
			StringBuilder query= new StringBuilder(
						"  SELECT user_id, CASE "
						+ "		WHEN grade='A' THEN '1' 	WHEN grade='B' THEN '2' 	WHEN grade='C' THEN '3' "
						+ "		WHEN grade='D' THEN '4' 	WHEN grade='E' THEN '5' 	WHEN grade='F' THEN '6' "
						+ "		WHEN grade='G' THEN '7' 	WHEN grade='H' THEN '8' 	WHEN grade='I' THEN '9' "
						+ "  ELSE '0' END AS gradeNumber  "
						+ "  FROM user u "
						+ "	WHERE legal_entity_code=:legalEntityCode AND user_id in (:userIds)  ");
										
				Query hqlQuery = session.createNativeQuery(query.toString())
						.setParameter("legalEntityCode", legalEntityCode).setParameter("userIds", userIds);
				
				List lst=hqlQuery.list();
				userGrades = new HashMap();
				//for ( Iterator iter = lst.iterator(); iter.hasNext(); ) {
					//Object[] obji=(Object[])iter.next();
				for ( Object obj: lst ) {
					Object[] obji=(Object[]) obj;
					logger.info("obj .. "+ obji[0].toString());
					userGrades.put(obji[0].toString(), Integer.parseInt(obji[1].toString()));
				}
				logger.info("User Grades for Inspection AIO/PIO comparision .. "+ userGrades);
			
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		return userGrades;
	}
	
	@Override
	public User isFirstLogin(Integer legalEntityCode, String userId){
		Session session=getSession();
		logger.info("Inside isFirstLogin .. "+legalEntityCode +" userId.."+userId );
		return  (User) session.createQuery(" from User  "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and userId =:userId  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("userId", userId).uniqueResult();
	}
	
	@Override
	public Boolean isUserHavingEntitlement(Integer legalEntityCode, String userId, String role, String functionId, String action) {

		logger.info("Inside isUserHavingEntitlement .. "+legalEntityCode +" userId.."+userId +" role.."+role + " functionId.. "+ functionId+" action.. "+ action);
		Session session=getSession();
		Boolean validUsr = true;
		try {
			StringBuilder query= new StringBuilder(" SELECT COUNT(u.userId),  "
					+ " (SELECT value FROM GeneralParameter WHERE key1 = 'ENTITLEMENT' AND key2 = 'APPLICABLE' ) AS entApplicability "
					+ " FROM UserRoleMapping m, User u, MaintRoleEntitlement e "
					+ " WHERE m.legalEntityCode=u.legalEntityCode AND m.userId=u.userId AND u.entityStatus='A' AND  "
					+ " e.legalEntityCode=m.legalEntityCode AND m.userRoleId=e.ugRoleCode AND "
					+ " m.legalEntityCode =:legalEntityCode AND m.userRoleId=:role AND u.userId =:userId AND "
					+ " e.functionId=:functionId AND (e.l1Fname=:action OR e.l2Fname=:action OR e.l3Fname=:action OR e.l4Fname=:action) ");
			
			@SuppressWarnings("deprecation")
			Query hqlQuery = session.createQuery(query.toString()).setParameter("legalEntityCode", legalEntityCode).setParameter("userId", userId)
					.setParameter("role", role).setParameter("functionId", functionId).setParameter("action", action);
			
			@SuppressWarnings("deprecation")
			Object obj = hqlQuery.uniqueResult();
			try{
				if(obj != null  && ((Object[])obj).length > 0)
				{
					int count  =Integer.parseInt( ((Object[])obj)[0].toString());
					String applicable=(((Object[])obj)[1] !=null?((Object[])obj)[1].toString():"N");
					logger.info("count .... "+ count +" Entitlement applicable.... "+applicable);
					if("N".equalsIgnoreCase(applicable) || count >0) validUsr=true;
					else validUsr=false;
				}
			}catch(Exception e) {
				logger.info("Exception occured ...."+e);
			}
			
			logger.info("validUsr is .. "+validUsr);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return validUsr;
	}
 
}
