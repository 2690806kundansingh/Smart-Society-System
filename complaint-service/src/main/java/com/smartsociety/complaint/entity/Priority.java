package com.smartsociety.complaint.entity;

public enum Priority {
    HIGH(2),      // 2 Hours SLA
    MEDIUM(12),   // 12 Hours SLA
    LOW(48);      // 48 Hours SLA

    private final int slaHours;

    Priority(int slaHours) {
        this.slaHours = slaHours;
    }

    public int getSlaHours() {
        return slaHours;
    }
}
