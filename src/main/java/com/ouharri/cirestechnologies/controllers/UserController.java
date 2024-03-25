package com.ouharri.cirestechnologies.controllers;

import com.ouharri.cirestechnologies.exceptions.ResourceNotFoundException;
import com.ouharri.cirestechnologies.model.dto.responses.UploadSummaryResponseDTO;
import com.ouharri.cirestechnologies.model.dto.responses.UserGeneratedResponses;
import com.ouharri.cirestechnologies.model.dto.responses.UserResponses;
import com.ouharri.cirestechnologies.services.spec.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * Controller class for handling user-related endpoints.
 * <p>
 * This controller provides endpoints for generating users, uploading user data in batches,
 * and retrieving the currently authenticated user.
 *
 * @author <a href="mailto:ouharrioutman@gmail.com">Ouharri Outman</a>
 * @version 1.0
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    /**
     * Generates users based on the given count.
     *
     * @param count The number of users to generate.
     * @return ResponseEntity containing the generated users.
     * @throws ResourceNotFoundException If the count value is invalid.
     */
    @GetMapping(value = "generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserGeneratedResponses>> generateUser(@RequestParam("count") int count) {

        if (count <= 0 || count > 200000)
            throw new ResourceNotFoundException("Invalid count value. Please provide a positive integer value less than or equal to 200000.");

        List<UserGeneratedResponses> users = service.generateUsers(count);

        if (users.isEmpty())
            return ResponseEntity.noContent().build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated_" + count + "_users.json");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(users);

    }

    /**
     * Uploads a batch of users from the provided file.
     *
     * @param file The multipart file containing user data.
     * @return ResponseEntity containing the upload summary.
     */
    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadSummaryResponseDTO> uploadUserBatch(@RequestParam("file") MultipartFile file) {
        try {
            if (!Objects.equals(file.getContentType(), "application/json"))
                throw new ResourceNotFoundException("Invalid file format. Please provide a valid JSON file.");

            if (file.isEmpty())
                throw new ResourceNotFoundException("No user data provided to upload. The file is empty.");

            UploadSummaryResponseDTO summary = service.uploadBatch(file);

            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            log.error("Error processing file", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return ResponseEntity containing the current user's details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserGeneratedResponses> getCurrentUser() {
        return ResponseEntity.ok(service.getCurrentUser());
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return ResponseEntity containing the user's details if found, otherwise returns a 404 Not Found response.
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserResponses> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.getUserByUsername(username));
    }
}