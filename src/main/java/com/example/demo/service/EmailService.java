package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);
        mailSender.send(email);
    }

    public void enviarCorreo(String remitente, String destinatario, String asunto, String mensaje) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(remitente);
        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);
        mailSender.send(email);
    }

    public void enviarCorreoConAdjunto(String destinatario, String asunto, String mensaje, 
                                        byte[] adjunto, String nombreArchivo) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(mensaje);
        
        if (adjunto != null && adjunto.length > 0) {
            helper.addAttachment(nombreArchivo, new ByteArrayResource(adjunto));
        }
        
        mailSender.send(mimeMessage);
    }
}
