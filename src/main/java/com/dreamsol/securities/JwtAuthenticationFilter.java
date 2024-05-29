package com.dreamsol.securities;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final String[] PUBLIC_URLS = {
            "/swagger-ui/index.html",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/v3/api-docs/swagger-config",
            "/swagger-ui/favicon-32x32.png",
            "/v3/api-docs",
            "/api/authenticate-user",
            "/api/register-user"
    };
    List<String> publicUrls = List.of(PUBLIC_URLS);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        if(!publicUrls.contains(request.getRequestURI()))
        {
            String requestToken = request.getHeader("Authorization");
            String username = null;
            String actualToken = null;
            if(requestToken!=null && requestToken.startsWith("Bearer"))
            {

                actualToken = requestToken.substring(7);
                try{
                    username = jwtUtil.getUsernameFromToken(actualToken);
                }catch(ExpiredJwtException e)
                {
                    throw new RuntimeException(e.getMessage());
                }
                if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
                {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if(jwtUtil.validateToken(actualToken,userDetails))
                    {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                    else{
                        response.sendError(403,"Invalid token!");
                    }
                }
            }else{
                response.sendError(403,"Token is in incorrect format. missing prefix 'Bearer'");
            }
        }
        filterChain.doFilter(request,response);
    }
}