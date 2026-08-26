package ecommerce_order_api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ecommerce_order_api.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT obj FROM Order obj JOIN FETCH obj.items WHERE obj.id IN :orderIds")
	List<Order> searchOrdersWithItems(List<Long> orderIds);
}
