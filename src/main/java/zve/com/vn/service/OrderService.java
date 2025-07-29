package zve.com.vn.service;

import java.util.List;
import java.util.Optional;

import zve.com.vn.entity.Order;

public interface OrderService {
	Optional<Order> findByWoNumberandStatus(String woNumber);
	Optional<Order> findById(Long id);
	List<Order> findByStatus(Integer status);
	List<Order> fileAllOrder();
	public String generateOrderName();
	public int updateOrderByStatus(Long id, Integer status);
	public Order save(Order order);
	public boolean isSerialExistsInOrder(Long orderId, String serialNo);

}
