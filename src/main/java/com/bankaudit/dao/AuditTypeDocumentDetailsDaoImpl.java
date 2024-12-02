package com.bankaudit.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
 
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.process.model.AuditDocumentDetails;
import com.bankaudit.process.model.AuditDocumentDetailsWrk;
import com.bankaudit.util.BankAuditUtil;

@Repository("auditTypeDocumentDetailsDao")
public class AuditTypeDocumentDetailsDaoImpl extends AbstractDao implements AuditTypeDocumentDetailsDao{
	
	static final Logger logger = Logger.getLogger(AuditTypeDocumentDetailsDaoImpl.class);
	
	@Override
	public DataTableResponse getAuditTypeDocumentDetails(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size,  String documentType, String documentSubType) {

		Session session = getSession();
		session.setDefaultReadOnly(true);
		List<AuditDocumentDetails> auditTypeDocs = null;

		DataTableResponse dataTableResponse=new DataTableResponse();
		StringBuilder queryString=new StringBuilder(
				  " Select legal_entity_code,"
				  + " audit_type_code, "
				  + " status, "
				  + " maker "
				  + " From (SELECT Distinct "
						  +	" legal_entity_code, " 						  
						  +	" (select concat(a.audit_type_code,'-',a.audit_type_desc) from maint_audit_type_desc a "
						  +	" where a.audit_type_code=aa.audit_type_code and  a.legal_entity_code=aa.legal_entity_code ) as audit_type_code, " 
						   
						  +	" (select concat(gp.value , '-' , gp.description)"
						  + " from general_parameter gp where "
						  + " gp.key_1='STATUS' and gp.value=aa.status "
						  + " and gp.legal_entity_code=aa.legal_entity_code ) as status , "					  
						
						  + " (select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						  + " from user u where "
						  + " u.user_id=aa.maker "
						  + " and u.legal_entity_code=aa.legal_entity_code ) as maker  "	 
						  + "  FROM ("
										+ " SELECT * FROM audit_document_details_wrk WHERE legal_entity_code =:legal_entity_code and document_type=:documentType "								 
							  + " ) AS aa " //WHERE legal_entity_code =:legal_entity_code "
						  + " UNION ALL "
						  + " select legal_entity_code, "
						  + "    concat(a.audit_type_code,'-',a.audit_type_desc) audit_type_code,"
						  + " 'A-Authorised' as status,"
						  //+ " '' as maker "
						  + " (select concat(u.user_id , '-' ,u.first_name,' ',u.last_name )"
						  + " from user u where "
						  + " u.user_id=a.maker "
						  + " and u.legal_entity_code=a.legal_entity_code ) as maker  "	 
						  + " from maint_audit_type_desc a WHERE a.legal_entity_code =:legal_entity_code "
						  + " )   AS bb "
						  
				);
		
		if(!BankAuditUtil.isEmptyString(search)){
			
			queryString=queryString.append(" Where  "
					+ " bb.audit_type_code IN (	select atd.audit_type_code from maint_audit_type_desc atd where "
					+ " 		atd.audit_type_desc LIKE :search and atd.legal_entity_code=bb.legal_entity_code ) "
					+ " or  bb.maker like :search"
					+ " or  bb.maker in ( "
					+ " select u.user_id "
					+ " from user u where "
					+ " (concat(u.first_name , ' ' , u.last_name) like :search  ) "
					+ " and u.legal_entity_code=bb.legal_entity_code ) "
					+ " or  bb.status IN (SELECT value FROM general_parameter  WHERE key_1='STATUS' and  description LIKE :search ) "
					+ " "
					);
		}


		String[] columns={
				"bb.audit_type_code",
				//"aa.file_name",
				"bb.maker",	
				"bb.status"};

		if(orderColumn != null && orderColumn == -1 && !BankAuditUtil.isEmptyString(orderDirection)){ // -1 send for the first time from the UI during landing page.
			queryString=queryString.append(" order by FIELD(STATUS,'DF','R','RM','U','M','A') ASC  "); // The code modified to get the records in the order of Status on landing page, the sequence is  Draft, Reject, Unauth, Modify, Auth
		}else if(orderColumn!=null && !BankAuditUtil.isEmptyString(orderDirection)){
			queryString=queryString.append(" order by FIELD(STATUS,'DF') DESC ,  ").append(columns[orderColumn]).append(" ").append(orderDirection);
		}

		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {

					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						AuditDocumentDetails auditTypeDocumentDetails=null;
						if(tuple!=null){
							
							auditTypeDocumentDetails=new AuditDocumentDetails();
							
							if(tuple[0]!=null){
								auditTypeDocumentDetails.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								auditTypeDocumentDetails.setAuditTypeCode( tuple[1].toString());
							}
							if(tuple[2]!=null){
								auditTypeDocumentDetails.setStatus(tuple[2].toString());
							}
							if(tuple[3]!=null){
								auditTypeDocumentDetails.setMaker(tuple[3].toString());
							}
							
						}
						
						return auditTypeDocumentDetails;
					}

					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legal_entity_code", legalEntityCode);  
				query.setParameter("documentType", documentType);




		if(!BankAuditUtil.isEmptyString(search)){
			query.setParameter("search", "%"+search+"%");
		}


		ScrollableResults resultScroll = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
		if(resultScroll.first()&&resultScroll.scroll(page*size)){

			auditTypeDocs= new ArrayList<>();
			int i = 0;
			while (size > i++) {
				auditTypeDocs.add((AuditDocumentDetails) resultScroll.get(0));
				if (!resultScroll.next())
					break;
			}
			dataTableResponse.setData(auditTypeDocs);
			resultScroll.last();
			if(size!=0){
				dataTableResponse.setRecordsFiltered((resultScroll.getRowNumber()+1l/size)+1);
				dataTableResponse.setRecordsTotal((resultScroll.getRowNumber()+1l/size)+1);
			}else{
				dataTableResponse.setError("page size zero");
				dataTableResponse.setData(Collections.EMPTY_LIST);
				dataTableResponse.setRecordsTotal(0l);
				dataTableResponse.setRecordsFiltered(0l);
			}

		}else{
			dataTableResponse.setError(null);
			dataTableResponse.setData(Collections.EMPTY_LIST);
			dataTableResponse.setRecordsTotal(0l);
			dataTableResponse.setRecordsFiltered(0l);
		}
		return dataTableResponse;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AuditDocumentDetailsWrk getAuditTypeDocumentDetail(Integer legalEntityCode, Integer id, String auditTypeCode, String status ){
		Session session =getSession();
		session.setDefaultReadOnly(true);
		logger.info("legalEntityCode.. "+legalEntityCode+"id .."+ id);
		StringBuilder str =new StringBuilder(" from AuditDocumentDetailsWrk where "
				+ "	legalEntityCode=:legalEntityCode "
				+ " AND id=:id "
				+ " And auditTypeCode=:auditTypeCode");
		
		return (AuditDocumentDetailsWrk)(session.createQuery(str.toString())
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("id", id)
				.setParameter("auditTypeCode", auditTypeCode) 
				.uniqueResult());
	}
	
	@Override
	public void deleteAuditTypeDocumentDetails(Integer legalEntityCode,Integer id, String auditTypeCode, String statusUnauth) {

		Session session=getSession();
		
		if(!BankAuditConstant.STATUS_AUTH.equals(statusUnauth)){
			
			session.createQuery("delete from AuditDocumentDetailsWrk  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode=:auditTypeCode and  id =:id  ").setParameter("id", id).setParameter("auditTypeCode", auditTypeCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			
		}else {
			session.createQuery("delete from AuditDocumentDetails  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode=:auditTypeCode and  id =:id  ").setParameter("id", id).setParameter("auditTypeCode", auditTypeCode)
			.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
		}
	}
	
	
	//New  Code
	@Override
	public String deleteAuditTypeDocumentFromUI(Integer legalEntityCode,Integer id, String auditTypeCode, String  status, String entityStatus, 
			 String documentType, String documentSubType){
		logger.info("Inside deleteAuditTypeDocument .." );
		
		try{
			Session session=getSession();
			String deleteQry = " ";
			StringBuilder insertToWrk = new StringBuilder("");
			if(BankAuditConstant.STATUS_AUTH.equals(status)){
				//deleteQry = " update audit_document_details  set   " //status='"+BankAuditConstant.STATUS_UNAUTH+"',
				deleteQry = " update audit_document_details  set   " //status='"+BankAuditConstant.STATUS_UNAUTH+"',
						+ "  entity_status='"+BankAuditConstant.STATUS_DEL+"' "
						+ "  where legal_entity_code =:legalEntityCode "
						+ "  and audit_type_code=:auditTypeCode "
						+ "  and  id =:id ";					
							
				session.createSQLQuery(deleteQry).setParameter("id", id).setParameter("auditTypeCode", auditTypeCode)
				.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
				
				
				/*insertToWrk = "insert into AuditDocumentDetailsWrk (select from AuditDocumentDetails "
				+ " Where auditDocumentDetails.legalEntityCode =:legalEntityCode "
				+ " and auditDocumentDetails.auditTypeCode=:auditTypeCode  )";*/

				insertToWrk.append(" insert into audit_document_details_wrk(legal_entity_code,audit_type_code,document_type,"
				+ " system_id,file_name,reference_number,reference_path,status,role, maker,"
				+ " maker_timestamp,checker,checker_timestamp, "
				+ " entity_status, auth_rej_remarks, document_name, document_sub_type) "
				+ " Select  legal_entity_code,audit_type_code,document_type,"
				+ " system_id,file_name,reference_number,reference_path,'"+BankAuditConstant.STATUS_UNAUTH+"',role, maker,"
				+ " maker_timestamp,checker,checker_timestamp, "
				+ " entity_status, auth_rej_remarks, document_name, document_sub_type from audit_document_details "
				+ " Where  legal_entity_code =:legalEntityCode "
				+ " and audit_type_code=:auditTypeCode "
				+ " and  document_type=:documentType ");
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 
					insertToWrk.append(" and  document_sub_type=:documentSubType ") ;
				}
				session.flush();
			Query qry =	session.createSQLQuery(insertToWrk.toString())
			  	.setParameter("auditTypeCode", auditTypeCode)
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("documentType", documentType);
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 
					qry.setParameter("documentSubType", documentSubType);
				}
			qry.executeUpdate();
				
			} else if (BankAuditConstant.STATUS_UNAUTH.equals(status) && !BankAuditConstant.STATUS_NEW.equals(entityStatus)) { 				
			
				deleteQry = " Update audit_document_details_wrk   set   entity_status='"+BankAuditConstant.STATUS_DEL+"' "
						+ "   where   legal_entity_code =:legalEntityCode "
						+ " and audit_type_code=:auditTypeCode "
						+ " and  id =:id ";	
				session.createSQLQuery(deleteQry).setParameter("id", id).setParameter("auditTypeCode", auditTypeCode)
				.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
				
			}else{
				 deleteQry = "delete from audit_document_details_wrk   "
							+ "   where   legal_entity_code =:legalEntityCode "
							+ " and audit_type_code=:auditTypeCode "
							+ " and  id =:id ";	
				 session.createSQLQuery(deleteQry).setParameter("id", id).setParameter("auditTypeCode", auditTypeCode)
					.setParameter("legalEntityCode", legalEntityCode).executeUpdate();
			}
			 
		  	return BankAuditConstant.SUCCESS;
		}catch (Exception e) {
			logger.error("Error .." + e.getMessage() +" :: "+e.getCause());
			return BankAuditConstant.FAILED;
		}		 
	}
	
