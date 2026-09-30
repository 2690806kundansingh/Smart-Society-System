package com.smartsociety.complaint.dto;

import com.smartsociety.complaint.entity.Priority;

public class PredictPriorityResponse {
    private Priority priority;
    private Integer slaHours;
    private String reason;

    public PredictPriorityResponse() {}

    public PredictPriorityResponse(Priority priority, Integer slaHours, String reason) {
        this.priority = priority;
        this.slaHours = slaHours;
        this.reason = reason;
    }

    public static PredictPriorityResponseBuilder builder() {
        return new PredictPriorityResponseBuilder();
    }

    public static class PredictPriorityResponseBuilder {
        private Priority priority;
        private Integer slaHours;
        private String reason;

        public PredictPriorityResponseBuilder priority(Priority priority) { this.priority = priority; return this; }
        public PredictPriorityResponseBuilder slaHours(Integer slaHours) { this.slaHours = slaHours; return this; }
        public PredictPriorityResponseBuilder reason(String reason) { this.reason = reason; return this; }

        public PredictPriorityResponse build() {
            return new PredictPriorityResponse(priority, slaHours, reason);
        }
    }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Integer getSlaHours() { return slaHours; }
    public void setSlaHours(Integer slaHours) { this.slaHours = slaHours; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
