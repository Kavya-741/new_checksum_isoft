package com.bankaudit.service;

import java.util.ArrayList;
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
import com.bankaudit.model.MaintEntity;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;
import com.bankaudit.model.MaintEntityAuditSubgroupMappingWrk;

@Service
@Transactional("transactionManager")
public class MaintEntityAuditSubgroupMappingServiceImpl implements MaintEntityAuditSubgroupMappingService {

	static final Logger logger = Logger.getLogger(MaintEntityAuditSubgroupMappingServiceImpl.class);


	@Autowired
	MaintEntityService maintEntityService;

	@Autowired
	MaintEntityAuditSubgroupMappingDao maintEntityAuditSubgroupMappingDao;


	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMappingIdWithName(Integer legalEntityCode,
			String mappingType ,String id, String auditTypeCode, String status) {
		
		logger.info(" getMaintEntityAuditSubgroupMappingIdWithName :: "+ legalEntityCode +" :: "+ mappingType +" :: "+ id + " :: "+ auditTypeCode + " :: " + status );
		
		List<MaintEntity> maintEntities=maintEntityService.getMaintEntityByLegalEntityCode(legalEntityCode);
		Map<String, String> unitNameMap = maintEntities.stream().collect(Collectors.toMap(MaintEntity::getUnitCode, MaintEntity::getUnitName));
		
		Map<String, Object> properties=new HashMap<>();
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

	@Override
	public void deleteMaintEntityAuditSubgroupMappingWrk(Integer legalEntityCode, String mappingType, String id) {
		
		maintEntityAuditSubgroupMappingDao.deleteMaintEntityAuditSubgroupMappingWrk( legalEntityCode,  mappingType,  id);
		
	}

}
