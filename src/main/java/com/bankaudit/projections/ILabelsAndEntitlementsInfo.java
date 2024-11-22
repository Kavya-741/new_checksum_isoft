package com.bankaudit.projections;

public interface ILabelsAndEntitlementsInfo {
    Long getLabelKey();
    String getLabelName();
    String getAdd();
    String getEdit();
    String getView();
    String getDelete();
    String getAuthorize();
    String getCertify();
    String getFileUpload();
    String getFileView();
    Integer getParentId();
    Integer getFunctionId();
}
