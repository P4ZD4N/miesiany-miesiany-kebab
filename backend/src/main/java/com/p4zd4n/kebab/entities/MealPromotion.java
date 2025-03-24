package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.p4zd4n.kebab.entities.interfaces.Observable;
import com.p4zd4n.kebab.entities.interfaces.Promotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.utils.interfaces.Observer;
import jakarta.mail.MessagingException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "meals_promotions")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MealPromotion extends WithTimestamp implements Promotion, Observable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @ElementCollection(targetClass = Size.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "promotion_sizes", joinColumns = @JoinColumn(name = "promotion_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Set<Size> sizes;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @ManyToMany(mappedBy = "promotions")
    @JsonIgnore
    private List<Meal> meals = new ArrayList<>();

    @Transient
    @JsonIgnore
    private List<Observer> observers = new ArrayList<>();

    @Builder
    public MealPromotion(String description, Set<Size> sizes, BigDecimal discountPercentage) {
        this.description = description;
        this.sizes = sizes;
        this.discountPercentage = discountPercentage;
    }

    @Override
    public void registerObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() throws MessagingException {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}
