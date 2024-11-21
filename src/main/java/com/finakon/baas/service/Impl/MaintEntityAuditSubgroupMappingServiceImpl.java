package com.finakon.baas.service.Impl;

import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.MaintAuditTypeDesc;
import com.finakon.baas.entities.MaintEntity;
import com.finakon.baas.entities.MaintEntityAuditSubgroupMapping;
import com.finakon.baas.entities.MaintEntityAuditSubgroupMappingWrk;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.repository.CustomRepositories.MaintAuditTypeDescCustomRepository;
import com.finakon.baas.repository.JPARepositories.MaintAuditTypeDescRepository;
import com.finakon.baas.repository.JPARepositories.MaintEntityAuditSubgroupMappingRepository;
import com.finakon.baas.repository.JPARepositories.MaintEntityAuditSubgroupMappingWrkRepository;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditTypeDescService;
import com.finakon.baas.service.ServiceInterfaces.MaintEntityAuditSubgroupMappingService;
import com.finakon.baas.service.ServiceInterfaces.MaintEntityService;

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

@Service
@Transactional("transactionManager")
public class MaintEntityAuditSubgroupMappingServiceImpl implements MaintEntityAuditSubgroupMappingService {

	static final Logger logger = Logger.getLogger(MaintEntityAuditSubgroupMappingServiceImpl.class);
	/**
	 * The maint entity audit subgroup mapping dao is autowired and make methods
	 * available from dao layer .
	 */
	@Autowired
	MaintEntityAuditSubgroupMappingRepository maintEntityAuditSubgroupMappingRepository;

	@Autowired
	MaintEntityAuditSubgroupMappingWrkRepository maintEntityAuditSubgroupMappingWrkRepository;

	@Autowired
	MaintEntityService maintEntityService;


	@Override
	public List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMappingIdWithName(Integer legalEntityCode,
			String mappingType, String id, String auditTypeCode, String status) {

		logger.info(" getMaintEntityAuditSubgroupMappingIdWithName :: " + legalEntityCode + " :: " + mappingType
				+ " :: " + id + " :: " + auditTypeCode + " :: " + status);

		List<MaintEntity> maintEntities = maintEntityService.getMaintEntityByLegalEntityCode(legalEntityCode);
		Map<String, String> unitNameMap = maintEntities.stream()
				.collect(Collectors.toMap(MaintEntity::getUnitCode, MaintEntity::getUnitName));
		List<MaintEntityAuditSubgroupMapping> maintEntityAuditSubgroupMappings = new ArrayList<>();
		MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping = null;
		if (!status.equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			List<MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappingWrks = maintEntityAuditSubgroupMappingWrkRepository
					.findByMatchingProperties(legalEntityCode, mappingType, id, auditTypeCode);
			if (maintEntityAuditSubgroupMappingWrks != null) {
				for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk : maintEntityAuditSubgroupMappingWrks) {
					maintEntityAuditSubgroupMapping = new MaintEntityAuditSubgroupMapping();
					BeanUtils.copyProperties(maintEntityAuditSubgroupMappingWrk, maintEntityAuditSubgroupMapping);
					maintEntityAuditSubgroupMappings.add(maintEntityAuditSubgroupMapping);
				}
			}
			maintEntityAuditSubgroupMappings.forEach(i1 -> i1.setId(i1.getId() + "_" + unitNameMap.get(i1.getId())));
			return maintEntityAuditSubgroupMappings;
		} else {
			maintEntityAuditSubgroupMappings = maintEntityAuditSubgroupMappingRepository
					.findByMatchingProperties(legalEntityCode, mappingType, id, auditTypeCode);
			maintEntityAuditSubgroupMappings.forEach(i1 -> i1.setId(i1.getId() + "_" + unitNameMap.get(i1.getId())));
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
		String auditTypeCode = null;
		if(!status.equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			List< MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappingWrks=maintEntityAuditSubgroupMappingWrkRepository
			.findByMatchingProperties(legalEntityCode, mappingType, id, auditTypeCode);;
			if(maintEntityAuditSubgroupMappingWrks!=null){
				for (MaintEntityAuditSubgroupMappingWrk maintEntityAuditSubgroupMappingWrk : maintEntityAuditSubgroupMappingWrks) {
					maintEntityAuditSubgroupMapping=new MaintEntityAuditSubgroupMapping();
					BeanUtils.copyProperties(maintEntityAuditSubgroupMappingWrk, maintEntityAuditSubgroupMapping);
					maintEntityAuditSubgroupMappings.add(maintEntityAuditSubgroupMapping);
				}
			}
			return maintEntityAuditSubgroupMappings;
		}else {
			return maintEntityAuditSubgroupMappingRepository.findByMatchingProperties(legalEntityCode, mappingType, id, auditTypeCode);
		}
		
	}

}
