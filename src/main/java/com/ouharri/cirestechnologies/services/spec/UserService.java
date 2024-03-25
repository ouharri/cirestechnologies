package com.ouharri.cirestechnologies.services.spec;

import com.ouharri.cirestechnologies.model.dto.requests.ChangePasswordRequest;
import com.ouharri.cirestechnologies.model.dto.requests.ChangeRoleRequest;
import com.ouharri.cirestechnologies.model.dto.requests.UserRequest;
import com.ouharri.cirestechnologies.model.dto.responses.UploadSummaryResponseDTO;
import com.ouharri.cirestechnologies.model.dto.responses.UserGeneratedResponses;
import com.ouharri.cirestechnologies.model.dto.responses.UserResponses;
import com.ouharri.cirestechnologies.model.entities.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing User entities.
 *
 * <p>This interface provides methods for managing user-related operations such as
 * retrieval, creation, modification, and deletion of users.</p>
 *
 * @author <a href="mailto:ouharri.outman@gmail.com">ouharri</a>
 */
public interface UserService extends _Service<UUID, UserRequest, UserResponses> {

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id The unique identifier of the user.
     * @return An Optional containing the user, or an empty Optional if not found.
     */
    Optional<User> findById(UUID id);

    /**
     * Changes the role of a user.
     *
     * @param changeRoleRequest The request containing the user's ID and new role.
     * @return The response containing the updated user details.
     */
    UserResponses changeRole(ChangeRoleRequest changeRoleRequest);

    /**
     * Retrieves the currently authenticated user.
     *
     * <p>This method fetches the current user's details from the Spring Security context.
     * It performs checks to ensure that there is an authenticated user and that the user
     * is not an instance of {@link AnonymousAuthenticationToken}.</p>
     *
     * @return The response containing the details of the current user.
     */
    UserGeneratedResponses getCurrentUser();

    /**
     * Generates a specified number of users asynchronously.
     *
     * @param count The number of users to generate.
     * @return A CompletableFuture containing a list of generated users.
     */
    List<UserGeneratedResponses> generateUsers(int count);

    /**
     * Generates users for the specified batch.
     *
     * @param batch The batch of user indices to generate.
     * @return A list of generated users.
     */
    List<UserGeneratedResponses> generateBatch(List<Integer> batch);

    /**
     * Uploads a batch of users from a multipart file asynchronously.
     *
     * @param file The multipart file containing user data.
     * @return A CompletableFuture containing the upload summary.
     */
    UploadSummaryResponseDTO uploadBatch(MultipartFile file);

    /**
     * Saves a user to the database.
     *
     * @param user The user to save.
     * @return The saved user.
     */
    User saveUser(User user);

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user to retrieve.
     * @return Optional containing the user if found, otherwise empty.
     */
    User findByEmail(String email);

    /**
     * Changes the password for the user identified by the connected user principal.
     *
     * @param request       The request containing the old and new passwords.
     * @param connectedUser The principal representing the connected user.
     */
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    /**
     * Revokes all valid tokens for a user by marking them as expired and revoked.
     *
     * @param user User for whom tokens are revoked.
     */
    void revokeAllUserTokens(User user);

    /**
     * Saves a new user token to the database.
     *
     * @param user     User for whom the token is generated.
     * @param jwtToken JWT token to be saved.
     */
    void saveUserToken(User user, String jwtToken);

    /**
     * Updates the status of the specified user to offline.
     *
     * @param user The user to update.
     */
    void disconnect(User user);

    UserResponses getUserByUsername(String username);

    /**
     * Retrieves a user by username.
     *
     * @param username The email of the user to retrieve.
     * @return Optional containing the user if found, otherwise empty.
     */
    User findByUsernameOrEmail(String username);
}
