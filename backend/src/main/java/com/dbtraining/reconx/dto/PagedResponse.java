package com.dbtraining.reconx.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Tiny wrapper that flattens Spring Data Page<T> into a
 * JSON-friendly shape. Avoids exposing Spring Data internals to clients.
 */
public record PagedResponse<T>(

        List<T> items,

        int page,

        int size,

        long totalElements,

        int totalPages

) {

    public static <S, T> PagedResponse<T> from(Page<S> src,
                                               Function<S, T> mapper) {

        return new PagedResponse<>(

                src.getContent()
                        .stream()
                        .map(mapper)
                        .toList(),

                src.getNumber(),
                src.getSize(),
                src.getTotalElements(),
                src.getTotalPages()
        );
    }
}