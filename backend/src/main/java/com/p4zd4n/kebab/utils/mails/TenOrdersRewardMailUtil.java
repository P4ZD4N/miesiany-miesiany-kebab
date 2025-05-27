package com.p4zd4n.kebab.utils.mails;

import com.p4zd4n.kebab.entities.DiscountCode;
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
import java.util.Map;

@Component
public class TenOrdersRewardMailUtil implements DiscountCodeSendable {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private static final String SUBJECT_ENG = "Claim your discount code!";
    private static final String HEADING_ENG = "Dear Customer,";
    private static final String PARAGRAPH_1_ENG = "Thank you for your last 10 orders! We would like to give you this little gift and encourage to take advantage of it next time!";
    private static final String PARAGRAPH_2_FIRST_ENG = "Your ";
    private static final String PARAGRAPH_2_SECOND_ENG = "% discount code:";
    private static final String PARAGRAPH_3_ENG = "This code is valid for one month and can be used once";
    private static final String PARAGRAPH_4_ENG = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. All rights reserved.";

    private static final String SUBJECT_PL = "Odbierz swój kod rabatowy!";
    private static final String HEADING_PL = "Szanowny Kliencie,";
    private static final String PARAGRAPH_1_PL = "Dziękujemy za twoje ostatnie 10 zamówień! Chcielibyśmy podarować Ci ten mały prezent i zachęcić do wykorzystania go następnym razem!";
    private static final String PARAGRAPH_2_FIRST_PL = "Twój ";
    private static final String PARAGRAPH_2_SECOND_PL = "% kod rabatowy:";
    private static final String PARAGRAPH_3_PL = "Kod jest ważny przez miesiąc i można go użyć tylko raz";
    private static final String PARAGRAPH_4_PL = "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. Wszelkie prawa zastrzeżone.";

    public TenOrdersRewardMailUtil(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEng(NewOrderRequest request, DiscountCode discountCode) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(discountCode, NewsletterMessagesLanguage.ENGLISH);
        String htmlContent = templateEngine.process("ten-orders-reward-mail", context);

        mimeMessageHelper.setTo(request.customerEmail());
        mimeMessageHelper.setSubject(SUBJECT_ENG);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    @Async
    public void sendPl(NewOrderRequest request, DiscountCode discountCode) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        Context context = getContext(discountCode, NewsletterMessagesLanguage.POLISH);
        String htmlContent = templateEngine.process("ten-orders-reward-mail", context);

        mimeMessageHelper.setTo(request.customerEmail());
        mimeMessageHelper.setSubject(SUBJECT_PL);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    private Context getContext(DiscountCode discountCode, NewsletterMessagesLanguage language) {

        Context context = new Context();

        if (language.equals(NewsletterMessagesLanguage.ENGLISH)) {
            context.setVariables(Map.of(
                    "heading", HEADING_ENG,
                    "paragraph1", PARAGRAPH_1_ENG,
                    "paragraph2first", PARAGRAPH_2_FIRST_ENG,
                    "paragraph2second", PARAGRAPH_2_SECOND_ENG,
                    "paragraph3", PARAGRAPH_3_ENG,
                    "paragraph4", PARAGRAPH_4_ENG,
                    "code", discountCode.getCode(),
                    "discountPercentage", discountCode.getDiscountPercentage()
            ));

            return context;
        }

        context.setVariables(Map.of(
                "heading", HEADING_PL,
                "paragraph1", PARAGRAPH_1_PL,
                "paragraph2first", PARAGRAPH_2_FIRST_PL,
                "paragraph2second", PARAGRAPH_2_SECOND_PL,
                "paragraph3", PARAGRAPH_3_PL,
                "paragraph4", PARAGRAPH_4_PL,
                "code", discountCode.getCode(),
                "discountPercentage", discountCode.getDiscountPercentage()
        ));

        return context;
    }
}
