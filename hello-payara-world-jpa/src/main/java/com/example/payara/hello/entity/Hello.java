package com.example.payara.hello.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Hello {

    /**
     * Generate id.
     */
    @PrePersist
    private void generateId() {
        if (null == this.id) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Id
    String id;
    String message;
}
