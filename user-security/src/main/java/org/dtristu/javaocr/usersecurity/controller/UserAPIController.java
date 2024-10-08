package org.dtristu.javaocr.usersecurity.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.usersecurity.dto.CreateAccountDTO;
import org.dtristu.javaocr.usersecurity.service.IncomingTaskService;
import org.dtristu.javaocr.usersecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class UserAPIController {
    protected static final Logger logger = LogManager.getLogger(UserAPIController.class);
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    JwtEncoder jwtEncoder;
    @Autowired
    UserService userService;
    @GetMapping("/api/validate")
    public String validate(@RequestParam("token") String token){
        try {
            Jwt jwt= jwtDecoder.decode(token);
            return "true";
        } catch (Exception e){
            return "false";
        }
    }
    @PostMapping("/api/login")
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
    @PostMapping("/api/signup")
    public ResponseEntity<AccountDTO> signup(@RequestBody CreateAccountDTO createAccountDTO){
       try {
           AccountDTO accountDTO= userService.createAccount(createAccountDTO);
           return new ResponseEntity<>(accountDTO,HttpStatus.OK);
       } catch (Exception e) {
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
    @GetMapping("/api/get-account")
    public ResponseEntity<AccountDTO> getAccount(@RequestParam("token") String token){
        Optional<AccountDTO> optionalAccountDTO= userService.getAccount(token);
        if (optionalAccountDTO.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AccountDTO>(optionalAccountDTO.get(),HttpStatus.OK);
    }
    @PostMapping("/api/get-account")
    public ResponseEntity<Void> postAccount(@RequestParam("token") String token, @RequestBody AccountDTO accountDTO) {
        Optional<AccountDTO> optionalAccountDTO = userService.getAccount(token);
        if (optionalAccountDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            userService.updateAccountOcrTasks(accountDTO, token);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/api/get-all-file-ids")
    public ResponseEntity<Set<String>> getAllFileIds(){
        Set<String> fileIds = userService.getAllFileIds();
        logger.trace("Returning all file ids");
        return new ResponseEntity<>(fileIds,HttpStatus.OK);
    }
}
