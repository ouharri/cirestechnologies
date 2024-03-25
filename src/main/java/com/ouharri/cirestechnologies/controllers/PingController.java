package com.ouharri.cirestechnologies.controllers;

import com.ouharri.cirestechnologies.exceptions.ResourceNotFoundException;
import com.ouharri.cirestechnologies.model.entities.User;
import com.ouharri.cirestechnologies.model.enums.Role;
import com.ouharri.cirestechnologies.services.impl.JwtServiceImpl;
import com.ouharri.cirestechnologies.services.spec.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Controller class to handle requests related to token validation, user roles, and authority checks.
 * This controller provides endpoints to check token validity, user roles, and specific authorities.
 *
 * @author <a href="mailto:ouharrioutman@gmail.com">Ouharri Outman</a>
 * @version 1.0
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ping")
public class PingController {
    private final JwtServiceImpl jwtServiceImpl;
    private final UserService userService;

    /**
     * Checks if the server is reachable.
     *
     * @return ResponseEntity with a boolean indicating server reachability.
     */
    @PostMapping("/")
    public ResponseEntity<Boolean> check() {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * Checks the user role based on the provided token and user ID.
     *
     * @param id  The user ID.
     * @param jwt The JWT token.
     * @return ResponseEntity with the user's role.
     */
    @PostMapping("/checkRole/{id}")
    public ResponseEntity<Role> checkRole(
            @PathVariable("id") UUID id,
            @RequestBody String jwt
    ) {
        if (jwtServiceImpl.isTokenExpired(jwt)) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        Optional<User> user = userService.findById(id);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        return new ResponseEntity<>(
                user.get().getRole(),
                HttpStatus.OK
        );
    }

    /**
     * Checks if the provided token is valid for the specified user.
     *
     * @param id  The user ID.
     * @param jwt The JWT token.
     * @return ResponseEntity with a boolean indicating token validity.
     */
    @PostMapping("/{id}")
    public ResponseEntity<Boolean> checkToken(
            @PathVariable("id") UUID id,
            @RequestBody String jwt
    ) {
        if (jwtServiceImpl.isTokenExpired(jwt)) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }

        Optional<User> user = userService.findById(id);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        return new ResponseEntity<>(
                jwtServiceImpl.isTokenValid(jwt, user.get()),
                HttpStatus.OK
        );
    }
}