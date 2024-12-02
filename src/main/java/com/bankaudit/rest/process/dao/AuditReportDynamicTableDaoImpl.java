package com.bankaudit.rest.process.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.bankaudit.dao.AbstractDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.process.model.AuditReportDynamicTable;
import com.bankaudit.process.model.AuditSchedule;
import com.bankaudit.util.BankAuditUtil;

@Repository
public class AuditReportDynamicTableDaoImpl extends AbstractDao implements AuditReportDynamicTableDao{

	static final Logger logger = Logger.getLogger(AuditReportDynamicTableDaoImpl.class);

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public DataTableResponse getDynamicTables(int legalEntityCode,String userId, String role, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {

		Session session = getSession();
		session.setDefaultReadOnly(true);

		logger.info("Inside getDynamicTables ..## "+ legalEntityCode +" :: "+ userId +" :: "+role);
		DataTableResponse dataTableResponse=new DataTableResponse();
		try {
		StringBuilder queryString=new StringBuilder("select ard as ardTable, "// 0
				 + " sch as auditSchedule, "
				 + "   (select concat(auditTypeCode , '-' , auditTypeDesc) from MaintAuditTypeDesc at where at.auditTypeCode=ard.auditTypeCode and at.legalEntityCode= ard.legalEntityCode ) as auditTypeCodeTmp, "  // 2
				 + "   (Select concat(unitCode , '--' , unitName) from  MaintEntity e where e.unitCode =ard.unitId and e.legalEntityCode =ard.legalEntityCode ) as unitIdTmp,  "  // 3
				 + "   (Select concat(sch.auditStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS' and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus) as auditStatusDescTmp, " // 4
				 + "   (Select concat(u.userId , '-' , ifnull(u.firstName,''), ' ',ifnull(u.middleName,''),' ',ifnull(u.lastName,'') )  from  User u where u.legalEntityCode =ard.legalEntityCode and u.userId=ard.maker) as makerTmp, "  // 5
				 + "   (Select concat(g.value , '-' , ifnull(g.description,''))  from  GeneralParameter g where g.legalEntityCode =ard.legalEntityCode and "
				 + "   g.value=ard.statementType and g.key1 = 'AUDIT_DYNAMIC_DATA' and g.key2 = 'ENRICHMENT_TYPE_OR_OPTIONS' ) as stmtType, "  // 6

				 + "   ( Select distinct concat(grp.auditGroupCode,'-',grp.auditGroupName) "
			   		+ "  from  MaintAuditGroup grp  where  grp.auditGroupCode = ard.auditGroupCode "
			   		+ "  and grp.auditTypeCode=ard.auditTypeCode  and grp.legalEntityCode=ard.legalEntityCode"
			   		+ "  and grp.status = 'A' and grp.entityStatus = 'A') as chapter, " //7

			   	 + "  ( Select distinct concat(subgrp.auditSubGroupCode,'-',subgrp.auditSubGroupName)  "
					+ "  from MaintAuditSubgroup subgrp  where subgrp.auditSubGroupCode = ard.auditSubGroupCode"
					+ "  and subgrp.auditTypeCode = ard.auditTypeCode and subgrp.auditGroupCode = ard.auditGroupCode "
					+ "  and subgrp.legalEntityCode = ard.legalEntityCode "
					+ "  and subgrp.status = 'A' and subgrp.entityStatus = 'A' ) as section, "				//8

				 + "  ( Select distinct concat(act.activityCode,'-',act.activityName)  "
					+ "  from MaintAuditActivity act where act.activityCode = ard.activityCode "
					+ "  and act.legalEntityCode = ard.legalEntityCode "
					+ "  and act.status = 'A' and act.entityStatus = 'A' ) as product , "		//9

				 + " ( Select  distinct concat(proc.processCode , '-', proc.processName) "
					+ "  from MaintAuditProcess proc  where  proc.processCode = ard.processCode "
					+ "  and proc.legalEntityCode = ard.legalEntityCode"
					+ "  and proc.status = 'A' and proc.entityStatus = 'A') as process,  " //10
					
				 + " ( Select  distinct  fileName  "
					+ "  from AuditLevelDocumentDetails doc  where  doc.auditId = ard.auditId  and doc.legalEntityCode = ard.legalEntityCode "
					+ "  and doc.auditTypeCode = ard.auditTypeCode  and doc.id = ard.docId  and doc.uploadStatus = 'S'  ) as fileName  "  //11
					
				 + " from AuditReportDynamicTable ard , AuditSchedule sch , AuditTeam at " 
				 + " where ard.legalEntityCode=sch.legalEntityCode and sch.legalEntityCode=at.legalEntityCode  "
				 + "     and sch.auditId=ard.auditId and sch.auditId=at.auditId and ard.auditId=at.auditId  "
				 + " 	 and IFNULL(sch.auditStatus,'NotNull') not in ('AD','AU','DRP','UP','AP') "
				 + "     and ard.legalEntityCode =:legalEntityCode and at.userId=:userId ");


		logger.info("Before the qry....");

		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append(" and ( "
					+ " ard.auditTypeCode IN (SELECT a.auditTypeCode FROM MaintAuditTypeDesc a  WHERE a.legalEntityCode=ard.legalEntityCode AND (a.auditTypeDesc LIKE :search OR a.auditTypeCode LIKE :search)) "
					+ " or ard.planId like :search "
					+ " or ard.auditId like :search "
					+ " or ard.unitId like :search "
					+ " or ard.unitId IN (SELECT m.unitCode FROM MaintEntity m WHERE m.legalEntityCode=ard.legalEntityCode AND (m.unitName LIKE :search or m.unitCode LIKE :search )) "
					+ " or ard.maker IN (SELECT u.userId FROM User u WHERE u.legalEntityCode =ard.legalEntityCode and (u.userId Like :search or u.firstName LIKE :search or u.lastName Like :search) ) "
					//+ " or sch.auditStatus IN (SELECT g.value FROM GeneralParameter g WHERE sch.legalEntityCode =g.legalEntityCode and g.key1='STATUS' and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus and g.description LIKE :search) "
					//+ " or sch.auditId IN (select t.auditId from AuditTeam t, User u where t.legalEntityCode=sch.legalEntityCode AND t.auditId=sch.auditId AND t.userType='M' AND t.legalEntityCode=u.legalEntityCode AND t.userId=u.userId "
					//+ "						AND sch.unitId=u.unitCode AND (u.firstName like :search OR u.middleName like :search OR u.lastName like :search OR u.userId like :search )) "


//					+ " or ard.tableId like :search "
					+ " or ard.statementType like :search "
//					+ " or ard.tableName like :search "
					+ " or ard.sequence like :search "
					+ " or sch.auditStatus IN (SELECT g.value FROM GeneralParameter g WHERE sch.legalEntityCode =g.legalEntityCode and g.key1='STATUS' and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus and g.description LIKE :search) "

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

					//+ " ) "
					);
					if((search.length()==2 && search.toUpperCase().startsWith("EX") )
						|| (search.length()==3 && search.toUpperCase().startsWith("EXI"))
						|| (search.length()==4 && search.toUpperCase().startsWith("EXIT"))
						|| ((search.length()==5 || search.length()==6) && search.toUpperCase().startsWith("EXITE")) ){
							queryString.append("Or (sch.auditStatus='AI' and sch.observationStatus='AE' ) )");
					}	else{
						queryString.append(" )" );
						if(search.toUpperCase().startsWith("INI")){
							queryString.append("and  ( sch.auditStatus='AI' and (sch.observationStatus is null or sch.observationStatus='' ) )");
						}
					}
		}

		/*queryString=queryString.append("  ORDER BY FIELD(auditStatus, 'AI','P','AP','AS','AC','AU') asc ");*/
			String[] columns={
					"ard.auditTypeCode",
					"(ard.auditId *1 ), (ard.planId *1 )", //"CAST(planId AS Integer),CAST(auditId AS Integer) "
					"(ard.unitId * 1) ",  //"unitId",
					"ard.statementType",
					"ard.sequence",
					"sch.auditStatus",
					"ard.maker",
			};

			logger.info("orderColumn **.... "+ orderColumn +"  orderDirection ...."+ orderDirection );
			if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
				queryString=queryString.append(" ORDER BY FIELD(sch.auditStatus, 'AI','AP','AS','AR','AC') asc  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Initiated, Planned, Submitted, Respond, Reviewed, Closed
			}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
				queryString=queryString.append(" ORDER BY  ").append(columns[orderColumn]).append(" ").append(orderDirection);
			}else {
				queryString=queryString.append(" ORDER BY FIELD(sch.auditStatus, 'AI','AP','AS','AR','AC') asc  ");
			}

			logger.info("queryString ..##.. "+ queryString.toString());

		@SuppressWarnings("deprecation")
		Query query=(Query) session.createQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						AuditReportDynamicTable ardTable=null;
						if(tuple[0]!=null){
							ardTable=(AuditReportDynamicTable)tuple[0];
							AuditSchedule auditSchedule = (AuditSchedule)tuple[1];

							ardTable.setObservationStatus(auditSchedule.getObservationStatus());
							if(tuple[2]!=null) ardTable.setAuditTypeCode(tuple[2].toString());
							if(tuple[3]!=null){
								ardTable.setUnitId(tuple[3].toString().split("--")[0]);
								ardTable.setUnitName(tuple[3].toString().split("--")[1]);
							}
							if(tuple[4]!=null){
								ardTable.setAuditStatus(tuple[4].toString().split("-")[0]);
								ardTable.setAuditStatusDesc(tuple[4].toString().split("-")[1]);
							}
							if(tuple[5]!=null){
								ardTable.setMaker(tuple[5].toString());
							}
							if(tuple[6]!=null){
								ardTable.setStatementType(tuple[6].toString());
							}
							if(tuple[7]!=null)
								ardTable.setAuditGroupCode(tuple[7].toString());
							if(tuple[8]!=null)
								ardTable.setAuditSubGroupCode(tuple[8].toString());
							if(tuple[9]!=null)
								ardTable.setActivityCode(tuple[9].toString());
							if(tuple[10]!=null)
								ardTable.setProcessCode(tuple[10].toString());
							if(tuple[11]!=null)
								ardTable.setFileName(tuple[11].toString());

						}
						return ardTable;
					}
			@Override
			public List transformList(List collection) {
				return collection;
			}
		})
		.setParameter("legalEntityCode",legalEntityCode).setParameter("userId",userId);
		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}

		logger.info("page :: "+page +"size :: "+ size);
		@SuppressWarnings("deprecation")
		List<AuditReportDynamicTable> tmpAuditReportDynamicTable =query.list();
		Long totalRecords=null;
		try {
			totalRecords = (long)tmpAuditReportDynamicTable.size();
		}catch(Exception e) {
			totalRecords = (long)0;
		}
		List<AuditReportDynamicTable> auditReportDynamicTable=query.setFirstResult((page*size)).setMaxResults(size).list();

		logger.info("Bfr size tmpAuditReportDynamicTable is .... "+tmpAuditReportDynamicTable.size()+" totalRecords:: "+ totalRecords);

		dataTableResponse.setData(auditReportDynamicTable);
		dataTableResponse.setRecordsTotal(totalRecords);
		dataTableResponse.setRecordsFiltered(totalRecords);

		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		return dataTableResponse;
	}

}
