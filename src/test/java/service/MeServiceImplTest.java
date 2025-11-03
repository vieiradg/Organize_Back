package service;

import com.organize.dto.ChangePasswordRequestDTO;
import com.organize.dto.ProfileResponseDTO;
import com.organize.dto.UpdateProfileRequestDTO;
import com.organize.model.User;
import com.organize.repository.UserRepository;
import com.organize.service.MeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MeServiceImpl meService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setPhone("999999999");
        user.setPassword("encodedPassword");
    }

    @Test
    void shouldReturnUserProfile_whenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ProfileResponseDTO result = meService.getUserProfile(userId);

        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getPhone(), result.phone());
    }

    @Test
    void shouldThrowException_whenUserNotFound_getUserProfile() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> meService.getUserProfile(userId));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void shouldUpdateUserProfile_whenValidData() {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO("Novo Nome", "novo@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(dto.email(), userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileResponseDTO result = meService.updateUserProfile(userId, dto);

        assertEquals(dto.name(), result.name());
        assertEquals(dto.email(), result.email());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowException_whenEmailAlreadyInUse() {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO("Novo Nome", "duplicado@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(dto.email(), userId)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> meService.updateUserProfile(userId, dto));

        assertEquals("O e-mail informado já está em uso.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenUserNotFound_updateProfile() {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO("Teste", "teste@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> meService.updateUserProfile(userId, dto));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void shouldNotUpdateBlankFields() {
        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO(" ", " ");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileResponseDTO result = meService.updateUserProfile(userId, dto);

        assertEquals(user.getName(), result.name());
        assertEquals(user.getEmail(), result.email());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldChangePassword_whenOldPasswordMatches() {
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("oldPass", "newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.newPassword())).thenReturn("encodedNewPass");

        meService.changeUserPassword(userId, dto);

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    void shouldThrowException_whenOldPasswordDoesNotMatch() {
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("wrongPass", "newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.oldPassword(), user.getPassword())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> meService.changeUserPassword(userId, dto));

        assertEquals("Senha antiga incorreta.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenUserNotFound_changePassword() {
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("old", "new");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> meService.changeUserPassword(userId, dto));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}
