package com.example.DispacherOCRJava.Controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    protected static final Logger logger = LogManager.getLogger(TokenController.class);

        @Autowired
        JwtEncoder encoder;

        @PostMapping("/token")
        public String token(Authentication authentication) {
            Instant now = Instant.now();
            long expiry = 360000L;
            // @formatter:off
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(authentication.getName())
                    .claim("scope", scope)
                    .build();
            // @formatter:on
            logger.trace("Returned token");
            return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }

    }

