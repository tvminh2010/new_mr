package zve.com.vn.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import zve.com.vn.entity.Order;
import zve.com.vn.entity.WorkOrder;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	 // Tìm order theo WorkOrder và status
    Optional<Order> findByWorkOrderAndStatus(WorkOrder workOrder, Integer status);

    // Hoặc nếu muốn dùng theo woNumber (vì woNumber là duy nhất)
    @Query("SELECT o FROM Order o WHERE o.workOrder.woNumber = :woNumber AND o.status = 1")
    Optional<Order> findActiveOrderByWoNumber(@Param("woNumber") String woNumber);
    
    List<Order> findByStatus(Integer status);
    List<Order> findAllByOrderByStatusAsc();
    int countByCreatedDateBetween(Date start, Date end);
    
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    int updateOrderByStatus(Long id, Integer status);

}
