package zve.com.vn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import zve.com.vn.entity.WorkOrder;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, String> {
	Optional<WorkOrder> findByWoNumber(String woNumber);
	boolean existsByWoNumber(String woNumber);
}
