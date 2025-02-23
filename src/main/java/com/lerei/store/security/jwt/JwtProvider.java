package com.lerei.store.security.jwt;

import java.util.Date;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class JwtProvider {
    //Logger to display errors
    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    //Key to verify the token
    @Value("${jwt.secret}")
    private String secret;

    //Base expiration time
    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication) {

        UserDetails mainUser = (UserDetails) authentication.getPrincipal();
        logger.error(mainUser.getUsername());

        return Jwts.builder().setSubject(mainUser.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration *  100L * 365 * 24 * 60 * 60 * 1000))// 100 years in milliseconds
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }


 /*       ///////////////////////////////

        // Check if the remaining time on the token is less than a threshold (e.g., 10 minutes)
        Date now = new Date();
        Date tokenExpiration = new Date(now.getTime() + expiration * 1); // Convert seconds to milliseconds
        long remainingTimeMillis = tokenExpiration.getTime() - now.getTime();

        if (remainingTimeMillis <= 10 * 60 * 1000) { // 10 minutes in milliseconds
            // If remaining time is less than or equal to 10 minutes, generate a new token with refreshed expiration
            return Jwts.builder()
                    .setSubject(mainUser.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + expiration * 2))
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
        } else {
            // If the remaining time is still sufficient, return the original token
            return Jwts.builder()
                    .setSubject(mainUser.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(tokenExpiration)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
        }
    }
*/
    /*
    Date tokenExpiration;
    public String generateToken(Authentication authentication) {
        UserDetails mainUser = (UserDetails) authentication.getPrincipal();
        logger.error(mainUser.getUsername());

        Date now = new Date();
         tokenExpiration = new Date(now.getTime() + expiration * 1); // Convert seconds to milliseconds
/*
        // Check if the remaining time on the token is less than a threshold (e.g., 10 minutes)
        long remainingTimeMillis = tokenExpiration.getTime() - now.getTime();
        if (remainingTimeMillis <= 10 * 60 * 1000) { // 10 minutes in milliseconds
            // If remaining time is less than or equal to 10 minutes, generate a new token with refreshed expiration
            return refreshAndGenerateToken(mainUser.getUsername());
        } else {*/
            // If the remaining time is still sufficient, return the original token

        /*
            return Jwts.builder()
                    .setSubject(mainUser.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(tokenExpiration)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
      //  }
    }
*/

    /*
    private String refreshAndGenerateToken(String username) {
        Date now = new Date();
        Date refreshedExpiration = new Date(now.getTime() + expiration * 2); // Extend the expiration time
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(refreshedExpiration)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
*/

    //Creamos una función que permita obtener el nombre de usuario con el token
    public String getUserNameFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("invalid token");
        }catch (UnsupportedJwtException e){
            logger.error("token not supported");
        }catch (ExpiredJwtException e){
            logger.error("expired token");

        }catch (IllegalArgumentException e){
            logger.error("empty token");
        }catch (SignatureException e){
            logger.error("signature fail");
        }
        return false;
    }
}
