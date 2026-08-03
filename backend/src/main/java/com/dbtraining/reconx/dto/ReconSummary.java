package com.dbtraining.reconx.dto;

public record ReconSummary(long total, long matched, long broken) {
    
    // The Collector uses this Builder class to keep running totals
    public static class Builder {
        public long total = 0;
        public long matched = 0;
        public long broken = 0;
    }
}