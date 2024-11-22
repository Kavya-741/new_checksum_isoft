package com.bankaudit.dao;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.jboss.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintEntity;
import com.bankaudit.model.MaintEntityWrk;
import com.bankaudit.model.User;

@Repository("maintEntityDao")
public class MaintEntityDaoImpl  extends AbstractDao implements MaintEntityDao{
	
	static final Logger logger = Logger.getLogger(MaintEntityDaoImpl.class);

	//This method use provide the details till 8 levels of hierarchy, use this method instead the below method which takes lots of iteration for getting the list.
	@SuppressWarnings({ "deprecation", "unchecked" })
	public List<String> getSubBranchesGyUserIdOrUnitId(Integer legalEntityCode,String type,String userIdOrUnitId) {
		List<String> list=null;
		try {
			
			Session session=getSession();
			String unitCode="";
			String sql="";
			if(type.equalsIgnoreCase("user")) {
				sql="SELECT u.unitCode as unitCode FROM User u WHERE u.userId=:userIdOrUnitId AND u.legalEntityCode=:legalEntityCode ";
				User usr= (User) session.createQuery(sql)
						.setResultTransformer(Transformers.aliasToBean(User.class))
						.setParameter("legalEntityCode", legalEntityCode)
						.setParameter("userIdOrUnitId", userIdOrUnitId).uniqueResult();
				unitCode=usr.getUnitCode();
			}else {
				unitCode=userIdOrUnitId;
			}
			if(unitCode!=null) {
				sql = " select distinct unit_code from maint_entity where unit_code in (" + 
						"		select distinct unit_code from maint_entity" + 
						"		join ( " + 
						"		    select A.unit_code Aid," + 
						"		    B.unit_code Bid," + 
						"		    C.unit_code Cid," + 
						"		    D.unit_code Did," + 
						"		    E.unit_code Eid," + 
						"		    F.unit_code Fid," + 
						"		    G.unit_code Gid," + 
						"		    H.unit_code Hid " + 
						"		    FROM maint_entity A" + 
						"		    left join maint_entity B on B.parent_unit_code=A.unit_code and B.legal_entity_code=A.legal_entity_code" + 
						"		    left join maint_entity C on C.parent_unit_code=B.unit_code and C.legal_entity_code=B.legal_entity_code" + 
						"		    left join maint_entity D on D.parent_unit_code=C.unit_code and D.legal_entity_code=C.legal_entity_code" + 
						"		    left join maint_entity E on E.parent_unit_code=D.unit_code and E.legal_entity_code=D.legal_entity_code" + 
						"		    left join maint_entity F on F.parent_unit_code=E.unit_code and F.legal_entity_code=E.legal_entity_code" + 
						"		    left join maint_entity G on G.parent_unit_code=F.unit_code and G.legal_entity_code=F.legal_entity_code" + 
						"		    left join maint_entity H on H.parent_unit_code=G.unit_code and H.legal_entity_code=G.legal_entity_code" + 
						"		    Where A.unit_code='"+unitCode+"' and A.legal_entity_code=" +legalEntityCode+  " AND A.status='A' AND A.entity_status='A' "+
						"		) X" + 
						"		where unit_code in (Aid,Bid,Cid,Did,Eid,Fid,Gid,Hid) AND status='A' AND entity_status='A' ) ";
				list = session.createSQLQuery(sql).list(); 
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(); 
		}
		System.out.println("getSubBranchesGyUserIdOrUnitId .. list<String>::"+ list.size()+" ::"+list);
		return list;
	}

	@Override
	public MaintEntity getUnique(Integer legalEntityCode, String unitCode,String status) {
		
		Session session=getSession();
		MaintEntity maintEntity=null;
	
		if(!status.equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			MaintEntityWrk maintEntityWrk=(MaintEntityWrk)session.createQuery("from MaintEntityWrk me "
					+ " where me.legalEntityCode =:legalEntityCode "
					+ " and me.unitCode =:unitCode")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("unitCode", unitCode).uniqueResult();
			
			if(maintEntityWrk!=null){
				maintEntity=new MaintEntity();
				BeanUtils.copyProperties(maintEntityWrk, maintEntity);
			}
			return maintEntity;
			
		}else {
			return (MaintEntity)session.createQuery("from MaintEntity me " 
					+ " where me.legalEntityCode =:legalEntityCode "
					+ " and me.unitCode =:unitCode")
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("unitCode", unitCode).uniqueResult();
		}
		
	}

	
	@Override
	public void deleteMaintEntityWrk(Integer legalEntityCode, String unitCode) {
		
		getSession().createQuery(" delete from MaintEntityWrk "
				+ " where legalEntityCode =:legalEntityCode "
				+ " and unitCode =:unitCode")
		.setParameter("unitCode", unitCode)
		.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public DataTableResponse getMaintEntity(Integer legalEntityCode, String levelCodeStr,String userId,String parentUnitCode, String search,
			Integer orderColumn, String orderDirection, Integer page, Integer size) {
		
		
	    Session session = getSession();
	    
		session.setDefaultReadOnly(true);
	    
		List<MaintEntity> maintEntitys = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();

		StringBuilder queryString2=new StringBuilder("select  "
				
                  + "unit_name, "
  
			      + "  (SELECT concat(unit_code , '-' , unit_name) FROM maint_entity WHERE "
			      + " unit_code= me.parent_unit_code AND legal_entity_code =:legal_entity_code)as parentUnitCode , "
			    
    			  + "(select concat(gp.value , '-' , gp.description)"
    			  + " from general_parameter gp where "
    			  + " gp.key_1='STATUS' and gp.value=me.status "
    			  + " and gp.legal_entity_code=me.legal_entity_code ) as status , "
			    
				  + "(select concat(gp.value , '-' , gp.description)"
				  + " from general_parameter gp where "
				  + " gp.key_1='ENTITY_STATUS' and gp.value=me.entity_status "
				  + " and gp.legal_entity_code=me.legal_entity_code ) as entityStatus , "
				  
				  + "(select concat(gp.value , '-' , gp.description)"
				  + " from general_parameter gp where "
				  + " gp.key_1='UNIT_TYPE' and gp.value=me.unit_type "
				  + " and gp.legal_entity_code=me.legal_entity_code ) as unit_type, "
	              
			      + "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
			      + " from user u where "
			      + " u.user_id=me.contact_person "
			      + " and u.legal_entity_code=me.legal_entity_code ) as contactPerson , "
			      
			      + "(select distinct concat(u.country_alpha3_code , '-' ,u.country_name )"
			      + " from maint_address u where "
			      + " u.country_alpha3_code=me.country "
			      + " and u.legal_entity_code=me.legal_entity_code ) as country , "
                  
			      + "(select distinct concat(u.state_code , '-' ,u.state_name )"
			      + " from maint_address u where "
			      + " u.state_code=me.state "
			      + " and u.country_alpha3_code=me.country "
			      + " and u.legal_entity_code=me.legal_entity_code ) as state , "
                  
                  
			      + "(select distinct concat(u.district_code , '-' ,u.district_name )"
			      + " from maint_address u where "
			      + " u.district_code=me.district "
			      + " and u.country_alpha3_code=me.country "
			      + " and u.state_code=me.state "
			      + " and u.legal_entity_code=me.legal_entity_code ) as district , "
			      	
			      + "(select distinct concat(u.city_code , '-' ,u.city_name )"
			      + " from maint_address u where "
			      + " u.city_code=me.city "
			      + " and u.district_code=me.district "
			      + " and u.country_alpha3_code=me.country "
			      + " and u.state_code=me.state "
			      + " and u.legal_entity_code=me.legal_entity_code ) as city ,  "
			     
			      + "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
			      + " from user u where "
			      + " u.user_id=me.maker "
			      + " and u.legal_entity_code=me.legal_entity_code ) as maker ,  "
			      
			      + "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
			      + " from user u where "
			      + " u.user_id=me.checker "
			      + " and u.legal_entity_code=me.legal_entity_code ) as checker ,  "
			      
				   + "  (SELECT concat(level_code , '-' , level_desc) FROM entity_level_code_desc WHERE "
				   + " level_code= me.level_code AND legal_entity_code =:legal_entity_code) as level_code , "

			      
			
                  
                  + "legal_entity_code, "
                  + "address1, "
                  + "address2, "
                  + "address3, "
                  + "pincode, "
                  + "landline1, "
                  + "landline2, "
                  + "mobile1, "
                  + "mobile2, "
                  + "mailid, "
                  + "u_criticality, "
                  + "weekly_holiday, "
                  + "fortnightly_holiday, "
                  + "unit_open_date, "
                  + "unit_head_inception_date, "
                  + "auth_rej_remarks, "
                  + "maker_timestamp, "
                  + "checker_timestamp, "
                 /* + "unit_head_user , "*/
              	+ "(select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
				+ " from user u where "
				+ " u.user_id=me.unit_head_user "
				+ " and u.legal_entity_code=me.legal_entity_code ) as unit_head_user ,  "
                  + "unit_code "
                  
			      + " from ( "
			      
			      + " select * from maint_entity "
			      + " where legal_entity_code =:legal_entity_code "
			      
			      + " union ALL "
			      
			      + " select * from maint_entity_wrk "
			      + " where legal_entity_code =:legal_entity_code "
			      + " and (status !='DF' OR ( status ='DF' and maker =:user_id ) )  "
			      
			      + " ) as me  where legal_entity_code =:legal_entity_code ");
		
		
		
		String[] columns={
			/*	"LENGTH( me.unit_code),  me.unit_code",*/
				
				" CAST( me.unit_code AS UNSIGNED),  me.unit_code",
				"me.unit_name",
				"me.unit_type",
                "me.parent_unit_code",
                "me.level_code",
                "me.unit_head_user",
            	"me.contact_person",
            	/*"LENGTH(),  me.mailid",*/
            	" CAST( me.mailid AS UNSIGNED),  me.mailid",
    			"me.u_criticality",
    			"me.mobile1",
    			"me.maker",
    			"me.entity_status",
				"me.status",
				};
			
		if(!BankAuditUtil.isEmptyString(parentUnitCode)){
			queryString2=queryString2.append("and me.parent_unit_code =:parent_unit_code");
		}else {
			queryString2=queryString2.append("and me.level_code =:level_code");
		}
		
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString2=queryString2.append(" and (   "
					//	+ " or  me.address1 like :search "
					/*+ " or  me.entityLevelCodeDesc.levelDesc like :search "*/
					//+ " or  me.city like :search  "
					
					+ "     me.unit_code like :search  "
					+ " or  me.unit_name like :search  "
					+ " or  me.unit_type IN (SELECT value FROM general_parameter  WHERE key_1='UNIT_TYPE' AND description LIKE :search ) "
					+ " or  me.parent_unit_code like :search  "
					+ " or  me.parent_unit_code IN (SELECT e.unit_code FROM maint_entity e WHERE e.unit_code= me.parent_unit_code AND e.legal_entity_code =:legal_entity_code and e.unit_name LIKE :search ) "
					+ " or  me.level_code IN (SELECT level_code FROM entity_level_code_desc WHERE level_desc like :search AND legal_entity_code =:legal_entity_code) "
					+ " or  me.unit_head_user IN (SELECT user_id FROM user  WHERE legal_entity_code=me.legal_entity_code AND (concat(first_name , ' ' , last_name) like :search  ) ) "
					+ " or  me.contact_person like :search  "
					+ " or  me.contact_person IN (SELECT user_id FROM user  WHERE legal_entity_code=me.legal_entity_code AND (concat(first_name , ' ' , last_name) like :search  ) ) "
					+ " or  me.mailid like :search"
					+ " or  me.u_criticality like :search  "
					+ " or  me.mobile1 like :search  "
					+ " or  me.maker like :search  "
					+ " or  me.maker IN (SELECT user_id FROM user  WHERE legal_entity_code=me.legal_entity_code AND (concat(first_name , ' ' , last_name) like :search  ) ) "
					+ " or  me.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' AND description LIKE :search ) "
					+ " or  me.status IN (SELECT value FROM general_parameter  WHERE key_1='STATUS' and  description LIKE :search ) "
					
					
					/*+ " or  me.country IN (SELECT country_alpha3_code FROM maint_address "
                    + " WHERE legal_entity_code=me.legal_entity_code "
                    + " AND country_name LIKE :search ) "

					+ " or  me.state IN (SELECT state_code FROM maint_address "
					+ " WHERE legal_entity_code=me.legal_entity_code "
					+ " AND country_alpha3_code =me.country"
					+ " AND state_name LIKE :search )"
					
					+ " or  me.district IN (SELECT district_code FROM maint_address "
					+ " WHERE legal_entity_code=me.legal_entity_code "
					+ " AND country_alpha3_code =me.country"
					+ " AND state_code =me.state"
					+ " AND district_name LIKE :search )"
					
					+ " or  me.city IN (SELECT city_code FROM maint_address "
					+ " WHERE legal_entity_code=me.legal_entity_code "
					+ " AND country_alpha3_code =me.country"
					+ " AND state_code =me.state"
					+ " AND district_code =me.district"
					+ " AND city_name LIKE :search )"		*/ // Remove the search criteria on Country, State, District & City, as this values are not appearing on the Landing page			
					
					+ "  )   ");
		}


		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString2=queryString2.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString2=queryString2.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		@SuppressWarnings("deprecation")
		Query query = session.createSQLQuery(queryString2.toString())
				.setResultTransformer(new ResultTransformer() {
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						MaintEntity maintEntity=new MaintEntity();
						if(tuple[0]!=null){
							maintEntity.setUnitName(tuple[0].toString());	
						}
						
						if(tuple[1]!=null){
							maintEntity.setParentUnitCode(tuple[1].toString());								
						}
						if(tuple[2]!=null){
							maintEntity.setStatus(tuple[2].toString());
						}
						
						if(tuple[3]!=null){
							maintEntity.setEntityStatus(tuple[3].toString());
						}
						
						if(tuple[4]!=null){
							maintEntity.setUnitType(tuple[4].toString());
						}
						if(tuple[5]!=null){
							maintEntity.setContactPerson(tuple[5].toString());
						}
						if(tuple[6]!=null){
							maintEntity.setCountry(tuple[6].toString());
						}
						if(tuple[7]!=null){
							maintEntity.setState(tuple[7].toString());
						}
						if(tuple[8]!=null){
							maintEntity.setDistrict(tuple[8].toString());
						}
						if(tuple[9]!=null){
							maintEntity.setCity(tuple[9].toString());
						}
						if(tuple[10]!=null){
							maintEntity.setMaker(tuple[10].toString());
						}
		
						if(tuple[11]!=null){
							maintEntity.setChecker(tuple[11].toString());								
						}
						if(tuple[12]!=null){
							maintEntity.setLevelCode(tuple[12].toString());
						}
						
						if(tuple[13]!=null){
							maintEntity.setLegalEntityCode(Integer.parseInt(tuple[13].toString()));
						}
						
						if(tuple[14]!=null){
							maintEntity.setAddress1(tuple[14].toString());
						}
						if(tuple[15]!=null){
							maintEntity.setAddress2(tuple[15].toString());
						}
						if(tuple[16]!=null){
							maintEntity.setAddress3(tuple[16].toString());
						}
						if(tuple[17]!=null){
							maintEntity.setPincode(Integer.parseInt(tuple[17].toString()));
						}
						if(tuple[18]!=null){
							maintEntity.setLandline1(tuple[18].toString());
						}
						if(tuple[19]!=null){
							maintEntity.setLandline2(tuple[19].toString());
						}
						if(tuple[20]!=null){
							maintEntity.setMobile1(tuple[20].toString());
						}
						if(tuple[21]!=null){
							maintEntity.setMobile2(tuple[21].toString());
						}
						
						if(tuple[22]!=null){
							maintEntity.setMailid(tuple[22].toString());
						}
						
						if(tuple[23]!=null){
							maintEntity.setUCriticality(tuple[23].toString());
						}
						
						if(tuple[24]!=null){
							maintEntity.setWeeklyHoliday(tuple[24].toString());
						}
						if(tuple[25]!=null){
							maintEntity.setFortnightlyHoliday(tuple[25].toString());
						}
						// if(tuple[26]!=null){
						// 	maintEntity.setUnitOpenDate((Date)tuple[26]);
						// }
						// if(tuple[27]!=null){
						// 	maintEntity.setUnitHeadInceptionDate((Date)tuple[27]);
						// }
						if(tuple[28]!=null){
							maintEntity.setAuthRejRemarks(tuple[28].toString());
						}
						// if(tuple[29]!=null){
						// 	maintEntity.setMakerTimestamp((Date)tuple[29]);
						// }
						// if(tuple[30]!=null){
						// 	maintEntity.setCheckerTimestamp((Date)tuple[30]);
						// }
						if(tuple[31]!=null){
							maintEntity.setUnitHeadUser(tuple[31].toString());
						}
						if(tuple[32]!=null){
							maintEntity.setUnitCode(tuple[32].toString());
						}
						return maintEntity;
					}
						
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legal_entity_code", legalEntityCode)
				.setParameter("user_id", userId);

		
		if(!BankAuditUtil.isEmptyString(parentUnitCode)){
			query.setParameter("parent_unit_code", parentUnitCode);
		}else {
			query.setParameter("level_code", levelCodeStr);
		}
		
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){
			
			maintEntitys= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintEntitys.add((MaintEntity) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintEntitys);
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
		return dataTableResponse;

	}

	@Override
	public Boolean isMaintEntity(Integer legalEntityCode, String unitCode) {
		Session session=getSession();
		Long count=(Long)session.createQuery(
				" select count(*) "
				+ " from MaintEntity   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and unitCode =:unitCode  ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("unitCode", unitCode).uniqueResult();
		
		Long count1=(Long)session.createQuery(
		
				
				" select count(*) "
				+ " from MaintEntityWrk   "
				+ " where "
				+ " legalEntityCode =:legalEntityCode "
				+ " and unitCode =:unitCode ")
		.setParameter("legalEntityCode", legalEntityCode)
		.setParameter("unitCode", unitCode).uniqueResult();
		Boolean flag=false;
		if((count1!=null && count1 >0)||(count!=null && count >0)){
			flag= true;
		}else {
			flag= false;
		}
		return flag;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<MaintEntity> getMaintEntityByLegalEntityCodeAndLevelCode(Integer legalEntityCode, String levelCode) {
		
		Session session=getSession();
		
		
		return session.createQuery(" select m.unitCode as unitCode , m.unitName as unitName from MaintEntity m "
				+ " where m.legalEntityCode =:legalEntityCode and m.levelCode =:levelCode and entityStatus='"+BankAuditConstant.STATUS_ACTIVE+"' "
						+ " order by  m.unitName asc")
				.setResultTransformer(Transformers.aliasToBean(MaintEntity.class))
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("levelCode", levelCode).list();
	}
	
	
}