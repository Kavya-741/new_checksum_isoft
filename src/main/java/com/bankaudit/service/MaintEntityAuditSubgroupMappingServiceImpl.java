package com.bankaudit.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintEntityAuditSubgroupMappingDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.MaintEntityAuditSubGroupMappingDto;
import com.bankaudit.model.MaintEntity;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;
import com.bankaudit.model.MaintEntityAuditSubgroupMappingWrk;
import com.bankaudit.model.User;
import com.bankaudit.util.BankAuditUtil;

@Service
@Transactional("transactionManager")
public class MaintEntityAuditSubgroupMappingServiceImpl implements MaintEntityAuditSubgroupMappingService {

	static final Logger logger = Logger.getLogger(MaintEntityAuditSubgroupMappingServiceImpl.class);


	@Autowired
	MaintEntityService maintEntityService;

	@Autowired
	MaintEntityAuditSubgroupMappingDao maintEntityAuditSubgroupMappingDao;

	@Autowired
	UserService userService;


	@Override
	public void createMaintEntityAuditSubgroupMapping(MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping) {
		maintEntityAuditSubgroupMappingDao.save(maintEntityAuditSubgroupMapping);
	}


	@Override
	public void updateMaintEntityAuditSubgroupMapping(MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping) {

		maintEntityAuditSubgroupMappingDao.update(maintEntityAuditSubgroupMapping);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMapping(Integer legalEntityCode,
			String mappingType ,String id,String status) {
		
		Map<String, Object> properties=new HashMap<String,Object>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("mappingType", mappingType);
		properties.put("id", id);
		List<MaintEntityAuditSubgroupMapping> maintEntityAuditSubgroupMappings=new ArrayList<>();
		MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping=null;
		if(!status.equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			List< MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappingWrks=maintEntityAuditSubgroupMappingDao.getEntitiesByMatchingProperties(MaintEntityAuditSubgroupMappingWrk.class, properties);
			if(maintEntityAuditSubgroupMappingWrks!=null){
				for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk : maintEntityAuditSubgroupMappingWrks) {
					maintEntityAuditSubgroupMapping=new MaintEntityAuditSubgroupMapping();
					BeanUtils.copyProperties(maintEntityAuditSubgroupMappingWrk, maintEntityAuditSubgroupMapping);
					maintEntityAuditSubgroupMappings.add(maintEntityAuditSubgroupMapping);
				}
			}
			return maintEntityAuditSubgroupMappings;
		}else {
			return maintEntityAuditSubgroupMappingDao.getEntitiesByMatchingProperties(MaintEntityAuditSubgroupMapping.class, properties);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMappingIdWithName(Integer legalEntityCode,
			String mappingType ,String id, String auditTypeCode, String status) {
		
		logger.info(" getMaintEntityAuditSubgroupMappingIdWithName :: "+ legalEntityCode +" :: "+ mappingType +" :: "+ id + " :: "+ auditTypeCode + " :: " + status );
		
		List<MaintEntity> maintEntities=maintEntityService.getMaintEntityByLegalEntityCode(legalEntityCode);
		Map<String, String> unitNameMap = maintEntities.stream().collect(Collectors.toMap(MaintEntity::getUnitCode, MaintEntity::getUnitName));
		
		Map<String, Object> properties=new HashMap<String,Object>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("mappingType", mappingType);
		properties.put("id", id);
		if(auditTypeCode !=null && !auditTypeCode.trim().equals("")) properties.put("auditTypeCode", auditTypeCode);
		
		List<MaintEntityAuditSubgroupMapping> maintEntityAuditSubgroupMappings=new ArrayList<>();
		MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping=null;
		if(!status.equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			List< MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappingWrks=maintEntityAuditSubgroupMappingDao.getEntitiesByMatchingProperties(MaintEntityAuditSubgroupMappingWrk.class, properties);
			if(maintEntityAuditSubgroupMappingWrks!=null){
				for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk : maintEntityAuditSubgroupMappingWrks) {
					maintEntityAuditSubgroupMapping=new MaintEntityAuditSubgroupMapping();
					BeanUtils.copyProperties(maintEntityAuditSubgroupMappingWrk, maintEntityAuditSubgroupMapping);
					maintEntityAuditSubgroupMappings.add(maintEntityAuditSubgroupMapping);
				}
			}
			maintEntityAuditSubgroupMappings.forEach(i1 -> i1.setId(i1.getId()+"_"+unitNameMap.get(i1.getId())));
			return maintEntityAuditSubgroupMappings;
		}else {
			maintEntityAuditSubgroupMappings= maintEntityAuditSubgroupMappingDao.getEntitiesByMatchingProperties(MaintEntityAuditSubgroupMapping.class, properties);
			maintEntityAuditSubgroupMappings.forEach(i1 -> i1.setId(i1.getId()+"_"+unitNameMap.get(i1.getId())));
			return maintEntityAuditSubgroupMappings;
		}
		
	}

	
	@Override
	public void deleteMaintEntityAuditSubgroupMapping(Integer legalEntityCode, String mappingType, String id) {
		
		maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMapping( legalEntityCode,  mappingType,  id);
		
	}
	

	@Override
	public void deleteMaintEntityAuditSubgroupMappingWrk(Integer legalEntityCode, String mappingType, String id) {
		
		maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMappingWrk( legalEntityCode,  mappingType,  id);
		
	}

	@Override
	public void createMaintEntityAuditSubgroupMappings(MaintEntityAuditSubGroupMappingDto mappingDto) {
		if(mappingDto.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)
				|| mappingDto.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH))
		{
			if(mappingDto.getMaintEntityAuditSubgroupMappingWrks()!=null 
					 && !mappingDto.getMaintEntityAuditSubgroupMappingWrks().isEmpty()){
				for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk  : mappingDto.getMaintEntityAuditSubgroupMappingWrks()) {
					 //maintEntityAuditSubgroupMappingWrk.setId(mappingDto.getEntityOrUser());
					 maintEntityAuditSubgroupMappingWrk.setMappingType(mappingDto.getMappingType());
					 maintEntityAuditSubgroupMappingWrk.setMaker(mappingDto.getMaker());
					 maintEntityAuditSubgroupMappingWrk.setMakerTimestamp(mappingDto.getMakerTimestamp());
					 maintEntityAuditSubgroupMappingWrk.setStatus(mappingDto.getStatus());
					 maintEntityAuditSubgroupMappingDao.save(maintEntityAuditSubgroupMappingWrk);
					}
			 }
		}else if (mappingDto.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			if(mappingDto.getMaintEntityAuditSubgroupMappingWrks()!=null 
					 && !mappingDto.getMaintEntityAuditSubgroupMappingWrks().isEmpty()){
				 MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping=null;
				 for (Object maintEntityAuditSubgroupMappingWrk : mappingDto.getMaintEntityAuditSubgroupMappingWrks()) {
					 maintEntityAuditSubgroupMapping=new MaintEntityAuditSubgroupMapping();
					 BeanUtils.copyProperties(maintEntityAuditSubgroupMappingWrk, maintEntityAuditSubgroupMapping);
					 //maintEntityAuditSubgroupMapping.setId(mappingDto.getEntityOrUser());	
					 maintEntityAuditSubgroupMapping.setMappingType(mappingDto.getMappingType());
					 maintEntityAuditSubgroupMapping.setMaker(mappingDto.getMaker());
					 maintEntityAuditSubgroupMapping.setStatus(mappingDto.getStatus());
					 maintEntityAuditSubgroupMappingDao.save(maintEntityAuditSubgroupMapping);
				}
			 }
		}
	}
	
	@Override
	public void updateMaintEntityAuditSubgroupMappings(MaintEntityAuditSubGroupMappingDto mappingDto) {
		
		List<MaintEntityAuditSubgroupMappingWrk> listMaintEntityAuditSubgroupMappingWrk = mappingDto.getMaintEntityAuditSubgroupMappingWrks();
		logger.info("Insdie updateMaintEntityAuditSubgroupMappings of MaintEntityAuditSubgroupMappingServiceImpl");
		if(!mappingDto.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){

			if(mappingDto.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| mappingDto.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)
					|| mappingDto.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)){
				mappingDto.setMakerTimestamp(new Date());
			}		
			// delete the existing work maintEntityAuditSubgroupMapping			
			for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk  : listMaintEntityAuditSubgroupMappingWrk) {
				 
					maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMappingWrkByIds(maintEntityAuditSubgroupMappingWrk.getLegalEntityCode(), maintEntityAuditSubgroupMappingWrk.getMappingType(),
							maintEntityAuditSubgroupMappingWrk.getId(), maintEntityAuditSubgroupMappingWrk.getAuditTypeCode(), mappingDto.getAuthUniqueId());
				 
				
				maintEntityAuditSubgroupMappingDao.flushSession();
			}
			
			// save again 
			 if(mappingDto.getMaintEntityAuditSubgroupMappingWrks()!=null && !mappingDto.getMaintEntityAuditSubgroupMappingWrks().isEmpty()){
				for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk  : listMaintEntityAuditSubgroupMappingWrk) {
					 //maintEntityAuditSubgroupMappingWrk.setId(mappingDto.getEntityOrUser());
					 maintEntityAuditSubgroupMappingWrk.setMappingType(mappingDto.getMappingType());					
					 maintEntityAuditSubgroupMappingWrk.setMaker(mappingDto.getMaker());
					 maintEntityAuditSubgroupMappingWrk.setStatus(mappingDto.getStatus());
					 maintEntityAuditSubgroupMappingWrk.setAuditTypeCode(mappingDto.getAuditTypeCode());// added to fix
					 maintEntityAuditSubgroupMappingWrk.setAuthUniqueId(mappingDto.getAuthUniqueId());// added to fix
					 maintEntityAuditSubgroupMappingDao.saveOrUpdate(maintEntityAuditSubgroupMappingWrk);
				}
			 }
		}else{
			// delete the existing  maintEntityAuditSubgroupMapping from both table 
			
			for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk  : listMaintEntityAuditSubgroupMappingWrk) {
				/*maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMappingWrk( maintEntityAuditSubgroupMappingWrk.getLegalEntityCode(), "E",  maintEntityAuditSubgroupMappingWrk.getId());
				maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMapping( maintEntityAuditSubgroupMappingWrk.getLegalEntityCode(), "E",  maintEntityAuditSubgroupMappingWrk.getId());*/
				
				maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMappingWrkByIds(maintEntityAuditSubgroupMappingWrk.getLegalEntityCode(), maintEntityAuditSubgroupMappingWrk.getMappingType(),
						maintEntityAuditSubgroupMappingWrk.getId(), maintEntityAuditSubgroupMappingWrk.getAuditTypeCode(), mappingDto.getAuthUniqueId() );
				
				maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMappingByIds(maintEntityAuditSubgroupMappingWrk.getLegalEntityCode(), maintEntityAuditSubgroupMappingWrk.getMappingType(),
						maintEntityAuditSubgroupMappingWrk.getId(), maintEntityAuditSubgroupMappingWrk.getAuditTypeCode(), mappingDto.getAuthUniqueId());
			}
			
			maintEntityAuditSubgroupMappingDao.flushSession();
			
			if(mappingDto.getMaintEntityAuditSubgroupMappingWrks()!=null 
					 && !mappingDto.getMaintEntityAuditSubgroupMappingWrks().isEmpty()){
				 MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping=null; 
				 for (Object maintEntityAuditSubgroupMappingWrk  : listMaintEntityAuditSubgroupMappingWrk) {

					 maintEntityAuditSubgroupMapping=new MaintEntityAuditSubgroupMapping();
					 BeanUtils.copyProperties(maintEntityAuditSubgroupMappingWrk, maintEntityAuditSubgroupMapping);
					 String authUniqueId =maintEntityAuditSubgroupMapping.getLegalEntityCode() + mappingDto.getAuditTypeCode()+System.currentTimeMillis(); 
							 
					 maintEntityAuditSubgroupMapping.setMaker(mappingDto.getMaker());
					 maintEntityAuditSubgroupMapping.setMakerTimestamp(mappingDto.getCheckerTimestamp());
					 maintEntityAuditSubgroupMapping.setStatus(mappingDto.getStatus());
					 maintEntityAuditSubgroupMapping.setChecker(mappingDto.getChecker());
					 //maintEntityAuditSubgroupMapping.setId(mappingDto.getEntityOrUser());
					 maintEntityAuditSubgroupMapping.setCheckerTimestamp(mappingDto.getCheckerTimestamp());
					 maintEntityAuditSubgroupMapping.setAuditTypeCode(mappingDto.getAuditTypeCode());// added to fix
					 maintEntityAuditSubgroupMapping.setAuthUniqueId(authUniqueId);
					 maintEntityAuditSubgroupMappingDao.saveOrUpdate(maintEntityAuditSubgroupMapping);
				}
			}
		}
	}


	@Override
	public DataTableResponse getmaintEntityAuditSubgroupMapping(Integer legalEntityCode, String userId, String search,
			Integer orderColumn, String orderDirection, Integer page, Integer size) {

		return maintEntityAuditSubgroupMappingDao.getmaintEntityAuditSubgroupMapping(legalEntityCode,userId,
				search,orderColumn,orderDirection,page,size);
	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaintEntityAuditSubGroupMappingDto> getEntityOrUserByLevel(Integer legalEntityCode, String levelCode, String mappingType ,String status) {
		List<MaintEntityAuditSubGroupMappingDto> mappingDtoLst= new ArrayList<>();
		if("E".equalsIgnoreCase(mappingType)) {
			//for(MaintEntity maintEntity: maintEntityService.getMaintEntityByLegalEntityCodeAndLevelCode(legalEntityCode, levelCode)) {
			for(MaintEntity maintEntity: maintEntityService.getEntityByLevelAndNotInGrpEntityMapping(legalEntityCode, levelCode, status)) {
				MaintEntityAuditSubGroupMappingDto mappingDto =new MaintEntityAuditSubGroupMappingDto();
				mappingDto.setEntityOrUser(maintEntity.getUnitCode());
				mappingDto.setEntityOrUserName(maintEntity.getUnitName());
				mappingDtoLst.add(mappingDto);
			}
		}else if("U".equalsIgnoreCase(mappingType)) {
			//for(User user: userService.getUsersByLevel(legalEntityCode, levelCode, status)) {
			for(User user: userService.getUsersByLevelAndNotInGrpEntityMapping(legalEntityCode, levelCode, status)) {
				MaintEntityAuditSubGroupMappingDto mappingDto =new MaintEntityAuditSubGroupMappingDto();
				mappingDto.setEntityOrUser(user.getUserId());
				mappingDto.setEntityOrUserName(user.getFirstName().concat(" ").concat(user.getFirstName()!=null?user.getFirstName():""));
				mappingDtoLst.add(mappingDto);
			}
		}
		return mappingDtoLst;
		
	}


}
