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
import com.bankaudit.dao.MaintAuditProcessDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditProcess;
import com.bankaudit.model.MaintAuditProcessHst;
import com.bankaudit.model.MaintAuditProcessWrk;

@Service("maintAuditProcessService")
@Transactional("transactionManager")
public class MaintAuditProcessServiceImpl implements MaintAuditProcessService {

	@Autowired
	MaintAuditProcessDao maintAuditProcessDao;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	@Override
	public List<MaintAuditProcess> getMaintAuditProcess(Integer legalEntityCode) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", "A");
		return maintAuditProcessDao.getEntitiesByMatchingProperties(MaintAuditProcess.class, properties);
	}

	@Override
	public Boolean isMaintAuditProcess(Integer legalEntityCode, String processCode) {
		return maintAuditProcessDao.isMaintAuditProcess(legalEntityCode, processCode);
	}

	@Override
	public void createMaintAuditProcess(MaintAuditProcess maintAuditProcess) throws Exception {

		if (maintAuditProcess.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)) {

			MaintAuditProcessWrk maintAuditProcessWrk = new MaintAuditProcessWrk();
			BeanUtils.copyProperties(maintAuditProcess, maintAuditProcessWrk);
			maintAuditProcessDao.save(maintAuditProcessWrk);

		} else if (maintAuditProcess.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditProcessDao.save(maintAuditProcess);
		} else {
			throw new Exception("invalid status");
		}
	}

	@Override
	public DataTableResponse getMaintAuditProcess(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {

		return maintAuditProcessDao.getMaintAuditProcess(legalEntityCode, search, orderColumn,
				orderDirection, page, size);
	}

	@Override
	public void deleteProcessByID(Integer legalEntityCode, String processId, String userId) {
		maintAuditProcessDao.deleteMaintAuditProcess(legalEntityCode, processId, BankAuditConstant.STATUS_REJ);
	}


	@Override
	public void updateMaintAuditProcess(MaintAuditProcess maintAuditProcess) {

		
		if(!maintAuditProcess.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){

			if(maintAuditProcess.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintAuditProcess.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
				maintAuditProcess.setMakerTimestamp(new Date());
			}

			// get Db object and save into the history
			MaintAuditProcess maintAuditProcessDb=getMaintAuditProcess(maintAuditProcess.getLegalEntityCode(), maintAuditProcess.getProcessId(), BankAuditConstant.STATUS_UNAUTH);
			if(maintAuditProcessDb!=null){
				MaintAuditProcessHst MaintAuditProcessHst=new MaintAuditProcessHst();
				BeanUtils.copyProperties(maintAuditProcessDb, MaintAuditProcessHst);
				maintAuditProcessDao.save(MaintAuditProcessHst);
			}
			
			MaintAuditProcessWrk maintAuditProcessWrk=new MaintAuditProcessWrk();
			BeanUtils.copyProperties(maintAuditProcess, maintAuditProcessWrk);
			maintAuditProcessDao.flushSession();
			maintAuditProcessDao.saveOrUpdate(maintAuditProcessWrk);
			
		}else{

			// get Db object and save into the history
			MaintAuditProcess maintAuditProcessDb=getMaintAuditProcess(maintAuditProcess.getLegalEntityCode(), maintAuditProcess.getProcessId(), BankAuditConstant.STATUS_AUTH);
			if(maintAuditProcessDb!=null){
				maintAuditProcessDb.setEntityStatus(BankAuditConstant.STATUS_EXPIRE);
				maintAuditProcessDao.update(maintAuditProcessDb);
				maintAuditProcessDao.flushSession();
			}

			// delete from work
			maintAuditProcessDao.deleteMaintAuditProcess(maintAuditProcess.getLegalEntityCode(),maintAuditProcess.getProcessId(),BankAuditConstant.STATUS_UNAUTH);
			
			// save again 
			maintAuditProcessDao.flushSession();
			maintAuditProcess.setProcessId(sequenceAppenderService.getAutoSequenceId());
			maintAuditProcessDao.save(maintAuditProcess);
			
		}
	
	}

	@Override
	public MaintAuditProcess getMaintAuditProcess(Integer legalEntityCode, String processId,String status) {
		Map<String,Object> properties=new HashMap<String, Object>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("processId", processId);
		
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			MaintAuditProcess maintAuditProcess=null;
			MaintAuditProcessWrk maintAuditProcessWrk =(MaintAuditProcessWrk) maintAuditProcessDao.getUniqueEntityByMatchingProperties(MaintAuditProcessWrk.class, properties);
			if(maintAuditProcessWrk!=null){
				maintAuditProcess=new MaintAuditProcess();
				BeanUtils.copyProperties(maintAuditProcessWrk, maintAuditProcess);
			}
			return maintAuditProcess;
		}else {
			return (MaintAuditProcess) maintAuditProcessDao.getUniqueEntityByMatchingProperties(MaintAuditProcess.class, properties);
		}
		
	}


}
