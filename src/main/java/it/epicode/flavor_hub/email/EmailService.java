package it.epicode.flavor_hub.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender emailSender;

    public void sendWelcomeEmail(String recipientEmail) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Benvenuto in Flavor-Hub!");
            helper.setText("Grazie per esserti registrato. Esplora le ricette uniche degli utenti e divertiti a crearne di nuove!", true);

            emailSender.send(message);
            logger.info("Email di benvenuto inviata a {}", recipientEmail);
        } catch (MessagingException e) {
            logger.error("Errore durante l'invio dell'email di benvenuto a {}", recipientEmail, e);
        }
    }
}
