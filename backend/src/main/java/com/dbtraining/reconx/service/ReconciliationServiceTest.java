package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.EquityTrade;
import com.dbtraining.reconx.model.ReconciliationRule;
import com.dbtraining.reconx.model.Side;
import com.dbtraining.reconx.model.TradeRef;
import com.dbtraining.reconx.repository.ReconResultRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReconciliationServiceTest {

    @Test
    void testReconcile_savesResultWithMatchedStatus() {

        ReconResultRepository repo = mock(ReconResultRepository.class);

        ReconciliationEngine engine = new ReconciliationEngine();

        ReconciliationService service =
                new ReconciliationService(engine, repo);

        EquityTrade internal =
                EquityTrade.builder()
                        .tradeRef(TradeRef.of("EQU-20260603-0001"))
                        .instrumentSymbol("SAP.DE")
                        .quantity(new BigDecimal("100"))
                        .price(new BigDecimal("10"))
                        .currency("EUR")
                        .side(Side.BUY)
                        .tradeDate(LocalDate.now())
                        .counterpartyId(1L)
                        .build();

        EquityTrade external =
                EquityTrade.builder()
                        .tradeRef(TradeRef.of("EQU-20260603-0001"))
                        .instrumentSymbol("SAP.DE")
                        .quantity(new BigDecimal("100"))
                        .price(new BigDecimal("10"))
                        .currency("EUR")
                        .side(Side.BUY)
                        .tradeDate(LocalDate.now())
                        .counterpartyId(1L)
                        .build();

        service.runRecon(
                List.of(internal),
                List.of(external),
                ReconciliationRule.EXACT);

        ArgumentCaptor<ReconResult> captor =
                ArgumentCaptor.forClass(ReconResult.class);

        verify(repo).save(captor.capture());

        assertThat(captor.getValue().tradeRef())
                .isEqualTo("EQU-20260603-0001");

        assertThat(captor.getValue().status())
                .isEqualTo(ReconResult.Status.MATCHED);
    }
}