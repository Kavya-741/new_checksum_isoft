package com.bankaudit.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintAuditParameterDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditParameter;
import com.bankaudit.model.MaintAuditParameterHst;
import com.bankaudit.model.MaintAuditParameterWrk;
import com.bankaudit.helper.BankAuditUtil;

@Service("maintAuditParameterService")
@Transactional("transactionManager")
public class MaintAuditParameterServiceImpl implements MaintAuditParameterService{

	@Autowired
	MaintAuditParameterDao maintAuditParameterDao; 


	@Autowired
	SequenceAppenderService sequenceAppenderService;

	@Override
	public void createMaintAddress(MaintAuditParameter maintAuditParameter) throws Exception {
		//maintAuditParameterDao.save(maintAuditParameter);
		if(maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)){
			MaintAuditParameterWrk maintAuditParameterWrk=new MaintAuditParameterWrk(); 
			BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterWrk);
			maintAuditParameterDao.save(maintAuditParameterWrk);			
		}else if (maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditParameterDao.save(maintAuditParameter);
		}else{
			throw new Exception("invalid status");
		}
	}


	@Override

	// check need to save in wrk or not ,show master record.

	public void updateMaintAuditParameter(MaintAuditParameter maintAuditParameter) {
		//maintAuditParameterDao.update(maintAuditParameter);
		
		System.out.println("Inside updateMaintAuditParam .. "+ maintAuditParameter.getStatus());
		if(maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DEL)) {

			maintAuditParameter.setMakerTimestamp(new Date());

			if(getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH)==null) {

				// get Db object and save into the history
				MaintAuditParameter maintAuditParameterDb =	getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_AUTH);
				if(maintAuditParameterDb!=null) {
					MaintAuditParameterWrk maintAuditParameterWrk=new MaintAuditParameterWrk();
					BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterWrk);				
					maintAuditParameterDao.saveOrUpdate(maintAuditParameterWrk);
					maintAuditParameterDao.flushSession();
				}else {
					//delete the existing object from both the tables  
					maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);
					maintAuditParameterDao.flushSession();
				}
			}

		}else if(!maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){

			if(maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)					
					|| maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
				maintAuditParameter.setMakerTimestamp(new Date());
			}

			// get Db object and save into the history
			MaintAuditParameter maintAuditParameterDb=
					getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);
			if(maintAuditParameterDb!=null){
				maintAuditParameterDao.flushSession();
				MaintAuditParameterHst maintAuditParameterHst=new MaintAuditParameterHst();
				BeanUtils.copyProperties(maintAuditParameterDb, maintAuditParameterHst);
				maintAuditParameterDao.save(maintAuditParameterHst);
			}
			//delete the existing object rfrom work  
			/*			maintCriticalityDao.deleteMaintCriticalityWrk(maintCriticalityWrk.getLegalEntityCode(),maintCriticalityWrk.getUgRoleCode());*/
			// delete the existing work maintEntityAuditSubgroupMapping 
			MaintAuditParameterWrk maintAuditParameterWrk=new MaintAuditParameterWrk();
			BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterWrk);
			maintAuditParameterDao.flushSession();
			maintAuditParameterDao.saveOrUpdate(maintAuditParameterWrk);

		}

		else{

			// get Db object and save into the history
			MaintAuditParameter maintAuditParameterDb=
					getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);

			if(maintAuditParameterDb!=null && maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH) && maintAuditParameterDb.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DEL))
			{

				MaintAuditParameter maintAuditParameterMst=getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_AUTH);

				MaintAuditParameterHst maintAuditParameterHst=new MaintAuditParameterHst();
				BeanUtils.copyProperties(maintAuditParameterMst, maintAuditParameterHst);
				maintAuditParameterDao.save(maintAuditParameterHst);

				//delete the existing object from both the tables  
				maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_AUTH);
				maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);
				maintAuditParameterDao.flushSession();

			}else {

				if(maintAuditParameterDb!=null){
					MaintAuditParameterHst maintAuditParameterHst=new MaintAuditParameterHst();
					BeanUtils.copyProperties(maintAuditParameterDb, maintAuditParameterHst);
					maintAuditParameterDao.save(maintAuditParameterHst);
				}
				//delete the existing object from both the tables  
				maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);
				maintAuditParameterDao.flushSession();

				maintAuditParameter.setCheckerTimestamp(new Date());
				// save again 
				maintAuditParameterDao.saveOrUpdate(maintAuditParameter);
			}
		}

	}


	public Map<String,Object> isMaintAuditParameterAlreadyExist(MaintAuditParameterWrk maintAuditParameter) {
		return maintAuditParameterDao.isMaintAuditParameterAlreadyExist(maintAuditParameter);
	}


	@SuppressWarnings("removal")
	@Transactional(readOnly=false)
	public boolean updateMaintAuditsParameters(List<MaintAuditParameter> maintAuditParameterList,Date businessDate,Timestamp currentTimeStamp) {
		try {		
			for (MaintAuditParameter maintAuditParameter : maintAuditParameterList) {

				//for new record  
				if(maintAuditParameter.getId()==null) {
					maintAuditParameter.setId(new Integer(sequenceAppenderService.getAutoSequenceId()).intValue());
					maintAuditParameter.setMakerTimestamp(businessDate);

					MaintAuditParameterWrk maintAuditParameterWrk=new MaintAuditParameterWrk();
					BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterWrk);
					maintAuditParameterDao.saveOrUpdate(maintAuditParameterWrk);
					maintAuditParameterDao.flushSession();

				}else {
					//you can able to delete only unauthorized record 
					if(maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DEL)){	
						MaintAuditParameter maintAuditParameterDb =	getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);
						if(maintAuditParameterDb!=null){

							maintAuditParameterDb.setMakerTimestamp(businessDate);

							MaintAuditParameterHst maintAuditParameterHst=new MaintAuditParameterHst();
							BeanUtils.copyProperties(maintAuditParameterDb, maintAuditParameterHst);
							maintAuditParameterDao.save(maintAuditParameterHst);
							maintAuditParameterDao.flushSession();
						}
						//deleting from database
						maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);
					}else if(maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)					
							|| maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)
							){

						MaintAuditParameter maintAuditParameterDb=null;

						// get Db object and save into the history
						/*maintAuditParameterDb=getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_AUTH);
					if(maintAuditParameterDb!=null){
						MaintAuditParameterHst maintAuditParameterHst=new MaintAuditParameterHst();
						BeanUtils.copyProperties(maintAuditParameterDb, maintAuditParameterHst);
						maintAuditParameterDao.save(maintAuditParameterHst);						
						maintAuditParameterDao.flushSession();
						// maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_AUTH);
						maintAuditParameterDb=null;
					}*/

						// get object from Wrk update into HST , if exit update notexist create new record into Work
						maintAuditParameterDb =	getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);

						if(maintAuditParameterDb!=null){
							MaintAuditParameterHst maintAuditParameterHst=new MaintAuditParameterHst();
							BeanUtils.copyProperties(maintAuditParameterDb, maintAuditParameterHst);
							maintAuditParameterDao.save(maintAuditParameterHst);
							maintAuditParameterDao.flushSession();
						}

						maintAuditParameter.setMakerTimestamp(businessDate);

						MaintAuditParameterWrk maintAuditParameterWrk=new MaintAuditParameterWrk();
						BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterWrk);
						if(maintAuditParameterDb!=null)
							maintAuditParameterDao.saveOrUpdate(maintAuditParameterWrk);
						else
							maintAuditParameterDao.save(maintAuditParameterWrk);					
						maintAuditParameterDao.flushSession();

					}else if(maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {

						// get Db object and save into the history

						//Save Existing Master Data into History and delete from master
						MaintAuditParameter maintAuditParameterDb=getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_AUTH);
						if(maintAuditParameterDb!=null){
							MaintAuditParameterHst maintAuditParameterHst=new MaintAuditParameterHst();
							BeanUtils.copyProperties(maintAuditParameterDb, maintAuditParameterHst);						
							maintAuditParameterDao.save(maintAuditParameterHst);						
							maintAuditParameterDao.flushSession();
							maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_AUTH);
						}

						//Save Current Work Data into Master and then delete from work
						maintAuditParameter.setCheckerTimestamp(businessDate);

						MaintAuditParameter maintAuditParameterObj=new MaintAuditParameter();					
						BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterObj);
						maintAuditParameterDao.save(maintAuditParameterObj);
						maintAuditParameterDao.deleteMaintAuditParameter(maintAuditParameter.getLegalEntityCode(), maintAuditParameter.getAuditTypeCode(), maintAuditParameter.getId(), BankAuditConstant.STATUS_UNAUTH);
						maintAuditParameterDao.flushSession();

					}else if(maintAuditParameter.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_REJ)) {

						maintAuditParameter.setCheckerTimestamp(businessDate);

						MaintAuditParameterWrk maintAuditParameterWrk=new MaintAuditParameterWrk();
						BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterWrk);
						maintAuditParameterDao.saveOrUpdate(maintAuditParameterWrk);
						maintAuditParameterDao.flushSession();
					} else {
						//save as draft

						maintAuditParameter.setMakerTimestamp(businessDate);

						MaintAuditParameterWrk maintAuditParameterWrk=new MaintAuditParameterWrk();
						BeanUtils.copyProperties(maintAuditParameter, maintAuditParameterWrk);
						maintAuditParameterDao.saveOrUpdate(maintAuditParameterWrk);
						maintAuditParameterDao.flushSession();
					}

				}
			}//for loop
		}catch(RuntimeException e){
			System.out.println(e.getMessage());
			throw e;
		}
		return false;
	}

	@Override
	public DataTableResponse getAuditParameter(Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		return maintAuditParameterDao.getAuditParameter(legalEntityCode,auditTypeCode,entitlement,userName,search,orderColumn,orderDirection,page,size);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditParameter> getMaintAuditParameterbyAuditType(Integer legalEntityCode, String auditTypeCode) {
		Map<String,Object> properties=new HashMap<String ,Object>();
		properties.put("legalEntityCode", legalEntityCode);

		if(!BankAuditUtil.isEmptyString(auditTypeCode)){
			properties.put("auditTypeCode", auditTypeCode);
		}

		return maintAuditParameterDao.getMaintAuditParameterbyAuditType(legalEntityCode, auditTypeCode);
	}

	@Override
	
	public MaintAuditParameter getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(Integer legalEntityCode,String auditTypeCode,
			Integer id,String status) { 

		Map<String,Object> properties=new HashMap<String,Object>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("auditTypeCode", auditTypeCode);
		properties.put("id", id);

		/*System.out.println("id ............. : "+ id);
		System.out.println("Status ............. : "+ status);
		System.out.println("BankAuditUtilStatus ............. : "+ BankAuditConstant.STATUS_AUTH);
		System.out.println("If :"+!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status));
		 */

		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			MaintAuditParameter maintAuditParameter=null;
			MaintAuditParameterWrk maintAuditParameterWrk= (MaintAuditParameterWrk)maintAuditParameterDao.getUniqueEntityByMatchingProperties(MaintAuditParameterWrk.class, properties);			
			if(maintAuditParameterWrk!=null){
				maintAuditParameter=new MaintAuditParameter();
				BeanUtils.copyProperties(maintAuditParameterWrk, maintAuditParameter);
			}
			return maintAuditParameter;
		}else {
			return (MaintAuditParameter)maintAuditParameterDao.getUniqueEntityByMatchingProperties(MaintAuditParameter.class, properties);			
		}

	}

	@Override
	public List<Object[]> getMaintAuditParametersPreviousRatingLov(Integer legalEntityCode, String auditTypeCode) {
		// TODO Auto-generated method stub
		return maintAuditParameterDao.getMaintAuditParametersPreviousRatingLov(legalEntityCode,auditTypeCode);
	}

	@Override
	public boolean updateMaintAuditsParametersDerived(Integer legalEntityCode, String auditTypeCode) {
		// TODO Auto-generated method stub
		return maintAuditParameterDao.updateMaintAuditsParametersDerived(legalEntityCode,auditTypeCode);
	}
	
	@Override
	public DataTableResponse getAuditParameterDerived(Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		return maintAuditParameterDao.getAuditParameterDerived(legalEntityCode,auditTypeCode,entitlement,userName,search,orderColumn,orderDirection,page,size);
	}
	
	public void deleteMaintAuditParameter(Integer legalEntityCode, String auditTypeCode, Integer id,
			String statusAuth){
		maintAuditParameterDao.deleteMaintAuditParameter(legalEntityCode, auditTypeCode, id, statusAuth);
	}
}
