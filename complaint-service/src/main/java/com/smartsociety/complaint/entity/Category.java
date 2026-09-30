package com.smartsociety.complaint.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "categories")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_priority", nullable = false, length = 20)
    private Priority defaultPriority = Priority.MEDIUM;

    @Column(name = "default_sla_hours", nullable = false)
    private Integer defaultSlaHours = 12;

    public Category() {}

    public Category(Long id, String code, String name, String department, Priority defaultPriority, Integer defaultSlaHours) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.department = department;
        this.defaultPriority = defaultPriority != null ? defaultPriority : Priority.MEDIUM;
        this.defaultSlaHours = defaultSlaHours != null ? defaultSlaHours : 12;
    }

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public static class CategoryBuilder {
        private Long id;
        private String code;
        private String name;
        private String department;
        private Priority defaultPriority = Priority.MEDIUM;
        private Integer defaultSlaHours = 12;

        public CategoryBuilder id(Long id) { this.id = id; return this; }
        public CategoryBuilder code(String code) { this.code = code; return this; }
        public CategoryBuilder name(String name) { this.name = name; return this; }
        public CategoryBuilder department(String department) { this.department = department; return this; }
        public CategoryBuilder defaultPriority(Priority defaultPriority) { this.defaultPriority = defaultPriority; return this; }
        public CategoryBuilder defaultSlaHours(Integer defaultSlaHours) { this.defaultSlaHours = defaultSlaHours; return this; }

        public Category build() {
            return new Category(id, code, name, department, defaultPriority, defaultSlaHours);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Priority getDefaultPriority() { return defaultPriority; }
    public void setDefaultPriority(Priority defaultPriority) { this.defaultPriority = defaultPriority; }

    public Integer getDefaultSlaHours() { return defaultSlaHours; }
    public void setDefaultSlaHours(Integer defaultSlaHours) { this.defaultSlaHours = defaultSlaHours; }
}
