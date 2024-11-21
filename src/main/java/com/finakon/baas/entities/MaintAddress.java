package com.finakon.baas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import com.finakon.baas.entities.IdClasses.MaintAddressId;
import com.finakon.baas.entities.IdClasses.MaintAuditTypeDescId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MaintAddress.class)
@Table(name = "maint_address")
public class MaintAddress {

	@Id
	@Column(name = "country_alpha3_code")
    private String countryAlpha3Code;

	@Id
    @Column(name = "legal_entity_code")
    private Integer legalEntityCode;

	@Id
    @Column(name = "state_code")
    private String stateCode;

	@Id
    @Column(name = "district_code")
    private String districtCode;

	@Id
    @Column(name = "city_code")
    private String cityCode;

	@Column(name = "country_numeric_code")
	private String countryNumericCode;

	@Column(name = "country_name")
	private String countryName;

	@Column(name = "ccy_cd")
	private String ccyCd;

	@Column(name = "isd_code")
	private String isdCode;

	@Column(name = "gmt_plus_minus")
	private String gmtPlusMinus;

	@Column(name = "gmt_offset_hrs")
	private String gmtOffsetHrs;

	@Column(name = "gmt_offset_mins")
	private String gmtOffsetMins;

	@Column(name = "state_iso_code")
	private String stateIsoCode;

	@Column(name = "state_name")
	private String stateName;

	@Column(name = "district_name")
	private String districtName;

	@Column(name = "city_name")
	private String cityName;

	@Column(name = "status")
	private String status;

	@Column(name = "maker")
	private String maker;

	@Column(name = "maker_timestamp")
	private Date makerTimestamp;

	@Column(name = "checker")
	private String checker;

	@Column(name = "checker_timestamp")
	private Date checkerTimestamp;

}
