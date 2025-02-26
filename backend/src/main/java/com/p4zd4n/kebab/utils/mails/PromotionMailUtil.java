package com.p4zd4n.kebab.utils.mails;

import com.p4zd4n.kebab.entities.AddonPromotion;
import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.entities.Promotion;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PromotionMailUtil {

    private final JavaMailSender javaMailSender;

    private static final String SUBJECT_ENG = "New promotion!";

    private static final String SUBJECT_PL = "Nowa promocja!";

    public PromotionMailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEng(String recipient, Promotion promotion) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_ENG);

        if (promotion instanceof MealPromotion mealPromotion) {
            mimeMessageHelper.setText(
                "New meal promotion with " +
                mealPromotion.getDiscountPercentage() +
                "% discount percentage is available!"
            );
        } else if (promotion instanceof BeveragePromotion beveragePromotion) {
            mimeMessageHelper.setText(
                "New beverage promotion with " +
                beveragePromotion.getDiscountPercentage() +
                "% discount percentage is available!"
            );
        } else if (promotion instanceof AddonPromotion addonPromotion) {
            mimeMessageHelper.setText(
                "New addon promotion with " +
                addonPromotion.getDiscountPercentage() +
                "% discount percentage is available!"
            );
        }

        javaMailSender.send(message);
    }

    @Async
    public void sendPl(String recipient, Promotion promotion) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(SUBJECT_PL);

        if (promotion instanceof MealPromotion mealPromotion) {
            mimeMessageHelper.setText(
                "Nowa promocja na dania z " +
                mealPromotion.getDiscountPercentage() +
                "% zniżki jest dostępna!"
            );
        } else if (promotion instanceof BeveragePromotion beveragePromotion) {
            mimeMessageHelper.setText(
                "Nowa promocja na napoje z " +
                beveragePromotion.getDiscountPercentage() +
                "% zniżki jest dostępna!"
            );
        } else if (promotion instanceof AddonPromotion addonPromotion) {
            mimeMessageHelper.setText(
                "Nowa promocja na dodatki z " +
                addonPromotion.getDiscountPercentage() +
                "% zniżki jest dostępna!"
            );
        }

        javaMailSender.send(message);
    }
}
