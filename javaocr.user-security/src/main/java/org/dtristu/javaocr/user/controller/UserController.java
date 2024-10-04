package org.dtristu.javaocr.user.controller;

import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import java.time.Instant;
import java.util.Optional;
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
            return "true";
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
    @GetMapping("/getAccount")
    public ResponseEntity<AccountDTO> getAccount(@RequestParam("token") String token){
        Optional<AccountDTO> optionalAccountDTO= userService.getAccount(token);
        if (optionalAccountDTO.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AccountDTO>(optionalAccountDTO.get(),HttpStatus.OK);
    }
}
