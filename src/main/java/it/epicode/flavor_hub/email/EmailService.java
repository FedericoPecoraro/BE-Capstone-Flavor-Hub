package it.epicode.flavor_hub.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendWelcomeEmail(String recipientEmail) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(recipientEmail);
            helper.setSubject("Benvenuto nella nostra applicazione!");
            helper.setText("Grazie per esserti registrato. Benvenuto!");

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // gestisci l'errore in modo adeguato
        }
    }
}
