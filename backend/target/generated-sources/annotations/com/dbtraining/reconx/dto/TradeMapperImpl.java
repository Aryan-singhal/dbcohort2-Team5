package com.dbtraining.reconx.dto;

import com.dbtraining.reconx.repository.entity.Counterparty;
import com.dbtraining.reconx.repository.entity.Instrument;
import com.dbtraining.reconx.repository.entity.Trade;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-04T10:48:41+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 25.0.3 (Microsoft)"
)
@Component
public class TradeMapperImpl implements TradeMapper {

    @Override
    public TradeResponse toResponse(Trade trade) {
        if ( trade == null ) {
            return null;
        }

        Long instrumentId = null;
        String instrumentSymbol = null;
        Long counterpartyId = null;
        String counterpartyName = null;
        Long id = null;
        String tradeRef = null;
        BigDecimal quantity = null;
        BigDecimal price = null;
        LocalDate tradeDate = null;
        String status = null;
        Instant createdAt = null;
        Instant modifiedAt = null;

        instrumentId = tradeInstrumentId( trade );
        instrumentSymbol = tradeInstrumentSymbol( trade );
        counterpartyId = tradeCounterpartyId( trade );
        counterpartyName = tradeCounterpartyName( trade );
        id = trade.getId();
        tradeRef = trade.getTradeRef();
        quantity = trade.getQuantity();
        price = trade.getPrice();
        tradeDate = trade.getTradeDate();
        status = trade.getStatus();
        createdAt = trade.getCreatedAt();
        modifiedAt = trade.getModifiedAt();

        TradeResponse tradeResponse = new TradeResponse( id, tradeRef, counterpartyId, counterpartyName, instrumentId, instrumentSymbol, quantity, price, tradeDate, status, createdAt, modifiedAt );

        return tradeResponse;
    }

    @Override
    public Trade toEntity(TradeRequest req) {
        if ( req == null ) {
            return null;
        }

        Trade trade = new Trade();

        trade.setTradeRef( req.tradeRef() );
        trade.setAssetClass( req.assetClass() );
        trade.setSide( req.side() );
        trade.setQuantity( req.quantity() );
        trade.setPrice( req.price() );
        trade.setTradeDate( req.tradeDate() );

        return trade;
    }

    private Long tradeInstrumentId(Trade trade) {
        Instrument instrument = trade.getInstrument();
        if ( instrument == null ) {
            return null;
        }
        return instrument.getId();
    }

    private String tradeInstrumentSymbol(Trade trade) {
        Instrument instrument = trade.getInstrument();
        if ( instrument == null ) {
            return null;
        }
        return instrument.getSymbol();
    }

    private Long tradeCounterpartyId(Trade trade) {
        Counterparty counterparty = trade.getCounterparty();
        if ( counterparty == null ) {
            return null;
        }
        return counterparty.getId();
    }

    private String tradeCounterpartyName(Trade trade) {
        Counterparty counterparty = trade.getCounterparty();
        if ( counterparty == null ) {
            return null;
        }
        return counterparty.getName();
    }
}
