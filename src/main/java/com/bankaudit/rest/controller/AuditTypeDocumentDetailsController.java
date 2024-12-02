package com.bankaudit.rest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.process.model.AuditDocumentDetails;
import com.bankaudit.service.AuditTypeDocumentDetailsService;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.dto.ServiceStatus;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auditTypeDocumentDetails")
public class AuditTypeDocumentDetailsController {
	
	
	@Autowired
	AuditTypeDocumentDetailsService auditTypeDocumentDetailsService;
	
	private static final int BUFFER_SIZE = 4096;
	static final Logger logger = Logger.getLogger(MaintAuditTypeDescController.class);
	
	@GetMapping(value="/getAll",produces=MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getAuditTypeDocumentDetails(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]")String orderDirection,
			@RequestParam("legalEntityCode")String legalEntityCodeStr,
			@RequestParam(value="documentType",required=false)String documentType,
			@RequestParam(value="documentSubType",required=false)String documentSubType
			){
		
		DataTableResponse dataTableResponse=null;
		
		if(!BankAuditUtil.isEmptyString(drawStr)
				&& !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)){
			try {
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				Integer draw=Integer.parseInt(drawStr);
				Integer size=Integer.parseInt(length);
				Integer page=Integer.parseInt(start)/size;
				Integer orderColumn=null;
				if(!BankAuditUtil.isEmptyString(orderColumnStr)){
					orderColumn=Integer.parseInt(orderColumnStr);
				}
				dataTableResponse = auditTypeDocumentDetailsService.getAuditTypeDocumentDetails(legalEntityCode, search, orderColumn, orderDirection, 
						page, size, documentType, documentSubType); 
				dataTableResponse.setDraw(draw);    
					
			} catch (Exception e) {
                dataTableResponse=new DataTableResponse();
                dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}
		
		}else {
			dataTableResponse=new DataTableResponse();
			dataTableResponse.setError("invalid data");
		}
		
