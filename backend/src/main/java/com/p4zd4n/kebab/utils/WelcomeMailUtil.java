package com.p4zd4n.kebab.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class WelcomeMailUtil {

    private final JavaMailSender javaMailSender;

    private static final String SUBJECT_ENG = "Welcome!";
    private static final String TEXT_ENG = "From now you will receive emails about promotions :)";

    private static final String SUBJECT_PL = "Witamy!";
    private static final String TEXT_PL = "Od teraz będziesz dostawał emaile dotyczące promocji :)";

    public WelcomeMailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEng(String recipient) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_ENG);
        mimeMessageHelper.setText(TEXT_ENG);

        javaMailSender.send(message);
    }

    public void sendPl(String recipient) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_PL);
        mimeMessageHelper.setText(TEXT_PL);

        javaMailSender.send(message);
    }
}
