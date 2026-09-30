package com.smartsociety.user.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "apartments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"society_id", "block_name", "flat_number"})
})
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "block_name", nullable = false, length = 50)
    private String blockName;

    @Column(name = "flat_number", nullable = false, length = 50)
    private String flatNumber;

    @Column(name = "society_id", nullable = false)
    private Long societyId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public Apartment() {}

    public Apartment(Long id, String blockName, String flatNumber, Long societyId, Instant createdAt) {
        this.id = id;
        this.blockName = blockName;
        this.flatNumber = flatNumber;
        this.societyId = societyId;
        this.createdAt = createdAt;
    }

    public static ApartmentBuilder builder() {
        return new ApartmentBuilder();
    }

    public static class ApartmentBuilder {
        private Long id;
        private String blockName;
        private String flatNumber;
        private Long societyId;
        private Instant createdAt;

        public ApartmentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ApartmentBuilder blockName(String blockName) {
            this.blockName = blockName;
            return this;
        }

        public ApartmentBuilder flatNumber(String flatNumber) {
            this.flatNumber = flatNumber;
            return this;
        }

        public ApartmentBuilder societyId(Long societyId) {
            this.societyId = societyId;
            return this;
        }

        public ApartmentBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Apartment build() {
            return new Apartment(id, blockName, flatNumber, societyId, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public Long getSocietyId() { return societyId; }
    public void setSocietyId(Long societyId) { this.societyId = societyId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
