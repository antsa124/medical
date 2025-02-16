package com.antsasdomain.medicalapp.filter;

import com.antsasdomain.medicalapp.service.PersonDetailsService;
import com.antsasdomain.medicalapp.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final PersonDetailsService personDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, PersonDetailsService personDetailsService) {
        this.jwtUtils = jwtUtils;
        this.personDetailsService = personDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // extract the authentication header with help of "Authorization"
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // check that the authentication header has the Bearer Token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // No JWT, so continue filter chain
        }

        // extract JWT token
        String token = authHeader.substring(7);

        try {
            // Extract username from token
            String username = jwtUtils.extractUsername(token);

            // Ensure user is not already authenticated (securityContextHolder.getContext()
            // .getAutheticated() == null
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details using PersonDetailsService
                UserDetails userDetails = personDetailsService.loadUserByUsername(username);

                // Validate token with user details
                if (jwtUtils.validateToken(token)) {
                    //⃣ Create authentication object
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // update the securityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            System.out.println("JWT Authentication failed: " + e.getMessage());
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
