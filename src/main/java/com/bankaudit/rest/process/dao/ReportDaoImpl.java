package com.bankaudit.rest.process.dao;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bankaudit.dao.AbstractDao;
import com.bankaudit.dto.process.ComplianceTrackingReportDto;
import com.bankaudit.dto.process.LettersAuditDto;
import com.bankaudit.model.MaintLegalEntity;
import com.bankaudit.process.model.AuditSchedule;
import com.bankaudit.repository.MaintLegalEntityRepository;
import com.bankaudit.util.BankAuditUtil;

@Repository("reportDao")
public class ReportDaoImpl extends AbstractDao implements ReportDao {

	static final Logger logger = Logger.getLogger(ReportDaoImpl.class);

	@Autowired
	MaintLegalEntityRepository maintLegalEntityRepository;

	@SuppressWarnings("unchecked")
	@Override
	public boolean isDeptAsRMD(Integer legalEntityCode, String role, String userId) {
		Session session = getSession();
		boolean isDeptAsRMD = false;
		Integer isDeptAsRMDCnt = 0;
		try {
			isDeptAsRMDCnt = Integer.parseInt((session
					.createQuery("SELECT COUNT(*)  FROM MaintEntityAuditSubgroupMapping WHERE "
							+ " legalEntityCode =:legalEntityCode "
							+ " AND mappingType ='U' AND id =:userId "
							+ " AND status = 'A' AND auditTypeCode='RMD' ")
					.setParameter("legalEntityCode", legalEntityCode)
					.setParameter("userId", userId).uniqueResult()).toString());
			logger.info(" isDeptAsRMD cnt .. " + isDeptAsRMDCnt);
			if (isDeptAsRMDCnt > 0)
				isDeptAsRMD = true;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return isDeptAsRMD;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Date> getFinancialYearEndDate(Integer legalEntityCode) {
		Session session = getSession();
		session.setDefaultReadOnly(true);
		try {
			logger.info("getFinancialYearStartDate .. legalEntityCode:: " + legalEntityCode);
			StringBuilder str = new StringBuilder("SELECT DISTINCT financial_year FROM audit_schedule "
					+ "WHERE legal_entity_code =" + legalEntityCode
					+ " AND financial_year IS NOT NULL ORDER BY financial_year ASC ");

			@SuppressWarnings("deprecation")
			Query hqlQuery = getSession().createNativeQuery(str.toString());

			@SuppressWarnings("deprecation")
			List<Date> financialYearList = hqlQuery.list();

			MaintLegalEntity maintLegalEntity = maintLegalEntityRepository.findByLegalEntityCode(legalEntityCode);

			Date businessDate = maintLegalEntity.getBusinessdateTimestamp();
			Calendar cal = new GregorianCalendar();
			cal.setTime(businessDate);

			String financiyalYearEnd = "";
			String financiyalYearEndPrevious = "";
			if (cal.get(Calendar.MONTH) > 2) {
				financiyalYearEnd = (cal.get(Calendar.YEAR) + 1) + "-03-31";
				financiyalYearEndPrevious = (cal.get(Calendar.YEAR)) + "-03-31";
			} else {
				financiyalYearEnd = (cal.get(Calendar.YEAR)) + "-03-31";
				financiyalYearEndPrevious = (cal.get(Calendar.YEAR) - 1) + "-03-31";
			}

			Date financialYearDate = new SimpleDateFormat("yyyy-MM-dd").parse(financiyalYearEnd);
			Date financialYearDatePre = new SimpleDateFormat("yyyy-MM-dd").parse(financiyalYearEndPrevious);
			if (!financialYearList.contains(financialYearDate)) {
				financialYearList.add(financialYearDate);
			}
			if (!financialYearList.contains(financialYearDatePre)) {
				financialYearList.add(financialYearDatePre);
			}

			return financialYearList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<String> getAuditScheduleUnitIdsList(Integer legalEntityCode, List<String> unitList,
			LocalDate financialYear, String auditTypeCode) {
		List<String> assessmentList = null;
		try {
			StringBuilder query = new StringBuilder("SELECT audit_id  FROM audit_schedule WHERE  unit_id IN (:unitList)"
					+
					" AND financial_year=:financialYear AND legal_entity_code=:legalEntityCode AND audit_status in ('AS', 'AR', 'AW', 'AC')");
			if (!BankAuditUtil.isEmptyString(auditTypeCode)) {
				query.append("AND audit_type_code ='" + auditTypeCode + "'");
			}
			Query hqlQuery = getSession().createNativeQuery(query.toString())
					.setParameter("legalEntityCode", legalEntityCode)
					.setParameter("unitList", unitList)
					.setParameter("financialYear", (financialYear.minusYears(1)));
			assessmentList = (List<String>) hqlQuery.list();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		logger.info("assessmentList .." + assessmentList);
		return assessmentList;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ComplianceTrackingReportDto> getAuditScheduleReportList(Integer legalEntityCode, List<String> auditList,
			LocalDate financialYear, String auditTypeCode) {
		// String criticality =
		// environment.getRequiredProperty("complianceTrackingCriticality");

		StringBuilder query = new StringBuilder("SELECT ao.legal_entity_code,"
				+ "       ao.audit_type_code,"
				+ "       (SELECT Concat(e.unit_code, '--', e.unit_name)"
				+ "        FROM   maint_entity e,"
				+ "               audit_schedule s"
				+ "        WHERE  e.legal_entity_code = s.legal_entity_code"
				+ "               AND e.legal_entity_code = ao.legal_entity_code"
				+ "               AND s.legal_entity_code = ao.legal_entity_code"
				+ "               AND e.unit_code = s.unit_id"
				+ "               AND s.audit_id = ao.audit_id)                     AS unit_code,"
				+ "       (SELECT Concat(f.audit_group_code, '--', f.audit_group_name)"
				+ "        FROM   maint_audit_group f"
				+ "        WHERE  f.audit_group_code = apfm.audit_group_code"
				+ "               AND f.audit_type_code = apfm.audit_type_code"
				+ "               AND f.legal_entity_code = apfm.legal_entity_code) AS"
				+ "       audit_group_code,"
				+ "       (SELECT Concat(f.audit_sub_group_code, '--', f.audit_sub_group_name)"
				+ "        FROM   maint_audit_subgroup f"
				+ "        WHERE  f.audit_sub_group_code = apfm.audit_sub_group_code"
				+ "               AND f.audit_group_code = apfm.audit_group_code"
				+ "               AND f.audit_type_code = apfm.audit_type_code"
				+ "               AND f.legal_entity_code = apfm.legal_entity_code) AS"
				+ "       audit_sub_group_code,"
				+ "       (SELECT Concat(f.activity_id, '--', f.activity_name)"
				+ "        FROM   maint_audit_activity f"
				+ "        WHERE  f.activity_id = apfm.activity_id"
				+ "               AND f.legal_entity_code = apfm.legal_entity_code) AS activitycode,"
				+ "       (SELECT Concat(f.process_id, '--', f.process_name)"
				+ "        FROM   maint_audit_process f"
				+ "        WHERE  f.process_id = apfm.process_id"
				+ "               AND f.legal_entity_code = apfm.legal_entity_code) AS processcode,"
				+ "       (SELECT Concat(f.finding_code, '--', f.finding_name)"
				+ "        FROM   maint_audit_finding f"
				+ "        WHERE  f.finding_id = apfm.finding_id"
				+ "               AND f.legal_entity_code = apfm.legal_entity_code) AS finding_code,"
				+ "       (SELECT Concat(g.value, '--', g.description)"
				+ "        FROM   general_parameter g  WHERE key_2='COMPLIANCE_CATEGORY'  and key_1='OBSERVATION_CAPTURE' "
				+ "        and  g.value = ao.compliance_category and g.legal_entity_code =  apfm.legal_entity_code )  as complianceCategory, "
				// + " ao.compliance_category,"
				+ "       ao.audit_id, apfm.mapping_id, "

				+ "       (SELECT Concat(e1.unit_code, '--', e1.unit_name)"
				+ "        FROM   maint_entity e, "
				+ "              maint_entity e1, "
				+ "               audit_schedule s"
				+ "        WHERE  e.legal_entity_code = s.legal_entity_code"
				+ "               AND e.legal_entity_code = ao.legal_entity_code"
				+ "               AND e.unit_code = s.unit_id"
				+ "               AND s.audit_id = ao.audit_id"
				+ "				  AND e1.unit_code = e.parent_unit_code )  AS parentUnitCode "

				+ " FROM   audit_observation AS ao,"
				+ "       activity_process_finding_mapping AS apfm, maint_audit_finding AS mc, maint_criticality as c "
				+ " WHERE  ao.legal_entity_code = :legalEntityCode"
				+ "       AND ao.legal_entity_code = apfm.legal_entity_code"
				+ "       AND ao.mapping_id = apfm.mapping_id"
				+ " 	  AND mc.finding_id = apfm.finding_id"
				+ "       AND ao.audit_id IN (:auditList) "
				+ "		  AND c.legal_entity_code=mc.legal_entity_code AND c.criticality_code=mc.f_criticality AND c.criticality_of_type='Finding' "
				+ "		  AND c.score >= (SELECT description*1 FROM general_parameter g WHERE g.legal_entity_code=c.legal_entity_code AND key_1='CRITICALITY_CUTOFF' AND key_2='FINDING') ");

		/*
		 * + " FROM   audit_observation AS ao,"
		 * +
		 * "       activity_process_finding_mapping AS apfm, maint_audit_finding AS mc "
		 * + " WHERE  ao.legal_entity_code = :legalEntityCode"
		 * + "       AND ao.legal_entity_code = apfm.legal_entity_code"
		 * + "       AND ao.mapping_id = apfm.mapping_id"
		 * + " AND mc.finding_id = apfm.finding_id"
		 * + "       AND mc.f_criticality in("+criticality+")"
		 * + "       AND ao.audit_id IN (:auditList) ");
		 */
		Query hqlQuery = getSession().createNativeQuery(query.toString())
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditList", auditList).setResultTransformer(new ResultTransformer() {

					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						ComplianceTrackingReportDto complianceReport = new ComplianceTrackingReportDto();
						if (tuple[0] != null) {
							if (tuple[0] != null)
								complianceReport.setLegalEntityCode(tuple[0].toString());
							if (tuple[1] != null)
								complianceReport.setAuditTypeCode(tuple[1].toString());
							if (tuple[2] != null)
								complianceReport.setUnitName(tuple[2].toString());
							if (tuple[3] != null)
								complianceReport.setChapter(tuple[3].toString());
							if (tuple[4] != null)
								complianceReport.setSection(tuple[4].toString());
							if (tuple[5] != null)
								complianceReport.setProduct(tuple[5].toString());
							if (tuple[6] != null)
								complianceReport.setProcess(tuple[6].toString());
							if (tuple[7] != null)
								complianceReport.setCheckList(tuple[7].toString());
							if (tuple[8] != null)
								complianceReport.setComplianceStatus(tuple[8].toString());
							if (tuple[10] != null)
								complianceReport.setMappingId(tuple[10].toString());
							if (tuple[11] != null)
								complianceReport.setParentUnitName(tuple[11].toString());
							complianceReport.setFinancialYear(financialYear.format(DateTimeFormatter.ISO_DATE));
						}
						return complianceReport;
					}

					@Override
					public List transformList(List collection) {
						return collection;
					}
				});
		return hqlQuery.list();
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<LettersAuditDto> getAuditDetail(Integer legalEntityCode, LocalDate financialYear, String auditTypeCode,
			List<String> unitList) {

		Session session = getSession();
		session.setDefaultReadOnly(true);
		String financialYearStartDt = null;
		String financialYearEndDt = null;
		if (financialYear.getMonthValue() == 1 || financialYear.getMonthValue() == 2
				|| financialYear.getMonthValue() == 3) {
			financialYearStartDt = (financialYear.getYear() - 1) + "-04-01";
			financialYearEndDt = (financialYear.getYear()) + "-03-31";
		} else {
			financialYearStartDt = (financialYear.getYear()) + "-04-01";
			financialYearEndDt = (financialYear.getYear() + 1) + "-03-31";
		}

		StringBuilder query = new StringBuilder(" Select  sch as auditSchedule, "

				+ " (Select concat(sch.auditStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS'"
				+ " and g.key2='AUDIT_STATUS' and g.value=sch.auditStatus) as auditStatus, "
				+ " (Select concat(sch.reportStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS'"
				+ " and g.key2='REPORT_STATUS' and g.value=sch.reportStatus) as reportStatus, "
				+ " (Select concat(sch.respondStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS'"
				+ " and g.key2='AUDIT_STATUS' and g.value=sch.respondStatus) as respondStatus, "
				+ " (Select concat(sch.reviewStatus , '-' , g.description) from  GeneralParameter g where g.legalEntityCode =sch.legalEntityCode and g.key1='STATUS'"
				+ " and g.key2='AUDIT_STATUS' and g.value=sch.reviewStatus) as reviewStatus, "

				+ " (select concat(auditTypeCode , '-' , auditTypeDesc) from MaintAuditTypeDesc at where at.auditTypeCode=sch.auditTypeCode and at.legalEntityCode= sch.legalEntityCode ) as auditTypeCode,"
				+ " (Select concat(unitCode , '-' , unitName) from  MaintEntity e where e.unitCode =sch.unitId and e.legalEntityCode =sch.legalEntityCode ) as unitId,  "
				+ " (SELECT Concat(e1.unitCode, '--', e1.unitName) FROM  MaintEntity e INNER JOIN   MaintEntity e1   ON  e.legalEntityCode = sch.legalEntityCode "
				+ " AND e.unitCode = sch.unitId   AND e1.unitCode = e.parentUnitCode )  AS parentUnitCode,"

				+ " (select group_concat(u.userId,'-', ifnull(u.firstName,''), ' ', ifnull(u.middleName,''),' ', ifnull(u.lastName,'')) from AuditTeam t inner join User u on u.userId=t.userId where "
				+ " t.legalEntityCode=sch.legalEntityCode AND t.auditId=sch.auditId AND t.userType='M' ) AS teamMember,"

				+ " (select group_concat(u.userId,'-', ifnull(u.firstName,''), ' ', ifnull(u.middleName,''),' ', ifnull(u.lastName,'')) from AuditTeam t inner join User u on u.userId=t.userId where "
				+ " t.legalEntityCode=sch.legalEntityCode AND t.auditId=sch.auditId AND t.userType='L' )  AS teamLead "

				+ " FROM  AuditSchedule sch "
				+ " where sch.legalEntityCode = :legalEntityCode "
				+ " and sch.unitId in (:unitList)"
				+ " and sch.auditStatus  NOT IN  ('AD','AU','AN') "
				+ " ");
		query.append("  "
				+ " AND  (  (sch.scheduleStartDate between '" + financialYearStartDt + "' and '" + financialYearEndDt
				+ "' ) "
				+ "     	OR (sch.actualStartDate between '" + financialYearStartDt + "' and '" + financialYearEndDt
				+ "' ) "
				+ " 	 )  "
				+ "  ");
		if (!BankAuditUtil.isEmptyString(auditTypeCode)) {
			query.append(" and sch.auditTypeCode = :auditTypeCode ");
		}
		Query hqlQuery = getSession().createQuery(query.toString())
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameterList("unitList", unitList);
		if (!BankAuditUtil.isEmptyString(auditTypeCode)) {
			hqlQuery.setParameter("auditTypeCode", auditTypeCode);
		}

		hqlQuery.setResultTransformer(new ResultTransformer() {
			@Override
			public Object transformTuple(Object[] tuple, String[] aliases) {
				LettersAuditDto lettersAuditDto = new LettersAuditDto();
				AuditSchedule auditSchedule = new AuditSchedule();
				if (tuple[0] != null) {
					auditSchedule = (AuditSchedule) tuple[0];

					lettersAuditDto.setAuditId(auditSchedule.getAuditId());
					lettersAuditDto.setPlanId(auditSchedule.getPlanId() + "-" + auditSchedule.getAuditId());
					lettersAuditDto.setLegalEntityCode(legalEntityCode + "");
					lettersAuditDto.setFinancialYear(financialYear.format(DateTimeFormatter.ISO_DATE));
					lettersAuditDto.setObservationStatus(auditSchedule.getObservationStatus());

					if (auditSchedule.getActualStartDate() != null)
						lettersAuditDto.setActualStartDate(auditSchedule.getActualStartDate().toString());

					if (auditSchedule.getReportDate() != null)
						lettersAuditDto.setExitDate(auditSchedule.getReportDate().toString());

					if (auditSchedule.getReportDispatchDate() != null)
						lettersAuditDto.setDispatchDate(auditSchedule.getReportDispatchDate().toString());

					if (tuple[1] != null)
						lettersAuditDto.setAuditStatus(tuple[1].toString());

					if (tuple[2] != null)
						lettersAuditDto.setReportStatus(tuple[2].toString());

					if (tuple[3] != null)
						lettersAuditDto.setComplianceStatus(tuple[3].toString());

					if (tuple[4] != null)
						lettersAuditDto.setScrutinyStatus(tuple[4].toString());

					if (tuple[5] != null)
						lettersAuditDto.setAuditTypeCode(tuple[5].toString());

					if (tuple[6] != null)
						lettersAuditDto.setUnitName(tuple[6].toString());

					if (tuple[7] != null)
						lettersAuditDto.setParentUnitName(tuple[7].toString());

					if (tuple[8] != null)
						lettersAuditDto.setAio(tuple[8].toString());
					if (tuple[9] != null)
						lettersAuditDto.setPio(tuple[9].toString());

				}
				return lettersAuditDto;
			}

			@Override
			public List transformList(List collection) {
				return collection;
			}
		});
		return hqlQuery.list();
	}

}
