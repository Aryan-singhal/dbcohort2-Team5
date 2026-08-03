package com.dbtraining.reconx.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ReconciliationIntegrationTest {

        @Autowired
    private InternalTradeRepository internalTradeRepo;

    @Autowired
    private ExternalTradeRepository externalTradeRepo;

    @Autowired
    private ReconResultRepository reconResultRepo;

    @Autowired
    private ReconciliationService reconciliationService;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("reconx")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void containerIsRunning() {
        // sanity: if this passes, Testcontainers and Spring Boot are wired correctly.
    }

        @Test
    void insertedTradesAreReconciledAndPersisted() {

        Trade internal = new Trade(
                "TRD-INT-1",
                "CP-1",
                "SAP.DE",
                new BigDecimal("100"),
                new BigDecimal("245.50"),
                LocalDate.now()
        );

        Trade external = new Trade(
                "TRD-INT-1",
                "CP-1",
                "SAP.DE",
                new BigDecimal("100"),
                new BigDecimal("245.50"),
                LocalDate.now()
        );

        internalTradeRepo.save(internal);
        externalTradeRepo.save(external);

        reconciliationService.runRecon(
                internalTradeRepo.findAll(),
                externalTradeRepo.findAll(),
                ReconciliationRule.EXACT
        );

        List<ReconResult> persisted = reconResultRepo.findAll();

        assertThat(persisted).hasSize(1);
        assertThat(persisted.get(0).status())
                .isEqualTo(ReconResult.Status.MATCHED);
        assertThat(persisted.get(0).tradeRef())
                .isEqualTo("TRD-INT-1");
    }
}