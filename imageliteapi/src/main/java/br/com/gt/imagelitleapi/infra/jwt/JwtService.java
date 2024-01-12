package br.com.gt.imagelitleapi.infra.jwt;

import br.com.gt.imagelitleapi.domain.AccessToken;
import br.com.gt.imagelitleapi.domain.entity.User;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class JwtService {

    private SecretKeyGenerator keyGenerator;

    public AccessToken generateToken(User user){

        var key = keyGenerator.getKey();
        var expirationDate = generateExpirationDate();
        var claims = generateTokenClaims(user);

        String token = Jwts
                .builder()
                .signWith(key)
                .subject(user.getEmail())
                .expiration(expirationDate)
                .claims(claims)
                .compact();

        return new AccessToken(token);
    }

    private Date generateExpirationDate(){
        var expirationMinutes = 60;
        LocalDateTime now = LocalDateTime.now().plusMinutes(expirationMinutes);
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Map<String, Object> generateTokenClaims(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        return claims;
    }

    public String getEmailFromToken(String token){
        try {
            return Jwts.parser()
                    .verifyWith(keyGenerator.getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }catch (InvalidTokenException e){
            throw new InvalidTokenException(e.toString());
        }
    }
}
