package com.bankaudit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sequence_appender")
public class SequenceAppender {
/*	Table: sequence_appender
	----------------------------

	Column Information
	----------------------

	Field       Type    Collation  Null    Key     Default  Extra           Privileges                       Comment  
	----------  ------  ---------  ------  ------  -------  --------------  -------------------------------  ---------
	sequnec_no  int(4) */
	
	@Id
	@Column(name="sequnec_no")
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Integer sequnecNo;

	/**
	 * @return the sequnecNo
	 */
	public Integer getSequnecNo() {
		return sequnecNo;
	}

	/**
	 * @param sequnecNo the sequnecNo to set
	 */
	public void setSequnecNo(Integer sequnecNo) {
		this.sequnecNo = sequnecNo;
	}
	
	
}
