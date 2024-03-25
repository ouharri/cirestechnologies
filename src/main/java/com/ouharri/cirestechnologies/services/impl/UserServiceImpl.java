package com.ouharri.cirestechnologies.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.ouharri.cirestechnologies.exceptions.NoAuthenticateUser;
import com.ouharri.cirestechnologies.exceptions.ResourceNotCreatedException;
import com.ouharri.cirestechnologies.exceptions.ResourceNotFoundException;
import com.ouharri.cirestechnologies.mapper.UserMapper;
import com.ouharri.cirestechnologies.model.dto.requests.ChangePasswordRequest;
import com.ouharri.cirestechnologies.model.dto.requests.ChangeRoleRequest;
import com.ouharri.cirestechnologies.model.dto.requests.UserRequest;
import com.ouharri.cirestechnologies.model.dto.responses.UploadSummaryResponseDTO;
import com.ouharri.cirestechnologies.model.dto.responses.UserGeneratedResponses;
import com.ouharri.cirestechnologies.model.dto.responses.UserResponses;
import com.ouharri.cirestechnologies.model.entities.Token;
import com.ouharri.cirestechnologies.model.entities.User;
import com.ouharri.cirestechnologies.model.enums.Gender;
import com.ouharri.cirestechnologies.model.enums.Role;
import com.ouharri.cirestechnologies.model.enums.TokenType;
import com.ouharri.cirestechnologies.model.enums.UserStatus;
import com.ouharri.cirestechnologies.repositories.TokenRepository;
import com.ouharri.cirestechnologies.repositories.UserRepository;
import com.ouharri.cirestechnologies.services.spec.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service class for managing user-related operations.
 *
 * <p>This service class provides methods for retrieving user data based on various criteria,
 * updating user information, and handling user status changes.</p>
 *
 * @author <a href="mailto:ouharrioutman@gmail.com">Ouharri Outman</a>
 * @version 1.0
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@CacheConfig(cacheNames = "Users")
public class UserServiceImpl extends _ServiceImp<UUID, UserRequest, UserResponses, User, UserRepository, UserMapper> implements UserService {
    private final Faker faker;
    private final Random random;
    private final ModelMapper modelMapper;
    private final FakeValuesService fakeValuesService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * Saves a user to the database.
     *
     * @param user The user to save.
     * @return The saved user.
     * @throws ResourceNotCreatedException If the user could not be created.
     */
    public User saveUser(User user) {
        try {
            return repository.save(user);
        } catch (Exception e) {
            throw new ResourceNotCreatedException("User not created");
        }
    }