		return dataTableResponse;
	}
	
	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateAuditTypeDocumentDetails(@RequestBody AuditDocumentDetails auditTypeDocumentDetails){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(auditTypeDocumentDetails!=null
				&& auditTypeDocumentDetails.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getAuditTypeCode())
				&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentType())
				&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getMaker())
				&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getStatus())
				){
			
			try {
				if(auditTypeDocumentDetails.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
					auditTypeDocumentDetails.setCheckerTimestamp(new Date());
				} else{
					auditTypeDocumentDetails.setMakerTimestamp(new Date());
				}  
				auditTypeDocumentDetailsService.updateAuditTypeDocumentDetails(auditTypeDocumentDetails);
			    
			    serviceStatus.setStatus("success");
			    serviceStatus.setMessage("successfully updated");
		
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}
			
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}
		
		return serviceStatus;
	}
	
	//// new code Start from here
	@PostMapping(value="/uploadMultipleDocument" ,consumes=MediaType.MULTIPART_FORM_DATA_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus uploadMultipleDocument(
			@RequestParam(value="maker")String maker,
			@RequestParam(value="role")String role,
			@RequestParam(value="files")CommonsMultipartFile[] files,
			@RequestParam(value="legalEntityCode")Integer legalEntityCode,
			@RequestParam(value="auditTypeCode")String auditTypeCode,			 
			@RequestParam(value="documentType")String documentType,
			@RequestParam(value="documentName")String documentName,
			@RequestParam(value="warningCheck")String warningCheck,
			@RequestParam(value="documentSubType")String documentSubType){
	
		ServiceStatus serviceStatus=new ServiceStatus();
		Map<String, Object> result=new HashMap<>();
		try {
			if(legalEntityCode !=null   && auditTypeCode !=null && files!=null ){
				//List<AuditTypeDocumentDetails> auditTypeDocumentDetailsLst=maintAuditTypeDescService.getAuditTypeDocumentDetails(legalEntityCode, auditTypeCode, documentType, maker, role);
				String fileName=legalEntityCode.toString()+auditTypeCode+documentType;
				
				Boolean isWarning=false;
				String duplicateFileNames="";
				List<Integer> duplicateDocIds=new ArrayList<>(); 
				for (CommonsMultipartFile commonsMultipartFile : files) {
					if(!BankAuditUtil.validateFileType(commonsMultipartFile, BankAuditConstant.DOC_TYPES)){
						serviceStatus.setStatus("failure");
						serviceStatus.setMessage("file type : "+BankAuditUtil.getFileExtension(commonsMultipartFile.getOriginalFilename())
						+" not supported");
						return serviceStatus;
					}

				}
				if(isWarning){
					serviceStatus.setStatus("warning");
				    serviceStatus.setMessage("file " + duplicateFileNames + "  already exist do you want to replace  ");
				    serviceStatus.setResult(duplicateDocIds);
				    return serviceStatus;
				} 
				  Map<String, Integer> uploadStatus = auditTypeDocumentDetailsService.uploadMultipleDocument
						  (legalEntityCode, auditTypeCode, documentType, documentName, maker, role, files, documentSubType);
						   
				  result.put("documentUploadIds", uploadStatus);
				  
				serviceStatus.setResult(result);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully uploaded");
				
			}else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("Invalid payload");
			}
			
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure");
		}
		return serviceStatus;
	}
	
	@PutMapping(value="/updateAuditTypeDocumentDetails" ,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateAuditTypeDocumentDetailsMulti(@RequestBody AuditDocumentDetails auditTypeDocumentDetails){
		ServiceStatus serviceStatus=new ServiceStatus();
		if(auditTypeDocumentDetails!=null
				&& auditTypeDocumentDetails.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getAuditTypeCode())
				//&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getDocumentType())
				&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getChecker())
				&& !BankAuditUtil.isEmptyString(auditTypeDocumentDetails.getStatus())){
			
			try {
				
				String updatestatus = auditTypeDocumentDetailsService.updateAuthorizeAuditTypeDocumentDetails(auditTypeDocumentDetails);
			    
				if(updatestatus.equals(BankAuditConstant.SUCCESS)){
					serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully updated");
				} else{
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("failure");
				}
		
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}
			
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}
		
		return serviceStatus;
	}
	
	@DeleteMapping(value="/deleteAuditTypeDocument" ,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus  deleteAuditTypeDocument(				  
			@RequestParam(value="legalEntityCode") Integer legalEntityCode, 
			@RequestParam(value="id") Integer id, 
			@RequestParam(value="auditTypeCode") String auditTypeCode, 
			@RequestParam(value="status") String status,
			@RequestParam(value="entityStatus") String entityStatus,
			@RequestParam(value="documentType") String documentType, 
			@RequestParam(value="documentSubType") String documentSubType)  {
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(!BankAuditUtil.isEmptyString(auditTypeCode) && !BankAuditUtil.isEmptyString(status) 
				&& id!=null && id>0
				&& legalEntityCode!=null && legalEntityCode>0){
			try{
			   String updateStatus =	auditTypeDocumentDetailsService.deleteAuditTypeDocumentFromUI
					   (legalEntityCode, id, auditTypeCode, status, entityStatus, documentType, documentSubType);
				 
				if(updateStatus.equals(BankAuditConstant.SUCCESS)){
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully deleted the selected document");
				}else{
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("failed to delete the selected document");
				}
			}catch (Exception e) {
				logger.error(e.getStackTrace());
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
		}else{
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("missing parameter value document id");
		} 			
		
		return serviceStatus;
	}
	
	@DeleteMapping(value="/deleteAllAuditTypeDocument" , produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteAllAuditTypeDocument(
			@RequestParam(value="legalEntityCode")Integer legalEntityCode,
			@RequestParam(value="auditTypeCode")String auditTypeCode, 
			@RequestParam(value="documentType")String documentType,
			@RequestParam(value="status") String status,  
			@RequestParam(value="documentSubType") String documentSubType
			){
	
		ServiceStatus serviceStatus=new ServiceStatus();
		
		try {
			
			String updateStatus = auditTypeDocumentDetailsService.deleteAllAuditTypeDocument(legalEntityCode, auditTypeCode, documentType, status, documentSubType); 
			
			if(updateStatus.equals(BankAuditConstant.SUCCESS)){
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully deleted all the documents");
			}else{
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failed to delete  documents");
			}
			
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure");
		}
		return serviceStatus;
	}

	@GetMapping(value="/getByAuditType/{legalEntityCode}/{auditTypeCode}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getAuditTypeDocumentDetailByAuditType(
			@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode,
			@PathVariable("status") String status,
			@PathVariable(value="documentType",required=false)String documentType,
			@PathVariable(value="documentSubType",required=false) String documentSubType
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		if(legalEntityCode!=null){
			
			try {
			    AuditDocumentDetails auditTypeDocumentDetails=auditTypeDocumentDetailsService.getAuditTypeDocumentDetailByAuditType(legalEntityCode, auditTypeCode, status, documentType, documentSubType);
			    		 
			    if(auditTypeDocumentDetails!=null){
			    	serviceStatus.setResult(auditTypeDocumentDetails);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAuditActivity not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}
		
		return serviceStatus;
	}

}
