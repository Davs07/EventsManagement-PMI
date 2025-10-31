package com.api.gestion.eventos.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("IV")
public class InvitacionVirtual extends EmailConfig {
    private String googleMeetLink;
}