	@Override
	public String deleteAllAuditTypeDocument(Integer legalEntityCode, String auditTypeCode, String documentType, String status, 
			String documentSubType){
	//public String deleteAllAuditTypeDocument(AuditTypeDocumentDetail auditTypeDocumentDetail){		
		logger.info("Inside deleteAllAuditTypeDocument .." );		
		
		try{
			Session session =getSession();				
			if(BankAuditConstant.STATUS_AUTH.equals(status)){ 
				
				StringBuilder deleteQuery = new StringBuilder(" update audit_document_details  set  status='"+BankAuditConstant.STATUS_UNAUTH+"', "
						+ "  entity_status='"+BankAuditConstant.STATUS_DEL+"' "
						+ "  where legal_entity_code =:legalEntityCode "
						+ "  and audit_type_code=:auditTypeCode   "
						+ " and  document_type=:documentType ");
					if(!BankAuditUtil.isEmptyString(documentSubType)){ 		
						deleteQuery.append(" and  document_sub_type=:documentSubType ");	
					}
				Query query = session.createSQLQuery(deleteQuery.toString())
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode) 
				.setParameter("documentType", documentType);
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 
					query.setParameter("documentSubType", documentSubType); 
				}
				query.executeUpdate();
				
				//Insert into work to authorize
				StringBuilder insertToWrk = new StringBuilder("insert into audit_document_details_wrk(legal_entity_code,audit_type_code,document_type,"
						+ " system_id,file_name,reference_number,reference_path,status,role, maker,"
						+ " maker_timestamp,checker,checker_timestamp, "
						+ " entity_status, auth_rej_remarks, document_name, document_sub_type) "
						+ " Select  legal_entity_code,audit_type_code,document_type,"
						+ " system_id,file_name,reference_number,reference_path,status,role, maker,"
						+ " maker_timestamp,checker,checker_timestamp, "
						+ " entity_status, auth_rej_remarks, document_name, document_sub_type from audit_document_details "
						+ " Where  legal_entity_code =:legalEntityCode "
						+ " and audit_type_code=:auditTypeCode "
						+ " and  document_type=:documentType ");
						
						if(!BankAuditUtil.isEmptyString(documentSubType)){ 		
							insertToWrk.append(" and  document_sub_type=:documentSubType ") ;
						}
				session.flush();
				Query queryInsert =	session.createSQLQuery(insertToWrk.toString())
			  	.setParameter("auditTypeCode", auditTypeCode)
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("documentType", documentType);
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 		
					queryInsert.setParameter("documentSubType", documentSubType);
				}
				queryInsert.executeUpdate();				
				
			}else{				 
				//Update which already authorized but wants to delete
				StringBuilder updateQuery = new StringBuilder(" update audit_document_details_wrk  set  status='"+BankAuditConstant.STATUS_UNAUTH+"', "
						+ "  entity_status='"+BankAuditConstant.STATUS_DEL+"' "
						+ "  where legal_entity_code =:legalEntityCode "
						+ "  and audit_type_code=:auditTypeCode"
						+ " and  document_type=:documentType "						
						+ "  and entity_status !=:entityStatus ");	
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 
					updateQuery.append(" and  document_sub_type=:documentSubType " );
				}
				session.flush();
				Query queryUpdate = session.createSQLQuery(updateQuery.toString())
				.setParameter("auditTypeCode", auditTypeCode)
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("documentType", documentType)
				.setParameter("entityStatus", BankAuditConstant.STATUS_NEW);
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 
					queryUpdate.setParameter("documentSubType", documentSubType);
				}
				queryUpdate.executeUpdate();
				
				//Delete newly added 
				StringBuilder deleteQuery = new StringBuilder(" delete from audit_document_details_wrk    "
						+ "  where legal_entity_code =:legalEntityCode "
						+ "  and audit_type_code=:auditTypeCode"
						+ " and  document_type=:documentType "
						+ "  and entity_status =:entityStatus ");
				
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 
					deleteQuery.append(" and  document_sub_type=:documentSubType " );
				}		
				
				session.flush();
				Query querydelete = session.createSQLQuery(deleteQuery.toString())
				.setParameter("auditTypeCode", auditTypeCode)
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("documentType", documentType)
				.setParameter("entityStatus", BankAuditConstant.STATUS_NEW);
				if(!BankAuditUtil.isEmptyString(documentSubType)){ 
					querydelete.setParameter("documentSubType", documentSubType);
				}
				querydelete.executeUpdate();				
				
			}
					
			//Delete from work
			/* deleteQuery="delete from AuditDocumentDetailsWrk   where"
						+ " legalEntityCode =:legalEntityCode"
						+ " and auditTypeCode =:auditTypeCode "  
						+ " and documentType in (:documentType) " ;
			
			 session.flush();
			 session.createQuery(deleteQuery)
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode) 
				.setParameterList("documentType", documentType.split(","));
				query.executeUpdate();*/
				
			return BankAuditConstant.SUCCESS;
		}catch (Exception e) {
			logger.error("Error .." + e.getMessage() +" :: "+e.getCause());
			return BankAuditConstant.FAILED;
		}
	}
	
	@Override
	//public String updateAuditTypeDocumentDetails(Integer legalEntityCode, String auditTypeCode, String checker, String status, String authRejRemarks){
	//public String updateAuthorizeAuditTypeDocumentDetails(AuditTypeDocumentDetails auditTypeDocumentDetails){
	public String updateAuthorizeAuditTypeDocumentDetails(AuditDocumentDetails auditTypeDocumentDetails){
		try{
			String status = auditTypeDocumentDetails.getStatus();
			String checker = auditTypeDocumentDetails.getChecker();
			Integer legalEntityCode = auditTypeDocumentDetails.getLegalEntityCode();
			String auditTypeCode = auditTypeDocumentDetails.getAuditTypeCode();
			String authRejRemarks = auditTypeDocumentDetails.getAuthRejRemarks();
			
			Session session =getSession();  
			
			if(auditTypeDocumentDetails.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
				auditTypeDocumentDetails.setCheckerTimestamp(new Date());
			} else{
				auditTypeDocumentDetails.setMakerTimestamp(new Date());
			}  
			
			if(status.equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){
			
				//Delete all from master
				deleteAllByAuditTypeCodeFromMaster(legalEntityCode, auditTypeCode, auditTypeDocumentDetails.getDocumentType(), auditTypeDocumentDetails.getDocumentSubType());
				session.flush();
			
				//Update ALL to Authorize in work
				StringBuilder updateQry = new StringBuilder("Update AuditDocumentDetailsWrk auditDocumentDetails set "						 
						+ " auditDocumentDetails.status='"+BankAuditConstant.STATUS_AUTH+"', "
					//	+ " auditDocumentDetails.entity_status='"+BankAuditConstant.STATUS_ACTIVE+"', "
								+ "auditDocumentDetails.checkerTimestamp= '"+new java.sql.Timestamp(auditTypeDocumentDetails.getCheckerTimestamp().getTime())+"'  , " 
								+ "auditDocumentDetails.checker= '"+checker+"', "
								+ "auditDocumentDetails.authRejRemarks= '"+authRejRemarks+"' "
						+ " where legalEntityCode =:legalEntityCode"
						+ " and auditTypeCode =:auditTypeCode "
						+ " and  documentType=:documentType ");
				if(!BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentSubType())){ 
					updateQry.append(" and  documentSubType=:documentSubType " ); 
				}
				
				Query query = session.createQuery(updateQry.toString())
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode) 
				.setParameter("documentType", auditTypeDocumentDetails.getDocumentType());
				if(!BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentSubType())){ 
					query.setParameter("documentSubType", auditTypeDocumentDetails.getDocumentSubType()); 
				}
				query.executeUpdate();
								
				//Insert all authorized to master
				StringBuilder insertToMaster = new StringBuilder("insert into audit_document_details(legal_entity_code,audit_type_code,document_type,"
						+ " system_id,file_name,reference_number,reference_path,status,role, maker,"
						+ " maker_timestamp,checker,checker_timestamp, "
						+ " entity_status, auth_rej_remarks, document_name, document_sub_type) "
						+ " Select  legal_entity_code,audit_type_code,document_type,"
						+ " system_id,file_name,reference_number,reference_path,status,role, maker,"
						+ " maker_timestamp,checker,checker_timestamp, "
						//" sysdate_timestamp,entity_status, auth_rej_remarks, document_name,document_sub_type from audit_document_details_wrk "
						+ "'"+BankAuditConstant.STATUS_ACTIVE+"' , auth_rej_remarks, document_name,document_sub_type from audit_document_details_wrk "
						+ " Where  legal_entity_code =:legalEntityCode "
						+ " and audit_type_code=:auditTypeCode"
						+ " and  document_type=:documentType " 
						+ " and entity_status !=:entityStatus " );
				if(!BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentSubType())){ 
					insertToMaster.append(" and  document_sub_type=:documentSubType " );
				}	
				
				session.flush();
			Query inserQry = session.createSQLQuery(insertToMaster.toString())
			  	.setParameter("auditTypeCode", auditTypeCode)
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("documentType", auditTypeDocumentDetails.getDocumentType());
				if(!BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentSubType())){
					inserQry.setParameter("documentSubType", auditTypeDocumentDetails.getDocumentSubType());
				}
				inserQry.setParameter("entityStatus", BankAuditConstant.STATUS_DEL);
				inserQry.executeUpdate();		
			 
			  	//Delete from Work
			  	session.flush();
			  	deleteAllByAuditTypeCodeFromWork(legalEntityCode, auditTypeCode, auditTypeDocumentDetails.getDocumentType(), auditTypeDocumentDetails.getDocumentSubType());
			
			}else{
				if(auditTypeDocumentDetails.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
						|| auditTypeDocumentDetails.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)){
					auditTypeDocumentDetails.setMakerTimestamp(new Date());
				}
				StringBuilder updateQry = new StringBuilder("Update AuditDocumentDetailsWrk auditDocumentDetails set "						 
						+ " auditDocumentDetails.status='"+BankAuditConstant.STATUS_REJ+"',"
								+ "auditDocumentDetails.checkerTimestamp= '"+ new java.sql.Timestamp(auditTypeDocumentDetails.getCheckerTimestamp().getTime())+"' , " 
										+ "auditDocumentDetails.makerTimestamp= '"+ new java.sql.Timestamp(auditTypeDocumentDetails.getMakerTimestamp().getTime())+"' , " 
								+ "auditDocumentDetails.checker= '"+checker+"', "
								+ "auditDocumentDetails.authRejRemarks= '"+authRejRemarks+"' "
						+ " where legalEntityCode =:legalEntityCode"
						+ " and auditTypeCode =:auditTypeCode " 
						+ " and  documentType=:documentType ");
				if(!BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentSubType())){
					updateQry.append( " and  documentSubType=:documentSubType "); 
				}						
				Query query = session.createQuery(updateQry.toString())
				.setParameter("legalEntityCode", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode) 
				.setParameter("documentType", auditTypeDocumentDetails.getDocumentType());
				if(!BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentSubType())){
					query.setParameter("documentSubType", auditTypeDocumentDetails.getDocumentSubType());
				}
				query.executeUpdate();
			}
			
			return BankAuditConstant.SUCCESS;
		}catch (Exception e) {
			logger.error("Error .." + e.getMessage() +" :: "+e.getCause());
			return BankAuditConstant.FAILED;
		}
	}
	
	public String deleteAllByAuditTypeCodeFromMaster(Integer legalEntityCode, String auditTypeCode, 
			String documentType, String documentSubType){
		try{
			Session session =getSession();
			StringBuilder strQry=new StringBuilder("delete from AuditDocumentDetails  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode=:auditTypeCode "
					+ " and  documentType=:documentType ");
			if(!BankAuditUtil.isEmptyString(documentSubType)){
				strQry.append(" and  documentSubType=:documentSubType ");
			}
			Query qry = session.createQuery(strQry.toString())
					.setParameter("auditTypeCode", auditTypeCode)
					.setParameter("legalEntityCode", legalEntityCode)
					.setParameter("documentType", documentType);
					if(!BankAuditUtil.isEmptyString(documentSubType)){
						qry.setParameter("documentSubType", documentSubType);
					}
					qry.executeUpdate();
				
			return BankAuditConstant.SUCCESS;
		}catch (Exception e) {
			logger.error("Error .." + e.getMessage() +" :: "+e.getCause());
			return BankAuditConstant.FAILED;
		}
		
	}
	
	public String deleteAllByAuditTypeCodeFromWork(Integer legalEntityCode, String auditTypeCode, 
			String documentType, String documentSubType){
		try{
			Session session =getSession();
			
			StringBuilder strQry=new StringBuilder("delete from AuditDocumentDetailsWrk  "
					+ " where legalEntityCode =:legalEntityCode "
					+ " and auditTypeCode=:auditTypeCode "
					+ " and  documentType=:documentType ");
			if(!BankAuditUtil.isEmptyString(documentSubType)){
				strQry.append(" and  documentSubType=:documentSubType ");
			}
			Query qry = session.createQuery(strQry.toString())
					.setParameter("auditTypeCode", auditTypeCode)
					.setParameter("legalEntityCode", legalEntityCode)
					.setParameter("documentType", documentType);
			if(!BankAuditUtil.isEmptyString(documentSubType)){
					qry.setParameter("documentSubType", documentSubType);
			}
					qry.executeUpdate();
				
			return BankAuditConstant.SUCCESS;
		}catch (Exception e) {
			logger.error("Error .." + e.getMessage() +" :: "+e.getCause());
			return BankAuditConstant.FAILED;
		}
		
	}
	
	public void updateMaker(Integer legalEntityCode, String auditTypeCode,  String maker,
			String documentType, String documentSubType){
		
		try{		
			Session session =getSession();
			
			//insert all from master to work to authorize if any
			StringBuilder insertToWrk = new StringBuilder("insert into audit_document_details_wrk(legal_entity_code,audit_type_code,document_type,"
					+ " system_id,file_name,reference_number,reference_path,status,role, maker,"
					+ " maker_timestamp,checker,checker_timestamp, "
					+ " entity_status, auth_rej_remarks, document_name, document_sub_type) "
					+ " Select  legal_entity_code,audit_type_code,document_type,"
					+ " system_id,file_name,reference_number,reference_path,status,role, maker,"
					+ " maker_timestamp,checker,checker_timestamp, "
					+ " entity_status, auth_rej_remarks, document_name, document_sub_type from audit_document_details "
					+ " Where  legal_entity_code =:legalEntityCode "
					+ " and audit_type_code=:auditTypeCode "
					+ " and  document_type=:documentType  "
					+ " and  status='"+BankAuditConstant.STATUS_AUTH+"' " );	
			
					if(!BankAuditUtil.isEmptyString(documentSubType)){
						insertToWrk.append(" and  document_sub_type=:documentSubType  ");
					}
			
			session.flush();
		  Query qry =	session.createSQLQuery(insertToWrk.toString()).
		  	setParameter("auditTypeCode", auditTypeCode)
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("documentType", documentType);
			if(!BankAuditUtil.isEmptyString(documentSubType)){
				qry.setParameter("documentSubType", documentSubType);
			}
			qry.executeUpdate();
		  			  	
		  //UPdate all from master  	
			StringBuilder deleteQuery = new StringBuilder( "update audit_document_details  set  status='"+BankAuditConstant.STATUS_UNAUTH+"', "
					+ "  entity_status='"+BankAuditConstant.STATUS_ACTIVE+"' "
					+ "  where legal_entity_code =:legalEntityCode "
					+ "  and audit_type_code=:auditTypeCode   "
					+ " and  document_type=:documentType  ");
			if(!BankAuditUtil.isEmptyString(documentSubType)){	
				deleteQuery.append(  " and  document_sub_type=:documentSubType  ");	
			}
			session.flush();
			Query qryDelete = session.createSQLQuery(deleteQuery.toString())
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("documentType", documentType);
			if(!BankAuditUtil.isEmptyString(documentSubType)){
				qryDelete.setParameter("documentSubType", documentSubType);
			}
			qryDelete.executeUpdate();	  			 	
		  	
		    /// Update maker
			StringBuilder updateStatus = new StringBuilder("Update AuditDocumentDetailsWrk auditDocumentDetails set "						 
					+ " auditDocumentDetails.maker='"+maker+"',"
					+ " auditDocumentDetails.status= '"+BankAuditConstant.STATUS_UNAUTH+"' "
					+ " where legalEntityCode =:legalEntityCode"
					+ " and auditTypeCode =:auditTypeCode " 
					+ " and  document_type=:documentType  ");
					if(!BankAuditUtil.isEmptyString(documentSubType)){
						updateStatus.append( " and  document_sub_type=:documentSubType  ");  
					}	
			 Query updateQry =  session.createQuery(updateStatus.toString())
			.setParameter("legalEntityCode", legalEntityCode)
			.setParameter("auditTypeCode", auditTypeCode)
			.setParameter("documentType", documentType);
			if(!BankAuditUtil.isEmptyString(documentSubType)){
				updateQry.setParameter("documentSubType", documentSubType);
			}
			updateQry.executeUpdate();
			
			
		}catch (Exception e) {
			logger.error("Error .." + e.getMessage() +" :: "+e.getCause()); 
		}		
	}
 
	public AuditDocumentDetails getAuditTypeDocumentDetailByAuditType(Integer legalEntityCode,  String auditTypeCode,String status, 
			String documentType, String documentSubType) {
		Session session =getSession();
		session.setDefaultReadOnly(true);
		 		
		StringBuilder queryString=new StringBuilder(
				  "Select legal_entity_code, "
				  + "  audit_type_code, "
				  + "  status, "
				  + "  auth_rej_remarks, "
				  + " maker,  checker, "
	    		  + " maker_timestamp, checker_timestamp "
				  + " From (SELECT Distinct "
						  +" legal_entity_code, " 						  
						  +"  (select concat(a.audit_type_code,'-',a.audit_type_desc) from maint_audit_type_desc a "
						  + "		where a.audit_type_code=aa.audit_type_code and  a.legal_entity_code=aa.legal_entity_code ) as audit_type_code, " 
						   
						  + " (select concat(gp.value , '-' , gp.description)  from general_parameter gp where "
			    		  + " gp.key_1='STATUS' and gp.value=aa.status   and gp.legal_entity_code=aa.legal_entity_code ) as status,"
			    		 
			    		  + " auth_rej_remarks,"
			    		  + " maker,  checker, "
			    		  + " maker_timestamp, checker_timestamp "
			    		  + "  "			    		 
						  +"  FROM ");
							 if(status.equals(BankAuditConstant.STATUS_AUTH)){
								 queryString.append("  ( SELECT * FROM audit_document_details WHERE legal_entity_code =:legal_entity_code "); 
							 }
							 else{
								 queryString.append(" (SELECT * FROM audit_document_details_wrk WHERE legal_entity_code =:legal_entity_code "); 								 		 	
							 }
							 queryString.append(" ) AS aa WHERE legal_entity_code =:legal_entity_code "
							  + "  and  aa.audit_type_code=:auditTypeCode ");
						 
						  queryString.append(""
						  + "  UNION ALL "
						  + " select legal_entity_code, "
						  + "    concat(a.audit_type_code,'-',a.audit_type_desc) audit_type_code,"
						  + " 'A-Authorised' as status,"
						  + " '' as auth_rej_remarks, "
						  + " '' as maker, "
						  + " '' as checker, "
			    		  + " CURRENT_TIMESTAMP as maker_timestamp, "
			    		  + " CURRENT_TIMESTAMP as checker_timestamp "
						  + " from maint_audit_type_desc a WHERE a.legal_entity_code =:legal_entity_code "
						  + " and audit_type_code not in "
						  					 + "( SELECT audit_type_code FROM audit_document_details_wrk Where legal_entity_code =:legal_entity_code"   
							  + "  UNION ALL "
							  					 + " SELECT audit_type_code FROM audit_document_details Where legal_entity_code =:legal_entity_code  " 
								+ " ) " 
							 + " and audit_type_code =:auditTypeCode  )   AS bb ");
		
		@SuppressWarnings("deprecation")
		Query query=session.createSQLQuery(queryString.toString())
				.setResultTransformer(new ResultTransformer() {

					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						AuditDocumentDetails auditTypeDocumentDetails=null;
						if(tuple!=null){
							
							auditTypeDocumentDetails=new AuditDocumentDetails();
							
							if(tuple[0]!=null){
								auditTypeDocumentDetails.setLegalEntityCode(Integer.parseInt(tuple[0].toString()));
							}
							if(tuple[1]!=null){
								auditTypeDocumentDetails.setAuditTypeCode( tuple[1].toString());
							}
							if(tuple[2]!=null){
								auditTypeDocumentDetails.setStatus(tuple[2].toString());
							}
							if(tuple[3]!=null){
								auditTypeDocumentDetails.setAuthRejRemarks(tuple[3].toString());
							}
							
							if(tuple[4]!=null) 	auditTypeDocumentDetails.setMaker(tuple[4].toString());
							if(tuple[5]!=null) 	auditTypeDocumentDetails.setChecker(tuple[5].toString());
							if(tuple[6]!=null && !BankAuditUtil.isEmptyString(tuple[6].toString())) 	auditTypeDocumentDetails.setMakerTimestamp((Date)tuple[6]);
							if(tuple[7]!=null && !BankAuditUtil.isEmptyString(tuple[7].toString())) 	auditTypeDocumentDetails.setCheckerTimestamp((Date)tuple[7]); 						
							
						}						
						return auditTypeDocumentDetails;
					}
					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.setParameter("legal_entity_code", legalEntityCode)
				.setParameter("auditTypeCode", auditTypeCode);
				


				List lst = query.list();
				if(!lst.isEmpty() && lst.size()>0) {
					return (AuditDocumentDetails)lst.get(0);
				}
				else{
					return null;
				}
			}
}
