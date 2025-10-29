package com.api.gestion.eventos.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("IP")
@EqualsAndHashCode(callSuper = true)
public class InvitacionPresencial extends EmailConfig {
    private String qrCode;
}
