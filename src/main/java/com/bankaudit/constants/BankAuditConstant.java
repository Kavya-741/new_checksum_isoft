package com.bankaudit.constants;

/**
 * {@link ArtemisConstants} class contains the bank audit application  constants such as regular expressions
 * for validation and strings for various roles and keys
 * 
 *   @author amit.patel
 *   @version 1.0
 */
public class BankAuditConstant {
	
	/**
	 * <a>REGEX_PASSWORD</a> is a regular expression to match the password 
	 */
	public static final String REGEX_PASSWORD ="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
	
	/**
	 * <a>REGEX_EMAIL</a> is a regular expression to match the email address 
	 */
	public static final String REGEX_EMAIL ="^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	
	/**
	 * This property specify the default page number for pagination
	 **/
	public static final Integer DEFAULT_PAGE_NUM = 0;

	/**
	 * This property specify the default page size for pagination
	 **/
	public static final Integer DEFAULT_PAGE_SIZE = 10;
		
	public static final String[] DOC_TYPES = new String[]{"application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/pdf","image/jpeg",
			"image/png","image/jpeg",
			"image/pjpeg",
			"application/vnd.ms-excel",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
		};
	/**
	 * This property specify the property name in properties file of document
	 * upload directory
	 **/
	public static final String DIR_DOC_UPLOAD = "docUploadDir";
	public static final String DIR_DOC_UPLOAD_AD_HOC = "docUploadDirAdHOc";
	public static final String DIR_USER_UPLOAD = "userUploadDir";
	public static final String DIR_DOC_UPLOAD_JASPER = "docUploadDirJasper";
	public static final String DIR_DOC_UPLOAD_LETTERS = "docUploadDirJasperLetters";
	public static final String DIR_DOC_UPLOAD_JASPER_TMP = "docUploadDirJasperTmp";
	public static final String DIR_DOC_UPLOAD_REPORTS = "docUploadDirJasperReports";  //IR CR and SR 
	
	/**
	 * This property specify URL delimiter that is used to separate multiple URLs
	 **/
	public static final String DELIMITER_URL= ">@<";
	
	/**
	 * This property specify URL delimiter that is used to separate multiple URLs
	 **/
	public static final String STATUS_DEL= "D";
	
	/**
	 * This property specify the draft status of the records  
	 **/
	public static final String STATUS_DF= "DF";
	
	/**
	 * This property specify the unauthorized status of the record  
	 **/
	public static final String STATUS_UNAUTH= "U";
	public static final String STATUS_MOD= "M";
	public static final String STATUS_AUTH= "A";
	public static final String STATUS_ACTIVE= "A";
	public static final String STATUS_REJ= "R";
	public static final String STATUS_INACTIVE= "I";
	public static final String STATUS_EXPIRE= "E";
	public static final String STATUS_PENDING_AUTH= "PA";
	public static final String STATUS_FURTHER_COMPLIANCE= "FC";
	public static final String STATUS_FURTHER_COMPLIANCE_WITH_DESCRIPTION= "FC-Further Compliance";
	//public static final String GP_DEFAULT_PASS= "DEFAULT_PASSWORD";
	public static final String GP_DEFAULT_PASS= "DEFAULT_PASSWORD_BAAS";
	
	public static final String SUCCESS="S"; 
	public static final String FAILED="F";		
	public static final String STATUS_NEW="N";
	
	public static final String YES="Y";
	public static final String NO="N";
	
	public static final String FOR_CHAPTER = "CH";
	public static final String FOR_SECTION = "SC";
	public static final String FOR_PRODUCT = "PD";
	public static final String FOR_PROCESS = "PC";
	public static final String FOR_CHECKLIST = "CK";	
	public static final String FOR_STATIC_STMT = "ST";
	public static final String FOR_DERIVED_STMT = "DR";
	public static final String FOR_DERIVED_1_STMT = "DR1";
	public static final String FOR_STANDARD_STMT = "SN";
	public static final String FOR_REMARKS = "RM";
	public static final String FOR_REMARKS_BANK = "RMB";
	public static final String FOR_REMARKS_SCRUTINY = "RMS";
	public static final String FOR_PAGE_BREAK = "PB";
	
