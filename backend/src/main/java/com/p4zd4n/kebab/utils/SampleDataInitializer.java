package com.p4zd4n.kebab.utils;

import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.enums.*;
import com.p4zd4n.kebab.exceptions.notfound.IngredientNotFoundException;
import com.p4zd4n.kebab.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final BeverageRepository beverageRepository;
    private final AddonRepository addonRepository;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;
    private final ContactRepository contactRepository;
    private final JobOfferRepository jobOfferRepository;
    private final MealPromotionsRepository mealPromotionsRepository;
    private final BeveragePromotionsRepository beveragePromotionsRepository;
    private final AddonPromotionsRepository addonPromotionsRepository;
    private final NewsletterRepository newsletterRepository;
    private final OrdersRepository ordersRepository;
    private final DiscountCodesRepository discountCodesRepository;

    public SampleDataInitializer(
            EmployeeRepository employeeRepository,
            OpeningHoursRepository openingHoursRepository,
            BeverageRepository beverageRepository,
            AddonRepository addonRepository,
            MealRepository mealRepository,
            IngredientRepository ingredientRepository,
            ContactRepository contactRepository,
            JobOfferRepository jobOfferRepository,
            MealPromotionsRepository mealPromotionsRepository,
            BeveragePromotionsRepository beveragePromotionsRepository,
            AddonPromotionsRepository addonPromotionsRepository,
            NewsletterRepository newsletterRepository,
            OrdersRepository ordersRepository,
            DiscountCodesRepository discountCodesRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.openingHoursRepository = openingHoursRepository;
        this.beverageRepository = beverageRepository;
        this.addonRepository = addonRepository;
        this.mealRepository = mealRepository;
        this.ingredientRepository = ingredientRepository;
        this.contactRepository = contactRepository;
        this.jobOfferRepository = jobOfferRepository;
        this.mealPromotionsRepository = mealPromotionsRepository;
        this.beveragePromotionsRepository = beveragePromotionsRepository;
        this.addonPromotionsRepository = addonPromotionsRepository;
        this.newsletterRepository = newsletterRepository;
        this.ordersRepository = ordersRepository;
        this.discountCodesRepository = discountCodesRepository;
    }

    @Override
    public void run(String... args) {

        initOpeningHours();
        initSampleEmployees();
        initSampleManagers();
        initBeverages();
        initAddons();
        initIngredients();
        initMeals();
        initContacts();
        initJobOffers();
        initPromotions();
        initNewsletterSubscribers();
        initOrders();
        initDiscountCodes();
    }

    private void initOpeningHours() {

        if (openingHoursRepository.count() == 0) {
            Arrays.stream(DayOfWeek.values()).forEach(dayOfWeek -> {
                OpeningHour openingHour = OpeningHour.builder()
                        .dayOfWeek(dayOfWeek)
                        .openingTime(LocalTime.of(10, 0))
                        .closingTime(LocalTime.of(22, 0))
                        .build();
                openingHoursRepository.save(openingHour);
            });
        }
    }

    private void initSampleEmployees() {

        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("employee@example.com")
                .password(PasswordEncoder.encodePassword("employee123"))
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .employmentType(EmploymentType.PERMANENT)
                .hourlyWage(BigDecimal.valueOf(30))
                .isActive(true)
                .hiredAt(LocalDate.now())
                .build();

        employeeRepository.save(employee);
    }

    private void initSampleManagers() {

        Manager manager = new Manager(
                "Wiktor",
                "Chudy",
                "manager@example.com",
                PasswordEncoder.encodePassword("manager123"),
                LocalDate.of(2003, 8, 1),
                "123456798",
                BigDecimal.valueOf(40),
                true,
                LocalDate.now()
        );

        employeeRepository.save(manager);
    }

    private void initBeverages() {

        Beverage cocaCola = Beverage.builder()
                .name("Coca-Cola")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage cocaColaZero = Beverage.builder()
                .name("Coca-Cola Zero")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage fanta = Beverage.builder()
                .name("Fanta")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage sprite = Beverage.builder()
                .name("Sprite")
                .price(BigDecimal.valueOf(6))
                .capacity(BigDecimal.valueOf(0.33))
                .build();

        Beverage ayran = Beverage.builder()
                .name("Ayran")
                .price(BigDecimal.valueOf(5))
                .capacity(BigDecimal.valueOf(0.5))
                .build();

        Beverage water = Beverage.builder()
                .name("Cisowianka Niegazowana")
                .price(BigDecimal.valueOf(5))
                .capacity(BigDecimal.valueOf(0.5))
                .build();

        beverageRepository.save(cocaCola);
        beverageRepository.save(cocaColaZero);
        beverageRepository.save(fanta);
        beverageRepository.save(sprite);
        beverageRepository.save(ayran);
        beverageRepository.save(water);
    }

    private void initAddons() {

        Addon fries140g = Addon.builder()
                .name("Fries (140g)")
                .price(BigDecimal.valueOf(12))
                .build();

        Addon fries200g = Addon.builder()
                .name("Fries (200g)")
                .price(BigDecimal.valueOf(15))
                .build();

        Addon nuggets7pcs = Addon.builder()
                .name("Nuggets (7pcs)")
                .price(BigDecimal.valueOf(21))
                .build();

        Addon zapiekanka = Addon.builder()
                .name("Zapiekanka")
                .price(BigDecimal.valueOf(15))
                .build();

        addonRepository.save(fries140g);
        addonRepository.save(fries200g);
        addonRepository.save(nuggets7pcs);
        addonRepository.save(zapiekanka);
    }

    private void initIngredients() {

        Ingredient pita = Ingredient.builder()
                .name("Pita")
                .ingredientType(IngredientType.BREAD)
                .build();

        Ingredient tortilla = Ingredient.builder()
                .name("Tortilla")
                .ingredientType(IngredientType.BREAD)
                .build();

        Ingredient chicken = Ingredient.builder()
                .name("Chicken")
                .ingredientType(IngredientType.MEAT)
                .build();

        Ingredient mutton = Ingredient.builder()
                .name("Mutton")
                .ingredientType(IngredientType.MEAT)
                .build();

        Ingredient meatMix = Ingredient.builder()
                .name("Meat Mix")
                .ingredientType(IngredientType.MEAT)
                .build();

        Ingredient falafel = Ingredient.builder()
                .name("Falafel")
                .ingredientType(IngredientType.MEAT)
                .build();

        Ingredient cabbage = Ingredient.builder()
                .name("Cabbage")
                .ingredientType(IngredientType.VEGETABLE)
                .build();

        Ingredient tomato = Ingredient.builder()
                .name("Tomato")
                .ingredientType(IngredientType.VEGETABLE)
                .build();

        Ingredient cucumber = Ingredient.builder()
                .name("Cucumber")
                .ingredientType(IngredientType.VEGETABLE)
                .build();

        Ingredient onion = Ingredient.builder()
                .name("Onion")
                .ingredientType(IngredientType.VEGETABLE)
                .build();

        Ingredient garlicSauce = Ingredient.builder()
                .name("Garlic Sauce")
                .ingredientType(IngredientType.SAUCE)
                .build();

        Ingredient bbqSauce = Ingredient.builder()
                .name("BBQ Sauce")
                .ingredientType(IngredientType.SAUCE)
                .build();

        Ingredient mildSauce = Ingredient.builder()
                .name("Mild Sauce")
                .ingredientType(IngredientType.SAUCE)
                .build();

        Ingredient hotSauce = Ingredient.builder()
                .name("Hot Sauce")
                .ingredientType(IngredientType.SAUCE)
                .build();

        Ingredient sauceMix = Ingredient.builder()
                .name("Sauce Mix")
                .ingredientType(IngredientType.SAUCE)
                .build();

        Ingredient cheese = Ingredient.builder()
                .name("Cheese")
                .ingredientType(IngredientType.OTHER)
                .build();

        Ingredient fries = Ingredient.builder()
                .name("Fries")
                .ingredientType(IngredientType.OTHER)
                .build();

        List<Ingredient> allIngredients = List.of(
                pita,
                tortilla,
                chicken,
                mutton,
                meatMix,
                falafel,
                cabbage,
                tomato,
                cucumber,
                onion,
                garlicSauce,
                bbqSauce,
                mildSauce,
                hotSauce,
                sauceMix,
                cheese,
                fries
        );

        ingredientRepository.saveAll(allIngredients);
    }

    private void initMeals() {

        List<Ingredient> salad = List.of(
                ingredientRepository.findByName("Cabbage").orElseThrow(() -> new IngredientNotFoundException("Cabbage")),
                ingredientRepository.findByName("Tomato").orElseThrow(() -> new IngredientNotFoundException("Tomato")),
                ingredientRepository.findByName("Cucumber").orElseThrow(() -> new IngredientNotFoundException("Cucumber")),
                ingredientRepository.findByName("Onion").orElseThrow(() -> new IngredientNotFoundException("Onion"))
        );

        EnumMap<Size, BigDecimal> pitaKebabSaladsPrices = new EnumMap<>(Size.class);

        pitaKebabSaladsPrices.put(Size.SMALL, new BigDecimal("20"));
        pitaKebabSaladsPrices.put(Size.MEDIUM, new BigDecimal("25"));
        pitaKebabSaladsPrices.put(Size.LARGE, new BigDecimal("30"));
        pitaKebabSaladsPrices.put(Size.XL, new BigDecimal("39"));

        Meal pitaKebabSalads = Meal.builder()
                .name("Pita Kebab Salads")
                .prices(pitaKebabSaladsPrices)
                .build();

        pitaKebabSalads.addIngredient(ingredientRepository.findByName("Pita").orElseThrow(
                () -> new IngredientNotFoundException("Pita")));
        salad.forEach(pitaKebabSalads::addIngredient);

        EnumMap<Size, BigDecimal> pitaKebabSaladsAndFriesPrices = new EnumMap<>(Size.class);

        pitaKebabSaladsAndFriesPrices.put(Size.SMALL, new BigDecimal("22"));
        pitaKebabSaladsAndFriesPrices.put(Size.MEDIUM, new BigDecimal("28"));
        pitaKebabSaladsAndFriesPrices.put(Size.LARGE, new BigDecimal("31"));
        pitaKebabSaladsAndFriesPrices.put(Size.XL, new BigDecimal("39"));

        Meal pitaKebabSaladsAndFries = Meal.builder()
                .name("Pita Kebab Salads and Fries")
                .prices(pitaKebabSaladsAndFriesPrices)
                .build();

        pitaKebabSaladsAndFries.addIngredient(ingredientRepository.findByName("Pita").orElseThrow(
                () -> new IngredientNotFoundException("Pita")));
        salad.forEach(pitaKebabSaladsAndFries::addIngredient);
        pitaKebabSaladsAndFries.addIngredient(ingredientRepository.findByName("Fries").orElseThrow(
                () -> new IngredientNotFoundException("Fries")));

        mealRepository.saveAll(List.of(pitaKebabSalads, pitaKebabSaladsAndFries));
    }

    private void initContacts() {

        Contact telephone = Contact.builder()
                .contactType(ContactType.TELEPHONE)
                .value("123456789")
                .build();

        Contact email = Contact.builder()
                .contactType(ContactType.EMAIL)
                .value("kontakt@miesianymiesiany.pl")
                .build();

        contactRepository.saveAll(List.of(telephone, email));
    }

    private void initJobOffers() {

        JobOffer jobOffer1 = JobOffer.builder()
                .positionName("Cook")
                .description("We are searching a cook with a lot of passion and with some experience in preparing kebabs!")
                .hourlyWage(BigDecimal.valueOf(25))
                .build();

        jobOffer1.addRequirement(
                JobRequirement.builder()
                .requirementType(JobRequirementType.MANDATORY)
                .description("1+ year of experience working on the similar position in other Kebab restaurant")
                .build()
        );
        jobOffer1.addRequirement(
                JobRequirement.builder()
                        .requirementType(JobRequirementType.MANDATORY)
                        .description("Health card")
                        .build()
        );

        jobOffer1.addRequirement(
                JobRequirement.builder()
                        .requirementType(JobRequirementType.MANDATORY)
                        .description("Ability to work in a fast-paced setting and manage pressure during peak hours")
                        .build()
        );
        jobOffer1.addRequirement(
                JobRequirement.builder()
                .requirementType(JobRequirementType.MANDATORY)
                .description("Smile :)")
                .build()
        );
        jobOffer1.addRequirement(
                JobRequirement.builder()
                        .requirementType(JobRequirementType.NICE_TO_HAVE)
                        .description("Willingness to work various shifts")
                        .build()
        );
        jobOffer1.addEmploymentType(new JobEmploymentType(EmploymentType.PERMANENT));
        jobOffer1.addEmploymentType(new JobEmploymentType(EmploymentType.MANDATE_CONTRACT));

        JobOffer jobOffer2 = JobOffer.builder()
                .positionName("Cashier")
                .description("We are searching a cashier with student status!")
                .hourlyWage(BigDecimal.valueOf(27.70))
                .build();

        jobOffer2.addRequirement(
                JobRequirement.builder()
                        .requirementType(JobRequirementType.MANDATORY)
                        .description("Student status")
                        .build()
        );
        jobOffer2.addRequirement(
                JobRequirement.builder()
                .requirementType(JobRequirementType.MANDATORY)
                .description("Smile :)")
                .build()
        );
        jobOffer2.addRequirement(
                JobRequirement.builder()
                        .requirementType(JobRequirementType.MANDATORY)
                        .description("Willingness to work various shifts")
                        .build()
        );
        jobOffer2.addEmploymentType(new JobEmploymentType(EmploymentType.MANDATE_CONTRACT));

        jobOfferRepository.save(jobOffer1);
        jobOfferRepository.save(jobOffer2);
    }

    private void initPromotions() {

        MealPromotion largeSizePromotion = MealPromotion.builder()
                .description("All medium and large sizes -20%!")
                .sizes(Set.of(Size.MEDIUM, Size.LARGE))
                .discountPercentage(BigDecimal.valueOf(20))
                .build();

        mealPromotionsRepository.save(largeSizePromotion);

        mealRepository.findAll()
            .forEach(meal -> {
                meal.getPromotions().add(largeSizePromotion);
                mealRepository.save(meal);
        });

        BeveragePromotion cocaColaPromotion = BeveragePromotion.builder()
                .description("All Coca-Cola -50%!")
                .discountPercentage(BigDecimal.valueOf(50))
                .build();

        beveragePromotionsRepository.save(cocaColaPromotion);

        beverageRepository.findAll().stream()
                .filter(beverage -> beverage.getName().contains("Coca-Cola"))
                .forEach(beverage -> {
                    beverage.setPromotion(cocaColaPromotion);
                    beverageRepository.save(beverage);
                });

        AddonPromotion friesPromotion = AddonPromotion.builder()
                .description("All fries -50%!")
                .discountPercentage(BigDecimal.valueOf(50))
                .build();

        addonPromotionsRepository.save(friesPromotion);

        addonRepository.findAll().stream()
                .filter(addon -> addon.getName().contains("Fries"))
                .forEach(addon -> {
                    addon.setPromotion(friesPromotion);
                    addonRepository.save(addon);
                });
    }

    private void initNewsletterSubscribers() {
        NewsletterSubscriber newsletterSubscriber = NewsletterSubscriber.builder()
                .subscriberFirstName("Wiktor")
                .email("example@example.com")
                .newsletterMessagesLanguage(NewsletterMessagesLanguage.POLISH)
                .isActive(true)
                .build();

        newsletterRepository.save(newsletterSubscriber);
    }

    private void initOrders() {

        Meal meal = mealRepository.findByName("Pita Kebab Salads and Fries")
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        Addon addon = addonRepository.findByName("Zapiekanka")
                .orElseThrow(() -> new RuntimeException("Addon not found"));
        Beverage beverage = beverageRepository.findByNameAndCapacity("Coca-Cola", BigDecimal.valueOf(0.33))
                .orElseThrow(() -> new RuntimeException("Beverage not found"));

        Order order1 = Order.builder()
                .orderType(OrderType.ON_SITE)
                .orderStatus(OrderStatus.IN_PREPARATION)
                .customerPhone("123456789")
                .customerEmail("example@example.com")
                .build();

        order1 = ordersRepository.save(order1);

        order1.addMeal(meal, Size.XL, 1);
        order1.addAddon(addon, 2);
        order1.addBeverage(beverage, 1);

        ordersRepository.save(order1);

        Order order2 = Order.builder()
                .orderType(OrderType.ON_SITE)
                .orderStatus(OrderStatus.RECEIVED)
                .customerPhone("123456789")
                .customerEmail("example@example.com")
                .build();

        order2 = ordersRepository.save(order2);

        order2.addMeal(meal, Size.XL, 1);
        order2.addBeverage(beverage, 1);
        order2.setCreatedAt(LocalDateTime.now().minusMonths(1));

        ordersRepository.save(order2);
    }

    private void initDiscountCodes() {
        DiscountCode discountCode1 = DiscountCode.builder()
                        .discountPercentage(BigDecimal.valueOf(50))
                        .expirationDate(LocalDate.now().minusMonths(1))
                        .remainingUses(1L)
                        .build();

        discountCodesRepository.save(discountCode1);

        DiscountCode discountCodeExpired = new DiscountCode(
                "expired", BigDecimal.valueOf(20), LocalDate.now().minusMonths(1), 1L);

        discountCodesRepository.save(discountCodeExpired);

        DiscountCode discountCode2 = new DiscountCode(
                "kodzik", BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L);

        discountCodesRepository.save(discountCode2);
    }
}
