package com.finakon.baas.repository.CustomRepositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.MaintAuditTypeDesc;
import com.finakon.baas.helper.BankAuditUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


@Repository
public class MaintAuditTypeDescCustomRepository {

    private static final Logger logger = Logger.getLogger(MaintAuditTypeDescCustomRepository.class);
    
    @PersistenceContext
    private EntityManager entityManager;

    public DataTableResponse getMaintAuditTypeDesc(Integer legalEntityCode, String search, Integer orderColumn,
                                                   String orderDirection, Integer page, Integer size) {

        DataTableResponse dataTableResponse = new DataTableResponse();

        StringBuilder queryString = new StringBuilder("SELECT at AS maintAuditTypeDesc, ")
            .append("(SELECT CONCAT(gp.value , '-' , gp.description) ")
            .append("FROM GeneralParameter gp WHERE gp.key1='STATUS' AND gp.value=at.status ")
            .append("AND gp.legalEntityCode=at.legalEntityCode) AS status ")
            .append("FROM MaintAuditTypeDesc at WHERE at.legalEntityCode = :legalEntityCode");

        Map<String, Object> params = new HashMap<>();
        params.put("legalEntityCode", legalEntityCode);

        if (!BankAuditUtil.isEmptyString(search)) {
            queryString.append(" AND (at.auditTypeCode LIKE :search OR at.auditTypeDesc LIKE :search ")
                .append("OR at.status IN (SELECT value FROM GeneralParameter WHERE key1='STATUS' AND description LIKE :search) ")
                .append("OR at.maker LIKE :search)");
            params.put("search", "%" + search + "%");
        }

        String[] columns = { "at.auditTypeCode", "at.auditTypeDesc", "at.maker", "at.status" };
        if (orderColumn != null && !BankAuditUtil.isEmptyString(orderDirection)) {
            queryString.append(" ORDER BY ").append(columns[orderColumn]).append(" ").append(orderDirection);
        }

        @SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
        Query query = entityManager.createQuery(queryString.toString())
            .unwrap(org.hibernate.query.Query.class) // Access the Hibernate native query if necessary
            .setResultTransformer(new ResultTransformer() {

                @Override
                public Object transformTuple(Object[] tuple, String[] aliases) {
                    MaintAuditTypeDesc maintAuditTypeDesc = null;
                    if (tuple[0] != null) {
                        maintAuditTypeDesc = (MaintAuditTypeDesc) tuple[0];
                        if (tuple[1] != null) {
                            maintAuditTypeDesc.setStatus(tuple[1].toString());
                        }
                    }
                    return maintAuditTypeDesc;
                }

                @Override
                public List transformList(List collection) {
                    return collection;
                }
            });

        params.forEach(query::setParameter);

        List<MaintAuditTypeDesc> maintAuditTypeDescs = new ArrayList<>();
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        
        maintAuditTypeDescs = query.getResultList();
        dataTableResponse.setData(maintAuditTypeDescs);

        // Count total records for pagination
        long totalRecords = (long) entityManager.createQuery("SELECT COUNT(at) FROM MaintAuditTypeDesc at WHERE at.legalEntityCode = :legalEntityCode")
            .setParameter("legalEntityCode", legalEntityCode)
            .getSingleResult();
        
        dataTableResponse.setRecordsTotal(totalRecords);
        dataTableResponse.setRecordsFiltered(totalRecords);

        return dataTableResponse;
    }

}
