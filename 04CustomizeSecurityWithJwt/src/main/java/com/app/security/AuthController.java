package com.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.security.auth.AuthRequest;
import com.app.security.auth.AuthResponse;
import com.app.security.entity.Customer;
import com.app.security.jwt.JwtService;
import com.app.security.rep.CustomerRepository;
import com.app.security.service.CustomerServices;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerServices customerServices;

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Customer customer) {
        // Check if username or email already exists
        if (customerRepository.findByUname(customer.getUname()) != null) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }
        if (customerRepository.findByUname(customer.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }

        // Encrypt password and save user
        customer.setPsw(passwordEncoder.encode(customer.getPsw()));
        customerRepository.save(customer);

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Authenticate user and generate JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {
            // Perform authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            // Generate token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            // Return the token as part of the response
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    /**
     * Protected endpoint example.
     * This can be accessed only by authenticated users with a valid JWT.
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        Customer customer = customerRepository.findByUname(authentication.getName());
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }
}

