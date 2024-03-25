package com.ouharri.cirestechnologies.controllers;

import com.ouharri.cirestechnologies.exceptions.ResourceNotCreatedException;
import com.ouharri.cirestechnologies.model.dto.requests.AuthenticationRequest;
import com.ouharri.cirestechnologies.model.dto.responses.AuthenticationResponse;
import com.ouharri.cirestechnologies.services.spec.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling authentication-related requests.
 * This class provides endpoints for user registration, authentication,
 * token validation, and token refreshing.
 *
 * @author <a href="mailto:ouharri.outman@gmail.com">Ouharri Outman</a>
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService service;

    /**
     * Endpoint for user authentication.
     *
     * @param request       The authentication request containing user credentials.
     * @param bindingResult BindingResult for validation errors.
     * @return ResponseEntity with the authenticated user's response.
     * @throws ResourceNotCreatedException If there are validation errors.
     */
    @PostMapping
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody final AuthenticationRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors())
            throw new ResourceNotCreatedException(bindingResult);

        return ResponseEntity.ok(service.authenticate(request));
    }

}