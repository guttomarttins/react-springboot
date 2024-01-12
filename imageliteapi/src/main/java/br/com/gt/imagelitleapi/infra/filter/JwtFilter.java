package br.com.gt.imagelitleapi.infra.filter;

import br.com.gt.imagelitleapi.domain.entity.User;
import br.com.gt.imagelitleapi.domain.service.UserService;
import br.com.gt.imagelitleapi.infra.jwt.InvalidTokenException;
import br.com.gt.imagelitleapi.infra.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpRequest;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        if(StringUtils.hasText(token)){
            try {
                String email = jwtService.getEmailFromToken(token);
                User user = userService.getByEmail(email);
                setUserAuthenticated(user);
            }catch (InvalidTokenException e){
                log.error("Invalid Token: {} " + e.getMessage());
            }catch (Exception ex){
                log.error("Error Token Validation: {} " + ex.getMessage());
            }

        }

        filterChain.doFilter(request, response);
    }

    private void setUserAuthenticated(User user){
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(StringUtils.hasText(authHeader)){
           String[] authHeadersParts = authHeader.split(" ");
           if(authHeadersParts.length == 2){
              return authHeadersParts[1];
           }
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/v1/users");
    }
}
