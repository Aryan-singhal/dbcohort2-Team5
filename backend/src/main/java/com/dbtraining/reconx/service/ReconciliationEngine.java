package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TICKET-ADV040 / TICKET-ADV041 / TICKET-ADV042 — TDD: write the test FIRST,
 * then the impl.
 */
class ReconciliationEngineTest {

    private final ReconciliationEngine engine = new ReconciliationEngine();

    @Test
    void testReconcile_exactMatch_returnsMatched() {
        EquityTrade internal = equity("EQU-20260603-0001", "100.00", "1000");
        EquityTrade external = equity("EQU-20260603-0001", "100.00", "1000");

        List<ReconResult> out = engine.reconcile(List.of(internal), List.of(external), ReconciliationRule.EXACT);

        assertThat(out).hasSize(1);
        assertThat(out.get(0).status()).isEqualTo(ReconResult.Status.MATCHED);
    }

    private EquityTrade equity(String ref, String price, String qty) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .price(new BigDecimal(price))
                .quantity(new BigDecimal(qty))
                .currency("EUR").side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}
    @Timed(value = "reconciliation.duration", description = "Wall time of reconcile()",
           percentiles = {0.5, 0.95, 0.99}, histogram = true)
    public List<ReconResult> reconcile(List<TradeType> internal,
                                       List<TradeType> external,
                                       ReconciliationRule rule) {
        if (internal == null || internal.isEmpty()) return List.of();
        List<TradeType> ext = external == null ? List.of() : external;
        Map<String, TradeType> externalByRef = ext.stream()
                .collect(Collectors.toMap(
                        t -> t.tradeRef().value(),
                        Function.identity(),
                        (a, b) -> a));
        return internal.parallelStream()
                .map(in -> matchOne(in, externalByRef.get(in.tradeRef().value()), rule))
                .toList();
    }


    /**
     * TICKET-ADV037 — split by counterparty, reconcile each batch concurrently,
     * combine into a single result list. Caller passes one external feed per
     * counterparty (typical real-world shape).
     */
    public CompletableFuture<List<ReconResult>> reconcileByCounterparty(
            Map<Long, List<TradeType>> internalByCp,
            Map<Long, List<TradeType>> externalByCp,
            ReconciliationRule rule) {
        // TODO(TICKET-ADV037): for each counterparty key in internalByCp launch a
        //   CompletableFuture.supplyAsync(() -> reconcile(...)). Combine via
        //   CompletableFuture.allOf(...).thenApply(v -> futures.stream()
        //       .flatMap(f -> f.join().stream()).toList()).
        throw new UnsupportedOperationException("TICKET-ADV037");
    }

    private ReconResult matchOne(TradeType internal, TradeType external, ReconciliationRule rule) {
        String ref = internal.tradeRef().value();
        if (external == null) {
            return ReconResult.breakResult(ref, "MISSING_EXTERNAL",
                    "no external trade found for " + ref);
        }
        BigDecimal[] in  = priceQty(internal);
        BigDecimal[] out = priceQty(external);
        if (rule.matches(in[0], in[1], out[0], out[1])) {
            return ReconResult.matched(ref);
        }
        return ReconResult.breakResult(ref, "VALUE_MISMATCH",
                "internal price=%s qty=%s vs external price=%s qty=%s"
                        .formatted(in[0], in[1], out[0], out[1]));
    }

    /** TICKET-ADV018 — exhaustive switch over the sealed hierarchy. */
    private BigDecimal[] priceQty(TradeType t) {
        // TODO(TICKET-ADV018): switch over the sealed TradeType hierarchy
        //   (EquityTrade, FXTrade, BondTrade, DerivativeTrade) and return a
        //   BigDecimal[]{price, qty}. The compiler enforces exhaustiveness —
        //   omit a case and the build fails.
        throw new UnsupportedOperationException("TICKET-ADV018");
    }
}
