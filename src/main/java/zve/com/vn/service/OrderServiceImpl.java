package zve.com.vn.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import zve.com.vn.entity.Order;
import zve.com.vn.repository.OrderItemSerialNoRepository;
import zve.com.vn.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
	
	private final OrderRepository repository;
	private final OrderItemSerialNoRepository serialNoRepository;
	/* ---------------------------------------------------- */
	public OrderServiceImpl(OrderRepository repository, OrderItemSerialNoRepository serialNoRepository) {
        this.repository = repository;
        this.serialNoRepository = serialNoRepository;
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
	@Override
	public boolean isSerialExistsInOrder(Long orderId, String serialNo) {
		return repository.existsBySerialNoAndOrderId(serialNo, orderId);
	}
	/* ---------------------------------------------------- */
	@Override
	public boolean isReceivingComplete(Long orderId) {
	    System.out.println("==> ĐANG VÀO isReceivingComplete(orderId=" + orderId + ")");

	    Object row = serialNoRepository.getTotalPickingAndReceivedQtyByOrderId(orderId);

	    if (!(row instanceof Object[] resultArray) || resultArray.length < 2) {
	        System.out.println("⚠️ Không lấy được dữ liệu tổng Picking/Received");
	        return false;
	    }

	    BigDecimal totalPicking = toBigDecimal(resultArray[0]);
	    BigDecimal totalReceived = toBigDecimal(resultArray[1]);

	    System.out.println("-----------------------------------------");
	    System.out.println("Picking: " + totalPicking + ", Received: " + totalReceived);

	    return totalPicking.compareTo(totalReceived) == 0;
	}
	/* ---------------------------------------------------- */
	@Override
	public void deleteOrderByOrderName(String orderName) {
		 Optional<Order> orderOpt = repository.findByOrderName(orderName);
		    if (orderOpt.isPresent()) {
		    	repository.delete(orderOpt.get());
		    } else {
		        throw new RuntimeException("Không tìm thấy order: " + orderName);
		    }
	}
	/* ---------------------------------------------------- */
	private BigDecimal toBigDecimal(Object value) {
	    if (value instanceof BigDecimal) {
	        return (BigDecimal) value;
	    } else if (value instanceof Number) {
	        return BigDecimal.valueOf(((Number) value).doubleValue());
	    } else if (value != null) {
	        try {
	            return new BigDecimal(value.toString());
	        } catch (NumberFormatException e) {
	        }
	    }
	    return BigDecimal.ZERO;
	}
	/* ---------------------------------------------------- */
}
