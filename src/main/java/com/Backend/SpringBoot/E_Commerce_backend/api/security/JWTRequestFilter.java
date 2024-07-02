package com.Backend.SpringBoot.E_Commerce_backend.api.security;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.LocalUserDAO;
import com.Backend.SpringBoot.E_Commerce_backend.services.JWTServices;
import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

        private JWTServices jwtServices;
        LocalUserDAO localUserDAO;

    public JWTRequestFilter(JWTServices jwtServices,LocalUserDAO localUserDAO) {
        this.jwtServices = jwtServices;
        this.localUserDAO=localUserDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader=request.getHeader("Authorization");
        if(tokenHeader!= null && tokenHeader.startsWith("Bearer")){
            String token=tokenHeader.substring(7);
            try{
                String username= jwtServices.getUsername(token);
                Optional<LocalUser> opUser=localUserDAO.findByUsernameIgnoreCase(username);
                if(opUser.isPresent()){
                    LocalUser user=opUser.get();
                    UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(user,null,new ArrayList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }catch (JWTDecodeException ex){

            }

        }
        filterChain.doFilter(request,response);

    }

}
