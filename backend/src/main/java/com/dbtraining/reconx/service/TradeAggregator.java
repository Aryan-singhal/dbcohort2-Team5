package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.TradeEvent;
import com.dbtraining.reconx.repository.AuditLogRepository;
import com.dbtraining.reconx.repository.entity.AuditLogEntry;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TradeAggregator {

    private final AuditLogRepository auditRepo;

    public TradeAggregator(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    public Optional<JsonNode> rebuild(String tradeRef) {

        List<AuditLogEntry> events =
                auditRepo.findByTradeRefOrderByEventTimestampAsc(tradeRef);

        if (events.isEmpty()) {
            return Optional.empty();
        }

        JsonNode state = null;

        for (AuditLogEntry entry : events) {

            switch (TradeEvent.EventType.valueOf(entry.getOperation())) {

                case TRADE_CREATED:
                case TRADE_UPDATED:
                    state = entry.getAfterData();
                    break;

                case TRADE_CANCELLED:
                    state = null;
                    break;
            }
        }

        return Optional.ofNullable(state);
    }
}
