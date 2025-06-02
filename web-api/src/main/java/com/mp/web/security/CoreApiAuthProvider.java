package com.mp.web.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class CoreApiAuthProvider implements AuthenticationProvider {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${core.api.url}")
    private String coreApiUrl;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // call core-api /api/users/login
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> req = Map.of("username", username, "password", password);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, headers);
        try {
            ResponseEntity<Map> respEntity = restTemplate.postForEntity(coreApiUrl + "/api/users/login", entity, Map.class);
            Map resp = respEntity.getBody();
            if (resp != null && Boolean.TRUE.equals(resp.get("success"))) {
                Map user = (Map) resp.get("user");
                if (user != null) {
                    // Store session token
                    String token = (String) user.get("token");
                    if (token != null) {
                        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                        HttpSession session = attr.getRequest().getSession(true);
                        session.setAttribute("sessionToken", token);
                    }
                    
                    // Get roles
                    List<Map<String, Object>> roleObjs = (List<Map<String, Object>>) user.get("roles");
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (roleObjs != null) {
                        for (Map<String, Object> roleObj : roleObjs) {
                            String roleName = (String) roleObj.get("name");
                            if (roleName != null) {
                                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                            }
                        }
                    }
                    return new UsernamePasswordAuthenticationToken(username, password, authorities);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BadCredentialsException("Invalid username/email or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
} 