package com.p4zd4n.kebab.utils.mails;

import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.Map;

@Component
public class GoodbyeMailUtil {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private static final String SUBJECT_ENG = "Hope to see you again!";
    private static final String HEADING_ENG = "Dear ";
    private static final String PARAGRAPH_1_ENG =
            "We really appreciate, that you were with us. Thank you for being " +
            "part of our kebab-loving community! We hope you enjoyed our deals " +
            "and delicious offers. If you ever get a craving for the best-tasting kebabs, " +
            "you know where to find us. See you next time!";
    private static final String PARAGRAPH_2_ENG = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. All rights reserved.";

    private static final String SUBJECT_PL = "Mamy nadzieję, że do zobaczenia!";
    private static final String HEADING_PL = "Witaj ";
    private static final String PARAGRAPH_1_PL =
            "Bardzo doceniamy, że byłeś z nami. Dziękujemy za bycie częścią " +
            "community kochającego kebaby! Mamy nadzieję, że podobały ci się nasze oferty. " +
            "Jesli kiedys miałbyś jeszcze chęć na najlepiej smakujące kebaby, " +
            "wiesz gdzie nas znaleźć. Do zobaczenia następnym razem!";
    private static final String PARAGRAPH_2_PL = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. Wszelkie prawa zastrzeżone.";

    public GoodbyeMailUtil(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEng(NewsletterSubscriber subscriber) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(subscriber, NewsletterMessagesLanguage.ENGLISH);
        String htmlContent = templateEngine.process("goodbye-mail", context);

        mimeMessageHelper.setTo(subscriber.getEmail());
        mimeMessageHelper.setSubject(SUBJECT_ENG);
        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(message);
    }

    public void sendPl(NewsletterSubscriber subscriber) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(subscriber, NewsletterMessagesLanguage.POLISH);
        String htmlContent = templateEngine.process("goodbye-mail", context);

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
                    "paragraph2", PARAGRAPH_2_ENG

            ));
            return context;
        }

        context.setVariables(Map.of(
                "heading", HEADING_PL + subscriber.getSubscriberFirstName() + "!",
                "paragraph1", PARAGRAPH_1_PL,
                "paragraph2", PARAGRAPH_2_PL
        ));

        return context;
    }
}
