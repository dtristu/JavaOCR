package org.dtristu.javaocr.usersecurity.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookie;
        if (cookies!=null) {
            cookie = Arrays.stream(cookies).filter(e -> Objects.equals(e.getName(), "Authorization")).findFirst();
        } else {
            cookie = Optional.empty();
        }
        String userName = null;
        String token = null;

        if (cookie.isPresent()) {
            token = cookie.get().getValue().substring(7);
            Jwt jwt= jwtDecoder.decode(token);
            userName = jwt.getClaimAsString("sub");

        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                                            userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
