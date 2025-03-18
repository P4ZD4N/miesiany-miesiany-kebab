package com.p4zd4n.kebab.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.p4zd4n.kebab.entities.interfaces.Observable;
import com.p4zd4n.kebab.entities.interfaces.Promotion;
import com.p4zd4n.kebab.utils.interfaces.Observer;
import jakarta.mail.MessagingException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "beverages_promotions")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BeveragePromotion extends WithTimestamp implements Promotion, Observable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @OneToMany(mappedBy = "promotion")
    @JsonIgnore
    private List<Beverage> beverages = new ArrayList<>();

    @Transient
    private List<Observer> observers = new ArrayList<>();

    @Builder
    public BeveragePromotion(String description, BigDecimal discountPercentage) {
        this.description = description;
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
