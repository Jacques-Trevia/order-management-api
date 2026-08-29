package ecommerce_order_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce_order_api.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
