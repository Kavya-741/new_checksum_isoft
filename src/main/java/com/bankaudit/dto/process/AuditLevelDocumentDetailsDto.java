package com.bankaudit.dto.process;

import java.util.List;

import com.bankaudit.process.model.AuditLevelDocumentDetails;

import lombok.Data;

@Data
public class AuditLevelDocumentDetailsDto {

	List<AuditLevelDocumentDetails> auditLevelDocumentDetails;
}
