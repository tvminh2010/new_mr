package zve.com.vn.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import zve.com.vn.entity.Order;
import zve.com.vn.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
	
	private final OrderRepository repository;
	/* ---------------------------------------------------- */
	public OrderServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }
	/* ---------------------------------------------------- */
	@Override
	public Optional<Order> findByWoNumberandStatus(String woNumber) {
		return repository.findActiveOrderByWoNumber(woNumber);
	}
	/* ---------------------------------------------------- */
	@Override
	public List<Order> findByStatus(Integer status) {
		return repository.findByStatus(status);
	}
	/* ---------------------------------------------------- */
	@Override
	public List<Order> fileAllOrder() {
		return repository.findAllByOrderByStatusAsc();
	}
	/* ---------------------------------------------------- */
	@Override
	public String generateOrderName() {
		LocalDate today = LocalDate.now();
	    String datePart = today.format(DateTimeFormatter.ofPattern("MMddyyyy"));
	    Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
	    Date endOfDay = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	    int countToday = repository.countByCreatedDateBetween(startOfDay, endOfDay);
	    String sequence = String.format("%03d", countToday + 1);
	    return "OD" + datePart + "-" + sequence;
	}
	/* ---------------------------------------------------- */
	@Override
	public int updateOrderByStatus(Long id, Integer status) {
		// TODO Auto-generated method stub
		return repository.updateOrderByStatus(id, status);
	}
	/* ---------------------------------------------------- */
	@Override
	public Optional<Order> findById(Long id) {
		return repository.findById(id);
	}
	/* ---------------------------------------------------- */
	@Override
	public Order save(Order order) {
		return repository.save(order);
	}
	/* ---------------------------------------------------- */
}
