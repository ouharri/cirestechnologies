package com.ouharri.cirestechnologies.repositories;

import com.ouharri.cirestechnologies.model.entities.User;
import com.ouharri.cirestechnologies.model.enums.Role;
import com.ouharri.cirestechnologies.model.enums.UserStatus;
import io.micrometer.common.lang.NonNullApi;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repository interface for accessing and managing {@link User} entities in the database.
 *
 * <p>This repository provides methods to interact with user entities, such as finding users by
 * email or ID, and retrieving users based on their status.</p>
 *
 * @author <a href="mailto:ouharri.outman@gmail.com">ouharri</a>
 * @version 1.0
 */
@Repository
@NonNullApi
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return An optional containing the user, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user.
     * @return An optional containing the user, or empty if not found.
     */
    Optional<User> findById(UUID id);

    /**
     * Retrieves a page of all users.
     *
     * @param pageable Pageable object for pagination.
     * @return A page containing all users.
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Retrieves a page of users with a specific status.
     *
     * @param status   The status of the users to retrieve.
     * @param pageable Pageable object for pagination.
     * @return A page of users with the specified status.
     */
    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

    /**
     * Retrieves a page of users with a specific role.
     *
     * @param role     The role of the users to retrieve.
     * @param pageable Pageable object for pagination.
     * @return A page of users with the specified role.
     */
    Page<User> findAllByRole(Role role, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmailOrUsername(@Email(message = "Email was not provided") @Size(max = 80, message = "Email is too long") String email, String username);

    Collection<User> findAllByEmailIn(Set<String> emailsToImport);

    Collection<User> findAllByUsernameIn(Set<String> usernamesToImport);

}
