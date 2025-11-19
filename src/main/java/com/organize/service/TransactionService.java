package com.organize.service;

import com.organize.dto.TransactionDTO;
import com.organize.dto.TransactionResponseDTO;
import com.organize.model.Appointment; 
import com.organize.model.Establishment;
import com.organize.model.Transaction;
import com.organize.model.TransactionStatus;
import com.organize.repository.AppointmentRepository; 
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; 
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionsRepository transactionsRepository;
    private final EstablishmentRepository establishmentRepository;
    private final AppointmentRepository appointmentRepository; 

    public TransactionService(TransactionsRepository transactionsRepository, 
                              EstablishmentRepository establishmentRepository,
                              AppointmentRepository appointmentRepository) { 
        this.transactionsRepository = transactionsRepository;
        this.establishmentRepository = establishmentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    private Establishment getAdminEstablishment(UUID adminId) {
        return establishmentRepository.findByOwnerId(adminId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado para admin: " + adminId));
    }

    public List<TransactionResponseDTO> getAllEstablishmentTransaction(UUID adminId) {
        Establishment est = getAdminEstablishment(adminId);

        return transactionsRepository.findByEstablishmentIdOrderByTransactionDateDescCreatedAtDesc(est.getId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getEstablishmentTransactionsOnRangeDate(UUID adminId, LocalDate startDate, LocalDate endDate) {
        Establishment est = getAdminEstablishment(adminId);

        return transactionsRepository.findByEstablishmentIdAndTransactionDateBetween(est.getId(), startDate, endDate)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO createTransactionForAdmin(UUID adminId, TransactionDTO dto) {
        Establishment est = getAdminEstablishment(adminId);

        Transaction transaction = new Transaction();
        transaction.setAppointmentId(dto.appointment_id());
        transaction.setDescription(dto.description());
        transaction.setAmountCents(dto.amount_cents());
        transaction.setTransactionDate(dto.transaction_date() != null ? dto.transaction_date() : LocalDate.now());
        transaction.setStatus(dto.status() != null ? dto.status() : TransactionStatus.PENDING);
        transaction.setEstablishmentId(est.getId());

        Transaction saved = transactionsRepository.save(transaction);
        return toResponseDTO(saved);
    }

    public TransactionResponseDTO updateTransactionStatus(UUID adminId, UUID transactionId, TransactionStatus status) {
        Establishment est = getAdminEstablishment(adminId);

        Transaction transaction = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction não encontrada"));

        if (!transaction.getEstablishmentId().equals(est.getId())) {
            throw new RuntimeException("Acesso negado: Transaction não pertence ao seu estabelecimento");
        }

        transaction.setStatus(status);
        Transaction saved = transactionsRepository.save(transaction);

        return toResponseDTO(saved);
    }

    private TransactionResponseDTO toResponseDTO(Transaction transaction) {
        String clientName = "Transação Manual"; 
        String description = transaction.getDescription(); 
        
        if (transaction.getAppointmentId() != null) {
            Optional<Appointment> appointment = appointmentRepository.findById(transaction.getAppointmentId());
            
            if (appointment.isPresent()) {
                Appointment app = appointment.get();
                
                if (app.getClient() != null) {
                    clientName = app.getClient().getName();
                }

         
                if (app.getService() != null) {
                    description = app.getService().getName();
                } else {
                    description = "Agendamento - Serviço não encontrado"; 
                }
                
            }
        }

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAppointmentId() != null ? transaction.getAppointmentId() : null,
                transaction.getEstablishmentId() != null ? transaction.getEstablishmentId() : null,
                description, 
                transaction.getAmountCents(),
                transaction.getTransactionDate(),
                transaction.getStatus(),
                clientName 
        );
    }
}