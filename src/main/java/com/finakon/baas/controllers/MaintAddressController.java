package com.finakon.baas.controllers;

import com.finakon.baas.dto.Request.MaintAddressRequest;
import com.finakon.baas.dto.Response.ApiResponse;
import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.MaintAddress;
import com.finakon.baas.helper.BankAuditUtil;
import com.finakon.baas.service.ServiceInterfaces.MaintAddressService;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditTypeDescService;

import java.sql.Timestamp;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maintAddress")
public class MaintAddressController {
	
	/**
	 * The maint address service is autowired and make methods available from
	 * service layer .
	 */
	@Autowired
	MaintAddressService maintAddressService;
	
	/** The Constant logger is used to specify the . */
	static final Logger logger = Logger.getLogger(MaintAddressController.class);
	
	/**
	 * This method is use to Creates the maint address.
	 *
	 * @param maintAddressRequest
	 *            specify the maint address
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ApiResponse createMaintAddress(@RequestBody MaintAddressRequest maintAddressRequest){
		ApiResponse apiRespose=new ApiResponse();
		
		System.out.println("Inside createMaintAddress .... ");
		try{
			if(maintAddressRequest!=null
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getCountryAlpha3Code())
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getStateCode()) 
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getDistrictCode())
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getCityCode())
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getMaker())
					){ 
				    maintAddressRequest.setStatus("A");

				    maintAddressRequest.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
				    
				    maintAddressService.createMaintAddress(maintAddressRequest);
				    
				    apiRespose.setStatus("success");
				    apiRespose.setMessage("successfully created");
			}else {
				apiRespose.setStatus("failure");
				apiRespose.setMessage("invalid payload ");
			}
		}catch (Exception e) {
			e.printStackTrace();
			apiRespose.setStatus("failure");
			apiRespose.setMessage("failure"+e.getMessage());
			if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
				apiRespose.setMessage("DATAINTGRTY_VIOLATION");
			}
		}
		
		return apiRespose;
	}
	
	/**
	 * This method is use to Update maint address.
	 *
	 * @param maintAddressRequest
	 *            specify the maint address
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ApiResponse updateMaintAddress(@RequestBody MaintAddressRequest maintAddressRequest){
		ApiResponse apiRespose = new ApiResponse();
		
		try {
			if(maintAddressRequest!=null
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getCountryAlpha3Code())
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getStateCode()) 
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getDistrictCode())
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getCityCode())
					&& !BankAuditUtil.isEmptyString(maintAddressRequest.getMaker())
					){  
				maintAddressRequest.setStatus("A");
				maintAddressRequest.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
				    
				maintAddressService.updateMaintAddress(maintAddressRequest);
				    
				apiRespose.setStatus("success");
				apiRespose.setMessage("successfully updated");
			}else {
				apiRespose.setStatus("failure");
				apiRespose.setMessage("invalid payload ");
			}
		}catch (Exception e) {
			e.printStackTrace();
			apiRespose.setStatus("failure");
			apiRespose.setMessage("failure"+e.getMessage());
			if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
				apiRespose.setMessage("DATAINTGRTY_VIOLATION");
			}
		}
		
		return apiRespose;
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
	ApiResponse getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode){
		ApiResponse apiResponse=new ApiResponse();
		
		try {
			    List<MaintAddress> maintAddress=maintAddressService.getAllAddress(legalEntityCode);
			    if(maintAddress!=null){
			    	apiResponse.setResult(maintAddress);
				    apiResponse.setStatus("success");
				    apiResponse.setMessage("successfully retrieved ");	
			    }else {
					apiResponse.setStatus("failure");
					apiResponse.setMessage("maintAddress not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				apiResponse.setStatus("failure");
				apiResponse.setMessage("failure");
			}
			
		return apiResponse;
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
	ApiResponse getMaintAddress_Country(@PathVariable("legalEntityCode")Integer legalEntityCode){
		ApiResponse apiResponse=new ApiResponse();
		
		try {
			    List<MaintAddress> maintAddress=maintAddressService.getAllCountryCode(legalEntityCode);
			    if(maintAddress!=null){
			    	apiResponse.setResult(maintAddress);
				    apiResponse.setStatus("success");
				    apiResponse.setMessage("successfully retrieved ");	
			    }else {
					apiResponse.setStatus("failure");
					apiResponse.setMessage("maintAddress not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				apiResponse.setStatus("failure");
				apiResponse.setMessage("failure");
			}
			
		return apiResponse;
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
	ApiResponse getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("countryAlpha3Code") String countryAlpha3Code
			){
		ApiResponse apiResponse=new ApiResponse();
		
		System.out.println("Inside getStatesByCountryCode is.... "+ countryAlpha3Code);
		if(!BankAuditUtil.isEmptyString(countryAlpha3Code)){
			
			try {
				List<MaintAddress> maintAddress=maintAddressService.getStatesbyCountry(legalEntityCode,countryAlpha3Code);
			    if(maintAddress!=null){
			    	apiResponse.setResult(maintAddress);
				    apiResponse.setStatus("success");
				    apiResponse.setMessage("successfully retrieved ");	
			    }else {
					apiResponse.setStatus("failure");
					apiResponse.setMessage("maintAddressService not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				apiResponse.setStatus("failure");
				apiResponse.setMessage("failure");
			}
			
		}else {
			apiResponse.setStatus("failure");
			apiResponse.setMessage("invalid payload ");
		}
		
		return apiResponse;
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
	ApiResponse getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("countryAlpha3Code") String countryAlpha3Code,
			@PathVariable("stateCode") String stateCode
			){
		ApiResponse apiResponse=new ApiResponse();
		System.out.println("getStatesbyCountry in IMPL ...."+ countryAlpha3Code);
		
		if(!BankAuditUtil.isEmptyString(countryAlpha3Code) && !BankAuditUtil.isEmptyString(stateCode)){
			
			try {
				List<MaintAddress> maintAddress=maintAddressService.getDistrictsByCountryState(legalEntityCode,countryAlpha3Code,stateCode);
			    if(maintAddress!=null){
			    	apiResponse.setResult(maintAddress);
				    apiResponse.setStatus("success");
				    apiResponse.setMessage("successfully retrieved ");	
			    }else {
					apiResponse.setStatus("failure");
					apiResponse.setMessage("maintAddressService not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				apiResponse.setStatus("failure");
				apiResponse.setMessage("failure");
			}
			
		}else {
			apiResponse.setStatus("failure");
			apiResponse.setMessage("invalid payload ");
		}
		
		return apiResponse;
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
	 * @param districtCode
	 *            specify the district code
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@GetMapping(value="/getCities/{legalEntityCode}/{countryAlpha3Code}/{stateCode}/{districtCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ApiResponse getMaintAddress(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("countryAlpha3Code") String countryAlpha3Code,
			@PathVariable("stateCode") String stateCode,@PathVariable("districtCode") String districtCode
			){
		ApiResponse apiResponse=new ApiResponse();
		
		if(!BankAuditUtil.isEmptyString(countryAlpha3Code) && !BankAuditUtil.isEmptyString(stateCode)){
			
			try {
				List<MaintAddress> maintAddress=maintAddressService.getCitiesByCountryStateDist(legalEntityCode,countryAlpha3Code,stateCode,districtCode);
			    if(maintAddress!=null){
			    	apiResponse.setResult(maintAddress);
				    apiResponse.setStatus("success");
				    apiResponse.setMessage("successfully retrieved ");	
			    }else {
					apiResponse.setStatus("failure");
					apiResponse.setMessage("maintAuditActivity not found");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				apiResponse.setStatus("failure");
				apiResponse.setMessage("failure"+e.getMessage());
			}
			
		}else {
			apiResponse.setStatus("failure");
			apiResponse.setMessage("invalid payload ");
		}
		
		return apiResponse;
	}

}
