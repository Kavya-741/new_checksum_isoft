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
import com.bankaudit.dao.MaintAuditFindingDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditFinding;
import com.bankaudit.model.MaintAuditFindingHst;
import com.bankaudit.model.MaintAuditFindingWrk;

@Service("maintAuditFindingService")
@Transactional("transactionManager")
public class MaintAuditFindingServiceImpl implements MaintAuditFindingService {

	@Autowired
	MaintAuditFindingDao maintAuditFindingDao;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	@Override
	public List<MaintAuditFinding> getMaintAuditFinding(Integer legalEntityCode) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", "A");

		return maintAuditFindingDao.getEntitiesByMatchingProperties(MaintAuditFinding.class, properties);
	}

	@Override
	public Boolean isMaintAuditFinding(Integer legalEntityCode, String findingCode) {

		return maintAuditFindingDao.isMaintAuditFinding(legalEntityCode, findingCode);
	}

	@Override
	public void createMaintAuditFinding(MaintAuditFinding maintAuditFinding) throws Exception {

		if (maintAuditFinding.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)) {

			MaintAuditFindingWrk maintAuditFindingWrk = new MaintAuditFindingWrk();
			BeanUtils.copyProperties(maintAuditFinding, maintAuditFindingWrk);
			maintAuditFindingDao.save(maintAuditFindingWrk);

		} else if (maintAuditFinding.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			maintAuditFindingDao.save(maintAuditFinding);
		} else {
			throw new Exception("invalid status");
		}
	}

	@Override
	public void deleteFindingByID(Integer legalEntityCode, String findingId, String userId) {
		maintAuditFindingDao.deleteMaintAuditFinding(legalEntityCode, findingId, BankAuditConstant.STATUS_REJ);
	}

	@Override
	public void updateMaintAuditFinding(MaintAuditFinding maintAuditFinding) {

		
		if(!maintAuditFinding.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){

			if(maintAuditFinding.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintAuditFinding.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
				maintAuditFinding.setMakerTimestamp(new Date());
			}

			// get Db object and save into the history
			MaintAuditFinding maintAuditFindingDb=getMaintAuditFinding(maintAuditFinding.getLegalEntityCode(), maintAuditFinding.getFindingId(), BankAuditConstant.STATUS_UNAUTH);
			if(maintAuditFindingDb!=null){
				MaintAuditFindingHst maintEntityHst=new MaintAuditFindingHst();
				BeanUtils.copyProperties(maintAuditFindingDb, maintEntityHst);
				maintAuditFindingDao.save(maintEntityHst);
			}
			
			MaintAuditFindingWrk maintAuditFindingWrk=new MaintAuditFindingWrk();
			BeanUtils.copyProperties(maintAuditFinding, maintAuditFindingWrk);
			maintAuditFindingDao.flushSession();
			
			maintAuditFindingDao.saveOrUpdate(maintAuditFindingWrk);
			
		}else{

			// get Db object and save into the history
			MaintAuditFinding maintAuditFindingDb=getMaintAuditFinding(maintAuditFinding.getLegalEntityCode(), maintAuditFinding.getFindingId(), BankAuditConstant.STATUS_AUTH);
			if(maintAuditFindingDb!=null){
				maintAuditFindingDb.setEntityStatus(BankAuditConstant.STATUS_EXPIRE);
				maintAuditFindingDao.update(maintAuditFindingDb);
			}

			// delete from work
			maintAuditFindingDao.deleteMaintAuditFinding(maintAuditFinding.getLegalEntityCode(),maintAuditFinding.getFindingId(),BankAuditConstant.STATUS_UNAUTH);
			
			// save again 
			maintAuditFindingDao.flushSession();
			maintAuditFinding.setFindingId(sequenceAppenderService.getAutoSequenceId());
			maintAuditFindingDao.save(maintAuditFinding);
			
		}
	
	}

	@Override
	public MaintAuditFinding getMaintAuditFinding(Integer legalEntityCode, String findingId,String status) {
		Map<String,Object> properties=new HashMap<String, Object>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("findingId", findingId);
		
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			MaintAuditFinding maintAuditFinding=null;
			MaintAuditFindingWrk maintAuditFindingWrk =(MaintAuditFindingWrk) maintAuditFindingDao.getUniqueEntityByMatchingProperties(MaintAuditFindingWrk.class, properties);
			if(maintAuditFindingWrk!=null){
				maintAuditFinding=new MaintAuditFinding();
				BeanUtils.copyProperties(maintAuditFindingWrk, maintAuditFinding);
			}
			return maintAuditFinding;
		}else {
			return (MaintAuditFinding) maintAuditFindingDao.getUniqueEntityByMatchingProperties(MaintAuditFinding.class, properties);
		}
	}

		@Override
	public DataTableResponse getMaintAuditFindings(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size) {
		return maintAuditFindingDao.getMaintAuditFindings( legalEntityCode,  search,  orderColumn,
				 orderDirection,  page,  size);
	}


}
