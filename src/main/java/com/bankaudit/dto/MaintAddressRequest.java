package com.bankaudit.dto;

import lombok.Data;
import java.util.Date;


@Data
public class MaintAddressRequest {

	private String countryAlpha3Code;

	private Integer legalEntityCode;

	private String countryNumericCode;

	private String countryName;

	private String ccyCd;

	private String isdCode;

	private String gmtPlusMinus;

	private String gmtOffsetHrs;

	private String gmtOffsetMins;

	private String stateCode;

	private String stateIsoCode;

	private String stateName;

	private String districtCode;

	private String districtName;

	private String cityCode;

	private String cityName;

	private String status;

	private String maker;

	private Date makerTimestamp;

	private String checker;

	private Date checkerTimestamp;

}
