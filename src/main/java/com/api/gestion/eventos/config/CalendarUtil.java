package com.api.gestion.eventos.config;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.DateTime;
import java.io.FileOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class CalendarUtil {
    public static String crearArchivoICS(String resumen, String descripcion, ZonedDateTime inicio, ZonedDateTime fin, String lugar) throws Exception {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Eventos Facultad//iCal4j 1.0//ES"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        VEvent evento = new VEvent(new DateTime(java.util.Date.from(inicio.toInstant())),
                new DateTime(java.util.Date.from(fin.toInstant())),
                resumen);
        evento.getProperties().add(new Description(descripcion));
        evento.getProperties().add(new Location(lugar));
        evento.getProperties().add(new Uid(UUID.randomUUID().toString()));

        calendar.getComponents().add(evento);

        String filePath = "evento.ics";
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(calendar.toString().getBytes());
        }
        return filePath;
    }
}
