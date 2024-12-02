package com.bankaudit.rest.controller;

import java.sql.Timestamp;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintAddress;
import com.bankaudit.service.MaintAddressService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/maintAddress")
public class MaintAddressController {
	
	@Autowired
	MaintAddressService maintAddressService;


	static final Logger logger = Logger.getLogger(MaintAddressController.class);
	

	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintAddress(@RequestBody MaintAddress maintAddress){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		System.out.println("Inside createMaintAddress .... ");
		try{
			if(maintAddress!=null
					&& !BankAuditUtil.isEmptyString(maintAddress.getCountryAlpha3Code())
					&& !BankAuditUtil.isEmptyString(maintAddress.getStateCode()) 
					&& !BankAuditUtil.isEmptyString(maintAddress.getDistrictCode())
					&& !BankAuditUtil.isEmptyString(maintAddress.getCityCode())
					&& !BankAuditUtil.isEmptyString(maintAddress.getMaker())
					){ 
				    maintAddress.setStatus("A");
				    maintAddress.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
				    
				    maintAddressService.createMaintAddress(maintAddress);
				    
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully created");
			}else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload ");
			}
		}catch (Exception e) {
			e.printStackTrace();
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure"+e.getMessage());
			if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			}
		}
		
		return serviceStatus;
	}
	
	/**
	 * This method is use to Update maint address.
	 *
	 * @param maintAddress
	 *            specify the maint address
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateMaintAddress(@RequestBody MaintAddress maintAddress){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		try {
			if(maintAddress!=null
					&& !BankAuditUtil.isEmptyString(maintAddress.getCountryAlpha3Code())
					&& !BankAuditUtil.isEmptyString(maintAddress.getStateCode()) 
					&& !BankAuditUtil.isEmptyString(maintAddress.getDistrictCode())
					&& !BankAuditUtil.isEmptyString(maintAddress.getCityCode())
					&& !BankAuditUtil.isEmptyString(maintAddress.getMaker())
					){  
				maintAddress.setStatus("A");
				maintAddress.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
				    
				maintAddressService.updateMaintAddress(maintAddress);
				    
				 serviceStatus.setStatus("success");
				 serviceStatus.setMessage("successfully updated");
			}else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload ");
			}
		}catch (Exception e) {
			e.printStackTrace();
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure"+e.getMessage());
			if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			}
		}
		
		return serviceStatus;
	}
	
	/**
	 * This method is use to Gets the maint address.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@GetMapping(value="/getAll/{legalEntityCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		try {
			    List<MaintAddress> maintAddress=maintAddressService.getAllAddress(legalEntityCode);
			    if(maintAddress!=null){
			    	serviceStatus.setResult(maintAddress);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAddress not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
			
		return serviceStatus;
	}
	
	/**
	 * This method is use to Gets the maint address country.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@GetMapping(value="/getAllCountryCode/{legalEntityCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAddress_Country(@PathVariable("legalEntityCode")Integer legalEntityCode){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		try {
			    List<MaintAddress> maintAddress=maintAddressService.getAllCountryCode(legalEntityCode);
			    if(maintAddress!=null){
			    	serviceStatus.setResult(maintAddress);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAddress not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
			
		return serviceStatus;
	}
	
	/**
	 * This method is use to Gets the maint address.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @param countryAlpha3Code
	 *            specify the country alpha 3 code
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@GetMapping(value="/getStatesByCountryCode/{legalEntityCode}/{countryAlpha3Code}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("countryAlpha3Code") String countryAlpha3Code
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		System.out.println("Inside getStatesByCountryCode is.... "+ countryAlpha3Code);
		if(!BankAuditUtil.isEmptyString(countryAlpha3Code)){
			
			try {
				List<MaintAddress> maintAddress=maintAddressService.getStatesbyCountry(legalEntityCode,countryAlpha3Code);
			    if(maintAddress!=null){
			    	serviceStatus.setResult(maintAddress);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAddressService not found");
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
	
	/**
	 * This method is use to Gets the maint address.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @param countryAlpha3Code
	 *            specify the country alpha 3 code
	 * @param stateCode
	 *            specify the state code
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@GetMapping(value="/getDistricts/{legalEntityCode}/{countryAlpha3Code}/{stateCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("countryAlpha3Code") String countryAlpha3Code,
			@PathVariable("stateCode") String stateCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		System.out.println("getStatesbyCountry in IMPL ...."+ countryAlpha3Code);
		
		if(!BankAuditUtil.isEmptyString(countryAlpha3Code) && !BankAuditUtil.isEmptyString(stateCode)){
			
			try {
				List<MaintAddress> maintAddress=maintAddressService.getDistrictsByCountryState(legalEntityCode,countryAlpha3Code,stateCode);
			    if(maintAddress!=null){
			    	serviceStatus.setResult(maintAddress);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAddressService not found");
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
	
	
	@GetMapping(value="/getCities/{legalEntityCode}/{countryAlpha3Code}/{stateCode}/{districtCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("countryAlpha3Code") String countryAlpha3Code,
			@PathVariable("stateCode") String stateCode,@PathVariable("districtCode") String districtCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(!BankAuditUtil.isEmptyString(countryAlpha3Code) && !BankAuditUtil.isEmptyString(stateCode)){
			
			try {
				List<MaintAddress> maintAddress=maintAddressService.getCitiesByCountryStateDist(legalEntityCode,countryAlpha3Code,stateCode,districtCode);
			    if(maintAddress!=null){
			    	serviceStatus.setResult(maintAddress);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAuditActivity not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure"+e.getMessage());
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}
		
		return serviceStatus;
	}
}