	public static final String FOR_PREDEFINED_OR_EXTERNAL_TABLE = "ET";
	public static final String FOR_PREDEFINED_OR_EXTERNAL_INPUT_OR_REMARK = "ER";	
	public static final String FOR_FLEXIBLE_TABLE = "FT";
	public static final String FOR_FLEXIBLE_INPUT_OR_REMARK = "FR"; 
	public static final String FOR_ANNEXURE = "Annexure";
	public static final String FOR_PREDEFINED_INTERNAL_TABLE = "IT";
	public static final String FOR_PREDEFINED_INTERNAL_TABLE_OBSERVATION = "IT1";
	public static final String FOR_PREDEFINED_INTERNAL_INPUT_OR_REMARK= "IR";
	public static final String FOR_PREDEFINED_OR_EXTERNAL_PICTURE = "EP";
	public static final String FOR_FLEXIBLE_PICTURE = "FP";
	public static final String FOR_PREDEFINED_INTERNAL_PICTURE = "IP";
	 
	public static final String STATEMENT_TYPE_PREDEFINED = "PREDEFINED";
	public static final String STATEMENT_TYPE_PREDEFINED_OBSERVATION = "PREDEFINED_OBSERVATION";
	
	public static final String STAGE_OF_FILE_LATEST = "L";
	public static final String STAGE_OF_FILE_OLD = "O";
	
	public static final String DOCUMENT_TYPE_IR_PART_C ="IRPartC";
	public static final String DOCUMENT_TYPE_IR_PART_AB ="IRPartAB";
	public static final String IR_PART_C_NOT_EXIST="Not Exist"; 
	public static final String INTRODUCTION_CHAPTER="introduction"; 
	public static final String DOCUMENT_TYPE_OBSERVATION = "OBSERVATION";
	public static final String DOCUMENT_TYPE_RATING_CSV="RatingCSV";
	
	public static final String DOCUMENT_SUB_TYPE_AUDIT_INSPECTION = "AUDIT_INSPECTION";
	public static final String DOCUMENT_SUB_TYPE_AUDIT_REPORTREVIEW = "AUDIT_REPORTREVIEW";
	public static final String DOCUMENT_SUB_TYPE_AUDIT_COMPLIANCE = "AUDIT_COMPLIANCE";
	public static final String DOCUMENT_SUB_TYPE_AUDIT_SCRUTINY = "AUDIT_SCRUTINY";		
	public static final String DOCUMENT_SUB_TYPE_OBSERVATION_LEVEL = "OBSERVATION_LEVEL";
	public static final String DOCUMENT_SUB_TYPE_ACCOUNT_LEVEL = "ACCOUNT_LEVEL"; 
	
	public static final String DOCUMENT_NAME_COMPLIANCE_APPROVAL = "CGM/OIC Approval for Compliance";
	public static final String DOCUMENT_NAME_COMPLIANCE_CLOSURE = "Compliance Closure";
		
	public static final String COMPLIANCE_YES="C";
	public static final String COMPLIANCE_NO="N";
	public static final String COMPLIANCE_NA="NA";
	public static final String NOT_APPLICABLE="NA";
	
	public static final boolean EMAIL_INSPECTION=true;
	public static final boolean EMAIL_ALL=true;
	
	public static final String LETTER_CODE_FwdL="FwdL";
	public static final String LETTER_CODE_RN="RN";
	public static final String LETTER_CODE_HoR="HoR";
	public static final String LETTER_CODE_RoR="RoR";
	public static final String LETTER_CODE_HoLoSC="HoLoSC";
	public static final String LETTER_CODE_RoLoSC="RoLoSC";
	public static final String LETTER_CODE_Directive ="Dr";
	
