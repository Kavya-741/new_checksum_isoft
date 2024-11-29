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
import org.hibernate.transform.Transformers;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintReportStandardStmt;
import com.bankaudit.model.MaintReportStandardStmtWrk;

@Repository("maintReportStandardStmtDao")
public class MaintReportStandardStmtDaoImpl extends AbstractDao implements MaintReportStandardStmtDao {

	static final Logger logger = Logger.getLogger(MaintReportStandardStmtDaoImpl.class);
	
	
	@Override
	public void deleteMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String statusAuth) {
		Session session = getSession();
		if (!BankAuditConstant.STATUS_AUTH.equals(statusAuth)) {
			session.createQuery("delete from MaintReportStandardStmtWrk where legalEntityCode =:legalEntityCode  and " + " mappingId =:mappingId ")
					.setParameter("mappingId", mappingId).setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		} else {
			session.createQuery("delete from MaintReportStandardStmt where legalEntityCode =:legalEntityCode  and " + " mappingId =:mappingId ")
					.setParameter("mappingId", mappingId).setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}
	}
	@Override
	public MaintReportStandardStmt getMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String status) {
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public DataTableResponse getAllMaintReportStandardStmt(Integer legalEntityCode, String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size) {
	    Session session = getSession();
	    session.setDefaultReadOnly(true);
		List<MaintReportStandardStmt> maintReportStandardStmtList = null;
		
		DataTableResponse dataTableResponse=new DataTableResponse();
		StringBuilder queryString=new StringBuilder("SELECT legal_entity_code, mapping_id, report_id, standard_stmnt_non_compliance, "
				+ "standard_stmnt_compliance, reference, standard_stmnt_non_compliance_print, standard_stmnt_compliance_print, "
				+ "entity_status, status, maker, auth_rej_remarks, maker_timestamp, checker, checker_timestamp, "
				
				+ " (SELECT CONCAT(atd.audit_type_code , '-' , atd.audit_type_desc) "
				+ "FROM maint_audit_type_desc atd WHERE atd.audit_type_code=result.audit_type_code "
				+ "AND atd.legal_entity_code=result.legal_entity_code ) AS audit_type_code , "
				
				+ "(SELECT CONCAT(ag.audit_group_code , '-' , ag.audit_group_name) FROM maint_audit_group "
				+ "ag WHERE ag.audit_group_code=result.audit_group_code AND ag.audit_type_code=result.audit_type_code "
				+ "AND ag.legal_entity_code=result.legal_entity_code ) AS audit_group_code , "
				
				+ "(SELECT CONCAT(asg.audit_sub_group_code , '-' , asg.audit_sub_group_name) FROM maint_audit_subgroup asg "
				+ "WHERE asg.audit_sub_group_code=result.audit_sub_group_code AND asg.audit_group_code=result.audit_group_code "
				+ "AND asg.audit_type_code=result.audit_type_code AND asg.legal_entity_code=result.legal_entity_code ) AS audit_sub_group_code ,"
				
				+ " (SELECT CONCAT(aa.activity_id , '-' , aa.activity_name) FROM maint_audit_activity aa "
				+ "WHERE aa.activity_id=result.activity_id AND aa.legal_entity_code=result.legal_entity_code ) "
				+ "AS activity_id , "
				
				+ "(SELECT CONCAT(ap.process_id , '-' , ap.process_name) "
				+ "FROM maint_audit_process ap WHERE ap.process_id=result.process_id "
				+ "AND ap.legal_entity_code=result.legal_entity_code ) AS process_id , "
				
				+ "(SELECT CONCAT(af.finding_id , '-' , af.finding_name , '-' , af.f_criticality) FROM maint_audit_finding af"
				+ " WHERE af.finding_id=result.finding_id AND af.legal_entity_code=result.legal_entity_code ) AS finding_id"
				
				+ " FROM (SELECT mrss.*, apfm.audit_group_code,  apfm.audit_sub_group_code, apfm.activity_id, apfm.process_id, "
				+ "apfm.finding_id FROM maint_report_standard_stmt mrss INNER JOIN activity_process_finding_mapping apfm "
				+ "ON mrss.mapping_id = apfm.mapping_id AND mrss.legal_entity_code =apfm.legal_entity_code "
				
				+ "UNION SELECT mrsw.*, apfm.audit_group_code,  apfm.audit_sub_group_code, apfm.activity_id, apfm.process_id, "
				+ "apfm.finding_id  FROM maint_report_standard_stmt_wrk  mrsw INNER JOIN activity_process_finding_mapping apfm"
				+ " ON mrsw.mapping_id = apfm.mapping_id AND mrsw.legal_entity_code =apfm.legal_entity_code ) "
				+ " result  WHERE legal_entity_code =:legal_entity_code ");
		
		if(!BankAuditUtil.isEmptyString(search)){
			queryString=queryString.append("AND (result.mapping_id LIKE :search OR result.audit_type_code IN (SELECT atd.audit_type_code FROM maint_audit_type_desc "
					+ "atd WHERE atd.audit_type_desc LIKE :search AND atd.legal_entity_code=result.legal_entity_code) OR "
					+ " result.audit_type_code LIKE :search OR  result.audit_group_code IN (SELECT ag.audit_group_code "
					+ "FROM maint_audit_group ag WHERE ag.audit_group_name LIKE :search AND ag.legal_entity_code=result.legal_entity_code ) "
					+ "OR result.audit_group_code LIKE :search OR  result.audit_sub_group_code IN (SELECT asg.audit_sub_group_code "
					+ "FROM maint_audit_subgroup asg WHERE asg.audit_sub_group_name LIKE :search AND asg.legal_entity_code=result.legal_entity_code) "
					+ "OR  result.audit_sub_group_code LIKE :search OR  result.activity_id IN (SELECT aa.activity_id FROM maint_audit_activity aa"
					+ " WHERE aa.activity_name LIKE :search AND aa.legal_entity_code=result.legal_entity_code ) OR  result.activity_id LIKE :search "
					+ "OR  result.process_id IN (SELECT ap.process_id FROM maint_audit_process ap WHERE ap.process_name LIKE :search "
					+ "AND ap.legal_entity_code=result.legal_entity_code) OR  result.process_id LIKE :search OR  result.maker LIKE :search OR "
					+ " result.maker IN (SELECT u.user_id FROM user u WHERE (CONCAT(u.first_name , ' ' , u.last_name) LIKE :search) "
					+ "AND u.legal_entity_code=result.legal_entity_code) OR  result.status IN (SELECT VALUE FROM general_parameter "
					+ " WHERE key_1='STATUS' AND  description LIKE :search) OR  result.finding_id IN "
					+ "(SELECT af.finding_id FROM maint_audit_finding af WHERE af.finding_name LIKE :search AND "
					+ "af.legal_entity_code=result.legal_entity_code) OR  result.finding_id LIKE :search"
					+ " or  result.maker like :search  "
					+ " or  result.standard_stmnt_compliance like :search  "
					+ " or  result.standard_stmnt_non_compliance like :search  "
					+ " or  result.status IN (SELECT value FROM general_parameter WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " or  result.entity_status IN (SELECT value FROM general_parameter  WHERE key_1='ENTITY_STATUS' and key_2='ENTITY_STATUS' and description LIKE :search ) "
                    + " or  result.maker IN ( "
					+ " select u.user_id "
						 + " from user u where "
						 + " (concat(u.first_name , ' ' , u.last_name) like :search  ) "
						 + " and u.legal_entity_code =:legal_entity_code ) "
						 + " ) ");
		}
	
		String[] columns={
				"result.mapping_id",
				"result.audit_type_code",
				"result.audit_group_code",
				"result.audit_sub_group_code",
				"result.activity_id",
				"result.process_id",
				"result.finding_id",
				"result.standard_stmnt_compliance",
				"result.standard_stmnt_non_compliance",
				"result.maker",
				"result.status",
				"result.maker",
				"result.entity_status"
				};
		
		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by FIELD(STATUS,'DF') DESC,  ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {
					
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						
						MaintReportStandardStmt maintReportStandardStmt=null;
						
						if(tuple!=null){
							maintReportStandardStmt=new MaintReportStandardStmt();
							if(tuple[0]!=null){
								maintReportStandardStmt.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								maintReportStandardStmt.setMappingId(tuple[1].toString());
							}
							if(tuple[2]!=null){
								maintReportStandardStmt.setReportId(tuple[2].toString());
							}
							if(tuple[3]!=null){
								maintReportStandardStmt.setStandardStmntNonCompliance(tuple[3].toString());
							}
							if(tuple[4]!=null){
								maintReportStandardStmt.setStandardStmntCompliance(tuple[4].toString());
							}
							if(tuple[5]!=null){
								maintReportStandardStmt.setReference(tuple[5].toString());
							}
							if(tuple[6]!=null){
								maintReportStandardStmt.setStandardStmntNonCompliancePrint(tuple[6].toString());
							}
							if(tuple[7]!=null){
								maintReportStandardStmt.setStandardStmntCompliancePrint(tuple[7].toString());
							}
							if(tuple[8]!=null){
								maintReportStandardStmt.setEntityStatus(tuple[8].toString());
							}
							if(tuple[9]!=null){
								maintReportStandardStmt.setStatus(tuple[9].toString());
							}
							if(tuple[10]!=null){
								maintReportStandardStmt.setMaker(tuple[10].toString());
							}
							if(tuple[11]!=null){
								maintReportStandardStmt.setAuthRejRemarks(tuple[11].toString());
							}
							if(tuple[12]!=null){
								maintReportStandardStmt.setMakerTimestamp((Date)tuple[12]);
							}
							if(tuple[13]!=null){
								maintReportStandardStmt.setChecker(tuple[13].toString());
							}
							if(tuple[14]!=null){
								maintReportStandardStmt.setCheckerTimestamp((Date)tuple[14]);
							}
							if(tuple[15]!=null){
								maintReportStandardStmt.setAuditTypeCode(tuple[15].toString());
							}
							if(tuple[16]!=null){
								maintReportStandardStmt.setAuditGroupCode(tuple[16].toString());
							}
							if(tuple[17]!=null){
								maintReportStandardStmt.setAuditSubGroupCode(tuple[17].toString());
							}
							if(tuple[18]!=null){
								maintReportStandardStmt.setActivityCode(tuple[18].toString());
							}
							if(tuple[19]!=null){
								maintReportStandardStmt.setProcessCode(tuple[19].toString());
							}
							if(tuple[20]!=null){
								maintReportStandardStmt.setFindingCode(tuple[20].toString());
							}
						}
						return maintReportStandardStmt;
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
			
			maintReportStandardStmtList= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				maintReportStandardStmtList.add((MaintReportStandardStmt) resultScroll.get(0));
			    if (!resultScroll.next())
			        break;
			}
			dataTableResponse.setData(maintReportStandardStmtList);
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

	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<MaintReportStandardStmt> getMaintReportStandardStmtWrkByMappindId(Integer legalEntityCode, String mappingId) {
		Session session=getSession();
		return session.createSQLQuery(" SELECT legal_entity_code AS legalEntityCode, mapping_id AS mappingId, report_id AS reportId, "
				+ "standard_stmnt_non_compliance AS standardStmntNonCompliance, standard_stmnt_compliance AS standardStmntCompliance, "
				+ "reference, standard_stmnt_non_compliance_print AS standardStmntNonCompliancePrint, standard_stmnt_compliance_print AS standardStmntCompliancePrint, "
				+ "entity_status AS entityStatus, STATUS, maker, auth_rej_remarks AS authRejRemarks, maker_timestamp AS makerTimestamp, "
				+ "checker, checker_timestamp AS checkerTimestamp,  "
				+ "(SELECT CONCAT(atd.audit_type_code , '-' , atd.audit_type_desc) FROM maint_audit_type_desc atd WHERE "
				+ "atd.audit_type_code=result.audit_type_code AND atd.legal_entity_code=result.legal_entity_code ) AS auditTypeCode ,  "
				+ "(SELECT CONCAT(ag.audit_group_code , '-' , ag.audit_group_name) FROM maint_audit_group ag WHERE ag.audit_group_code=result.audit_group_code "
				+ "AND ag.audit_type_code=result.audit_type_code AND ag.legal_entity_code=result.legal_entity_code ) AS auditGroupCode ,  "
				+ "(SELECT CONCAT(asg.audit_sub_group_code , '-' , asg.audit_sub_group_name) FROM maint_audit_subgroup asg "
				+ "WHERE asg.audit_sub_group_code=result.audit_sub_group_code AND asg.audit_group_code=result.audit_group_code AND "
				+ "asg.audit_type_code=result.audit_type_code AND asg.legal_entity_code=result.legal_entity_code ) AS auditSubGroupCode ,  "
				+ "(SELECT CONCAT(aa.activity_id , '-' , aa.activity_name) FROM maint_audit_activity aa WHERE aa.activity_id=result.activity_id "
				+ "AND aa.legal_entity_code=result.legal_entity_code ) AS activityCode ,  (SELECT CONCAT(ap.process_id , '-' , ap.process_name) "
				+ "FROM maint_audit_process ap WHERE ap.process_id=result.process_id AND ap.legal_entity_code=result.legal_entity_code ) AS processCode ,  "
				+ "(SELECT CONCAT(af.finding_id , '-' , af.finding_name , '-' , af.f_criticality) FROM maint_audit_finding af WHERE af.finding_id=result.finding_id "
				+ "AND af.legal_entity_code=result.legal_entity_code ) AS findingCode  FROM (SELECT mrss.*, apfm.audit_group_code,  "
				+ "apfm.audit_sub_group_code, apfm.activity_id, apfm.process_id, apfm.finding_id FROM maint_report_standard_stmt_wrk mrss "
				+ "INNER JOIN activity_process_finding_mapping apfm ON mrss.mapping_id = apfm.mapping_id AND mrss.legal_entity_code =apfm.legal_entity_code  ) "
				+ "result  WHERE legal_entity_code =:legalEntityCode and mapping_id = :mappingId").setParameter("legalEntityCode", legalEntityCode)
		.setParameter("mappingId", mappingId)
		.setResultTransformer(Transformers.aliasToBean(MaintReportStandardStmt.class)).list();
	}
	
	@SuppressWarnings({"unchecked", "deprecation"})
	@Override
	public List<MaintReportStandardStmt> getMaintReportStandardStmtByMappindId(Integer legalEntityCode, String mappingId) {
		Session session=getSession();
		return session.createSQLQuery(" SELECT legal_entity_code AS legalEntityCode, mapping_id AS mappingId, report_id AS reportId, "
				+ "standard_stmnt_non_compliance AS standardStmntNonCompliance, standard_stmnt_compliance AS standardStmntCompliance, "
				+ "reference, standard_stmnt_non_compliance_print AS standardStmntNonCompliancePrint, standard_stmnt_compliance_print AS standardStmntCompliancePrint, "
				+ "entity_status AS entityStatus, STATUS, maker, auth_rej_remarks AS authRejRemarks, maker_timestamp AS makerTimestamp, "
				+ "checker, checker_timestamp AS checkerTimestamp,  "
				+ "(SELECT CONCAT(atd.audit_type_code , '-' , atd.audit_type_desc) FROM maint_audit_type_desc atd WHERE "
				+ "atd.audit_type_code=result.audit_type_code AND atd.legal_entity_code=result.legal_entity_code ) AS auditTypeCode ,  "
				+ "(SELECT CONCAT(ag.audit_group_code , '-' , ag.audit_group_name) FROM maint_audit_group ag WHERE ag.audit_group_code=result.audit_group_code "
				+ "AND ag.audit_type_code=result.audit_type_code AND ag.legal_entity_code=result.legal_entity_code ) AS auditGroupCode ,  "
				+ "(SELECT CONCAT(asg.audit_sub_group_code , '-' , asg.audit_sub_group_name) FROM maint_audit_subgroup asg "
				+ "WHERE asg.audit_sub_group_code=result.audit_sub_group_code AND asg.audit_group_code=result.audit_group_code AND "
				+ "asg.audit_type_code=result.audit_type_code AND asg.legal_entity_code=result.legal_entity_code ) AS auditSubGroupCode ,  "
				+ "(SELECT CONCAT(aa.activity_id , '-' , aa.activity_name) FROM maint_audit_activity aa WHERE aa.activity_id=result.activity_id "
				+ "AND aa.legal_entity_code=result.legal_entity_code ) AS activityCode ,  (SELECT CONCAT(ap.process_id , '-' , ap.process_name) "
				+ "FROM maint_audit_process ap WHERE ap.process_id=result.process_id AND ap.legal_entity_code=result.legal_entity_code ) AS processCode ,  "
				+ "(SELECT CONCAT(af.finding_id , '-' , af.finding_name , '-' , af.f_criticality) FROM maint_audit_finding af WHERE af.finding_id=result.finding_id "
				+ "AND af.legal_entity_code=result.legal_entity_code ) AS findingCode  FROM (SELECT mrss.*, apfm.audit_group_code,  "
				+ "apfm.audit_sub_group_code, apfm.activity_id, apfm.process_id, apfm.finding_id FROM maint_report_standard_stmt mrss "
				+ "INNER JOIN activity_process_finding_mapping apfm ON mrss.mapping_id = apfm.mapping_id AND mrss.legal_entity_code =apfm.legal_entity_code  ) "
				+ "result  WHERE legal_entity_code =:legalEntityCode and mapping_id = :mappingId").setParameter("legalEntityCode", legalEntityCode)
		.setParameter("mappingId", mappingId)
		.setResultTransformer(Transformers.aliasToBean(MaintReportStandardStmt.class)).list();
	}
	
}
