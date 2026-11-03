package CasoHospital.Cita_medica.service;

import CasoHospital.Cita_medica.dto.AuthRequest;
import CasoHospital.Cita_medica.dto.AuthResponse;
import CasoHospital.Cita_medica.model.Role;
import CasoHospital.Cita_medica.repository.UserRepository;
import CasoHospital.Cita_medica.security.JwtService;
import lombok.RequiredArgsConstructor;
import CasoHospital.Cita_medica.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario '" + request.getUsername() + "' ya existe");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new AuthResponse(jwtService.generateToken(user));
    }
}