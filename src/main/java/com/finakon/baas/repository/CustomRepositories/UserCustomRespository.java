package com.finakon.baas.repository.CustomRepositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.User;
import com.finakon.baas.entities.UserWrk;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.helper.BankAuditUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class UserCustomRespository {

	@PersistenceContext
	private EntityManager entityManager;

	static final Logger logger = Logger.getLogger(UserCustomRespository.class);

	@SuppressWarnings("unchecked")
	public DataTableResponse getUser(
			Integer legalEntityCode,
			String userId,
			String search,
			Integer orderColumn,
			String orderDirection,
			Integer page,
			Integer size,
			List<String> unitList) {

		DataTableResponse dataTableResponse = new DataTableResponse();
		List<User> users;

		logger.info("Inside UserDaoImpl getUser .." + legalEntityCode + " userId::" + userId);
		logger.info("unitList .." + unitList);

		try {

			StringBuilder countQueryString = new StringBuilder("select count(*) ");
			// Base query
			StringBuilder queryString = new StringBuilder(

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
							// + " WHERE gp.key_1='ENTITY_STATUS' "
							+ "      WHERE gp.key_1='USER_ENTITY_STATUS' "
							+ "      AND gp.value=entityStatus "
							+ "      AND gp.legal_entity_code=legalEntityCode ) AS entityStatus, "

							+ " emailId,password,"
							+ " createdDate,currentLogin,lastLogin,gender,salutation,"
							+ " firstName,middleName,lastName,shortName,address1,"
							+ " address2,address3,pincode,landline1,landline2,mobile1,"
							+ " mobile2, "

							+ "(select CONCAT(u.user_id , '-' ,u.first_name,' ',u.last_name )"
							+ " from user u where "
							+ " u.user_id=temp.maker "
							+ " and u.legal_entity_code=temp.legalEntityCode ) as maker ,  "
							+ " makerTimestamp,checker,checkerTimestamp,"
							+ " authRejRemarks "
							+ " ,roles ");

			StringBuilder commonCondition = new StringBuilder();

			commonCondition.append(" FROM("

					+ " SELECT user_id AS userId, " // legal_entity_code
					+ "        legal_entity_code AS legalEntityCode, " // USer_id
					+ "        unit_code  AS unitCode, " // EMAil_id
					+ "        country AS country, " // PASSWOrD
					+ "        state  AS state, " // CREATEd_date
					+ "        district  AS district, " // current_login
					+ "        city  AS city, " // last_login
					+ "        status AS status, " // uNSuccessful_attemPTS
					+ "        entity_status AS entityStatus, " // gender
					+ "        email_id AS emailId, " // first_name
					+ "        ' ' AS password, " // middle_name
					+ "        created_date AS createdDate, " // last_name
					+ "        current_login AS currentLogin, " // short_name
					+ "        last_login AS lastLogin, " // address1
					+ "        gender AS gender, " // AdDRESS2
					+ "        salutation AS salutation, " // address3
					+ "        first_name AS firstName, " // city
					+ "        middle_name AS middleName, " // pincode
					+ "        last_name AS lastName, " // distriCT
					+ "        short_name AS shortName, " // state
					+ "        address1 AS address1, " // COuNTRY
					+ "        address2 AS address2, " // lANDLINE1
					+ "        address3 AS address3, " // LANDLINe2
					+ "        pincode AS pincode, " // mobile1
					+ "        landline1 AS landline1, " // MOBIle2
					+ "        landline2 AS landline2, " // aUTH_REj_REMARkS
					+ "        mobile1 AS mobile1, " // ENTITY_status
					+ "        mobile2 AS mobile2, " // status
					+ "        maker AS maker, " // MAKer
					+ "        maker_timestamp AS makerTimestamp, " // maKEr_timestamp
					+ "        checker AS checker, " // CHECKER
					+ "        checker_timestamp AS checkerTimestamp, " // ChECKER_tIMESTAmP
					+ "        auth_rej_remarks as authRejRemarks " // sysdate_timesTAMP
					// + " (SELECT GROUP_CONCAT(concat(m.user_role_id,'-',r.ug_role_name)) FROM
					// user_role_mapping m,maint_usergroup_roles r "
					+ " 	   ,(SELECT GROUP_CONCAT(r.ug_role_name) FROM user_role_mapping m,maint_usergroup_roles r  "
					+ "				WHERE m.legal_entity_code=u.legal_entity_code AND m.user_id=u.user_id AND m.legal_entity_code=r.legal_entity_code AND m.user_role_id=r.ug_role_code "
					+ "				GROUP BY m.user_id) as roles " // Roles - to get the Roles for each User
																	// separted by Comma
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
					+ "        checker_timestamp AS checkerTimestamp, "
					+ "        auth_rej_remarks as authRejRemarks "
					// + " ,(SELECT GROUP_CONCAT(concat(m.user_role_id,'-',r.ug_role_name)) FROM
					// user_role_mapping_wrk m,maint_usergroup_roles r "
					+ "		   ,(SELECT GROUP_CONCAT(r.ug_role_name) FROM user_role_mapping_wrk m,maint_usergroup_roles r "
					+ "			 WHERE m.legal_entity_code=stg_u.legal_entity_code and m.user_id=stg_u.user_id and m.legal_entity_code=r.legal_entity_code and m.user_role_id=r.ug_role_code "
					+ "			 GROUP BY m.user_id) as roles "
					+ " FROM user_wrk stg_u "
					+ " WHERE stg_u.legal_entity_code =:legalEntityCode AND stg_u.unit_code in (:unitList)"
					+ " and (status !='DF' OR ( status ='DF' and maker =:userId ) )  "
					+ " ) as temp where legalEntityCode =:legalEntityCode ");

			if (!BankAuditUtil.isEmptyString(search)) {
				commonCondition = commonCondition.append(" and ( userId like :search "
						+ " or unitCode like :search  "
						+ " or unitCode In ( select atd.unit_code from maint_entity atd where atd.legal_entity_code=legal_entity_code and atd.unit_code=unitCode and atd.unit_name LIKE :search )" // records
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

						/*
						 * +
						 * " or  country IN (SELECT country_alpha3_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_name LIKE :search )"
						 * +
						 * " or  state IN (SELECT state_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_alpha3_code =country AND state_name LIKE :search ) "
						 * +
						 * " or  district IN (SELECT district_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_alpha3_code =country"
						 * + "  				AND state_code =state AND district_name LIKE :search ) "
						 * +
						 * " or  city IN (SELECT city_code FROM maint_address WHERE legal_entity_code=legal_entity_code AND country_alpha3_code =country "
						 * +
						 * "  				AND state_code =state AND district_code =district AND city_name LIKE :search ) "
						 */
						+ " ) ");
			}

			String[] columns = {
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
			 * FIELD(STATUS,'DF') DESC ,
			 */

			if (orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)) {
				commonCondition = commonCondition.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  ");
			} else if (orderColumn != null && !BankAuditUtil.isEmptyString(orderDirection)) {
				commonCondition = commonCondition.append(" order by   ").append(columns[orderColumn]).append(" ")
						.append(orderDirection);
			} else {
				commonCondition = commonCondition.append(" order by FIELD(STATUS,'DF') DESC");
			}

			// Create query
			countQueryString.append(commonCondition);
			queryString.append(commonCondition);
			@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
			Query query = entityManager.createNativeQuery(queryString.toString())
					.unwrap(NativeQuery.class)
					.setResultTransformer(new ResultTransformer() {

						@Override
						public Object transformTuple(Object[] tuple, String[] aliases) {
							User user = new User();

							if (tuple[0] != null) {
								user.setUserId(tuple[0].toString());
							}
							if (tuple[1] != null) {
								user.setLegalEntityCode(Integer.parseInt(tuple[1].toString()));
							}
							if (tuple[2] != null) {
								user.setUnitCode(tuple[2].toString());
							}
							if (tuple[3] != null) {
								user.setCountry(tuple[3].toString());
							}
							if (tuple[4] != null) {
								user.setState(tuple[4].toString());
							}
							if (tuple[5] != null) {
								user.setDistrict(tuple[5].toString());
							}
							if (tuple[6] != null) {
								user.setCity(tuple[6].toString());
							}
							if (tuple[7] != null) {
								user.setStatus(tuple[7].toString());
							}
							if (tuple[8] != null) {
								user.setEntityStatus(tuple[8].toString());
							}
							if (tuple[9] != null) {
								user.setEmailId(tuple[9].toString());
							}
							if (tuple[10] != null) {
								user.setPassword(tuple[10].toString());
							}
							if (tuple[11] != null) {
								user.setCreatedDate((Date) tuple[11]);
							}
							if (tuple[12] != null) {
								user.setCurrentLogin((Date) tuple[12]);
							}
							if (tuple[13] != null) {
								user.setLastLogin((Date) tuple[13]);
							}
							if (tuple[14] != null) {
								user.setGender(tuple[14].toString());
							}
							if (tuple[15] != null) {
								user.setSalutation(tuple[15].toString());
							}
							if (tuple[16] != null) {
								user.setFirstName(tuple[16].toString());
							}
							if (tuple[17] != null) {
								user.setMiddleName(tuple[17].toString());
							}
							if (tuple[18] != null) {
								user.setLastName(tuple[18].toString());
							}
							if (tuple[19] != null) {
								user.setShortName(tuple[19].toString());
							}
							if (tuple[20] != null) {
								user.setAddress1(tuple[20].toString());
							}
							if (tuple[21] != null) {
								user.setAddress2(tuple[21].toString());
							}
							if (tuple[22] != null) {
								user.setAddress3(tuple[22].toString());
							}
							if (tuple[23] != null) {
								user.setPincode(Integer.parseInt(tuple[23].toString()));
							}
							if (tuple[24] != null) {
								user.setLandline1(tuple[24].toString());
							}
							if (tuple[25] != null) {
								user.setLandline2(tuple[25].toString());
							}
							if (tuple[26] != null) {
								user.setMobile1(tuple[26].toString());
							}
							if (tuple[27] != null) {
								user.setMobile2(tuple[27].toString());
							}
							if (tuple[28] != null) {
								user.setMaker(tuple[28].toString());
							}
							if (tuple[29] != null) {
								user.setMakerTimestamp((Date) tuple[29]);
							}
							if (tuple[30] != null) {
								user.setChecker(tuple[30].toString());
							}
							if (tuple[31] != null) {
								user.setCheckerTimestamp((Date) tuple[31]);
							}
							if (tuple[32] != null) {
								user.setAuthRejRemarks(tuple[32].toString());
							}
							if (tuple[33] != null) {
								user.setUserRoles(tuple[33].toString());
							}
							return user;
						}

						@Override
						public List transformList(List collection) {

							return collection;
						}
					});
			query.setParameter("legalEntityCode", legalEntityCode);
			query.setParameter("userId", userId);
			query.setParameter("unitList", unitList);

			System.out.println("query: " + queryString);

			if (!BankAuditUtil.isEmptyString(search)) {
				query.setParameter("search", "%" + search + "%");
			}

			query.setFirstResult(page * size);
			query.setMaxResults(size);

			// countResults
			Query countQuery = entityManager.createNativeQuery(countQueryString.toString());
			countQuery.setParameter("legalEntityCode", legalEntityCode);
			countQuery.setParameter("userId", userId);
			countQuery.setParameter("unitList", unitList);
			;

			Long totalRecords = (long) countQuery.getSingleResult();

			List resultList = query.getResultList();
			if (!resultList.isEmpty()) {

				users = new ArrayList();
				int i = 0;
				resultList.forEach(user -> {
					users.add((User) user);
				});
				dataTableResponse.setData(users);
				if (size != 0) {
					dataTableResponse.setRecordsFiltered(totalRecords);
					dataTableResponse.setRecordsTotal(totalRecords);
				} else {
					dataTableResponse.setError("page size zero");
					dataTableResponse.setData(Collections.emptyList());
					dataTableResponse.setRecordsTotal(0l);
					dataTableResponse.setRecordsFiltered(0l);
				}

			} else {
				dataTableResponse.setError(null);
				dataTableResponse.setData(Collections.emptyList());
				dataTableResponse.setRecordsTotal(0l);
				dataTableResponse.setRecordsFiltered(0l);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataTableResponse;
	}

	public Boolean isUser(Integer legalEntityCode, String userId) {
		Long count = (Long) entityManager.createQuery(
				" select count(*) "
						+ " from User   "
						+ " where "
						+ " legalEntityCode =:legalEntityCode "
						+ " and userId =:userId  ")
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("userId", userId)
				.getSingleResult();

		Long count1 = (Long) entityManager.createQuery(
				" select count(*) "
						+ " from UserWrk   "
						+ " where "
						+ " legalEntityCode =:legalEntityCode "
						+ " and userId =:userId  ")
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("userId", userId)
				.getSingleResult();
		Boolean flag;
		if ((count1 != null && count1 > 0) || (count != null && count > 0)) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	public Boolean isUserHavingEntitlement(Integer legalEntityCode, String userId, String role, String functionId, String action) {

		logger.info("Inside isUserHavingEntitlement .. "+legalEntityCode +" userId.."+userId +" role.."+role + " functionId.. "+ functionId+" action.. "+ action);
		Boolean validUsr = true;
		try {
			StringBuilder query= new StringBuilder(" SELECT COUNT(u.userId),  "
					+ " (SELECT value FROM GeneralParameter WHERE key1 = 'ENTITLEMENT' AND key2 = 'APPLICABLE' ) AS entApplicability "
					+ " FROM UserRoleMapping m, User u, MaintRoleEntitlement e "
					+ " WHERE m.legalEntityCode=u.legalEntityCode AND m.userId=u.userId AND u.entityStatus='A' AND  "
					+ " e.legalEntityCode=m.legalEntityCode AND m.userRoleId=e.ugRoleCode AND "
					+ " m.legalEntityCode =:legalEntityCode AND m.userRoleId=:role AND u.userId =:userId AND "
					+ " e.functionId=:functionId AND (e.l1Fname=:action OR e.l2Fname=:action OR e.l3Fname=:action OR e.l4Fname=:action) ");
			
			Query hqlQuery = entityManager.createQuery(query.toString()).setParameter("legalEntityCode", legalEntityCode).setParameter("userId", userId)
					.setParameter("role", role).setParameter("functionId", functionId).setParameter("action", action);
			
			Object obj = hqlQuery.getSingleResult();
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

	@SuppressWarnings("unchecked")
	public List<User> getUser(Integer legalEntityCode, String unitCode ,String userId,String status) {
		
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
			queryStr1=queryStr1.append(" and u.userId =:userId ");
		}
		
	    if(className.getSimpleName().equalsIgnoreCase("User")){
	    	// queryStr1=queryStr1.append(" and u.entityStatus ='"+BankAuditConstant.STATUS_ACTIVE+"'"); // Condition commented to modify the Inactive user
		}
		
		Query query=entityManager.createQuery(queryStr1.toString())
				.setParameter("legalEntityCode", legalEntityCode);
		
		if(!BankAuditUtil.isEmptyString(unitCode)){
		
			query.setParameter("unitCode", unitCode);
		}
		
		if(!BankAuditUtil.isEmptyString(userId)){
			
			query.setParameter("userId", userId);
		}
		
		
		return query.getResultList();
		
		
	}


}
