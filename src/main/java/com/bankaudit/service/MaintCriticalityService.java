/*
 * 
 */
package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintCriticality;

public interface MaintCriticalityService {

	List<MaintCriticality> getMaintCriticalityByCriticalityOfType(Integer legalEntityCode ,String criticalityOfType);

	Boolean isMaintCriticality(Integer legalEntityCode, String criticalityCode, String criticalityOfType);

	Boolean validateScore(MaintCriticality maintCriticality);
	
	void deleteCriticalityByCodeAndType(Integer legalEntityCode,  String criticalityCode, String criticalityOfType, String userId);


	void createMaintCriticality(MaintCriticality maintCriticality)throws Exception;


	void updateMaintCriticality(MaintCriticality maintCriticality);


    MaintCriticality getMaintCriticalityByCriticalityCodeAndCriticalityOfType(Integer legalEntityCode,String criticalityCode,
			String criticalityOfType,String status);

	DataTableResponse getAllMaintCriticality(String search, Integer orderColumn, String orderDirection, Integer page,
			Integer size,Integer legalEntityCode);

	List<MaintCriticality> getByCriticalityScore(Integer legalEntityCode ,String criticalityOfType, String score ,String criticalityCode);




}
