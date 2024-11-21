package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import com.finakon.baas.entities.MaintEntity;

public interface MaintEntityService {

	List<String> getSubBranchesByUserIdOrUnitId(Integer legalEntityCode, String type, String userIdOrUnitId);
		/**
	 * This method is use to Gets the maint entity by legal entity code.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @return the list .
	 */
	List<MaintEntity> getMaintEntityByLegalEntityCode(Integer legalEntityCode);

}
