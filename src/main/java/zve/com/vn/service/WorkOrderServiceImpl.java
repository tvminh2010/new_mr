package zve.com.vn.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import zve.com.vn.entity.OrderItemSerialNo;
import zve.com.vn.entity.WorkOrder;
import zve.com.vn.repository.WorkOrderRepository;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {
	
	private final WorkOrderRepository repository;
	/* ---------------------------------------------------- */
	public WorkOrderServiceImpl(WorkOrderRepository repository) {
        this.repository = repository;
    }
	/* ---------------------------------------------------- */
	@Override
	public WorkOrder save(WorkOrder workOrder) {
		return repository.save(workOrder);
	}
	/* ---------------------------------------------------- */
	@Override
	public List<WorkOrder> findAll() {
		return repository.findAll();
	}
	/* ---------------------------------------------------- */
	@Override
	public Optional<WorkOrder> findByWoNumber(String woNumber) {
		 return repository.findByWoNumber(woNumber);
	}
	/* ---------------------------------------------------- */
	@Override
	public void delete(WorkOrder workOrder) {
		repository.delete(workOrder);	
	}
	/* ---------------------------------------------------- */
	@Override
	@Transactional
	public void saveAll(List<WorkOrder> workOrders) {
		repository.saveAll(workOrders);
	}
	/* ---------------------------------------------------- */
	@Override
	public boolean existsByWoNumber(String woNumber) {
		return repository.existsByWoNumber(woNumber);
	}
	/* ---------------------------------------------------- */
	@Override
	public List<String> getAllLine() {
		return repository.findAllDistinctLines();
	}
	/* ---------------------------------------------------- */
	@Override
	public List<String> getAllWoNumberByLine(String line) {
		return repository.findAllWoNumberByLine(line);
	}
	/* ---------------------------------------------------- */
	@Override
	public Optional<WorkOrder> findById(String id) {
		return repository.findById(id);
	}
	/* ---------------------------------------------------- */
	@Transactional
	@Override
	public BigDecimal calculateTotalReceivedQtyByItemCode(WorkOrder workOrder, String itemCode) {
		  return workOrder.getOrders().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .flatMap(orderItem -> orderItem.getOrderItemSerialNos().stream())
                .filter(orderItemSerialNo -> orderItemSerialNo.getItemcode().equals(itemCode))
                .map(OrderItemSerialNo::getReceivedQty)
                .filter(receivedQty -> receivedQty != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	/* ---------------------------------------------------- */
	@Override
	public BigDecimal calculateTotalReturnQtyByItemCode(WorkOrder workOrder, String itemCode) {
		return workOrder.getOrders().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .flatMap(orderItem -> orderItem.getOrderItemSerialNos().stream())
                .filter(orderItemSerialNo -> orderItemSerialNo.getItemcode().equals(itemCode))
                .map(OrderItemSerialNo::getReturnQty)
                .filter(returnQty -> returnQty != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	/* ---------------------------------------------------- */
	@Override
	public List<String> getAllModelByLine(String line) {
		return repository.findAllModelByLine(line);
	}
	/* ---------------------------------------------------- */
	 @Override
	    public Optional<WorkOrder> findByLineAndModel(String line, String model) {
	        return repository.findByLineAndModel(line, model);
	    }
	/* ---------------------------------------------------- */
}
