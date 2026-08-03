package com.dbtraining.reconx.service;

import com.dbtraining.reconx.repository.entity.Counterparty;
import com.dbtraining.reconx.repository.entity.Trade;
import com.dbtraining.reconx.repository.CounterpartyRepository;
import com.dbtraining.reconx.repository.TradeRepository;

import java.util.NoSuchElementException;

public class TradeLookupService {

    private final TradeRepository tradeRepo;
    private final CounterpartyRepository cpRepo;

    public TradeLookupService(TradeRepository tradeRepo, CounterpartyRepository cpRepo) {
        this.tradeRepo = tradeRepo;
        this.cpRepo = cpRepo;
    }

    public Counterparty counterpartyForTradeRef(String tradeRef) {
        return tradeRepo.findByRef(tradeRef)
                .map(trade -> trade.getCounterparty().getId()) 
// (Note: If your entity uses object relations, it might be .map(trade -> trade.getCounterparty().getId()) instead)
                .flatMap(cpRepo::findById)
                .orElseThrow(() -> new NoSuchElementException(
                        "No counterparty resolvable for trade " + tradeRef));
    }
}