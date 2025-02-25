package com.p4zd4n.kebab.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class VerificationMailUtil {

    private final JavaMailSender javaMailSender;

    private static final String SUBJECT_ENG = "Verify your newsletter subscription!";
    private static final String TEXT_ENG = "Your verification code is: ";

    private static final String SUBJECT_PL = "Zweyfikuj subskrypcję newslettera!";
    private static final String TEXT_PL = "Twój kod weryfikacyjny: ";

    public VerificationMailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEng(String recipient, Integer otp) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_ENG);
        mimeMessageHelper.setText(TEXT_ENG + otp);

        javaMailSender.send(message);
    }

    public void sendPl(String recipient, Integer otp) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_PL);
        mimeMessageHelper.setText(TEXT_PL + otp);

        javaMailSender.send(message);
    }
}
