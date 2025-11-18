package com.organize.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.organize.dto.*;
import com.organize.model.GeminiReport;
import com.organize.model.User;
import com.organize.repository.GeminiReportRepository;
import com.organize.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GeminiReportService {

    private final Client client;
    private final FinanceDashboardService financeDashboardService;
    private final DashboardService dashboardService;
    private final TransactionService transactionService;
    private final GeminiReportRepository reportRepository;
    private final CryptoUtils cryptoUtils;

    public GeminiReportService(
            FinanceDashboardService financeDashboardService,
            DashboardService dashboardService,
            TransactionService transactionService,
            GeminiReportRepository reportRepository,
            CryptoUtils cryptoUtils,
            @Value("${gemini.api.key}") String geminiApiKey
    ) {
        this.client = Client.builder()
                .apiKey(geminiApiKey)
                .build();

        this.financeDashboardService = financeDashboardService;
        this.dashboardService = dashboardService;
        this.transactionService = transactionService;
        this.reportRepository = reportRepository;
        this.cryptoUtils = cryptoUtils;
    }

    public String generateMonthlyReport(UUID adminId) {
        FinanceDashboardDTO financeData = financeDashboardService.getFinanceDashboard(adminId);
        DashboardDTO dashboardData = dashboardService.getDashboardData(adminId);

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<TransactionResponseDTO> transactions =
                transactionService.getEstablishmentTransactionsOnRangeDate(adminId, startOfMonth, endOfMonth);

        String prompt = buildPrompt(financeData, dashboardData, transactions);

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );

        String reportText = response.text();

        GeminiReport report = new GeminiReport();
        report.setAdminId(adminId);
        report.setReportMonth(startOfMonth);
        report.setEncryptedContent(cryptoUtils.encrypt(reportText));
        reportRepository.save(report);

        return reportText;
    }

    public String getReport(UUID adminId, LocalDate month) {
        Optional<GeminiReport> optionalReport = reportRepository.findByAdminIdAndReportMonth(adminId, month);
        if (optionalReport.isEmpty()) {
            throw new RuntimeException("Relatório não encontrado para o mês especificado.");
        }
        return cryptoUtils.decrypt(optionalReport.get().getEncryptedContent());
    }

    public List<GeminiReportResponseDTO> listReports(UUID adminId) {
        return reportRepository.findByAdminIdOrderByReportMonthDesc(adminId)
                .stream()
                .map(r -> new GeminiReportResponseDTO(
                        r.getId(),
                        r.getAdminId(),
                        r.getReportMonth(),
                        r.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    public GeminiReportDetailDTO getReportDetails(UUID reportId) {
        GeminiReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado."));
        return new GeminiReportDetailDTO(
                report.getId(),
                report.getAdminId(),
                report.getReportMonth(),
                cryptoUtils.decrypt(report.getEncryptedContent()),
                report.getCreatedAt().toString()
        );
    }

    private String buildPrompt(FinanceDashboardDTO finance, DashboardDTO dashboard, List<TransactionResponseDTO> transactions) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
        Gere um **relatório financeiro mensal completo** em **formato Markdown**, com linguagem natural, organizada e envolvente.
        Use **emojis** e **destaques em negrito** para tornar o texto mais visual. 
        Você deve se apresentar como **Otto**, assistente de IA financeiro da **Organize**.
        
        Estruture o relatório em seções como:
        - 📊 **Resumo Geral**
        - 💰 **Análise de Receitas e Despesas**
        - 🧠 **Insights Financeiros**
        - 💡 **Dicas e Sugestões Personalizadas**
        - 📅 **Atividades e Agendamentos**
        
        O texto deve ser **amigável, motivador** e soar como um **assistente financeiro inteligente e empático**.
        Baseie suas recomendações nos seguintes dados do usuário:
        """);

        sb.append("\n\n### 📈 Dados Financeiros do Mês\n");
        sb.append(String.format("- **Receita:** R$ %.2f\n", finance.monthlyRevenue() / 100.0));
        sb.append(String.format("- **Despesas:** R$ %.2f\n", finance.monthlyExpenses() / 100.0));
        sb.append(String.format("- **Lucro:** R$ %.2f\n", finance.monthlyProfit() / 100.0));
        sb.append(String.format("- **Crescimento de Receita:** %.2f%%\n", finance.revenueGrowthPercent()));
        sb.append(String.format("- **Crescimento de Lucro:** %.2f%%\n", finance.profitGrowthPercent()));
        sb.append(String.format("- **Média por Atendimento:** R$ %.2f\n", finance.averageRevenuePerAppointment() / 100.0));
        sb.append(String.format("- **Atendimentos Totais:** %d\n", finance.totalAppointments()));

        sb.append("\n\n### 🗓️ Dados de Agendamentos\n");
        if (dashboard.nextAppointment() != null) {
            sb.append(String.format("- **Próximo Agendamento:** %s (%s)\n",
                    dashboard.nextAppointment().startTime(),
                    dashboard.nextAppointment().serviceName()));
        } else {
            sb.append("- Nenhum agendamento futuro confirmado.\n");
        }

        sb.append("\n\n### 💸 Transações do Mês\n");
        if (transactions.isEmpty()) {
            sb.append("Nenhuma transação registrada neste mês.\n");
        } else {
            transactions.stream()
                    .limit(10)
                    .forEach(t -> {
                        String status = switch (t.status().toString().toUpperCase()) {
                            case "PAID" -> "✅ Pago";
                            case "PENDING" -> "🕓 Pendente";
                            case "CANCELED" -> "❌ Cancelado";
                            default -> t.status().toString();
                        };
                        sb.append(String.format(
                                "- **%s** — 💵 R$ %.2f → *%s*\n",
                                t.description(),
                                t.amount_cents() / 100.0,
                                status
                        ));
                    });
        }

        sb.append("\n\nGere o relatório com base nesses dados, destacando tendências e oferecendo conselhos personalizados.\n");
        return sb.toString();
    }

    public void deleteReport(UUID reportId) {
        GeminiReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));

        reportRepository.delete(report);
    }
}
