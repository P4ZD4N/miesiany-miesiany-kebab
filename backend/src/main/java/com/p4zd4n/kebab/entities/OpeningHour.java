package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.DayOfWeek;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "opening_hours")
@Getter
@Setter
@NoArgsConstructor
public class OpeningHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "day_of_week", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Builder
    public OpeningHour(DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
        this.dayOfWeek = dayOfWeek;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
}