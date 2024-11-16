package com.finakon.baas.jwthelper;
import com.finakon.baas.dto.GetTokenDetailsDTO;
import com.finakon.baas.entities.MaintEntity;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.User;
import com.finakon.baas.helper.DateUtil;
import com.finakon.baas.projections.ILabelsInfo;
import com.finakon.baas.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

public class JwtTokenUtil {

    @SuppressWarnings("deprecation")
    public static final String generateTemporaryToken(MaintLegalEntity maintLegalEntity, User user, MaintEntity maintEntity, Integer expirationSeconds) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + (expirationSeconds * 1000));

        return Jwts.builder()
                .claim("userId", user.getUserId())
                .claim("legalEntityCode", maintLegalEntity.getLegalEntityCode())
                .claim("unitCode",user.getUnitCode())
                .claim("unitName", maintEntity.getUnitName())
                .claim("levelCode", maintEntity.getLevelCode())
                .claim("parentUnitCode", maintEntity.getParentUnitCode())
                .claim("firstName", user.getFirstName())
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, maintLegalEntity.getJwtSecret())
                .compact();
    }

    @SuppressWarnings("deprecation")
    public static final String generatePermanentToken(MaintLegalEntity maintLegalEntity, GetTokenDetailsDTO tokenDetails, String roleCode, String roleName, Integer expirationSeconds) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + (expirationSeconds * 1000));
        return Jwts.builder()
                .claim("userId", tokenDetails.getUserId())
                .claim("legalEntityCode", tokenDetails.getLegalEntityCode())
                .claim("unitCode",tokenDetails.getUnitCode())
                .claim("unitName", tokenDetails.getUnitName())
                .claim("levelCode", tokenDetails.getLevelCode())
                .claim("parentUnitCode", tokenDetails.getParentUnitCode())
                .claim("roleName", roleName)
                .claim("roleCode", roleCode)
                .claim("firstName", tokenDetails.getFirstName())
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, maintLegalEntity.getJwtSecret())
                .compact();
    }

      public static final boolean validateJwt(String jwt, MaintLegalEntity maintLegalEntity) {
        try {
            Jwts.parserBuilder().setSigningKey(maintLegalEntity.getJwtSecret()).build().parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static final String getUserIdFromJwt(String jwt) {
        int signatureIndex = jwt.lastIndexOf('.');
        String withoutSignature = jwt.substring(0, signatureIndex+1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        if(claims.containsKey("userId") && claims.get("userId") != null) {
            return String.valueOf(claims.get("userId"));
        } else {
            return null;
        }
    }

    public static final String getLeCodeFromJwt(String jwt) {
        int signatureIndex = jwt.lastIndexOf('.');
        String withoutSignature = jwt.substring(0, signatureIndex+1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        if(claims.containsKey("legalEntityCode") && claims.get("legalEntityCode") != null) {
            return String.valueOf(claims.get("legalEntityCode"));
        } else {
            return null;
        }
    }

    public static final String getLevelFromJwt(String jwt) {
        int signatureIndex = jwt.lastIndexOf('.');
        String withoutSignature = jwt.substring(0, signatureIndex+1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        if(claims.containsKey("level") && claims.get("level") != null) {
            return String.valueOf(claims.get("level"));
        } else {
            return null;
        }
    }
    public static final String getUnitCodeFromJwt(String jwt) {
        int signatureIndex = jwt.lastIndexOf('.');
        String withoutSignature = jwt.substring(0, signatureIndex+1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        if(claims.containsKey("unitCode") && claims.get("unitCode") != null) {
            return String.valueOf(claims.get("unitCode"));
        } else {
            return null;
        }
    }


    public static final String getRoleCodeFromJwt(String jwt) {
        int signatureIndex = jwt.lastIndexOf('.');
        String withoutSignature = jwt.substring(0, signatureIndex+1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        if(claims.containsKey("roleCode") && claims.get("roleCode") != null) {
            return String.valueOf(claims.get("roleCode"));
        } else {
            return null;
        }
    }

    public static final String getQtrEndingDateFromJwt(String jwt) {
        int signatureIndex = jwt.lastIndexOf('.');
        String withoutSignature = jwt.substring(0, signatureIndex+1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        if(claims.containsKey("qtrEndingDate") && claims.get("qtrEndingDate") != null) {
            return String.valueOf(claims.get("qtrEndingDate"));
        } else {
            return null;
        }
    }

    public static final Claims decodeJwt(String jwtToken) {
        int signatureIndex = jwtToken.lastIndexOf('.');
        String withoutSignature = jwtToken.substring(0, signatureIndex+1);
        Claims claims = Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        return claims;
    }

    public static final GetTokenDetailsDTO getTokenDetails(Claims claims) {
        GetTokenDetailsDTO tokenDetails = new GetTokenDetailsDTO();
		 tokenDetails.setUserId(claims.get("userId") == null ? null : (String) claims.get("userId"));
		 tokenDetails.setLegalEntityCode(claims.get("legalEntityCode") == null ? null : (Integer) claims.get("legalEntityCode"));
		 tokenDetails.setUnitCode(claims.get("unitCode") == null ? null : (String) claims.get("unitCode"));
         tokenDetails.setUnitName(claims.get("unitName") == null ? null : (String) claims.get("unitName"));
		 tokenDetails.setRoleCode(claims.get("roleCode") == null ? null : (String) claims.get("roleCode"));
         tokenDetails.setRoleName(claims.get("roleName") == null ? null : (String) claims.get("roleName"));
         tokenDetails.setLevelCode(claims.get("levelCode") == null ? null : (String) claims.get("levelCode"));
		 tokenDetails.setParentUnitCode(claims.get("parentUnitCode") == null ? null : (String) claims.get("parentUnitCode"));
         tokenDetails.setFirstName(claims.get("firstName") == null ? null : (String) claims.get("firstName"));
		 tokenDetails.setLoginTime(claims.getIssuedAt() == null ? null : DateUtil.convertToLocalDateTime(claims.getIssuedAt()));
		 tokenDetails.setTokenExpirationTime(claims.getExpiration() == null ? null : DateUtil.convertToLocalDateTime(claims.getExpiration()));
        return tokenDetails;
    }
}