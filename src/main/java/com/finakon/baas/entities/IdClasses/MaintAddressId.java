package com.finakon.baas.entities.IdClasses;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class MaintAddressId implements Serializable{

    private String countryAlpha3Code;

    private Integer legalEntityCode;

    private String stateCode;

    private String districtCode;

    private String cityCode;

}
