package com.bankaudit.dao;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintReportStandardStmt;

public interface MaintReportStandardStmtDao extends Dao {

	void deleteMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String statusAuth);

	MaintReportStandardStmt getMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String status);

	DataTableResponse getAllMaintReportStandardStmt(Integer legalEntityCode, String search, Integer orderColumn, String orderDirection, Integer page,
			Integer size);

	List<MaintReportStandardStmt> getMaintReportStandardStmtByMappindId(Integer legalEntityCode, String mappingId);

	List<MaintReportStandardStmt> getMaintReportStandardStmtWrkByMappindId(Integer legalEntityCode, String mappingId);


}
