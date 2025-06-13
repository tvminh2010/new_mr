package zve.com.vn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import zve.com.vn.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	List<OrderItem> findByItemcode(String itemcode);
}
