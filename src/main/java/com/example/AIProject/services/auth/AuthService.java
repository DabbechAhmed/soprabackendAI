package com.example.AIProject.services.auth;

import com.example.AIProject.entities.Role;
import com.example.AIProject.entities.User;
import com.example.AIProject.exceptions.AlreadyExistsException;
import com.example.AIProject.repository.RoleRepository;
import com.example.AIProject.repository.UserRepository;
import com.example.AIProject.requests.auth.LoginRequest;
import com.example.AIProject.requests.auth.RegisterRequest;
import com.example.AIProject.responses.JwtResponse;
import com.example.AIProject.security.jwt.JwtUtils;
import com.example.AIProject.security.user.ShopUserDetails;
import com.example.AIProject.security.user.ShopUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateTokenForUser(authentication);
        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        return JwtResponse.builder()
                .id(userDetails.getId())
                .token(jwt)
                .build();

    }

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new AlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFirstName() + " " + registerRequest.getLastName());

        // Assigner le rôle par défaut
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Rôle employé non trouvé"));
        user.setRoles(Set.of(employeeRole));

        userRepository.save(user);

        // Authentifier le nouvel utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateTokenForUser(authentication);
        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();

        return JwtResponse.builder()
                .id(userDetails.getId())
                .token(jwt)
                .build();
    }


   @Override
   public void logout(String token) {
       Long userId = jwtUtils.getUserIdFromToken(token);
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
       user.setTokenVersion(user.getTokenVersion() + 1); // Invalide tous les anciens tokens
       userRepository.save(user);
   }
}
