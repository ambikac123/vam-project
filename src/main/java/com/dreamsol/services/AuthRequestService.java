package com.dreamsol.services;

import com.dreamsol.dtos.responseDtos.AuthResponseDto;
import com.dreamsol.securities.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthRequestService
{
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    public ResponseEntity<?> getToken(String username, String password)
    {
        getAuthentication(username,password);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = getRefreshToken();
        AuthResponseDto response = AuthResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    public void getAuthentication(String username,String password)
    {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,password);
        try{
             authenticationManager.authenticate(authentication);
        }catch (BadCredentialsException e)
        {
            throw new BadCredentialsException(" Invalid username or password !");
        }
    }
    public String getRefreshToken()
    {
        return UUID.randomUUID()+"."+UUID.randomUUID()+"."+ UUID.randomUUID();
    }
}
