package com.finakon.baas.entities.IdClasses;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class GeneralParameterIdClass implements Serializable {
    private Integer legalEntityCode;
    private String modCode;
    private String language;
	private String key1;
	private String key2;
	private String value;
}