package com.smartsociety.complaint.engine;

import com.smartsociety.complaint.entity.Category;
import com.smartsociety.complaint.entity.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class PriorityPredictionEngine {

    private static final Set<String> HIGH_SEVERITY_KEYWORDS = Set.of(
            "burst", "flood", "leakage", "leak", "pipe burst", "water gushing",
            "spark", "fire", "smoke", "shock", "short circuit", "burning",
            "lift stuck", "trapped", "elevator trapped", "elevator stuck", "lift failure",
            "blackout", "power outage", "total outage", "no electricity", "no power",
            "gas leak", "emergency", "danger", "hazard", "urgent"
    );

    private static final Set<String> LOW_SEVERITY_KEYWORDS = Set.of(
            "cleaning", "cleanliness", "garbage", "trash", "dust", "sweep",
            "light bulb", "flicker", "paint", "peeling", "aesthetic", "cosmetic",
            "gardening", "grass", "lawn", "routine", "minor", "suggestion"
    );

    /**
     * Analyzes title, description, and selected category to predict the SLA Priority.
     * High severity keywords take precedence.
     */
    public Priority predictPriority(String title, String description, Category category) {
        String combinedText = ((title != null ? title : "") + " " + (description != null ? description : ""))
                .toLowerCase();

        // 1. Check for emergency / high urgency keywords
        for (String keyword : HIGH_SEVERITY_KEYWORDS) {
            if (combinedText.contains(keyword)) {
                log.info("Priority predicted as HIGH due to keyword match: '{}'", keyword);
                return Priority.HIGH;
            }
        }

        // 2. Check for low urgency keywords
        boolean hasLowKeyword = false;
        for (String keyword : LOW_SEVERITY_KEYWORDS) {
            if (combinedText.contains(keyword)) {
                hasLowKeyword = true;
                break;
            }
        }

        // 3. Fall back to category default priority
        if (category != null && category.getDefaultPriority() != null) {
            if (category.getDefaultPriority() == Priority.HIGH) {
                return Priority.HIGH;
            }
            if (hasLowKeyword) {
                return Priority.LOW;
            }
            return category.getDefaultPriority();
        }

        return hasLowKeyword ? Priority.LOW : Priority.MEDIUM;
    }

    /**
     * Computes the SLA deadline from the assigned priority.
     * HIGH = 2 Hours, MEDIUM = 12 Hours, LOW = 48 Hours.
     */
    public Instant calculateSlaDeadline(Priority priority) {
        return Instant.now().plus(Duration.ofHours(priority.getSlaHours()));
    }
}
