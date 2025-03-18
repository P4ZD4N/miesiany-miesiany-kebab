package com.p4zd4n.kebab.utils.mails;

import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.Map;

@Component
public class VerificationMailUtil {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private static final String SUBJECT_ENG = "Verify your newsletter subscription!";
    private static final String HEADING_ENG = "Dear Customer,";
    private static final String PARAGRAPH_1_ENG = "Thank you for your desire to subscribe to our newsletter! To confirm your subscription, use the verification code below.";
    private static final String PARAGRAPH_2_ENG = "Your verification code is:";
    private static final String PARAGRAPH_3_ENG = "This code is valid for 10 minutes";
    private static final String PARAGRAPH_4_ENG = "If you have not requested this code, please ignore this message.";
    private static final String PARAGRAPH_5_ENG = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. All rights reserved.";
    private static final String ANCHOR_1_ENG = "Unsubscribe";


    private static final String SUBJECT_PL = "Zweyfikuj subskrypcję newslettera!";
    private static final String HEADING_PL = "Szanowny Kliencie,";
    private static final String PARAGRAPH_1_PL = "Dziękujemy za chęć zapisania się na nasz newsletter! Aby potwierdzić subskrypcję, użyj poniższego kodu weryfikacyjnego.";
    private static final String PARAGRAPH_2_PL = "Twój kod weryfikacyjny to:";
    private static final String PARAGRAPH_3_PL = "Kod jest ważny przez 10 minut";
    private static final String PARAGRAPH_4_PL = "Jeśli nie wnioskowałeś o ten kod, prosimy o zignorowanie tej wiadomości.";
    private static final String PARAGRAPH_5_PL = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. Wszelkie prawa zastrzeżone.";
    private static final String ANCHOR_1_PL = "Wypisz się";

    public VerificationMailUtil(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEng(String recipient, Integer otp) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(otp, NewsletterMessagesLanguage.ENGLISH);
        String htmlContent = templateEngine.process("verification-mail", context);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_ENG);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    @Async
    public void sendPl(String recipient, Integer otp) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(otp, NewsletterMessagesLanguage.POLISH);
        String htmlContent = templateEngine.process("verification-mail", context);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_PL);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    private Context getContext(Integer otp, NewsletterMessagesLanguage language) {

        Context context = new Context();

        if (language.equals(NewsletterMessagesLanguage.ENGLISH)) {
            context.setVariables(Map.of(
                    "heading", HEADING_ENG,
                    "paragraph1", PARAGRAPH_1_ENG,
                    "paragraph2", PARAGRAPH_2_ENG,
                    "paragraph3", PARAGRAPH_3_ENG,
                    "paragraph4", PARAGRAPH_4_ENG,
                    "paragraph5", PARAGRAPH_5_ENG,
                    "otp", otp
            ));

            return context;
        }

        context.setVariables(Map.of(
                "heading", HEADING_PL,
                "paragraph1", PARAGRAPH_1_PL,
                "paragraph2", PARAGRAPH_2_PL,
                "paragraph3", PARAGRAPH_3_PL,
                "paragraph4", PARAGRAPH_4_PL,
                "paragraph5", PARAGRAPH_5_PL,
                "otp", otp
        ));

        return context;
    }
}
