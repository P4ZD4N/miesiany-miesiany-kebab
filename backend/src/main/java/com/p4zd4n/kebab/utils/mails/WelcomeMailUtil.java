package com.p4zd4n.kebab.utils.mails;

import com.p4zd4n.kebab.entities.NewsletterSubscriber;
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
public class WelcomeMailUtil {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private static final String SUBJECT_ENG = "Welcome to our newsletter!";
    private static final String HEADING_ENG = "Welcome ";
    private static final String PARAGRAPH_1_ENG = "We are really happy, that you are with us! From now on, whenever a new promotion appears, you will be notified personally!";
    private static final String PARAGRAPH_2_ENG = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. All rights reserved.";
    private static final String ANCHOR_1_ENG = "Unsubscribe";

    private static final String SUBJECT_PL = "Witamy w naszym newsletterze!";
    private static final String HEADING_PL = "Witaj ";
    private static final String PARAGRAPH_1_PL = "Bardzo się cieszymy, że z nami jesteś! Od teraz, gdy tylko pojawi się nowa promocja, będziesz o tym powiadomiony osobiście!";
    private static final String PARAGRAPH_2_PL = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. Wszelkie prawa zastrzeżone.";
    private static final String ANCHOR_1_PL = "Wypisz się";

    public WelcomeMailUtil(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEng(NewsletterSubscriber subscriber) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(subscriber, NewsletterMessagesLanguage.ENGLISH);
        String htmlContent = templateEngine.process("welcome-mail", context);

        mimeMessageHelper.setTo(subscriber.getEmail());
        mimeMessageHelper.setSubject(SUBJECT_ENG);
        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(message);
    }

    @Async
    public void sendPl(NewsletterSubscriber subscriber) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(subscriber, NewsletterMessagesLanguage.POLISH);
        String htmlContent = templateEngine.process("welcome-mail", context);

        mimeMessageHelper.setTo(subscriber.getEmail());
        mimeMessageHelper.setSubject(SUBJECT_PL);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    private Context getContext(NewsletterSubscriber subscriber, NewsletterMessagesLanguage language) {

        Context context = new Context();

        if (language.equals(NewsletterMessagesLanguage.ENGLISH)) {
            context.setVariables(Map.of(
                    "heading", HEADING_ENG + subscriber.getSubscriberFirstName() + "!",
                    "paragraph1", PARAGRAPH_1_ENG,
                    "paragraph2", PARAGRAPH_2_ENG,
                    "anchor1", ANCHOR_1_ENG,
                    "subscriber", subscriber
            ));
            return context;
        }

        context.setVariables(Map.of(
                "heading", HEADING_PL + subscriber.getSubscriberFirstName() + "!",
                "paragraph1", PARAGRAPH_1_PL,
                "paragraph2", PARAGRAPH_2_PL,
                "anchor1", ANCHOR_1_PL,
                "subscriber", subscriber
        ));

        return context;
    }
}
