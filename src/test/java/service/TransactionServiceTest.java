package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.organize.dto.TransactionDTO;
import com.organize.dto.TransactionResponseDTO;
import com.organize.model.Establishment;
import com.organize.model.Transaction;
import com.organize.model.TransactionStatus;
import com.organize.model.User;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.TransactionsRepository;
import com.organize.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class TransactionServiceTest {
    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private EstablishmentRepository establishmentRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID adminId;
    private Establishment establishment;
    private UUID establishmentId;
    private List<Transaction> mockList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminId = UUID.randomUUID();

        User owner = new User();
        owner.setId(adminId);

        establishment = new Establishment();
        establishmentId = UUID.randomUUID();
        establishment.setId(establishmentId);
        establishment.setOwner(owner);

        Transaction t1 = new Transaction();
        t1.setEstablishmentId(establishmentId);
        t1.setDescription("desc1");
        t1.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        Transaction t2 = new Transaction();
        t2.setEstablishmentId(establishmentId);
        t2.setDescription("desc2");
        t2.setCreatedAt(LocalDateTime.now().minusMinutes(2));

        mockList = List.of(t1, t2);
    }

    @Test
    void shouldReturnAllEstablishmentTransactions() {
        when(establishmentRepository.findByOwnerId(adminId))
                .thenReturn(Optional.of(establishment));
        when(transactionsRepository.findByEstablishmentIdOrderByTransactionDateDescCreatedAtDesc(establishmentId))
                .thenReturn(mockList);

        List<TransactionResponseDTO> result = transactionService.getAllEstablishmentTransaction(adminId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(dto -> "desc1".equals(dto.description())));
        assertTrue(result.stream().anyMatch(dto -> "desc2".equals(dto.description())));

        verify(establishmentRepository, times(1)).findByOwnerId(adminId);
        verify(transactionsRepository, times(1))
                .findByEstablishmentIdOrderByTransactionDateDescCreatedAtDesc(establishmentId);
        verifyNoMoreInteractions(establishmentRepository, transactionsRepository);
    }

    @Test
    void shouldReturnTransactionsOnRangeDateTime() {
        when(establishmentRepository.findByOwnerId(adminId))
                .thenReturn(Optional.of(establishment));
        when(transactionsRepository.findByEstablishmentIdAndTransactionDateBetween(establishmentId, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)))
                .thenReturn(mockList);

        List<TransactionResponseDTO> result = transactionService.getEstablishmentTransactionsOnRangeDate(adminId, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.stream().anyMatch(dto -> "desc1".equals(dto.description())));
        assertTrue(result.stream().anyMatch(dto -> "desc2".equals(dto.description())));

        verify(establishmentRepository, times(1)).findByOwnerId(adminId);
        verify(transactionsRepository, times(1))
                .findByEstablishmentIdAndTransactionDateBetween(establishmentId, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        verifyNoMoreInteractions(establishmentRepository, transactionsRepository);
    }

    @Test
    void shouldCreateTransactionForAdmin() {
        // given
        when(establishmentRepository.findByOwnerId(adminId))
                .thenReturn(Optional.of(establishment));

        TransactionDTO dto = new TransactionDTO(
                UUID.randomUUID(),
                establishmentId,
                "teste",
                10000,
                LocalDate.now().minusDays(1),
                TransactionStatus.PAID
        );

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(UUID.randomUUID());
        savedTransaction.setAppointmentId(establishmentId);
        savedTransaction.setDescription(dto.description());
        savedTransaction.setAmountCents(dto.amount_cents());
        savedTransaction.setTransactionDate(dto.transaction_date());
        savedTransaction.setStatus(dto.status());

        when(transactionsRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        TransactionResponseDTO result = transactionService.createTransactionForAdmin(adminId, dto);

        assertNotNull(result);
        assertEquals(savedTransaction.getDescription(), result.description());
        assertEquals(savedTransaction.getAmountCents(), result.amount_cents());
        assertEquals(savedTransaction.getStatus(), result.status());

        verify(establishmentRepository, times(1)).findByOwnerId(adminId);
        verify(transactionsRepository, times(1)).save(any(Transaction.class));
        verifyNoMoreInteractions(establishmentRepository, transactionsRepository);
    }

    @Test
    void shouldUpdateTransactionStatus() {
        UUID transactionId = UUID.randomUUID();

        when(establishmentRepository.findByOwnerId(adminId))
                .thenReturn(Optional.of(establishment));

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setEstablishmentId(establishmentId);
        existingTransaction.setDescription("Old transaction");
        existingTransaction.setStatus(TransactionStatus.PENDING);

        when(transactionsRepository.findById(transactionId))
                .thenReturn(Optional.of(existingTransaction));

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(transactionId);
        updatedTransaction.setEstablishmentId(establishmentId);
        updatedTransaction.setDescription("Old transaction");
        updatedTransaction.setStatus(TransactionStatus.PAID);

        when(transactionsRepository.save(any(Transaction.class)))
                .thenReturn(updatedTransaction);

        TransactionResponseDTO result = transactionService.updateTransactionStatus(
                adminId, transactionId, TransactionStatus.PAID
        );

        assertNotNull(result);
        assertEquals(TransactionStatus.PAID, result.status());

        verify(establishmentRepository, times(1)).findByOwnerId(adminId);
        verify(transactionsRepository, times(1)).findById(transactionId);
        verify(transactionsRepository, times(1)).save(any(Transaction.class));
        verifyNoMoreInteractions(establishmentRepository, transactionsRepository);
    }

    @Test
    void shouldThrowWhenTransactionDoesNotBelongToEstablishment() {
        UUID transactionId = UUID.randomUUID();

        when(establishmentRepository.findByOwnerId(adminId))
                .thenReturn(Optional.of(establishment));

        Transaction otherTransaction = new Transaction();
        otherTransaction.setId(transactionId);
        otherTransaction.setEstablishmentId(UUID.randomUUID());
        otherTransaction.setStatus(TransactionStatus.PENDING);

        when(transactionsRepository.findById(transactionId))
                .thenReturn(Optional.of(otherTransaction));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransactionStatus(adminId, transactionId, TransactionStatus.PAID);
        });

        assertEquals("Acesso negado: Transaction não pertence ao seu estabelecimento", ex.getMessage());

        verify(establishmentRepository, times(1)).findByOwnerId(adminId);
        verify(transactionsRepository, times(1)).findById(transactionId);
        verify(transactionsRepository, never()).save(any());
    }
}
