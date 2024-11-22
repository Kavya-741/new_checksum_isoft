package com.bankaudit.helper;

import org.springframework.stereotype.Component;

import com.bankaudit.model.MaintLegalEntity;
import com.bankaudit.repository.MaintLegalEntityRepository;

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
