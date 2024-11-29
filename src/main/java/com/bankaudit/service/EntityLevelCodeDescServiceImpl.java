/*
 * 
 */
package com.bankaudit.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.EntityLevelCodeDescDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.EntityLevelCodeDesc;
import com.bankaudit.model.EntityLevelCodeDescHst;
import com.bankaudit.model.EntityLevelCodeDescWrk;

@Service("entityLevelCodeDescService")
@Transactional("transactionManager")
public class EntityLevelCodeDescServiceImpl implements EntityLevelCodeDescService {

	@Autowired
	EntityLevelCodeDescDao entityLevelCodeDescDao;

	static final Logger logger = Logger.getLogger(EntityLevelCodeDescServiceImpl.class);

	@Override
	public void createEntityLevelCodeDesc(EntityLevelCodeDesc entityLevelCodeDesc) {
		entityLevelCodeDescDao.save(entityLevelCodeDesc);
	}

	@Override
	public Integer getEntityLevelCodeDescCount(Integer legalEntityCode) {

		return entityLevelCodeDescDao.getEntityLevelCodeDescCount(legalEntityCode);
	}

	@Override
	public void updateEntityLevelCodeDesc(EntityLevelCodeDesc entityLevelCodeDesc) {

		if (!entityLevelCodeDesc.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {

			if (entityLevelCodeDesc.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| entityLevelCodeDesc.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)) {
				entityLevelCodeDesc.setMakerTimestamp(new Date());
			}

			// get Db object and save into the history
			EntityLevelCodeDesc entityLevelCodeDescDb = getEntityLevelCodeDescByLegalEntityCodeAndLevelCode(
					entityLevelCodeDesc.getLegalEntityCode(), entityLevelCodeDesc.getLevelCode(),
					BankAuditConstant.STATUS_UNAUTH);
			if (entityLevelCodeDescDb != null) {
				EntityLevelCodeDescHst entityLevelCodeDescHst = new EntityLevelCodeDescHst();
				BeanUtils.copyProperties(entityLevelCodeDescDb, entityLevelCodeDescHst);
				entityLevelCodeDescDao.flushSession();
				entityLevelCodeDescDao.save(entityLevelCodeDescHst);
			}

			EntityLevelCodeDescWrk entityLevelCodeDescWrk = new EntityLevelCodeDescWrk();
			BeanUtils.copyProperties(entityLevelCodeDesc, entityLevelCodeDescWrk);
			entityLevelCodeDescDao.flushSession();
			entityLevelCodeDescDao.saveOrUpdate(entityLevelCodeDescWrk);

		} else {

			// get Db object and save into the history
			EntityLevelCodeDesc entityLevelCodeDescDb = getEntityLevelCodeDescByLegalEntityCodeAndLevelCode(
					entityLevelCodeDesc.getLegalEntityCode(), entityLevelCodeDesc.getLevelCode(),
					BankAuditConstant.STATUS_AUTH);
			if (entityLevelCodeDescDb != null) {
				EntityLevelCodeDescHst entityLevelCodeDescHst = new EntityLevelCodeDescHst();
				BeanUtils.copyProperties(entityLevelCodeDescDb, entityLevelCodeDescHst);
				entityLevelCodeDescDao.save(entityLevelCodeDescHst);
			}

			// delete from both the tables
			entityLevelCodeDescDao.deleteEntityLevelCodeDesc(entityLevelCodeDesc.getLegalEntityCode(),
					entityLevelCodeDesc.getLevelCode(), BankAuditConstant.STATUS_UNAUTH);

			// save again
			entityLevelCodeDescDao.flushSession();
			entityLevelCodeDescDao.update(entityLevelCodeDesc);

		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntityLevelCodeDesc> getEntityLevelCodeDescByLegalEntityCode(Integer legalEntityCode) {

		Map<String, Object> properties = new HashMap<>();

		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);

		return entityLevelCodeDescDao.getEntitiesByMatchingProperties(EntityLevelCodeDesc.class, properties);
	}

	@Override
	public DataTableResponse getEntityLevelCodeDesc(Integer legalEntityCode, String search,
			Integer orderColumn, String orderDirection, Integer page, Integer size) {

		return entityLevelCodeDescDao.getEntityLevelCodeDesc(legalEntityCode, search, orderColumn, orderDirection, page,
				size);

	}

	@Override
	public EntityLevelCodeDesc getEntityLevelCodeDescByLegalEntityCodeAndLevelCode(Integer legalEntityCode,
			String levelCode, String status) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("levelCode", levelCode);

		if (!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)) {
			EntityLevelCodeDesc entityLevelCodeDesc = null;
			EntityLevelCodeDescWrk entityLevelCodeDescWrk = (EntityLevelCodeDescWrk) entityLevelCodeDescDao
					.getUniqueEntityByMatchingProperties(EntityLevelCodeDescWrk.class, properties);
			if (entityLevelCodeDescWrk != null) {
				entityLevelCodeDesc = new EntityLevelCodeDesc();
				BeanUtils.copyProperties(entityLevelCodeDescWrk, entityLevelCodeDesc);
			}
			return entityLevelCodeDesc;
		} else {
			return (EntityLevelCodeDesc) entityLevelCodeDescDao
					.getUniqueEntityByMatchingProperties(EntityLevelCodeDesc.class, properties);
		}

	}

	@Override
	public void deleteEntityLevelCodeDesc(Integer legalEntityCode, String levelCode, String statusAuth) {
		entityLevelCodeDescDao.deleteEntityLevelCodeDesc(legalEntityCode, levelCode, statusAuth);
	}

	@Override
	public List<EntityLevelCodeDesc> getEntityLevelCodesDescByUserID(Integer legalEntityCode, String userId) {
		return entityLevelCodeDescDao.getEntityLevelCodesDescByUserID(legalEntityCode, userId);
	}

}
