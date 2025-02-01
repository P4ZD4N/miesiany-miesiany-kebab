package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.BeveragePromotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeveragePromotionsRepository extends JpaRepository<BeveragePromotion, Long> {
}
