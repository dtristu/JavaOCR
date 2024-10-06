package org.dtristu.javaocr.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.user.dto.CreateAccountDTO;
import org.dtristu.javaocr.user.dto.LoginFormDTO;
import org.dtristu.javaocr.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Controller
public class UserWebController {
    @Autowired
    UserService userService;
    @Autowired
    JwtEncoder jwtEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @GetMapping("/web/signup")
    public String signupPage (){
        return "signup";
    }
    @PostMapping("/web/signup")
    public String signup(@ModelAttribute CreateAccountDTO createAccountDTO){
        try {
            AccountDTO accountDTO= userService.createAccount(createAccountDTO);
            return "redirect:/web/login";
        } catch (Exception e) {
            return String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/web/login")
    public String login(){
        return "login";
    }
    @PostMapping("/web/login")
    public String token(Model model, @ModelAttribute LoginFormDTO loginFormDTO, HttpServletResponse response) {
        try {
            Authentication authentication= authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginFormDTO.getUsername(), loginFormDTO.getPassword())
            );
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
            Cookie jwtCookie = new Cookie("Authorization", "Bearer_" + this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(60*60*10);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);
            return "redirect:/web/upload";
        } catch (Exception e){
            model.addAttribute("error", "Invalid username or password!");
            return "login";
        }
    }
    @GetMapping("/web")
    public String landingPage(){
        return "landingPage";
    }
}
