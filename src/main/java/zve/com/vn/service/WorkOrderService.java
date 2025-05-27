package zve.com.vn.service;

import java.util.List;
import java.util.Optional;
import zve.com.vn.entity.WorkOrder;

public interface WorkOrderService {
	WorkOrder save(WorkOrder workOrder);
	void saveAll(List<WorkOrder> workOrders);
	List<WorkOrder> findAll();
	Optional<WorkOrder> findByWoNumber(String woNumber);
	Optional<WorkOrder> findById (String woNumber);
	void delete(WorkOrder workOrder);
	boolean existsByWoNumber(String woNumber);
	List<String> getAllLine();
	List<String> getAllWoNumberByLine(String line);
}
