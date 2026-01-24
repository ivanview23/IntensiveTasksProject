package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String user, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(user);
            message.setText(text);

            mailSender.send(message);
            log.info("Email отправлен на адрес: {}", to);
        } catch (Exception e) {
            log.error("Ошибка при отправке email на адрес: {}", to, e);
            throw new RuntimeException("Ошибка отправки email", e);
        }
    }
}