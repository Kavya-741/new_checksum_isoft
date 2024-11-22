package com.bankaudit.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="maint_legal_entity")
public class MaintLegalEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="legal_entity_code", nullable = false)
	private Integer legalEntityCode;
	
	@Column(name="legal_entity_name")
	private String legalEntityName;

	@Column(name = "url", nullable = false)
	private String url;
	
	@Column(name="number_of_levels", nullable = false)
	private Integer numberOfLevels;
	
	@Column(name="status", nullable = false)
	private String status;

	@Column(name = "businessdate_timestamp")
	private Date businessdateTimestamp;
	
	@Column(name = "sysdate_timestamp")
	private Date sysdateTimestamp;

	@Column(name = "audit_or_inspection")
	private String auditOrInspection;

	@Column(name = "enable_captcha")
	private boolean enableCaptcha;

	@Column(name = "header_logo")
	private String headerLogo;

	@Column(name = "footer_logo")
	private String footerLogo;

	@Column(name = "jwt_secret")
	private String jwtSecret;

}
