package com.finakon.baas.projections;

import java.time.LocalDate;

public interface IGetUsers {
    String getUserId();
    String getLevelName();
    String getParentUnit();
    String getName();
    String getGivenName();
    String getRoleName();
    String getStatusName();
    String getEntityStatusName();
    String getMaker();
    String getMakerTime();
    String getChecker();
    String getCheckerTime();
    String getRemarks();
    String getMobile();
    String getEmailId();
    String getDepartment();
    String getDesignation();
    String getGrade();
    String getUnitName();
    String getRole();
    Integer getLevel();
    String getParentUnitCode();
    String getUnitCode();
    String getMakerId();
    String getCheckerId();
    String getStatus();
    String getRecordStatus();
}
