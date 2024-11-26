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
import com.bankaudit.dao.MaintEntityDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintEntity;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;
import com.bankaudit.model.MaintEntityAuditSubgroupMappingHst;
import com.bankaudit.model.MaintEntityHst;
import com.bankaudit.model.MaintEntityWrk;

@Service("maintEntityService")
@Transactional("transactionManager")
public class MaintEntityServiceImpl implements MaintEntityService {


	@Autowired
	MaintEntityDao maintEntityDao;

	@Autowired 
	MaintEntityAuditSubgroupMappingService maintEntityAuditSubgroupMappingService;

	public List<String> getSubBranchesGyUserIdOrUnitId(Integer legalEntityCode,String type,String userIdOrUnitId){
		return maintEntityDao.getSubBranchesGyUserIdOrUnitId(legalEntityCode,type, userIdOrUnitId);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<MaintEntity> getMaintEntityByLegalEntityCode(Integer legalEntityCode) {
		
		Map<String,Object> properties=new HashMap<>();
		
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		return maintEntityDao.getEntitiesByMatchingProperties(MaintEntity.class, properties);
	}

	@Override
	public MaintEntity getUnique(Integer legalEntityCode, String unitCode,String status) {
		 MaintEntity maintEntity=maintEntityDao.getUnique(legalEntityCode,unitCode,status);
		 if(maintEntity!=null){
			 maintEntity.setMaintEntityAuditSubgroupMappings(maintEntityAuditSubgroupMappingService.getMaintEntityAuditSubgroupMapping(legalEntityCode, "E", unitCode,status));			 
		 }
		 return maintEntity;
	}


	@Override
	public void createMaintEntity(MaintEntityWrk maintEntityWrk) {
			
		if(maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)
				|| maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
				){
			
			maintEntityDao.save(maintEntityWrk);
		}else if (maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			MaintEntity maintEntity=new MaintEntity();
			BeanUtils.copyProperties(maintEntityWrk, maintEntity);
			maintEntityDao.save(maintEntity);
		}
	}

	@Override
	public void updateMaintEntity(MaintEntityWrk maintEntityWrk) {
		
		if(!maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){

			if(maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)
					|| maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)){
				maintEntityWrk.setMakerTimestamp(new Date());
			}
			

