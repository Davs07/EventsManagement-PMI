package com.api.gestion.eventos.services;

import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.InvitacionPresencial;
import com.api.gestion.eventos.entities.InvitacionVirtual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitacionService {
    @Autowired
    private EmailService emailService;

    public void enviarInvitacionesVirtuales(InvitacionVirtual invitacion) throws Exception {
        emailService.sendInvitacionVirtual(invitacion);
    }

    public void enviarInvitacionesPresenciales(InvitacionPresencial invitacion, Evento evento) throws Exception {
        emailService.sendInvitacionPresencial(invitacion, evento);
    }
}