	public static final String LETTER_DOC_TYPE_CODE_RN="RN";
	public static final String LETTER_DOC_TYPE_CODE_HoRRoR="HoRRoR"; 
	public static final String LETTER_DOC_TYPE_CODE_HoRoLoSC="LoSC";
	
	public static final String REPORT_CODE_IR="IR";
	public static final String REPORT_CODE_CR="CR";
	public static final String REPORT_CODE_SR="SR";
	public static final String SR_SUBMIT="SR_SUBMIT";
	public static final String SR_CLOSURE="SR_CLOSURE";
	public static final String IR_COVER_PAGE_KEY_1="IR_COVER_PAGE";
	
	public static final String REPORT_TYPE_PART_C_BOTH="BOTH";
	public static final String REPORT_TYPE_PART_C_DETAILED="DETAILED";
	public static final String REPORT_TYPE_PART_C_ABRIDGED="ABRIDGED";
	
	public static final String INSPECTION_TYPE_RRB="RRBI";
	public static final String INSPECTION_TYPE_DCCB="DCCBI"; 
	public static final String INSPECTION_TYPE_StCBI="StCBI";
	public static final String INSPECTION_TYPE_SCARDBI="SCARDBI";
	public static final String INSPECTION_TYPE_ITISI="ITISI";
	
	public static final String INSPECTION_TYPE_RBIIA="RBIIA";
	public static final String INSPECTION_TYPE_RBIA="RBIA";
	public static final String INSPECTION_TYPE_ISAUDIT="ISADT";
	public static final String INSPECTION_TYPE_CONCURRENT="CONCADT";
	
	public static final String INSPECTION_TYPE_RRB_CONTAINS="RRB";
	public static final String INSPECTION_TYPE_DCCB_CONTAIN="DCCB"; 
	public static final String INSPECTION_TYPE_StCB_CONTAIN="StCB";
	public static final String INSPECTION_TYPE_SCARDB_CONTAIN="SCARDB";
	
	public static final String VALIDATION_FAILED="VF";
	public static final String VALIDATION_FAILED_TRANSACTION_TYPE="VFT";
	
	public static final String REPORT_LANGUAGE_EN="en";
	public static final String REPORT_LANGUAGE_HI="hi";
	
	public static final String RATING_MODEL_PARAM_BASIC_CALCULATION="CL";
	public static final String RATING_MODEL_PARAM_BASIC_NON_COMPLIANCE="NC";
	public static final String RATING_MODEL_PARAM_BASIC_USER_INPUT="UI";
	
	public static final String CAPTURE_TYPE_CUSTOMER_ACC="Customer";
	public static final String CAPTURE_TYPE_PL_ACC="PL"; 
	public static final String CAPTURE_TYPE_OTHER_ACC="OtherAcct";
	public static final String CAPTURE_TYPE_OTHERS="Others";
	
	public static final String AUDIT_INITIATION_IR_ROW_WISE="ROW_WISE";
	public static final String AUDIT_INITIATION_IR_COL_WISE="COL_WISE";
	
	public static final String AUDIT_OBSERVATION_SAVE="SAVE";
	public static final String AUDIT_OBSERVATION_SUBMIT="SUBMIT";
	
	public static final String MAPPING_TYPE_USER="U";
	
	public static final String OBSERVATION_UPDATE="OBSERVATION_UPDATE";
	public static final String OTHERS_COMPLIANCE_CATEGORY="OTHERS_COMPLIANCE_CATEGORY";
	public static final String OBSERVATION_UPDATE_YES="1";
	
	//public static final String OBSERVATION_CHAPTER_CODE="5";
	
	public static final String AUDIT_CATEGORY_INTERNAL="I";
	public static final String AUDIT_CATEGORY_EXTERNAL="E";
	
	// Google recaptcha Constants
	public static final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";
	//public static final String SECRET_KEY = "6Lcl9rcpAAAAAMorY_u9niyEUb5gEgtmfQPU0sUS";
	//private final static String USER_AGENT = "Mozilla/5.0";
	
	
	private BankAuditConstant() {
	}			
}