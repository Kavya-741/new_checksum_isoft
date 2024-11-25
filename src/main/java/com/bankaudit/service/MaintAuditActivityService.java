package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.MaintAuditActivity;

public interface MaintAuditActivityService {

	List<MaintAuditActivity> getMaintAuditActivity(Integer legalEntityCode);

}
