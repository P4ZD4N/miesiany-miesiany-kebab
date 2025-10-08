package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Order, Long> {}
