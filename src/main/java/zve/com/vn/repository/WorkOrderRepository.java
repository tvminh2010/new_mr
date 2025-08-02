package zve.com.vn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import zve.com.vn.entity.Order;
import zve.com.vn.entity.WorkOrder;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, String> {
	Optional<WorkOrder> findByWoNumber(String woNumber);
	boolean existsByWoNumber(String woNumber);
	
	@Query("SELECT DISTINCT w.line FROM WorkOrder w WHERE w.line IS NOT NULL")
    List<String> findAllDistinctLines();
	
	@Query("SELECT w.woNumber FROM WorkOrder w WHERE w.line = :line")
	List<String> findAllWoNumberByLine(@Param("line") String line);
	
	@Query("SELECT w.model FROM WorkOrder w WHERE w.line = :line")
	List<String> findAllModelByLine(@Param("line") String line);
	
	@Query("SELECT w.woNumber FROM WorkOrder w WHERE w.line = :line")
	Order findOrderByStatus(@Param("OrderStatus") Long orderStatus);
	
	Optional<WorkOrder> findByLineAndModel(String line, String model);
}
