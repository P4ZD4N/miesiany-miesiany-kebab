package com.p4zd4n.kebab.utils.mails;

import com.p4zd4n.kebab.entities.Customer;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import com.p4zd4n.kebab.requests.orders.NewOrderRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class ThanksForOrderMailUtil {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private static final String SUBJECT_ENG = "Thanks for your order!";
    private static final String HEADING_ENG = "Dear Customer,";
    private static final String PARAGRAPH_1_SIMPLE_ENG = "Thank you for placing order!";
    private static final String PARAGRAPH_1_FIRST_ENG =
            "Thank you for placing order! " +
            "Now, you have opportunity to track your order by clicking ";
    private static final String PARAGRAPH_1_SECOND_ENG = "  and entering following data.";
    private static final String PARAGRAPH_2_ENG = "Order ID";
    private static final String PARAGRAPH_3_ENG = "Your phone";
    private static final String PARAGRAPH_4_ENG = "You can track order up to 2 hours from the last update. We will contact you when we prepare your meals. See you soon!";
    private static final String PARAGRAPH_5_FIRST_ENG = "Order ";
    private static final String PARAGRAPH_5_SECOND_ENG = " times with this email to receive a discount code for your next order!";
    private static final String PARAGRAPH_6_ENG = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. All rights reserved.";

    private static final String SUBJECT_PL = "Dziekujemy za twoje zamowienie!";
    private static final String HEADING_PL = "Szanowny Kliencie,";
    private static final String PARAGRAPH_1_SIMPLE_PL = "Dziękujemy za złożenie zamówienia!";
    private static final String PARAGRAPH_1_FIRST_PL =
            "Dziękujemy za złożenie zamówienia! " +
            "Masz teraz możliwość śledzić zamówienie klikając ";
    private static final String PARAGRAPH_1_SECOND_PL = "i wprowadzjąc następujące dane.";
    private static final String PARAGRAPH_2_PL = "ID zamówienia";
    private static final String PARAGRAPH_3_PL = "Twój telefon";
    private static final String PARAGRAPH_4_PL = "Zamówienie możesz śledzić do 2 godzin od jego ostatniej aktualizacji. Skontaktujemy się z tobą, gdy przygotujemy Twoje dania. Do zobaczenia!";
    private static final String PARAGRAPH_5_FIRST_PL = "Zamów jeszcze ";
    private static final String PARAGRAPH_5_SECOND_PL = " razy z podaniem tego emaila aby otrzymać kod rabatowy na kolejne zamówienie!";
    private static final String PARAGRAPH_6_PL = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. Wszelkie prawa zastrzeżone.";

    public ThanksForOrderMailUtil(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEng(NewOrderRequest request, Customer customer, Long orderId) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(customer.getOrderCount(), request.customerPhone(), orderId, NewsletterMessagesLanguage.ENGLISH);
        String htmlContent = templateEngine.process("thanks-for-order-mail", context);

        mimeMessageHelper.setTo(request.customerEmail());
        mimeMessageHelper.setSubject(SUBJECT_ENG);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    @Async
    public void sendPl(NewOrderRequest request, Customer customer, Long orderId) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(customer.getOrderCount(), request.customerPhone(), orderId, NewsletterMessagesLanguage.POLISH);
        String htmlContent = templateEngine.process("thanks-for-order-mail", context);

        mimeMessageHelper.setTo(request.customerEmail());
        mimeMessageHelper.setSubject(SUBJECT_PL);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    private Context getContext(Long orderCount, String phone, Long orderId, NewsletterMessagesLanguage language) {

        Context context = new Context();

        if (language.equals(NewsletterMessagesLanguage.ENGLISH)) {
            context.setVariables(Map.of(
                    "heading", HEADING_ENG,
                    "paragraph1first", PARAGRAPH_1_FIRST_ENG,
                    "paragraph1second", PARAGRAPH_1_SECOND_ENG,
                    "paragraph2", PARAGRAPH_2_ENG,
                    "paragraph3", PARAGRAPH_3_ENG,
                    "paragraph4", PARAGRAPH_4_ENG,
                    "paragraph5first", PARAGRAPH_5_FIRST_ENG,
                    "paragraph5second", PARAGRAPH_5_SECOND_ENG,
                    "paragraph6", PARAGRAPH_6_ENG
            ));
            context.setVariables(Map.of(
                    "paragraph1simple", PARAGRAPH_1_SIMPLE_ENG
            ));
        } else {
            context.setVariables(Map.of(
                    "heading", HEADING_PL,
                    "paragraph1first", PARAGRAPH_1_FIRST_PL,
                    "paragraph1second", PARAGRAPH_1_SECOND_PL,
                    "paragraph2", PARAGRAPH_2_PL,
                    "paragraph3", PARAGRAPH_3_PL,
                    "paragraph4", PARAGRAPH_4_PL,
                    "paragraph5first", PARAGRAPH_5_FIRST_PL,
                    "paragraph5second", PARAGRAPH_5_SECOND_PL,
                    "paragraph6", PARAGRAPH_6_PL
            ));
            context.setVariables(Map.of(
                    "paragraph1simple", PARAGRAPH_1_SIMPLE_PL
            ));
        }

        context.setVariables(Map.of(
                "orderId", orderId,
                "orderRemaining", 10 - (orderCount % 10)
        ));

        if (phone != null) {
            context.setVariables(Map.of(
                    "phone", phone
            ));
        }

        return context;
    }
}
