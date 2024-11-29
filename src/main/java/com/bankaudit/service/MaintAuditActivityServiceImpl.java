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
import com.bankaudit.dao.MaintAuditActivityDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditActivity;
import com.bankaudit.model.MaintAuditActivityHst;
import com.bankaudit.model.MaintAuditActivityWrk;

@Service("maintAuditActivityservice")
@Transactional("transactionManager")
public class MaintAuditActivityServiceImpl implements MaintAuditActivityService {

	@Autowired
	MaintAuditActivityDao maintAuditActivityDao;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	@Override
	public List<MaintAuditActivity> getMaintAuditActivity(Integer legalEntityCode) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", "A");
		return maintAuditActivityDao.getEntitiesByMatchingProperties(MaintAuditActivity.class, properties);
	}

	@Override
	public Boolean isMaintAuditActivity(Integer legalEntityCode, String activityCode) {

		return maintAuditActivityDao.isMaintAuditActivity(legalEntityCode, activityCode);
	}

	@Override
	public void createMaintAuditActivity(MaintAuditActivity maintAuditActivity) throws Exception {

		if (maintAuditActivity.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)) {

			MaintAuditActivityWrk maintAuditActivityWrk = new MaintAuditActivityWrk();
			BeanUtils.copyProperties(maintAuditActivity, maintAuditActivityWrk);
			maintAuditActivityDao.save(maintAuditActivityWrk);

		} else if (maintAuditActivity.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditActivityDao.save(maintAuditActivity);
		} else {
			throw new Exception("invalid status");
		}
	}

	@Override
	public DataTableResponse getMaintAuditActivities(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		return maintAuditActivityDao.getMaintAuditActivities( legalEntityCode ,  search,  orderColumn,
				 orderDirection,  page,  size);
	}

	@Override
	public void deleteActivityByID(Integer legalEntityCode, String activityId, String userId) {
		maintAuditActivityDao.deleteMaintAuditActivity(legalEntityCode, activityId, BankAuditConstant.STATUS_REJ);
	}

	@Override
	public void updateMaintAuditActivity(MaintAuditActivity maintAuditActivity) {

		
		if(!maintAuditActivity.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){

			if(maintAuditActivity.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintAuditActivity.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
				maintAuditActivity.setMakerTimestamp(new Date());
			}

			// get Db object and save into the history
			MaintAuditActivity maintAuditActivityDb=getMaintAuditActivity(maintAuditActivity.getLegalEntityCode(), maintAuditActivity.getActivityId(), BankAuditConstant.STATUS_UNAUTH);
			if(maintAuditActivityDb!=null){
				MaintAuditActivityHst MaintAuditActivityHst=new MaintAuditActivityHst();
				BeanUtils.copyProperties(maintAuditActivityDb, MaintAuditActivityHst);
				maintAuditActivityDao.save(MaintAuditActivityHst);
			}
			
			MaintAuditActivityWrk maintAuditActivityWrk=new MaintAuditActivityWrk();
			BeanUtils.copyProperties(maintAuditActivity, maintAuditActivityWrk);
			maintAuditActivityDao.flushSession();
			maintAuditActivityDao.saveOrUpdate(maintAuditActivityWrk);
			
		}else{

			// get Db object and save into the history
			MaintAuditActivity maintAuditActivityDb=getMaintAuditActivity(maintAuditActivity.getLegalEntityCode(), maintAuditActivity.getActivityId(), BankAuditConstant.STATUS_AUTH);
			if(maintAuditActivityDb!=null){
				maintAuditActivityDb.setEntityStatus(BankAuditConstant.STATUS_EXPIRE);
				maintAuditActivityDao.update(maintAuditActivityDb);
				maintAuditActivityDao.flushSession();
			}

			// delete from work
			maintAuditActivityDao.deleteMaintAuditActivity(maintAuditActivity.getLegalEntityCode(),maintAuditActivity.getActivityId(),BankAuditConstant.STATUS_UNAUTH);
			
			// save again 
			maintAuditActivityDao.flushSession();
			maintAuditActivity.setActivityId(sequenceAppenderService.getAutoSequenceId());
			maintAuditActivityDao.save(maintAuditActivity);
			
		}
	
	}

	@Override
	public MaintAuditActivity getMaintAuditActivity(Integer legalEntityCode, String activityId,String status) {
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("activityId", activityId);
		
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			MaintAuditActivity maintAuditActivity=null;
			MaintAuditActivityWrk maintAuditActivityWrk =(MaintAuditActivityWrk) maintAuditActivityDao.getUniqueEntityByMatchingProperties(MaintAuditActivityWrk.class, properties);
			if(maintAuditActivityWrk!=null){
				maintAuditActivity=new MaintAuditActivity();
				BeanUtils.copyProperties(maintAuditActivityWrk, maintAuditActivity);
			}
			return maintAuditActivity;
		}else {
			return (MaintAuditActivity) maintAuditActivityDao.getUniqueEntityByMatchingProperties(MaintAuditActivity.class, properties);
		}
	}



}
