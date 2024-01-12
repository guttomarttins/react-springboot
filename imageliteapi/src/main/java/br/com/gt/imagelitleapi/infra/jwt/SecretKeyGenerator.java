package br.com.gt.imagelitleapi.infra.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Objects;

@Component
public class SecretKeyGenerator {

    private SecretKey key;

    public SecretKey getKey() {
        if(Objects.nonNull(key)) return key;
        return Jwts.SIG.HS256.key().build();
    }
}
