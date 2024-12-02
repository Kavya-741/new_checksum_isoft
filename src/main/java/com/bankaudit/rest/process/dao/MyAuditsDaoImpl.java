package com.bankaudit.rest.process.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bankaudit.dao.AbstractDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintLegalEntity;
import com.bankaudit.model.User;
import com.bankaudit.process.model.AuditSchedule;
import com.bankaudit.repository.MaintLegalEntityRepository;
import com.bankaudit.service.MaintEntityService;
import com.bankaudit.service.SequenceAppenderService;
import com.bankaudit.util.BankAuditUtil;

/*MaintEntityDataEffect */
@Repository("myAuditsDao")
public class MyAuditsDaoImpl extends AbstractDao implements MyAuditsDao{
	
	@Autowired
    SequenceAppenderService sequenceAppenderService;
	
	@Autowired
    ReportDao reportDao;
	
	@Autowired
    MaintLegalEntityRepository maintLegalEntityRepository;

	@Autowired
	MaintEntityService maintEntityService;
	
	
	static final Logger logger = Logger.getLogger(MyAuditsDaoImpl.class);
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public DataTableResponse getMuAuditsList(int legalEntityCode,String userId, String unitCode, String roleId, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {

		Session session = getSession();
		session.setDefaultReadOnly(true);

		logger.info("Inside getMuAuditsList .."+ userId+" :: "+ unitCode+" :: "+roleId);
		/********************** This block is to retrieve the User details --> UserId and UserName **************************/
		StringBuilder queryString0=new StringBuilder(// " Select concat(u.firstName , ' ' , u.lastName) as firstName, u.userId "
				 " from User u  "
				+ " where "
				+ " u.legalEntityCode =:legalEntityCode "
				);
		
		Query query1=session.createQuery(queryString0.toString())
				.setParameter("legalEntityCode", legalEntityCode);
		
		List lst = query1.list();
		HashMap<String, String> userDtls = new HashMap<String, String>();
		for ( Iterator iter = lst.iterator(); iter.hasNext(); ) {
			User usr = (User) iter.next();
			if(usr.getUserId().equalsIgnoreCase(userId))
				userDtls.put(usr.getUserId(), "Me"); // for the same User we are stamping as "You"
			else userDtls.put(usr.getUserId(), (usr.getFirstName()==null?"":usr.getFirstName()) 
											+ (usr.getMiddleName()==null?"":(" "+usr.getMiddleName())) 
											+ (usr.getLastName()==null?"":(" "+usr.getLastName())) );	
		}
		System.out.println("userId is .... "+ userId+" serach:: "+search +" legal entity code:: "+legalEntityCode);
		System.out.println("User details val is .... "+ userDtls);

		// Date businessDate =businessDateService.getBusinessDateTimestampByLegalEntityCode(legalEntityCode);

		MaintLegalEntity maintLegalEntity = maintLegalEntityRepository.findByLegalEntityCode(legalEntityCode);
		Date businessDate = maintLegalEntity.getBusinessdateTimestamp();
		System.out.println("businessDate .... "+ businessDate);
		StringBuilder queryString2=new StringBuilder( " select nd.auditsInInterval,nads.noAudits from " 
				+ " ( select count(*) as auditsInInterval from audit_schedule ash, audit_team atm " 
				+ "	where ash.plan_id=atm.plan_id and ash.audit_id = atm.audit_id and ash.legal_entity_code=atm.legal_entity_code and atm.user_id=:userId " 
				+ "          and (ash.schedule_start_date between :businessDate and " 
				+ "				ADDDATE(:businessDate, INTERVAL (select g.value from general_parameter g where key_1='AUDITOR_VISIBILITY' AND key_2='DAYS' AND g.legal_entity_code=ash.legal_entity_code ) DAY))  ) as nd, " 
				+ " ( select g.value as noAudits from general_parameter g where g.key_1='AUDITOR_VISIBILITY' AND g.key_2='NO_OF_AUDITS' AND g.legal_entity_code=:legalEntityCode) as nads "  
				); 
		//(select g.value from GeneralParameter g where g.key1='AUDITOR_VISIBILITY' AND g.key2='DAYS' AND g.legalEntityCode=:legalEntityCode) 
		
		Query query2lst=session.createSQLQuery(queryString2.toString())
				.setParameter("legalEntityCode", legalEntityCode).setParameter("userId", userId)
				.setParameter("businessDate", businessDate);
		
		Object obj = query2lst.uniqueResult();
		int auidtsDisplay = 100;
		try{
			if(obj != null  && ((Object[])obj).length > 0)
			{
				int auditsInInterval  =Integer.parseInt( ((Object[])obj)[0].toString());
				int  noOfAudit=Integer.parseInt( ((Object[])obj)[1].toString());
				System.out.println("auditsInInterval .... "+ auditsInInterval +" noOfAudit.... "+noOfAudit);
				if(auditsInInterval > noOfAudit) auidtsDisplay=noOfAudit;
				else auidtsDisplay = auditsInInterval;
			}
		}catch(Exception e) {
			System.out.println("Exception occured ....");
		}
		System.out.println("auidtsDisplay .... "+auidtsDisplay);
		/******************** Logic for no of Audits, Ends here ********************************/
			
		/***************** Logic to retrieve the Visible Audits, which are in Planned state. Based on the above Output ****/
		System.out.println("businessDate .... "+ businessDate);
		StringBuilder queryString3=new StringBuilder( " select audit_id from audit_schedule s "
				+ "	where legal_entity_code=:legalEntityCode and audit_status in ('AP') " 
				+ "		and audit_id in(select t.audit_id from audit_team t where t.legal_entity_code=s.legal_entity_code and t.user_id=:userId) " 
				+ "	order by schedule_start_date asc limit :auidtsDisplay "); 
		
		List visibleAuditList=session.createNativeQuery(queryString3.toString())
				.setParameter("legalEntityCode", legalEntityCode).setParameter("userId", userId)
				.setParameter("auidtsDisplay", auidtsDisplay).list();
		logger.info("visibleAuditList .... "+visibleAuditList);
		if(visibleAuditList != null && visibleAuditList.size() < 1) visibleAuditList.add("NoAudit"); // If the Audit visibility is 0, then we are passing NoAudit
		/******************** Logic for Visible Audits, Ends here ********************************/
		
		/************************** ********************************************/
		/*StringBuilder queryString4=new StringBuilder( " select user_role_id from user_role_mapping where legal_entity_code=:legalEntityCode and user_id=:userId limit 1"); 
		String roleId=(String)session.createNativeQuery(queryString4.toString())
				.setParameter("legalEntityCode", legalEntityCode).setParameter("userId", userId)
				.uniqueResult(); */
		logger.info("roleId .... "+roleId);
		List<String> maintEntities = null;
		if(roleId != null && (roleId.equalsIgnoreCase("G1") || roleId.equalsIgnoreCase("G6"))) {
			maintEntities=maintEntityService.getSubBranchesGyUserIdOrUnitId(legalEntityCode,"user",userId);
			logger.info("maintEntities .... "+maintEntities);
		}
		
		/****************************** ****************************************/
		
		
		boolean isDeptAsRMD =false;
		isDeptAsRMD = reportDao.isDeptAsRMD(legalEntityCode, "", userId);
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		try {
		//StringBuilder queryString=new StringBuilder(" from AuditSchedule where legalEntityCode =:legalEntityCode ");
		StringBuilder queryString=new StringBuilder("select sch as auditSchedule, " 
				+ " legalEntityCode,"
				+ "(select concat(auditTypeCode , '-' , auditTypeDesc) from MaintAuditTypeDesc at where at.auditTypeCode=sch.auditTypeCode and at.legalEntityCode= sch.legalEntityCode ) as auditTypeCode,"
				
				+ "planId,auditId, "
				+ " (Select concat(unitCode , '-' , unitName) from  MaintEntity e where e.unitCode =sch.unitId and e.legalEntityCode =sch.legalEntityCode ) as unitId,  "
				
				+ " (Select concat(g.value,'-',g.description) from  MaintEntity e,GeneralParameter g where e.unitCode =sch.unitId and e.legalEntityCode =sch.legalEntityCode and e.legalEntityCode =g.legalEntityCode and g.key1='UNIT_TYPE' and g.key2='UNIT_TYPE' and g.value=e.unitType) as unitType, "
				
				+ " (Select c.criticalityDesc from  MaintEntity e,MaintCriticality c where e.unitCode =sch.unitId and e.legalEntityCode =sch.legalEntityCode and e.legalEntityCode =c.legalEntityCode and e.uCriticality=c.criticalityCode and c.criticalityOfType='Entity') as uCriticality, "
				
				//+ " (Select concat(u.firstName , ' ' , u.lastName) from  User u,AuditTeam t where u.userId =t.userId and t.auditId=sch.auditId and t.legalEntityCode =u.legalEntityCode and t.legalEntityCode =sch.legalEntityCode and t.userType='L') as teamLead, "
				+ " (Select t.userId from  AuditTeam t where t.auditId=sch.auditId and t.legalEntityCode =sch.legalEntityCode and t.userType='L') as teamLead, "
				
				+" duration," //Code to comment //+ " (Select p.duration from  MaintEntity e,MaintAuditParameter p where e.unitCode =sch.unitId and e.unitType=p.unitType and e.uCriticality=p.uCriticality and sch.auditTypeCode=p.auditTypeCode and e.legalEntityCode =sch.legalEntityCode and e.legalEntityCode =p.legalEntityCode ) as duration, "
				+" frequency," //Code to comment //+ " (Select p.frequency from  MaintEntity e,MaintAuditParameter p where e.unitCode =sch.unitId and e.unitType=p.unitType and e.uCriticality=p.uCriticality and sch.auditTypeCode=p.auditTypeCode and e.legalEntityCode =sch.legalEntityCode and e.legalEntityCode =p.legalEntityCode ) as frequency, "
				//+ " (select tm.auditTypeCode,GROUP_CONCAT(tm.name) from (Select t.auditTypeCode,t.planId, t.auditId, concat(u.firstName , ' ' , u.lastName) as name from  User u,AuditTeam t where u.userId =t.userId and t.auditId=sch.auditId and t.legalEntityCode =u.legalEntityCode and t.legalEntityCode =sch.legalEntityCode and t.userType='T') tm where  GROUP BY tm.auditTypeCode  ) as teamMember "
				+ " (Select concat(sch.auditStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS' and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus) as auditStatus, "
				
				//+ " (Select concat(u.userId , '-' , u.firstName, ' ',u.middleName,' ',u.lastName) from  User u where u.legalEntityCode =sch.legalEntityCode and u.userId=sch.maker) as maker "
				+ " (Select concat(u.userId , '-' , ifnull(u.firstName,''), ' ',ifnull(u.middleName,''),' ',ifnull(u.lastName,'') ) "
				+ " from  User u where u.legalEntityCode =sch.legalEntityCode and u.userId=sch.maker) as maker "
								
				//+ ", (Select count(*) from  AuditObservation o where o.legalEntityCode =sch.legalEntityCode and o.auditId=sch.auditId and o.complianceCategory='N' and o.reviewStatus='R' ) as responseReject "
				//+ ", (Select count(*) from  AuditObservation o where o.legalEntityCode =sch.legalEntityCode and o.auditId=sch.auditId and o.complianceCategory='C' and o.submissionStatus='A') as complianceCount "
				//+ ", (Select count(*) from  AuditObservation o where o.legalEntityCode =sch.legalEntityCode and o.auditId=sch.auditId and o.complianceCategory='NA' and o.submissionStatus='A') as notApplicableCount "
				+" ,0,0,0 "  // No need to calculate responseReject, Compliance, NA
				//+ " , auditStatus "
				+ ", (Select g.value from GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='AUDIT_EARLY_START' and g.key2='EARLY_START_ALLOWED') as earlyStartAllowed "
				+ ", (Select g.value from GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='AUDIT_OFFSET_DAYS' and g.key2='AUDIT_OFFSET_DAYS') as auditOffsetDays " 
				+ ", (Select concat(sch.auditStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS' and g.key2='AUDIT_STATUS' and g.value=sch.observationStatus) as auditStatusTemp " // This result to stamp in Audit status in the Observation status is AE i.e Audit Reported
				+ ", (select group_concat(t.userId) from AuditTeam t where t.legalEntityCode=sch.legalEntityCode AND t.auditId=sch.auditId AND t.userType='M' ) AS teamMember "
				+" ,   sch.reportStatus " // Report review status added for BaaSDOS implementation 
				+ ",  (Select concat(sch.reportStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS' and g.key2='REPORT_STATUS' and g.value=sch.reportStatus) as reportStatusDesc " // Report review status desc added for BaaSDOS implementation 
								
			 	+ " , (Select concat(sch.status , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode "
				+ " and g.key1='STATUS' and g.key2='STATUS' and g.value=sch.status) as authStatus "  
									
				
				+ " from AuditSchedule sch where legalEntityCode =:legalEntityCode "
				//+ " and sch.legalEntityCode=t.legalEntityCode and t.auditId=sch.auditId and t.userId=:userId"
				// + " and  IFNULL(auditStatus,'NotNull') not in ('AD','AU','DRP','UP') "
				// Condition added to get the Assessments, For processing check with AuitTeam table till AC audit status , for View check with User table from AC audit status
				+ " and  ( (sch.auditId IN (SELECT t.auditId FROM AuditTeam t WHERE t.auditId=sch.auditId and t.legalEntityCode =sch.legalEntityCode and t.userId=:userId  ) "
				+ "           AND IFNULL(auditStatus,'NotNull') IN ('AP','AI','AS','AR','AW','AC') )" );   // 'AW','AC' included to show the Inspections after Closure as well to the Inspectors
				//+ "        OR ( (sch.unitId IN  (SELECT u.unitCode FROM User u WHERE u.unitCode=sch.unitId and  u.legalEntityCode =sch.legalEntityCode and u.userId=:userId) ) "
				//+ "			  AND  IFNULL(auditStatus,'NotNull') IN ('AW','AC')  )   " );
				 
				
				if(roleId != null && (roleId.equalsIgnoreCase("G1") || roleId.equalsIgnoreCase("G6"))) {
					queryString.append(" OR (sch.unitId IN (:maintEntities) AND  IFNULL(auditStatus,'NotNull') IN ('AI','AS','AR','AW','AC') ) ");
				}else if(roleId != null && !(roleId.equalsIgnoreCase("G2") || roleId.equalsIgnoreCase("G8"))){
					queryString.append(" OR ( (sch.unitId IN  (SELECT u.unitCode FROM User u WHERE u.unitCode=sch.unitId and  u.legalEntityCode =sch.legalEntityCode and u.userId=:userId) ) "
							+ " AND  IFNULL(auditStatus,'NotNull') IN ('AW','AC')  ) ");
				}
				queryString.append(" ) ");
		// Condition end here
				/*if(!isDeptAsRMD) {
					queryString.append(" and  sch.auditTypeCode IN (SELECT asgm.auditTypeCode FROM MaintEntityAuditSubgroupMapping as asgm WHERE asgm.legalEntityCode=sch.legalEntityCode and asgm.mappingType='U' and asgm.id =:userId )"); // To show the Assessments to which the Assessor Mapped (Department mapping)
				}*/
				//if(visibleAuditList !=null && visibleAuditList.s)
				//+ " and (sch.auditId  IN :visibleAuditList  OR auditStatus in ('AI','AS','AR','AC','AU','UP','AD','AW')) " //commented to remove Visibility cond, Condition included to get the Audits as per the Visibility logic maintained in general parameter.
						
				
		System.out.println("Before the qry....");
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( "
					+ " sch.auditTypeCode IN (SELECT a.auditTypeCode FROM MaintAuditTypeDesc a  WHERE a.legalEntityCode=sch.legalEntityCode AND (a.auditTypeDesc LIKE :search OR a.auditTypeCode LIKE :search)) "
					+ " or planId like :search "
					+ " or auditId like :search "
					//+ " or unitId like :search "
					+ " or sch.unitId IN (SELECT m.unitCode FROM MaintEntity m WHERE m.legalEntityCode=sch.legalEntityCode AND (m.unitName LIKE :search or m.unitCode LIKE :search )) "
					+ " or sch.unitId IN (SELECT m.unitCode FROM MaintEntity m,GeneralParameter g WHERE sch.unitId=m.unitCode and m.legalEntityCode=sch.legalEntityCode AND m.legalEntityCode =g.legalEntityCode and g.key1='UNIT_TYPE' and g.key2='UNIT_TYPE' and g.value=m.unitType and g.description LIKE :search) "
					+ " or sch.unitId IN (SELECT m.unitCode FROM MaintEntity m,MaintCriticality c WHERE sch.unitId=m.unitCode and m.legalEntityCode=sch.legalEntityCode AND m.legalEntityCode =c.legalEntityCode and m.uCriticality=c.criticalityCode and c.criticalityOfType='Entity' and  c.criticalityDesc LIKE :search) "
					+ " or sch.unitId IN (SELECT m.unitCode FROM MaintEntity m,MaintAuditParameter p WHERE sch.unitId=m.unitCode and m.legalEntityCode=sch.legalEntityCode AND m.legalEntityCode =p.legalEntityCode and m.uCriticality=p.uCriticality and sch.auditTypeCode=p.auditTypeCode and  p.duration LIKE :search) "
					+ " or sch.auditId IN (SELECT t.auditId FROM User u,AuditTeam t WHERE t.auditId=sch.auditId and t.legalEntityCode =sch.legalEntityCode and t.legalEntityCode =u.legalEntityCode and t.userId=u.userId and (t.userId Like :search or u.firstName LIKE :search or u.lastName Like :search) ) "
					+ " or DATE_FORMAT(sch.scheduleStartDate, '%d %b %Y') LIKE :search "
					+ " or DATE_FORMAT(sch.scheduleEndDate, '%d %b %Y') LIKE :search "
					+ " or DATE_FORMAT(sch.actualStartDate, '%d %b %Y') LIKE :search "
					+ " or DATE_FORMAT(sch.actualEndDate, '%d %b %Y') LIKE :search "
					+ " or DATE_FORMAT(sch.lastAuditDate, '%d %b %Y') LIKE :search "
					+ " or sch.maker IN (SELECT u.userId FROM User u WHERE u.legalEntityCode =sch.legalEntityCode and (u.userId Like :search or u.firstName LIKE :search or u.lastName Like :search) ) "
					+ " or sch.auditStatus IN (SELECT g.value FROM GeneralParameter g WHERE sch.legalEntityCode =g.legalEntityCode and g.key1='STATUS' and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus and g.description LIKE :search) "
					+ " or sch.reportStatus IN (SELECT g.value FROM GeneralParameter g WHERE sch.legalEntityCode =g.legalEntityCode and g.key1='STATUS' and g.key2='REPORT_STATUS' and g.value=sch.reportStatus and g.description LIKE :search) "
					// + " or teamMember like :search "
					+ " or sch.auditId IN (select t.auditId from AuditTeam t, User u where t.legalEntityCode=sch.legalEntityCode AND t.auditId=sch.auditId AND t.userType='M' AND t.legalEntityCode=u.legalEntityCode AND t.userId=u.userId "
					+ "						AND sch.unitId=u.unitCode AND (u.firstName like :search OR u.middleName like :search OR u.lastName like :search OR u.userId like :search )) "
					
					//+ " ) "
					
						+ " or sch.auditStatus IN (SELECT g.value FROM GeneralParameter g WHERE sch.legalEntityCode =g.legalEntityCode and g.key1='STATUS' "
						+ " and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus "
						+ " and (g.description LIKE :search or g.value LIKE :search)"
						+ " and   ( sch.observationStatus !='AE'  ) "
						+ " ) "
						
						+ " or sch.auditStatus IN (SELECT g.value FROM GeneralParameter g WHERE sch.legalEntityCode =g.legalEntityCode and g.key1='STATUS' "
						+ " and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus "
						+ " and (g.description LIKE :search or g.value LIKE :search)"
						+ " and   ( sch.observationStatus  ='AE' and sch.auditStatus='AW'  ) "
						+ " ) " 
						
						+ "  " );
						
						if((search.length()==2 && search.toUpperCase().startsWith("EX") )
						|| (search.length()==3 && search.toUpperCase().startsWith("EXI"))
						|| (search.length()==4 && search.toUpperCase().startsWith("EXIT"))
						|| ((search.length()==5 || search.length()==6) && search.toUpperCase().startsWith("EXITE")) ){
							queryString.append("Or (sch.auditStatus='AI' and sch.observationStatus='AE' ) )");
						}	
						else{
							queryString.append(" )" );
							if(search.toUpperCase().startsWith("INI")){
								queryString.append("and  ( sch.auditStatus='AI' and (sch.observationStatus is null or sch.observationStatus='' ) )");
							}
						}
					/*queryString.append(" )" );*/
		}
		
		/*queryString=queryString.append("  ORDER BY FIELD(auditStatus, 'AI','P','AP','AS','AC','AU') asc ");*/
		String[] columns={
				"auditTypeCode",
				"(auditId *1 ), (planId *1 )", //"CAST(planId AS Integer),CAST(auditId AS Integer) "
				"unitType",
				"(unitId * 1) ",  //"unitId",
				"uCriticality",
				"duration",
				"lastAuditDate",//last audit date actually 
				"sch.scheduleStartDate",
				"sch.scheduleEndDate",
				"sch.actualStartDate",
				"sch.actualEndDate",
				"teamLead", // Team Lead
				// "sch.maker", // Team Member
				"sch.maker",
				"sch.reportStatus",
				"sch.auditStatus"
		};

		/*if(orderColumn!=null
				&& !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}*/
		logger.info(" orderColumn **.... "+ orderColumn +"  orderDirection ...."+ orderDirection );
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" ORDER BY FIELD(auditStatus, 'AI','AP','AS','AR','AC') asc  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Initiated, Planned, Submitted, Respond, Reviewed, Closed
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			if(orderColumn==0) {
				queryString=queryString.append(" ORDER BY  ").append(columns[orderColumn]).append(" ").append(orderDirection).append(" , FIELD(auditStatus, 'AI','AP','AS','AR','AC') asc ,(auditId *1 ) asc  ");
			}else {
				queryString=queryString.append(" ORDER BY  ").append(columns[orderColumn]).append(" ").append(orderDirection);
			}
		}else {
			queryString=queryString.append(" ORDER BY FIELD(auditStatus, 'AI','AP','AS','AR','AC') asc  ");
		}
		
		System.out.println("queryString .... "+ queryString.toString());
		
		Query query=(Query) session.createQuery(queryString.toString())				
				.setResultTransformer(new ResultTransformer() {
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						AuditSchedule auditSchedule=null;
						//System.out.println("tuple[0] ...."+tuple[0]);
						if(tuple[0]!=null){
							auditSchedule=(AuditSchedule)tuple[0];
							/* session.evict(maintEntity);*/
							
							if(tuple[1]!=null){
								auditSchedule.setLegalEntityCode(Integer.parseInt(tuple[1].toString()));								
							}
							if(tuple[2]!=null){
								auditSchedule.setAuditTypeCode(tuple[2].toString());
							}
							
							if(tuple[3]!=null){
								auditSchedule.setPlanId(tuple[3].toString());
							}
							
							if(tuple[4]!=null){
								auditSchedule.setAuditId(tuple[4].toString());
							}
							if(tuple[5]!=null){
								auditSchedule.setUnitId(tuple[5].toString());
							}
							if(tuple[6]!=null){
								auditSchedule.setUnitType(tuple[6].toString());
							}
							if(tuple[7]!=null){
								auditSchedule.setuCriticality(tuple[7].toString());
							}
							if(tuple[8]!=null){ 
									String teamLead = userDtls.get(tuple[8].toString());
									auditSchedule.setTeamLead(teamLead.equalsIgnoreCase("Me")?"Me":tuple[8].toString()+"-"+teamLead);
								 
								//auditSchedule.setTeamLead(tuple[8].toString()+"-"+userDtls.get(tuple[8].toString()));
								//auditSchedule.setTeamLead(userDtls.get(tuple[8].toString()));
							}
							if(tuple[9]!=null){
								auditSchedule.setDuration(tuple[9].toString());
							}
							if(tuple[10]!=null){
								auditSchedule.setFrequency(tuple[10].toString());
							}
							if(tuple[11]!=null){
								if("P".equalsIgnoreCase(tuple[11].toString()))auditSchedule.setAuditStatus("AP"); // temp purpose as in few earlier records the Planned status is reflecting as "P"
								else if("AE".equalsIgnoreCase(auditSchedule.getObservationStatus())) auditSchedule.setAuditStatus(tuple[18] !=null ?tuple[18].toString() :tuple[11].toString() );
								else auditSchedule.setAuditStatus(tuple[11].toString());
							}
							if(tuple[12]!=null){
								auditSchedule.setMaker(tuple[12].toString());
							}
							if(tuple[13]!=null && auditSchedule.getRespondedFindings() != null){
								auditSchedule.setResponseReject(Integer.parseInt(tuple[13].toString()));
								auditSchedule.setRespondedFindings(auditSchedule.getRespondedFindings()- (Integer.parseInt(tuple[13].toString()))); // Included to find out the Responded Observations
							}else auditSchedule.setResponseReject(0);
							
							if(tuple[14]!=null){
								auditSchedule.setComplianceCount(Integer.parseInt(tuple[14].toString()));
							}else auditSchedule.setComplianceCount(0);
							
							if(tuple[15]!=null){
								auditSchedule.setNotApplicableCount(Integer.parseInt(tuple[15].toString()));
							}else auditSchedule.setNotApplicableCount(0);
							
							if(tuple[16]!=null){
								auditSchedule.setEarlyStartAllowed(tuple[16].toString());
							}else auditSchedule.setEarlyStartAllowed("Y");
							
							if(tuple[17]!=null){
								auditSchedule.setAuditOffsetDays(Integer.parseInt(tuple[17].toString()));
							}else auditSchedule.setAuditOffsetDays(0);
							
							if(tuple[19]!=null){
								String teamMembers="";
								String[] members = tuple[19].toString().split(",");
								for (String member: members) {
									teamMembers+=member+"-"+userDtls.get(member)+",";
								}
								auditSchedule.setTeamMember(teamMembers.substring(0,teamMembers.length()-1));
							}
							
							if(tuple[20]!=null) auditSchedule.setReportStatus(tuple[20].toString());
							if(tuple[21]!=null) auditSchedule.setReportStatusDesc(tuple[21].toString());
							
							if(tuple[22]!=null) auditSchedule.setStatus(tuple[22].toString());
							
							/*int tmSize =auditSchedule.getAuditTeams().size()-1;
							String teamMember="";
							System.out.println("Audit Id .... "+ auditSchedule.getAuditId()+" tmSize .."+tmSize);
							for( AuditTeam adtTm: auditSchedule.getAuditTeams() ) {
								
								adtTm.setUserId(adtTm.getUserId()+"-"+userDtls.get(adtTm.getUserId()));
								
								if(!"L".equalsIgnoreCase(adtTm.getUserType())) teamMember +=adtTm.getUserId() + ",";
								System.out.println("teamMember .... "+ teamMember);
							}if(tmSize >0 ) auditSchedule.setTeamMember(teamMember.substring(0,teamMember.lastIndexOf(",")));*/
							//System.out.println("Team member .... "+auditSchedule.getTeamMember());
							//System.out.println("Team size ...."+auditSchedule.getAuditTeams().size());
							
						}
						return auditSchedule;
					}
			@Override
			public List transformList(List collection) {
				return collection;
			}
		})		
				.setParameter("legalEntityCode",legalEntityCode).setParameter("userId",userId); //.setParameter("visibleAuditList", visibleAuditList);
		if(roleId != null && (roleId.equalsIgnoreCase("G1") || roleId.equalsIgnoreCase("G6"))) 
			query.setParameter("maintEntities", maintEntities);
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		System.out.println("page :: "+page +"size :: "+ size);
		List<AuditSchedule> tmpAuditSchedule =query.list(); 
		Long totalRecords=null;
		try {
			totalRecords = (long)tmpAuditSchedule.size();
		}catch(Exception e) {
			totalRecords = (long)0;
		}
		List<AuditSchedule> auditSchedule=query.setFirstResult((page*size)).setMaxResults(size).list();
		
		System.out.println("Bfr size tmpAuditSchedule is .... "+tmpAuditSchedule.size()+" totalRecords:: "+ totalRecords);
		
		/*int i=0;
		for(AuditSchedule adSch:auditSchedule){
			System.out.println("Inside adSch ...");
			if("A2".equalsIgnoreCase(adSch.getAuditId()));
			else 	tmpAuditSchedule.add(adSch);
			System.out.println("after the call");
			
		}
		System.out.println("size tmpAuditSchedule .... "+tmpAuditSchedule);*/
		
		
		dataTableResponse.setData(auditSchedule);
		dataTableResponse.setRecordsTotal(totalRecords);
		dataTableResponse.setRecordsFiltered(totalRecords);
		
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		return dataTableResponse;
	
	}
}
