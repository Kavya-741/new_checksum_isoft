package com.bankaudit.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.User;

public interface UserDao extends Dao {

	void updateUserLoginTimestamp(Integer legalEntityCode, String userId);

	DataTableResponse getUser(Integer legalEntityCode,String userId, String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size, List<String> unitList);

	List<User> getUser(Integer legalEntityCode, String unitCode ,String userId,String status);
	
	void deleteUser(Integer legalEntityCode , String userId, String status);

	Boolean isUser(Integer legalEntityCode, String userId);

	List<User> getUserByRoleId(Integer legalEntityCode, String userRoleId);

	List<String> getUploadTables(Integer legalEntityCode);
	
	List<User> getAllActiveUsers(Integer legalEntityCode, String unitCode, String status);	
	
	Boolean validateUserWithIdandMobile(Integer legalEntityCode, String userId, String mobile1, String emailId, String status);
	
	String updateUserPassword(Integer legalEntityCode, String userId, String pwd);

	String getUnitIdforUser(Integer legalEntityCode, String userId);
	
	String getUnitLevelforUser(Integer legalEntityCode, String userId);
	
	int getUnsuccessfullAttempts(Integer legalEntityCode, String userId);
	
	void updateUnsuccessfullAttempts(Integer legalEntityCode, String userId);
	
	String validateUserOrgetUserStatus(Integer legalEntityCode, String userId, String status);
	
	List<User> getUsersByLevel(Integer legalEntityCode, String levelCode, String status);
	
	List<User> getUsersByLevelAndNotInGrpEntityMapping (Integer legalEntityCode, String levelCode, String status);
	
	List<User> getReportingUsers(Integer legalEntityCode,String unitCode,String userId,String typeOfUser);
	
	List<User> getImmedieateReportingUsers(Integer legalEntityCode,String unitCode,String userId,String typeOfUser,Boolean onlyParent, Boolean hOInclusion);
	
	List<User> getReportingUsersFromParent(Integer legalEntityCode,String unitCode,String userId,String typeOfUser);
	
	List<User> getReportReviewer(Integer legalEntityCode,String unitCode, Boolean onlyParent, Boolean inclusionHO);
	
	List<User> getReportReviewerApprover(Integer legalEntityCode,String unitCode, Boolean onlyRO, Boolean onlyHO);
	
	List<User> getFYEOUser(Integer legalEntityCode,String unitCode, Boolean onlyParent, Boolean inclusionHO);
	
	String checkUserSession(Integer legalEntityCode, String userId);
	
	String getUserPassword(Integer legalEntityCode, String userId);
	
	Map<String, Integer> getUserGrades(int legalEntityCode, List<String> userIds);
	
	User isFirstLogin(Integer legalEntityCode, String userId);
	
	Boolean isUserHavingEntitlement(Integer legalEntityCode, String userId, String role, String functionId, String action);
}
