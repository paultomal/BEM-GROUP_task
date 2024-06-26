package com.example.document_management_tool.auth;

import com.example.document_management_tool.component.UserInfoUserDetailsService;
import com.example.document_management_tool.entity.UserInfo;
import com.example.document_management_tool.enums.UserRoles;
import com.example.document_management_tool.repository.UserRepository;
import com.example.document_management_tool.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthController {

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserInfoUserDetailsService userDetailsService;

    private final UserRepository userRepository;

    public AuthController(JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          UserInfoUserDetailsService userDetailsService,
                          UserRepository userRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateGetToken(@RequestBody AuthRequestDTO authRequestDTO) {

        createSysRoot();

        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername().toLowerCase(), authRequestDTO.getPassword())
            );
            if (authenticate.isAuthenticated()) {

                UserInfo user = userRepository.findByUsername(authRequestDTO.getUsername()).orElse(null);
                ResponseDTO responseDTO = new ResponseDTO();

                responseDTO.setToken(jwtService.generateToken(authRequestDTO.getUsername().toLowerCase(),
                        (List) userDetailsService.loadUserByUsername(authRequestDTO.getUsername()).getAuthorities()));
                responseDTO.setUsername(authRequestDTO.getUsername().toLowerCase());
                responseDTO.setRoles(jwtService.extractRole(responseDTO.getToken()));
                responseDTO.setExpiredDate(jwtService.extractExpiration(responseDTO.getToken()));
                assert user != null;
                responseDTO.setUserId(user.getId());
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                throw new UsernameNotFoundException("Invalid user request!!");
            }

        } catch (AuthenticationException e) {
            String errorMessage = "Authentication failed: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }
    }

    @GetMapping("/getRole")
    public ResponseEntity<?> getRole(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(jwtService.extractRole(token.substring(7)), HttpStatus.OK);
    }

    public void createSysRoot() {
        if (userRepository.findByUsername("root").isEmpty()) {
            UserInfo user = new UserInfo();
            user.setName("root");
            user.setUsername("root");
            user.setEmail("root@bem.net");
            user.setPassword(passwordEncoder.encode("root"));
            user.setRoles(UserRoles.ROLE_ROOT);

            userRepository.save(user);
        }
    }
}

