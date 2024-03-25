package com.ouharri.services.impl;

import com.ouharri.cirestechnologies.exceptions.NoAuthenticateUser;
import com.ouharri.cirestechnologies.exceptions.ResourceNotCreatedException;
import com.ouharri.cirestechnologies.model.entities.Token;
import com.ouharri.cirestechnologies.model.entities.User;
import com.ouharri.cirestechnologies.model.enums.Gender;
import com.ouharri.cirestechnologies.model.enums.Role;
import com.ouharri.cirestechnologies.repositories.TokenRepository;
import com.ouharri.cirestechnologies.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setGender(Gender.MALE);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Test getCurrentUser method when user is not authenticated")
    public void testGetCurrentUserNotAuthenticated() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        assertThrows(NoAuthenticateUser.class, () -> userService.getCurrentUser());
    }

    @Test
    void uploadBatch_InvalidFile_ThrowResourceNotCreatedException() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Invalid file content".getBytes());

        // Act & Assert
        assertThrows(ResourceNotCreatedException.class, () -> userService.uploadBatch(file));
    }

    @Test
    @DisplayName("Test saveUserToken method")
    public void testSaveUserToken() {
        User user = new User();
        user.setEmail("test@example.com");
        String jwtToken = "jwt_token";

        userService.saveUserToken(user, jwtToken);

        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    @DisplayName("Test revokeAllUserTokens method")
    public void testRevokeAllUserTokens() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(List.of(new Token(), new Token(), new Token()));

        userService.revokeAllUserTokens(user);

        verify(tokenRepository, times(1)).saveAll(anyList());
    }
}

