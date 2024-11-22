package com.bankaudit.dao;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.model.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import org.jboss.logging.Logger;

import java.util.List;

@Repository("userDeptMappingDao")
public class UserDeptMappingDaoImpl extends AbstractDao implements UserDeptMappingDao {

    static final Logger logger = Logger.getLogger(UserDeptMappingDaoImpl.class);

    @Override
    public List<GeneralParameter> getUserDeptByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId, String status) {

        Session session = getSession();
        session.setDefaultReadOnly(true);
        Class className = null;
        if (!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)) {
            className = UserDeptMappingWrk.class;
        } else {
            className = UserDeptMapping.class;
        }
        logger.info("User Dept class details Dao .. " + className);
        System.out.println("Query" + "select gp as generalParam"
                + "  from " + className.getSimpleName() + " usr "
                + "inner join GeneralParameter gp "
                + "on gp.value = usr.departmentCode "
                + "and gp.legalEntityCode = usr.legalEntityCode "
                + "where gp.key1 = :key1 "
                + "and gp.key2 = :key2 "
                + "and usr.legalEntityCode = :legalEntityCode "
                + "and usr.userId = :userId ");
        @SuppressWarnings("unchecked")
        List<GeneralParameter> generalParameterList = session.createQuery("select gp as generalParam"
                        + "  from " + className.getSimpleName() + " usr "
                        + "inner join GeneralParameter gp "
                        + "on gp.value = usr.departmentCode "
                        + "and gp.legalEntityCode = usr.legalEntityCode "
                        + "where gp.key1 = :key1 "
                        + "and gp.key2 = :key2 "
                        + "and usr.legalEntityCode = :legalEntityCode "
                        + "and usr.userId = :userId ")
                .setParameter("key1", "DEPARTMENT")
                .setParameter("key2", "DEPARTMENT")
                .setParameter("legalEntityCode", legalEntityCode)
                .setParameter("userId", userId)
                .list();
        return generalParameterList;
    }

    @Override
    public void deleteUserDeptMapping(Integer legalEntityCode, String userId, String status) {

        Session session = getSession();

        if (!BankAuditConstant.STATUS_AUTH.equals(status)) {
            session.createQuery("delete from UserDeptMappingWrk where legalEntityCode =:legalEntityCode  and "
                            + " userId =:userId ").setParameter("userId", userId)
                    .setParameter("legalEntityCode", legalEntityCode).executeUpdate();

        } else {

            session.createQuery("delete from UserDeptMapping where legalEntityCode =:legalEntityCode  and "
                            + " userId =:userId ").setParameter("userId", userId)
                    .setParameter("legalEntityCode", legalEntityCode).executeUpdate();
        }
    }

}
