package com.sc.zipdistance.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.sc.zipdistance.model.entity.CustomUserDetails;
import com.sc.zipdistance.util.JwtUtil;
import com.sc.zipdistance.util.SpringContextUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove 'Bearer ' prefix

            try {
                JwtUtil jwtUtil = SpringContextUtil.getBean(JwtUtil.class);
                // ✅ Parse JWT
                Claims claims = jwtUtil.validateToken(token.replace("Bearer ", ""));


                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);
                List<String> permissions = claims.get("permissions", List.class);
                String userIdStr = claims.get("userId", String.class);
                UUID userId = UUID.fromString(userIdStr);
                // 🛡️ Map roles & permissions to authorities
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                }
                if (permissions != null) {
                    permissions.forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));
                }
                // ✅ Use a CustomUserDetails object to hold userId
                CustomUserDetails userDetails = new CustomUserDetails(userId, username, authorities);

                // ✅ Set Authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or Expired Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
