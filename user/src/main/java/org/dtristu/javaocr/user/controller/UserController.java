package org.dtristu.javaocr.user.controller;

import com.nimbusds.jwt.JWTClaimsSet;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    JwtEncoder jwtEncoder;
    @Autowired
    UserService userService;
    @GetMapping("/validate")
    public String validate(@RequestParam("token") String token){
        try {
            Jwt jwt= jwtDecoder.decode(token);
            return jwt.getClaimAsString("sub");
        } catch (Exception e){
            return "false";
        }
    }
    @PostMapping("/login")
    public String token(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 360000L;
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
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    @PostMapping("/addTask")
    public String addTaskToUser(@RequestBody OCRTask ocrTask){
       userService.addTaskToUser(ocrTask);
       return "true";
    }
}
