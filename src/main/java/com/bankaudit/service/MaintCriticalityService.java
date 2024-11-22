/*
 * 
 */
package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.MaintCriticality;

public interface MaintCriticalityService {

	List<MaintCriticality> getMaintCriticalityByCriticalityOfType(Integer legalEntityCode ,String criticalityOfType);

}
