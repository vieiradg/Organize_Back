package service;

import com.organize.model.PasswordResetToken;
import com.organize.model.User;
import com.organize.repository.PasswordResetTokenRepository;
import com.organize.repository.UserRepository;
import com.organize.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mustCreateRefreshTokenWhenUserExists() {
        String email = "teste@email.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        authService.createPasswordResetTokenForUser(email);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken.getToken());
        assertEquals(user, savedToken.getUser());
    }

    @Test
    void mustThrowExceptionWhenUserDoesNotExistWhenCreatingToken() {
        String email = "naoexistente@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.createPasswordResetTokenForUser(email));
        verify(passwordResetTokenRepository, never()).save(any());
    }

    @Test
    void mustResetPasswordWhenTokenIsValid() {
        String token = UUID.randomUUID().toString();
        String newPassword = "novasenha123";
        User user = new User();
        user.setPassword("senhaantiga123");

        PasswordResetToken validToken = new PasswordResetToken(token, user);
        validToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(validToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("senhaCodificada");

        authService.resetPassword(token, newPassword);

        assertEquals("senhaCodificada", user.getPassword());
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).delete(validToken);
    }

    @Test
    void MustThrowExceptionWhenTokenIsInvalid() {
        String token = "tokenExpirado";
        PasswordResetToken tokenExpired = new PasswordResetToken(token, new User());

        tokenExpired.setExpiryDate(LocalDateTime.now().minusMinutes(10));

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(tokenExpired));
        assertThrows(RuntimeException.class, () -> authService.resetPassword(token, "senhaCodificada"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void mustThrowExceptionWhenTokenIsExpired() {
        when(passwordResetTokenRepository.findByToken("invalido")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.resetPassword("invalido", "senha"));
    }
}
