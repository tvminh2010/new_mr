package zve.com.vn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zve.com.vn.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	Optional<OrderItem> findByItemcode(String itemcode);
}
