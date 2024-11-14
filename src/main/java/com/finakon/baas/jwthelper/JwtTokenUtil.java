package com.finakon.baas.jwthelper;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.projections.ILabelsInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.stream.Collectors;

public class JwtTokenUtil {

    @SuppressWarnings("deprecation")
    public static final String generateTemporaryToken(MaintLegalEntity maintLegalEntity, String userId, Integer expirationSeconds) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + (expirationSeconds * 1000));
        return Jwts.builder()
                .claim("userId", userId)
                .claim("legalEntityCode", maintLegalEntity.getLegalEntityCode())
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, maintLegalEntity.getJwtSecret())
                .compact();
    }

    @SuppressWarnings("deprecation")
    public static final String generateToken(String userId, MaintLegalEntity maintLegalEntity, String roleCode, Date businessDate, Integer expirationSeconds) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + (expirationSeconds * 1000));
        return Jwts.builder()
                .claim("userId", userId)
                .claim("legalEntityCode", maintLegalEntity.getLegalEntityCode())
                .claim("roleCode", roleCode)
                .claim("businessDate", businessDate)
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
}