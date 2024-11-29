package com.bankaudit.service;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintReportStandardStmt;

public interface MaintReportStandardStmtService {

	void createMaintReportStandardStmt(MaintReportStandardStmt maintReportStandardStmt) throws Exception;

	void updateMaintReportStandardStmt(MaintReportStandardStmt maintReportStandardStmt);


	DataTableResponse getAllMaintReportStandardStmt(Integer legalEntityCode, String search, Integer orderColumn, String orderDirection, Integer page,
			Integer size);

	void deleteMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String status);

	MaintReportStandardStmt getMaintReportStandardStmtWrk(Integer legalEntityCode, String mappingId);

	MaintReportStandardStmt getMaintReportStandardStmt(Integer legalEntityCodeStr, String mappingId);

	
	MaintReportStandardStmt getMaintReportStandardStmt(Integer legalEntityCode, String mappingId, String status);		

	boolean checkMaintReportStandardStmtExsits(Integer legalEntityCode, String mappingId);

	

	
	
}
