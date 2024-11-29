package com.bankaudit.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintAuditSubgroupDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintAuditSubgroup;
import com.bankaudit.model.MaintAuditSubgroupHst;
import com.bankaudit.model.MaintAuditSubgroupWrk;

@Service
@Transactional("transactionManager")
public class MaintAuditSubgroupServiceImpl implements MaintAuditSubgroupService {

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditSubgroup> getMaintAuditSubgroupes(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode) {
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		
		if(!BankAuditUtil.isEmptyString(auditTypeCode)){
			properties.put("auditTypeCode", auditTypeCode);
		}
		if(!BankAuditUtil.isEmptyString(auditGroupCode)){
			properties.put("auditGroupCode", auditGroupCode);
		}
		if(!BankAuditUtil.isEmptyString(auditSubGroupCode)){
			properties.put("auditSubGroupCode", auditSubGroupCode);
		}else {
			properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		}
		
		return maintAuditSubgroupDao.getEntitiesByMatchingProperties(MaintAuditSubgroup.class, properties);
	}

	

	@Autowired
	MaintAuditSubgroupDao maintAuditSubgroupDao;
	@Override
	public List<MaintAuditSubgroup> getMaintAuditSubgroupe(Integer legalEntityCode) {
		return maintAuditSubgroupDao.getMaintAuditSubgroupe( legalEntityCode);
	}

	@Override
	public void createMaintAuditSubgroup(MaintAuditSubgroup maintAuditSubgroup) throws Exception {

		
		if(maintAuditSubgroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)){
			MaintAuditSubgroupWrk maintAuditSubgroupWrk=new MaintAuditSubgroupWrk(); 
			BeanUtils.copyProperties(maintAuditSubgroup, maintAuditSubgroupWrk);
			maintAuditSubgroupDao.save(maintAuditSubgroupWrk);
			
		}else if (maintAuditSubgroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditSubgroupDao.save(maintAuditSubgroup);
		}else{
			throw new Exception("invalid status");
		}
	
	}

	@Override
	public void updateMaintAuditSubgroup(MaintAuditSubgroup maintAuditSubgroup) {
		
		if(!maintAuditSubgroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			if(maintAuditSubgroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintAuditSubgroup.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
				maintAuditSubgroup.setMakerTimestamp(new Date());
			}
			// get Db object and save into the history
			MaintAuditSubgroup maintAuditSubgroupDb=
					getMaintAuditSubgroup(maintAuditSubgroup.getLegalEntityCode(), maintAuditSubgroup.getAuditSubGroupCode(), maintAuditSubgroup.getAuditGroupCode(), maintAuditSubgroup.getAuditTypeCode(), BankAuditConstant.STATUS_UNAUTH);
			if(maintAuditSubgroupDb!=null){
				maintAuditSubgroupDao.flushSession();
				MaintAuditSubgroupHst maintAuditSubgroupHst=new MaintAuditSubgroupHst();
				BeanUtils.copyProperties(maintAuditSubgroupDb, maintAuditSubgroupHst);
				maintAuditSubgroupDao.save(maintAuditSubgroupHst);
			}
			//delete the existing object rfrom work  
			// delete the existing work maintEntityAuditSubgroupMapping 
			MaintAuditSubgroupWrk maintAuditSubgroupWrk=new MaintAuditSubgroupWrk();
			BeanUtils.copyProperties(maintAuditSubgroup, maintAuditSubgroupWrk);
			maintAuditSubgroupDao.flushSession();
			maintAuditSubgroupDao.saveOrUpdate(maintAuditSubgroupWrk);
			
		}else{
			// get Db object and save into the history
			MaintAuditSubgroup maintAuditSubgroupDb=
					getMaintAuditSubgroup(maintAuditSubgroup.getLegalEntityCode(), maintAuditSubgroup.getAuditSubGroupCode(), maintAuditSubgroup.getAuditGroupCode(), maintAuditSubgroup.getAuditTypeCode(), BankAuditConstant.STATUS_AUTH);
			if(maintAuditSubgroupDb!=null){
				MaintAuditSubgroupHst maintAuditSubgroupHst=new MaintAuditSubgroupHst();
				BeanUtils.copyProperties(maintAuditSubgroupDb, maintAuditSubgroupHst);
				maintAuditSubgroupDao.flushSession();
				maintAuditSubgroupDao.save(maintAuditSubgroupHst);
			}
			//delete the existing object from wrk
			maintAuditSubgroupDao.deleteMaintAuditSubgroup(maintAuditSubgroup.getLegalEntityCode(), maintAuditSubgroup.getAuditSubGroupCode(), maintAuditSubgroup.getAuditGroupCode(), maintAuditSubgroup.getAuditTypeCode(), BankAuditConstant.STATUS_UNAUTH);
			maintAuditSubgroupDao.flushSession();
			maintAuditSubgroup.setCheckerTimestamp(new Date());
			// save again 
			maintAuditSubgroupDao.saveOrUpdate(maintAuditSubgroup);
			 
		}
	}

	@Override
	public MaintAuditSubgroup getMaintAuditSubgroup(Integer legalEntityCode, String auditSubGroupCode,
			String auditGroupCode, String auditTypeCode,String status) {
		
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("auditSubGroupCode", auditSubGroupCode);
		properties.put("auditGroupCode", auditGroupCode);
		properties.put("auditTypeCode", auditTypeCode);
		

		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			MaintAuditSubgroup maintAuditSubgroup=null;
			MaintAuditSubgroupWrk maintAuditSubgroupWrk= (MaintAuditSubgroupWrk)maintAuditSubgroupDao.getUniqueEntityByMatchingProperties(MaintAuditSubgroupWrk.class, properties);			
			if(maintAuditSubgroupWrk!=null){
				maintAuditSubgroup=new MaintAuditSubgroup();
				BeanUtils.copyProperties(maintAuditSubgroupWrk, maintAuditSubgroup);
			}
			return maintAuditSubgroup;
		}else {
			return (MaintAuditSubgroup)maintAuditSubgroupDao.getUniqueEntityByMatchingProperties(MaintAuditSubgroup.class, properties);			
		}
	}

	@Override
	public DataTableResponse getMaintAuditSubgroupes(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		return maintAuditSubgroupDao.getMaintAuditSubgroupes( legalEntityCode,  search,  orderColumn,
				 orderDirection,  page,  size);
	}

	@Override
	public Boolean isMaintAuditSubgroup(Integer legalEntityCode, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode) {
		return maintAuditSubgroupDao.isMaintAuditSubgroup( legalEntityCode,  auditTypeCode,  auditGroupCode,
				 auditSubGroupCode);
	}
	
	@Override
	public Boolean isMaintAuditSubgroupLE(Integer legalEntityCode, String auditSubGroupCode) {
		return maintAuditSubgroupDao.isMaintAuditSubgroupLE( legalEntityCode, auditSubGroupCode);
	}
	
	@Override
	public void deleteAuditSubGroupByID(Integer legalEntityCode, String auditTypeCode, String auditGroupCode, String auditSubGroupCode, String userId) {
		maintAuditSubgroupDao.deleteMaintAuditSubgroup(legalEntityCode, auditSubGroupCode, auditGroupCode, auditTypeCode, BankAuditConstant.STATUS_REJ);
	}

}
