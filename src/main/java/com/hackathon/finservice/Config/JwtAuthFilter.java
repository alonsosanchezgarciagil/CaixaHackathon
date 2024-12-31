package com.hackathon.finservice.Config;

import com.hackathon.finservice.Repositories.TokenRepository;
import com.hackathon.finservice.Util.Constants;
import com.hackathon.finservice.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {

        if (StringUtils.containsAny(request.getServletPath(), Constants.WHITELIST_NOT_AUTH_ENDPOINTS)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWith(authHeader, "Bearer ")) {
            throw new BadCredentialsException(Constants.UNAUTHORIZED_RESPONSE);
        }
        String jwt = authHeader.substring(7);
        if(!tokenRepository.existsByToken(jwt)) {
            throw new BadCredentialsException(Constants.UNAUTHORIZED_RESPONSE);
        }
        String email = jwtUtil.getEmail(jwt);
        if(email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