    /**
     * Retrieves the currently authenticated user.
     * <p>
     * This method fetches the current user's details from the Spring Security context.
     * It performs checks to ensure that there is an authenticated user and that the user
     * is not an instance of {@link AnonymousAuthenticationToken}.
     * </p>
     */
    public UserGeneratedResponses getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken)
            throw new NoAuthenticateUser("User not authenticated");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = findByUsername(userDetails.getUsername());
        return modelMapper.map(user, UserGeneratedResponses.class);
    }

    /**
     * Asynchronously generates a batch of users.
     *
     * @param count The number of users to generate.
     * @return A CompletableFuture containing the list of generated users.
     */
    public List<UserGeneratedResponses> generateUsers(int count) {
        int batchSize = 1000;
        int numThreads = Math.max(1, Math.min(count / batchSize, Runtime.getRuntime().availableProcessors()));

        List<CompletableFuture<List<UserGeneratedResponses>>> futures = IntStream.range(0, count)
                .boxed()
                .collect(Collectors.groupingByConcurrent(i -> i % numThreads))
                .values()
                .parallelStream()
                .map(batch -> CompletableFuture.supplyAsync(() -> generateBatch(batch), executor))
                .toList();

        try {
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApplyAsync(
                            v -> futures.parallelStream()
                                    .flatMap(future -> future.join().stream())
                                    .collect(Collectors.toList()),
                            executor
                    ).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error generating users", e);
            throw new ResourceNotCreatedException("Error generating users");
        }
    }

    /**
     * Génère un lot d'utilisateurs.
     *
     * @param batch La liste des index des utilisateurs à générer.
     * @return Une liste d'utilisateurs générés.
     */
    public List<UserGeneratedResponses> generateBatch(List<Integer> batch) {
        return batch.parallelStream()
                .map(i -> UserGeneratedResponses.builder()
                        .firstname(faker.name().firstName())
                        .lastname(faker.name().lastName())
                        .birthDate(faker.date().birthday())
                        .city(faker.address().city())
                        .country(faker.address().country())
                        .avatar(faker.avatar().image())
                        .company(faker.company().name())
                        .jobPosition(faker.job().position())
                        .mobile(faker.phoneNumber().phoneNumber())
                        .username(fakeValuesService.bothify("????##"))
                        .email(faker.internet().emailAddress())
                        .password(faker.internet().password(6, 10))
                        .role(random.nextBoolean() ? Role.ROLE : Role.ADMIN)
                        .gender(random.nextBoolean() ? Gender.MALE : Gender.FEMALE)
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * Charge et traite de manière asynchrone un lot d'utilisateurs à partir d'un fichier.
     *
     * @param file Le fichier contenant les données des utilisateurs.
     * @return Un objet représentant le résumé de l'importation.
     */
    @Transactional
    public UploadSummaryResponseDTO uploadBatch(MultipartFile file) {
        try {
            List<UserResponses> users = new ObjectMapper().readValue(file.getInputStream(), new TypeReference<>() {
            });

            int batchSize = 100;
            int totalRecords = users.size();
            AtomicInteger successfullyImported = new AtomicInteger();

            IntStream.range(0, totalRecords)
                    .boxed()
                    .collect(Collectors.groupingBy(i -> i / batchSize))
                    .values()
                    .parallelStream()
                    .forEach(batchIndexes -> {
                        List<UserResponses> batch = batchIndexes.stream()
                                .map(users::get)
                                .toList();

                        Set<String> emailsToImport = batch.parallelStream().map(UserResponses::getEmail).collect(Collectors.toSet());
                        Set<String> usernamesToImport = batch.parallelStream().map(UserResponses::getUsername).collect(Collectors.toSet());

                        Set<String> existingEmails = repository.findAllByEmailIn(emailsToImport).parallelStream().map(User::getEmail).collect(Collectors.toSet());
                        Set<String> existingUsernames = repository.findAllByUsernameIn(usernamesToImport).parallelStream().map(User::getUsername).collect(Collectors.toSet());

                        List<User> usersToSave = batch.parallelStream()
                                .filter(user -> !existingEmails.contains(user.getEmail()) && !existingUsernames.contains(user.getUsername()))
                                .map(userResponse -> {
                                    User user = mapper.toEntityFromResponse(userResponse);
                                    user.setEnabled(true);
                                    user.setAccountNonLocked(true);
                                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                                    return user;
                                })
                                .collect(Collectors.toList());

                        try {
                            List<User> savedUsers = repository.saveAll(usersToSave);
                            successfullyImported.addAndGet(savedUsers.size());
                        } catch (DataIntegrityViolationException e) {
                            log.info("user whit email or userName already exist");
                        }
                    });

            int failedToImport = totalRecords - successfullyImported.get();

            return UploadSummaryResponseDTO.builder()
                    .totalRecords(totalRecords)
                    .successfullyImported(successfullyImported.get())
                    .failedToImport(failedToImport)
                    .build();
        } catch (Exception e) {
            throw new ResourceNotCreatedException(e.getMessage());
        }
    }


    /**
     * Changes the role of a user.
     *
     * @param changeRoleRequest The request containing the user and the new role.
     * @return The updated user response.
     * @throws ResourceNotFoundException   If the user is not found.
     * @throws ResourceNotCreatedException If the user's role cannot be updated.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserResponses changeRole(ChangeRoleRequest changeRoleRequest) {
        try {
            User user = mapper.toEntityFromResponse(changeRoleRequest.user());
            user = this.findByEmail(user.getEmail());
            user.setRole(changeRoleRequest.role());
            return mapper.toResponse(repository.save(user));
        } catch (Exception e) {
            throw new ResourceNotCreatedException("User Role not updated");
        }
    }


    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user to retrieve.
     * @return Optional containing the user if found, otherwise empty.
     */
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by username.
     *
     * @param username The email of the user to retrieve.
     * @return Optional containing the user if found, otherwise empty.
     */
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by username or email.
     *
     * @param username The email of the user to retrieve.
     * @return Optional containing the user if found, otherwise empty.
     */
    public User findByUsernameOrEmail(String username) {
        return repository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by ID.
     *
     * @param username The ID of the user to retrieve.
     * @return Optional containing the user if found, otherwise empty.
     */
    public Optional<User> findById(UUID username) {
        return repository.findById(username);
    }

    /**
     * Changes the password of the currently logged-in user.
     *
     * @param request       The change password request.
     * @param connectedUser The principal representing the currently connected user.
     * @throws IllegalStateException If the current password is incorrect, or if the new passwords do not match.
     */
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalStateException("Passwords are not the same");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        repository.save(user);
    }

    /**
     * Updates the status of the specified user to offline.
     *
     * @param user The user to update.
     */
    public void disconnect(User user) {
        if (user != null) {
            user.setStatus(UserStatus.OFFLINE);
            repository.save(user);
        }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return ResponseEntity containing the user's details if found, otherwise returns a 404 Not Found response.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserResponses getUserByUsername(String username) {
        return mapper.toResponse(repository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    /**
     * Saves a new user token to the database.
     *
     * @param user     User for whom the token is generated
     * @param jwtToken JWT token to be saved
     */
    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revokes all valid tokens for a user by marking them as expired and revoked.
     *
     * @param user User for whom tokens are revoked
     */
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }
}