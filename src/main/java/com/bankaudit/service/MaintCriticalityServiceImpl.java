/*
 * 
 */
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
import com.bankaudit.dao.MaintCriticalityDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintCriticality;
import com.bankaudit.model.MaintCriticalityHst;
import com.bankaudit.model.MaintCriticalityWrk;

@Service("maintCriticalityService")
@Transactional("transactionManager")
public class MaintCriticalityServiceImpl implements MaintCriticalityService {


	@Autowired
	MaintCriticalityDao maintCriticalityDao;
	

	@Override
	public void createMaintCriticality(MaintCriticality maintCriticality)throws Exception {
		
		if(maintCriticality.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)){
			MaintCriticalityWrk maintCriticalityWrk=new MaintCriticalityWrk(); 
			BeanUtils.copyProperties(maintCriticality, maintCriticalityWrk);
			maintCriticalityDao.save(maintCriticalityWrk);
			
		}else if (maintCriticality.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintCriticalityDao.save(maintCriticality);
		}else{
			throw new Exception("invalid status");
		}
	}


	@Override
	public void updateMaintCriticality(MaintCriticality maintCriticality) {
		if(!maintCriticality.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			if(maintCriticality.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintCriticality.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
				maintCriticality.setMakerTimestamp(new Date());
			}
			// get Db object and save into the history
			MaintCriticality maintCriticalityDb=
					getMaintCriticalityByCriticalityCodeAndCriticalityOfType(maintCriticality.getLegalEntityCode(), maintCriticality.getCriticalityCode(), maintCriticality.getCriticalityOfType(), BankAuditConstant.STATUS_UNAUTH);
			if(maintCriticalityDb!=null){
				maintCriticalityDao.flushSession();
				MaintCriticalityHst maintCriticalityHst=new MaintCriticalityHst();
				BeanUtils.copyProperties(maintCriticalityDb, maintCriticalityHst);
				maintCriticalityDao.save(maintCriticalityHst);
			}
			//delete the existing object rfrom work  
			MaintCriticalityWrk maintCriticalityWrk=new MaintCriticalityWrk();
			BeanUtils.copyProperties(maintCriticality, maintCriticalityWrk);
			maintCriticalityDao.flushSession();
			maintCriticalityDao.saveOrUpdate(maintCriticalityWrk);
			
		}else{
			// get Db object and save into the history
			MaintCriticality maintCriticalityDb=
					getMaintCriticalityByCriticalityCodeAndCriticalityOfType(maintCriticality.getLegalEntityCode(), maintCriticality.getCriticalityCode(), maintCriticality.getCriticalityOfType(), BankAuditConstant.STATUS_AUTH);
			if(maintCriticalityDb!=null){
				MaintCriticalityHst maintCriticalityHst=new MaintCriticalityHst();
				BeanUtils.copyProperties(maintCriticalityDb, maintCriticalityHst);
				maintCriticalityDao.save(maintCriticalityHst);
			}
			//delete the existing object from both the tables  
			maintCriticalityDao.deleteMaintCriticality(maintCriticality.getLegalEntityCode(), maintCriticality.getCriticalityCode(), maintCriticality.getCriticalityOfType(), BankAuditConstant.STATUS_UNAUTH);
			maintCriticalityDao.flushSession();
			maintCriticality.setCheckerTimestamp(new Date());
			// save again 
			maintCriticalityDao.saveOrUpdate(maintCriticality);
			 
		}
	}

	@Override
	public MaintCriticality getMaintCriticalityByCriticalityCodeAndCriticalityOfType(Integer legalEntityCode,String criticalityCode,
			String criticalityOfType,String status) {

		Map<String,Object> properties=new HashMap<>();
		properties.put("criticalityCode", criticalityCode);
		properties.put("criticalityOfType", criticalityOfType);
		properties.put("legalEntityCode", legalEntityCode);
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			MaintCriticality maintCriticality=null;
			MaintCriticalityWrk maintCriticalityWrk= (MaintCriticalityWrk)maintCriticalityDao.getUniqueEntityByMatchingProperties(MaintCriticalityWrk.class, properties);			
			if(maintCriticalityWrk!=null){
				maintCriticality=new MaintCriticality();
				BeanUtils.copyProperties(maintCriticalityWrk, maintCriticality);
			}
			return maintCriticality;
		}else {
			return (MaintCriticality)maintCriticalityDao.getUniqueEntityByMatchingProperties(MaintCriticality.class, properties);			
		}
	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintCriticality> getMaintCriticalityByCriticalityOfType(Integer legalEntityCode,String criticalityOfType) {
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("criticalityOfType", criticalityOfType);
		properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		return maintCriticalityDao.getEntitiesByMatchingProperties(MaintCriticality.class, properties);
	}


	@Override
	public DataTableResponse getAllMaintCriticality(String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size,Integer legalEntityCode) {
		
		return maintCriticalityDao.getAllMaintCriticality( search,  orderColumn,  orderDirection,
				 page,  size,legalEntityCode);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintCriticality> getByCriticalityScore(Integer legalEntityCode,String criticalityOfType, String score ,String criticalityCode) {
		return maintCriticalityDao.getByCriticalityScore(legalEntityCode,criticalityOfType,score,criticalityCode);
	}


	@Override
	public Boolean isMaintCriticality(Integer legalEntityCode, String criticalityCode, String criticalityOfType) {
		return maintCriticalityDao.isMaintCriticality( legalEntityCode,  criticalityCode,  criticalityOfType);
	}
	
	@Override
	public Boolean validateScore(MaintCriticality maintCriticality) {
		return maintCriticalityDao.validateScore(maintCriticality);
	}
	
	@Override
	public void deleteCriticalityByCodeAndType(Integer legalEntityCode,  String criticalityCode, String criticalityOfType, String userId) {
		maintCriticalityDao.deleteMaintCriticality(legalEntityCode, criticalityCode, criticalityOfType, BankAuditConstant.STATUS_REJ);
	}

}