			if(!maintEntityWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)){
				// get Db object and save into the history
				MaintEntity maintEntityDb=getUnique(maintEntityWrk.getLegalEntityCode(), maintEntityWrk.getUnitCode(), BankAuditConstant.STATUS_UNAUTH);
				if(maintEntityDb!=null){
					MaintEntityHst maintEntityHst=new MaintEntityHst();
					String[] ignoreProperties={
							"maintLegalEntity",
							"entityLevelCodeDesc"
					};
					BeanUtils.copyProperties(maintEntityDb, maintEntityHst, ignoreProperties);
					maintEntityDao.save(maintEntityHst);
					
				}
			}
			

			maintEntityDao.flushSession();
			// save again 
			maintEntityDao.saveOrUpdate(maintEntityWrk);
			 
			
			
		}else{

			// get Db object and save into the history
			MaintEntity maintEntityDb=getUnique(maintEntityWrk.getLegalEntityCode(), maintEntityWrk.getUnitCode(), BankAuditConstant.STATUS_AUTH);
			if(maintEntityDb!=null){
				MaintEntityHst maintEntityHst=new MaintEntityHst();
				String[] ignoreProperties={
						"maintLegalEntity",
						"entityLevelCodeDesc"
				};
				BeanUtils.copyProperties(maintEntityDb, maintEntityHst, ignoreProperties);
				maintEntityDao.save(maintEntityHst);
				 if(maintEntityHst.getMaintEntityAuditSubgroupMappings()!=null 
						 && !maintEntityHst.getMaintEntityAuditSubgroupMappings().isEmpty()){
					 MaintEntityAuditSubgroupMappingHst maintEntityAuditSubgroupMappingHst=null;
					 for (Object maintEntityAuditSubgroupMapping  : maintEntityHst.getMaintEntityAuditSubgroupMappings()) {
						 maintEntityAuditSubgroupMappingHst=new MaintEntityAuditSubgroupMappingHst();
						 BeanUtils.copyProperties(maintEntityAuditSubgroupMapping, maintEntityAuditSubgroupMappingHst);
						 maintEntityAuditSubgroupMappingHst.setId(maintEntityHst.getUnitCode());
						 maintEntityAuditSubgroupMappingHst.setMaker(maintEntityHst.getMaker());
						 maintEntityAuditSubgroupMappingHst.setStatus(maintEntityHst.getStatus());
						 maintEntityDao.save(maintEntityAuditSubgroupMappingHst);
						}
				 }
				
				
			}
			
			//delete the existing entity  
		/*	maintEntityDao.deleteMaintEntity(maintEntityWrk.getLegalEntityCode(),maintEntityWrk.getUnitCode());*/
			maintEntityDao.deleteMaintEntityWrk(maintEntityWrk.getLegalEntityCode(),maintEntityWrk.getUnitCode());
			
			// delete the existing  maintEntityAuditSubgroupMapping from both table 
			maintEntityAuditSubgroupMappingService.deleteMaintEntityAuditSubgroupMappingWrk(maintEntityWrk.getLegalEntityCode(),
					 "E", maintEntityWrk.getUnitCode());
		/*	maintEntityAuditSubgroupMappingService.deleteMaintEntityAuditSubgroupMapping(maintEntityWrk.getLegalEntityCode(),
					 "E", maintEntityWrk.getUnitCode());*/
			maintEntityDao.flushSession();
			
			MaintEntity maintEntity=new MaintEntity();
			
			BeanUtils.copyProperties(maintEntityWrk, maintEntity);
			maintEntity.setCheckerTimestamp(new Date());
			// save again 
			maintEntityDao.saveOrUpdate(maintEntity);
			 
			 if(maintEntity.getMaintEntityAuditSubgroupMappings()!=null 
					 && !maintEntity.getMaintEntityAuditSubgroupMappings().isEmpty()){
				 MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping=null; 
				 for (Object maintEntityAuditSubgroupMappingWrk  : maintEntity.getMaintEntityAuditSubgroupMappings()) {
					 maintEntityAuditSubgroupMapping=new MaintEntityAuditSubgroupMapping();
					 BeanUtils.copyProperties(maintEntityAuditSubgroupMappingWrk, maintEntityAuditSubgroupMapping);
					 maintEntityAuditSubgroupMapping.setStatus(maintEntity.getStatus());
					 maintEntityAuditSubgroupMapping.setMaker(maintEntity.getMaker());
					 maintEntityAuditSubgroupMapping.setId(maintEntity.getUnitCode());
					 maintEntityAuditSubgroupMapping.setMakerTimestamp(maintEntity.getMakerTimestamp());
					 maintEntityDao.saveOrUpdate(maintEntityAuditSubgroupMapping);
					}
				 
			 }
		}
		
	}

	@Override
	public DataTableResponse getMaintEntity(Integer legalEntityCode, String levelCodeStr,String userId,String parentUnitCode, String search,
			Integer orderColumn, String orderDirection, Integer page, Integer size) {

		return maintEntityDao.getMaintEntity(legalEntityCode,levelCodeStr,userId,parentUnitCode,search,orderColumn,orderDirection,page,size);
	
	}

	@Override
	public Boolean isMaintEntity(Integer legalEntityCode, String unitCode) {
		return maintEntityDao.isMaintEntity( legalEntityCode,  unitCode);
	}

	@Override
	public List<MaintEntity> getMaintEntityByLegalEntityCodeAndLevelCode(Integer legalEntityCode, String levelCode) {
			
		return maintEntityDao.getMaintEntityByLegalEntityCodeAndLevelCode(legalEntityCode,levelCode);
	}


}
