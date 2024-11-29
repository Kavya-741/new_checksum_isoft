package com.bankaudit.dto;

import java.util.Date;
import java.util.List;
import com.bankaudit.model.MaintEntityAuditSubgroupMappingWrk;
import lombok.Data;

@Data
public class MaintEntityAuditSubGroupMappingDto {

	List<MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappingWrks;
	Integer legalEntityCode;
	String auditTypeCode;
	String mappingType;
	String entityOrUser;
	String entityOrUserName;
	String maker;
	String entityStaus;
	String status;
	String authRejectRemarks;
	Date makerTimestamp;
	String checker;
	Date checkerTimestamp;
	String authUniqueId;
}
