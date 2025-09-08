package com.organize.controller;

import com.organize.dto.ClientDataRequestDTO;
import com.organize.dto.ClientDataResponseDTO;
import com.organize.model.ClientData;
import com.organize.model.User;
import com.organize.service.ClientDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/establishments/{establishmentId}/clients")
@Tag(name = "Client Data", description = "Gerenciamento de fichas de clientes dos estabelecimentos")
public class ClientDataController {

    private final ClientDataService clientDataService;

    public ClientDataController(ClientDataService clientDataService) {
        this.clientDataService = clientDataService;
    }

    @PostMapping
    @Operation(summary = "Criar ficha de cliente", description = "Cria uma nova ficha de cliente para um estabelecimento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ficha criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para este estabelecimento"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento ou cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Cliente já possui ficha neste estabelecimento")
    })
    public ResponseEntity<ClientDataResponseDTO> createClientData(
            @Parameter(description = "ID do estabelecimento") @PathVariable UUID establishmentId,
            @Parameter(description = "Dados da ficha do cliente") @RequestBody @Valid ClientDataRequestDTO requestDTO,
            @AuthenticationPrincipal User loggedUser) {
        try {
            ClientData clientData = clientDataService.createClientData(establishmentId, requestDTO, loggedUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ClientDataResponseDTO(clientData));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("já possui uma ficha")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("não tem permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar fichas de clientes", description = "Lista todas as fichas de clientes de um estabelecimento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de fichas retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para este estabelecimento"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado")
    })
    public ResponseEntity<List<ClientDataResponseDTO>> getClientsByEstablishment(
            @Parameter(description = "ID do estabelecimento") @PathVariable UUID establishmentId,
            @AuthenticationPrincipal User loggedUser) {
        try {
            List<ClientData> clientsData = clientDataService.getClientsByEstablishment(establishmentId, loggedUser);
            List<ClientDataResponseDTO> responseDTOs = clientsData.stream()
                    .map(ClientDataResponseDTO::new)
                    .toList();
            return ResponseEntity.ok(responseDTOs);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("não tem permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{clientDataId}")
    @Operation(summary = "Atualizar ficha de cliente", description = "Atualiza as informações de uma ficha de cliente específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficha atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para este estabelecimento"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento ou ficha não encontrada")
    })
    public ResponseEntity<ClientDataResponseDTO> updateClientData(
            @Parameter(description = "ID do estabelecimento") @PathVariable UUID establishmentId,
            @Parameter(description = "ID da ficha do cliente") @PathVariable UUID clientDataId,
            @Parameter(description = "Novos dados da ficha") @RequestBody @Valid ClientDataRequestDTO requestDTO,
            @AuthenticationPrincipal User loggedUser) {
        try {
            ClientData updatedClientData = clientDataService.updateClientData(establishmentId, clientDataId, requestDTO,
                    loggedUser);
            return ResponseEntity.ok(new ClientDataResponseDTO(updatedClientData));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("não tem permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{clientDataId}")
    @Operation(summary = "Excluir ficha de cliente", description = "Exclui uma ficha de cliente específica de um estabelecimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ficha excluída com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para este estabelecimento"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento ou ficha não encontrada")
    })
    public ResponseEntity<Void> deleteClientData(
            @Parameter(description = "ID do estabelecimento") @PathVariable UUID establishmentId,
            @Parameter(description = "ID da ficha do cliente") @PathVariable UUID clientDataId,
            @AuthenticationPrincipal User loggedUser) {
        try {
            clientDataService.deleteClientData(establishmentId, clientDataId, loggedUser);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("não tem permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}