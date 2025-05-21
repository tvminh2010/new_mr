package zve.com.vn.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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
}
