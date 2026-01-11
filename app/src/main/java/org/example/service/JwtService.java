package org.example.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    public static final String SECRET = "357638792F423F4428472B4B6250655368566D597133743677397A2443264629";
    /*
        is key ko bahut secure tareeke se store karein. Jaise ki,
        environment variables ($env:JWT_SECRET),
        secret managers (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault),
        ya secure configuration files mein. Ise seedhe-seedhe apne source code (like on GitHub)
        mein nahi daalna chahiye!

        Abhi seekhne ke liye daal diya hai
    */

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration);
    }

     /*
        new Date()
        Current system time.

        if (token_expiry_time < current_time) → token is expired
     */
    private boolean isExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token , UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isExpired(token));
    }

    public String GenerateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Map for extra data if empty no extra data will be added
    private String createToken(Map<String , Object> claims , String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000 * 60 * 60)) // 1 Hour from issue
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

        /*
            compact()
            poore token ko ek encoded string me convert karta hai
            header.payload.signature format me
        */
    }

    // T means yahan koi bhi data type ho skta hai
    public <T> T extractClaim(String token , Function<Claims , T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
