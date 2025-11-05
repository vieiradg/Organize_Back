package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.organize.model.User;
import com.organize.repository.UserRepository;
import com.organize.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mustFindUserByEmail() {
        String email = "teste@email.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> userOptional = userService.findByEmail(email);

        assertTrue(userOptional.isPresent());
        assertEquals(email, userOptional.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void mustReturnEmptyWhenEmailNotFound() {
        String email = "naoexiste@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> userOptional = userService.findByEmail(email);

        assertFalse(userOptional.isPresent());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void mustSaveUser() {
        User user = new User();
        user.setEmail("user@email.com");
        when(userRepository.save(user)).thenReturn(user);

        User salvo = userService.save(user);

        assertNotNull(salvo);
        assertEquals("user@email.com", salvo.getEmail());
        verify(userRepository, times(1)).save(user);
    }
}
