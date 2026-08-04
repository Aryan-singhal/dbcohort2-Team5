package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.*;
import io.micrometer.core.annotation.Timed;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReconciliationEngine {

        @Timed(value = "reconciliation.duration", description = "Wall time of reconcile()", percentiles = { 0.5, 0.95,
                        0.99 }, histogram = true)
        public List<ReconResult> reconcile(List<TradeType> internal,
                        List<TradeType> external,
                        ReconciliationRule rule) {
                if (internal == null || internal.isEmpty())
                        return List.of();
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

                List<CompletableFuture<List<ReconResult>>> futures = internalByCp.entrySet()
                                .stream()
                                .map(entry -> CompletableFuture.supplyAsync(() -> reconcile(
                                                entry.getValue(),
                                                externalByCp.getOrDefault(entry.getKey(), List.of()),
                                                rule)))
                                .toList();

                return CompletableFuture
                                .allOf(futures.toArray(new CompletableFuture[0]))
                                .thenApply(v -> futures.stream()
                                                .flatMap(future -> future.join().stream())
                                                .toList());
        }

        private ReconResult matchOne(TradeType internal, TradeType external, ReconciliationRule rule) {
                String ref = internal.tradeRef().value();
                if (external == null) {
                        return ReconResult.breakResult(ref, "MISSING_EXTERNAL",
                                        "no external trade found for " + ref);
                }
                BigDecimal[] in = priceQty(internal);
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
                return switch (t) {
                        case EquityTrade equity -> new BigDecimal[] { equity.price(), equity.quantity() };
                        case FXTrade fx -> new BigDecimal[] { fx.notional().amount(), BigDecimal.ONE };
                        case BondTrade bond -> new BigDecimal[] { bond.faceValue(), BigDecimal.ONE };
                        case DerivativeTrade derivative ->
                                new BigDecimal[] { derivative.strike(), derivative.quantity() };
                };
        }
}