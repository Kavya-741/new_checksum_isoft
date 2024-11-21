package com.finakon.baas.helper;

import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.repository.JPARepositories.MaintLegalEntityRepository;

import org.springframework.stereotype.Component;

@Component
public class DomainUtil {

    private static MaintLegalEntityRepository maintLegalEntityRepository;

    public DomainUtil(MaintLegalEntityRepository maintLegalEntityRepository) {
        DomainUtil.maintLegalEntityRepository = maintLegalEntityRepository;
    }

    public static final MaintLegalEntity getLegalEntityCodeByDomain(String domain) {
        MaintLegalEntity maintLegalEntity = maintLegalEntityRepository.findLegalEnityCodeByDomain(domain);
    
        if(maintLegalEntity != null) {
            return maintLegalEntity;
        } else {
            return null; // send null if domain is not found
        }
    }
}
