/*
 * 
 */
package com.bankaudit.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintAuditGroupDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditGroup;
import com.bankaudit.model.MaintAuditGroupHst;
import com.bankaudit.model.MaintAuditGroupWrk;
import com.bankaudit.util.BankAuditUtil;

@Service("maintAuditGroupService")
@Transactional("transactionManager")
public class MaintAuditGroupServiceImpl implements MaintAuditGroupService {

	@Autowired
	MaintAuditGroupDao maintAuditGroupDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditGroup> getMaintAuditGroups(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode) {

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("legalEntityCode", legalEntityCode);
		if (!BankAuditUtil.isEmptyString(auditTypeCode)) {

			properties.put("auditTypeCode", auditTypeCode);
		}
		if (!BankAuditUtil.isEmptyString(auditGroupCode)) {
			properties.put("auditGroupCode", auditGroupCode);
		} else {
			properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		}

		List<MaintAuditGroup> auditGroups = maintAuditGroupDao.getEntitiesByMatchingProperties(MaintAuditGroup.class,
				properties);

		for (MaintAuditGroup maintAuditGroup : auditGroups) {
			Hibernate.initialize(maintAuditGroup.getMaintAuditSubgroups());
		}

		return auditGroups;

	}

	@Override
	public DataTableResponse getMaintAuditGroups(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {

		return maintAuditGroupDao.getMaintAuditGroups(legalEntityCode, search, orderColumn,
				orderDirection, page, size);
	}

	@Override
	public void createMaintAuditGroup(MaintAuditGroup maintAuditGroup)throws Exception {
		
		if(maintAuditGroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)){
			MaintAuditGroupWrk maintAuditGroupWrk=new MaintAuditGroupWrk(); 
			BeanUtils.copyProperties(maintAuditGroup, maintAuditGroupWrk);
			maintAuditGroupDao.save(maintAuditGroupWrk);
			
		}else if (maintAuditGroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditGroupDao.save(maintAuditGroup);
		}else{
			throw new Exception("invalid status");
		}
	}

	@Override
	public void updateMaintAuditGroup(MaintAuditGroup maintAuditGroup) {
		
		if(!maintAuditGroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			if(maintAuditGroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintAuditGroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
				maintAuditGroup.setMakerTimestamp(new Date());
			}
			// get Db object and save into the history
			MaintAuditGroup maintAuditGroupDb=
					getMaintAuditGroup(maintAuditGroup.getLegalEntityCode(), maintAuditGroup.getAuditGroupCode(), maintAuditGroup.getAuditTypeCode(), BankAuditConstant.STATUS_UNAUTH);
			if(maintAuditGroupDb!=null){
				maintAuditGroupDao.flushSession();
				MaintAuditGroupHst maintAuditGroupHst=new MaintAuditGroupHst();
				BeanUtils.copyProperties(maintAuditGroupDb, maintAuditGroupHst);
				maintAuditGroupDao.save(maintAuditGroupHst);
			}
			//delete the existing object rfrom work  
/*			maintAuditGroupDao.deleteMaintAuditGroupWrk(maintAuditGroupWrk.getLegalEntityCode(),maintAuditGroupWrk.getUgRoleCode());*/
			// delete the existing work maintEntityAuditSubgroupMapping 
			MaintAuditGroupWrk maintAuditGroupWrk=new MaintAuditGroupWrk();
			BeanUtils.copyProperties(maintAuditGroup, maintAuditGroupWrk);
			maintAuditGroupDao.flushSession();
			maintAuditGroupDao.saveOrUpdate(maintAuditGroupWrk);
			
		}else{
			// get Db object and save into the history
			MaintAuditGroup maintAuditGroupDb=
					getMaintAuditGroup(maintAuditGroup.getLegalEntityCode(), maintAuditGroup.getAuditGroupCode(), maintAuditGroup.getAuditTypeCode(), BankAuditConstant.STATUS_AUTH);
			if(maintAuditGroupDb!=null){
				MaintAuditGroupHst maintAuditGroupHst=new MaintAuditGroupHst();
				BeanUtils.copyProperties(maintAuditGroupDb, maintAuditGroupHst);
				maintAuditGroupDao.flushSession();
				maintAuditGroupDao.save(maintAuditGroupHst);
			}
			//delete the existing object from wrk  
			maintAuditGroupDao.deleteMaintAuditGroup(maintAuditGroup.getLegalEntityCode(), maintAuditGroup.getAuditGroupCode(), maintAuditGroup.getAuditTypeCode(),BankAuditConstant.STATUS_UNAUTH);
			maintAuditGroupDao.flushSession();
			maintAuditGroup.setCheckerTimestamp(new Date());
			// save again 
			maintAuditGroupDao.saveOrUpdate(maintAuditGroup);
			 
		}
	}

	@Override
	public MaintAuditGroup getMaintAuditGroup(Integer legalEntityCode, String auditGroupCode, String auditTypeCode,String status) {
		Map<String, Object> properties=new HashMap<String,Object>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("auditGroupCode", auditGroupCode);
		properties.put("auditTypeCode", auditTypeCode);

		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			MaintAuditGroup maintAuditGroup=null;
			MaintAuditGroupWrk maintAuditGroupWrk= (MaintAuditGroupWrk)maintAuditGroupDao.getUniqueEntityByMatchingProperties(MaintAuditGroupWrk.class, properties);			
			if(maintAuditGroupWrk!=null){
				maintAuditGroup=new MaintAuditGroup();
				BeanUtils.copyProperties(maintAuditGroupWrk, maintAuditGroup);
			}
			return maintAuditGroup;
		}else {
			return (MaintAuditGroup)maintAuditGroupDao.getUniqueEntityByMatchingProperties(MaintAuditGroup.class, properties);			
		}
	
	
	}

	@Override
	public Boolean isMaintAuditGroupLE(Integer legalEntityCode, String auditGroupCode) {
		return maintAuditGroupDao.isMaintAuditGroupLE( legalEntityCode,  auditGroupCode);
	}


	@Override
	public void deleteAuditGroupByID(Integer legalEntityCode, String auditTypeCode, String auditGroupCode, String userId) {
		maintAuditGroupDao.deleteMaintAuditGroup(legalEntityCode, auditGroupCode, auditTypeCode, BankAuditConstant.STATUS_REJ);
	}

	@Override
	public Boolean isMaintAuditGroup(Integer legalEntityCode, String auditTypeCode, String auditGroupCode) {
		return maintAuditGroupDao.isMaintAuditGroup( legalEntityCode,  auditTypeCode,  auditGroupCode);
	}


}
