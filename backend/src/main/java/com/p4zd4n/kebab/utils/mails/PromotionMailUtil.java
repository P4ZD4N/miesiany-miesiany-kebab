package com.p4zd4n.kebab.utils.mails;

import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.entities.interfaces.Promotion;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import com.p4zd4n.kebab.repositories.NewsletterRepository;
import com.p4zd4n.kebab.utils.interfaces.Observer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class PromotionMailUtil implements Observer {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  private static final String SUBJECT_ENG = "New promotion!";
  private static final String HEADING_ENG = "Hello ";
  private static final String PARAGRAPH_1_ENG =
      "We are coming back to you with information about a new promotion that "
          + "has just started in our restaurant. We hope you will take advantage of this "
          + "unique offer and enjoy our delicacies at an even better price! Don't delay, "
          + "the promotion is available for a limited time.";
  private static final String PARAGRAPH_2_ENG =
      "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. All rights reserved.";
  private static final String ANCHOR_1_ENG = "Unsubscribe";

  private static final String SUBJECT_PL = "Nowa promocja!";
  private static final String HEADING_PL = "Witaj ";
  private static final String PARAGRAPH_1_PL =
      "Wracamy do Ciebie z informacją o nowej promocji, która właśnie zaczęła "
          + "obowiązywać w naszej restauracji. Mamy nadzieję, że skorzystasz z tej "
          + "wyjątkowej oferty i będziesz mógł cieszyć się naszymi pysznościami w "
          + "jeszcze lepszej cenie! Nie zwlekaj, promocja jest dostępna przez ograniczony czas.";
  private static final String PARAGRAPH_2_PL =
      "© " + LocalDate.now().getYear() + " Miesiany Miesiany Kebab. Wszelkie prawa zastrzeżone.";
  private static final String ANCHOR_1_PL = "Wypisz się";

  private final NewsletterRepository newsletterRepository;

  public PromotionMailUtil(
      JavaMailSender javaMailSender,
      TemplateEngine templateEngine,
      NewsletterRepository newsletterRepository) {
    this.javaMailSender = javaMailSender;
    this.templateEngine = templateEngine;
    this.newsletterRepository = newsletterRepository;
  }

  @Override
  @Async
  public void update(Promotion promotion) throws MessagingException {
    List<NewsletterSubscriber> subscribers = newsletterRepository.findAllByIsActiveTrue();

    for (NewsletterSubscriber subscriber : subscribers) {
      if (subscriber.getNewsletterMessagesLanguage().equals(NewsletterMessagesLanguage.ENGLISH))
        sendEng(subscriber, promotion);
      else sendPl(subscriber, promotion);
    }
  }

  @Async
  public void sendEng(NewsletterSubscriber subscriber, Promotion promotion)
      throws MessagingException {

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

    mimeMessageHelper.setTo(subscriber.getEmail());
    mimeMessageHelper.setSubject(SUBJECT_ENG);

    if (promotion instanceof MealPromotion mealPromotion) {
      Context context =
          getMealPromotionContext(subscriber, mealPromotion, NewsletterMessagesLanguage.ENGLISH);
      String htmlContent = templateEngine.process("meal-promotion-mail", context);
      mimeMessageHelper.setText(htmlContent, true);
    } else if (promotion instanceof BeveragePromotion beveragePromotion) {
      Context context =
          getBeveragePromotionContext(
              subscriber, beveragePromotion, NewsletterMessagesLanguage.ENGLISH);
      String htmlContent = templateEngine.process("beverage-promotion-mail", context);
      mimeMessageHelper.setText(htmlContent, true);
    } else if (promotion instanceof AddonPromotion addonPromotion) {
      Context context =
          getAddonPromotionContext(subscriber, addonPromotion, NewsletterMessagesLanguage.ENGLISH);
      String htmlContent = templateEngine.process("addon-promotion-mail", context);
      mimeMessageHelper.setText(htmlContent, true);
    }

    javaMailSender.send(message);
  }

  @Async
  public void sendPl(NewsletterSubscriber subscriber, Promotion promotion)
      throws MessagingException {

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

    mimeMessageHelper.setTo(subscriber.getEmail());
    mimeMessageHelper.setSubject(SUBJECT_PL);

    if (promotion instanceof MealPromotion mealPromotion) {
      Context context =
          getMealPromotionContext(subscriber, mealPromotion, NewsletterMessagesLanguage.POLISH);
      String htmlContent = templateEngine.process("meal-promotion-mail", context);
      mimeMessageHelper.setText(htmlContent, true);
    } else if (promotion instanceof BeveragePromotion beveragePromotion) {
      Context context =
          getBeveragePromotionContext(
              subscriber, beveragePromotion, NewsletterMessagesLanguage.POLISH);
      String htmlContent = templateEngine.process("beverage-promotion-mail", context);
      mimeMessageHelper.setText(htmlContent, true);
    } else if (promotion instanceof AddonPromotion addonPromotion) {
      Context context =
          getAddonPromotionContext(subscriber, addonPromotion, NewsletterMessagesLanguage.POLISH);
      String htmlContent = templateEngine.process("addon-promotion-mail", context);
      mimeMessageHelper.setText(htmlContent, true);
    }

    javaMailSender.send(message);
  }

  private Context getMealPromotionContext(
      NewsletterSubscriber subscriber,
      MealPromotion mealPromotion,
      NewsletterMessagesLanguage language) {

    Context context = new Context();

    if (language.equals(NewsletterMessagesLanguage.ENGLISH)) {
      context.setVariables(
          Map.of(
              "heading1", HEADING_ENG + subscriber.getSubscriberFirstName() + "!",
              "heading2", "New meal promotion",
              "paragraph1", PARAGRAPH_1_ENG,
              "paragraph2", PARAGRAPH_2_ENG,
              "description", "Promotion description: " + mealPromotion.getDescription(),
              "mealPromotion", mealPromotion,
              "anchor1", ANCHOR_1_ENG,
              "subscriber", subscriber));
      return context;
    }

    context.setVariables(
        Map.of(
            "heading1", HEADING_PL + subscriber.getSubscriberFirstName() + "!",
            "heading2", "Nowa promocja na dania",
            "paragraph1", PARAGRAPH_1_PL,
            "paragraph2", PARAGRAPH_2_PL,
            "description", "Opis promocji: " + mealPromotion.getDescription(),
            "mealPromotion", mealPromotion,
            "anchor1", ANCHOR_1_PL,
            "subscriber", subscriber));

    return context;
  }

  private Context getBeveragePromotionContext(
      NewsletterSubscriber subscriber,
      BeveragePromotion beveragePromotion,
      NewsletterMessagesLanguage language) {

    Context context = new Context();

    if (language.equals(NewsletterMessagesLanguage.ENGLISH)) {
      context.setVariables(
          Map.of(
              "heading1", HEADING_ENG + subscriber.getSubscriberFirstName() + "!",
              "heading2", "New beverage promotion",
              "paragraph1", PARAGRAPH_1_ENG,
              "paragraph2", PARAGRAPH_2_ENG,
              "description", "Promotion description: " + beveragePromotion.getDescription(),
              "beveragePromotion", beveragePromotion,
              "anchor1", ANCHOR_1_ENG,
              "subscriber", subscriber));
      return context;
    }

    context.setVariables(
        Map.of(
            "heading1", HEADING_PL + subscriber.getSubscriberFirstName() + "!",
            "heading2", "Nowa promocja na napoje",
            "paragraph1", PARAGRAPH_1_PL,
            "paragraph2", PARAGRAPH_2_PL,
            "description", "Opis promocji: " + beveragePromotion.getDescription(),
            "beveragePromotion", beveragePromotion,
            "anchor1", ANCHOR_1_PL,
            "subscriber", subscriber));

    return context;
  }

  private Context getAddonPromotionContext(
      NewsletterSubscriber subscriber,
      AddonPromotion addonPromotion,
      NewsletterMessagesLanguage language) {
    Context context = new Context();

    if (language.equals(NewsletterMessagesLanguage.ENGLISH)) {
      context.setVariables(
          Map.of(
              "heading1", HEADING_ENG + subscriber.getSubscriberFirstName() + "!",
              "heading2", "New addon promotion",
              "paragraph1", PARAGRAPH_1_ENG,
              "paragraph2", PARAGRAPH_2_ENG,
              "description", "Promotion description: " + addonPromotion.getDescription(),
              "addonPromotion", addonPromotion,
              "anchor1", ANCHOR_1_ENG,
              "subscriber", subscriber));
      return context;
    }

    context.setVariables(
        Map.of(
            "heading1", HEADING_PL + subscriber.getSubscriberFirstName() + "!",
            "heading2", "Nowa promocja na dodatki",
            "paragraph1", PARAGRAPH_1_PL,
            "paragraph2", PARAGRAPH_2_PL,
            "description", "Opis promocji: " + addonPromotion.getDescription(),
            "addonPromotion", addonPromotion,
            "anchor1", ANCHOR_1_PL,
            "subscriber", subscriber));

    return context;
  }
}
