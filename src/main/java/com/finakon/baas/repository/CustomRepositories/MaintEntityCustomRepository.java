package com.finakon.baas.repository.CustomRepositories;

import java.util.List;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class MaintEntityCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
    public List<String> getSubBranchesByUserIdOrUnitId(Integer legalEntityCode, String type, String userIdOrUnitId) {
        List<String> list = null;
        try {
            String unitCode;
            if ("user".equalsIgnoreCase(type)) {
                // Fetching unit code by user ID
                String userSql = "SELECT u.unitCode FROM User u WHERE u.userId = :userIdOrUnitId AND u.legalEntityCode = :legalEntityCode";
                Query userQuery = entityManager.createQuery(userSql, String.class);
                userQuery.setParameter("legalEntityCode", legalEntityCode);
                userQuery.setParameter("userIdOrUnitId", userIdOrUnitId);

                unitCode = (String) userQuery.getSingleResult();
            } else {
                unitCode = userIdOrUnitId;
            }

            if (unitCode != null) {
                // Query to fetch branches up to 8 levels of hierarchy
                String sql = "SELECT DISTINCT unit_code " +
                             "FROM maint_entity " +
                             "WHERE unit_code IN (" +
                             "    SELECT DISTINCT unit_code FROM maint_entity " +
                             "    JOIN (" +
                             "        SELECT A.unit_code Aid, B.unit_code Bid, C.unit_code Cid, D.unit_code Did, " +
                             "               E.unit_code Eid, F.unit_code Fid, G.unit_code Gid, H.unit_code Hid " +
                             "        FROM maint_entity A " +
                             "        LEFT JOIN maint_entity B ON B.parent_unit_code = A.unit_code AND B.legal_entity_code = A.legal_entity_code " +
                             "        LEFT JOIN maint_entity C ON C.parent_unit_code = B.unit_code AND C.legal_entity_code = B.legal_entity_code " +
                             "        LEFT JOIN maint_entity D ON D.parent_unit_code = C.unit_code AND D.legal_entity_code = C.legal_entity_code " +
                             "        LEFT JOIN maint_entity E ON E.parent_unit_code = D.unit_code AND E.legal_entity_code = D.legal_entity_code " +
                             "        LEFT JOIN maint_entity F ON F.parent_unit_code = E.unit_code AND F.legal_entity_code = E.legal_entity_code " +
                             "        LEFT JOIN maint_entity G ON G.parent_unit_code = F.unit_code AND G.legal_entity_code = F.legal_entity_code " +
                             "        LEFT JOIN maint_entity H ON H.parent_unit_code = G.unit_code AND H.legal_entity_code = G.legal_entity_code " +
                             "        WHERE A.unit_code = :unitCode " +
                             "          AND A.legal_entity_code = :legalEntityCode " +
                             "          AND A.status = 'A' " +
                             "          AND A.entity_status = 'A' " +
                             "    ) X " +
                             "    WHERE unit_code IN (Aid, Bid, Cid, Did, Eid, Fid, Gid, Hid) " +
                             "      AND status = 'A' " +
                             "      AND entity_status = 'A'" +
                             ")";
                Query branchQuery = entityManager.createNativeQuery(sql);
                branchQuery.setParameter("unitCode", unitCode);
                branchQuery.setParameter("legalEntityCode", legalEntityCode);

                list = branchQuery.getResultList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list != null) {
            System.out.println("getSubBranchesByUserIdOrUnitId .. list<String>::" + list.size() + " ::" + list);
        } else {
            System.out.println("getSubBranchesByUserIdOrUnitId .. no data found.");
        }

        return list;
    }
}
