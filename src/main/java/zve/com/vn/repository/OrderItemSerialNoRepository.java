package zve.com.vn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zve.com.vn.entity.OrderItemSerialNo;

@Repository
public interface OrderItemSerialNoRepository extends JpaRepository<OrderItemSerialNo, Long> {
	/* -------------------------------------------------------------- */
	Optional<OrderItemSerialNo> findBySerialNo(String serialNo);

	/* -------------------------------------------------------------- */
	boolean existsBySerialNo(String serialNo);

	/* -------------------------------------------------------------- */
	@Query("""
			SELECT COUNT(s) > 0
			FROM OrderItemSerialNo s
			WHERE s.serialNo = :serialNo
			AND s.orderItem.order.workOrder.woNumber = :woNumber
			""")
	boolean existsBySerialNoAndWorkOrderNumber(@Param("serialNo") String serialNo, @Param("woNumber") String woNumber);

	/* -------------------------------------------------------------- */
	Optional<OrderItemSerialNo> findBySerialNoAndOrderItem_Order_Id(String serialNo, Long orderId);
	Optional<OrderItemSerialNo> findBySerialNoAndOrderItem_Id(String serialNo, Long orderItemId);
	 Optional<OrderItemSerialNo> findByNewSerialNo(String newSerialNo);
	/* -------------------------------------------------------------- */
	@Query("SELECT s.serialNo FROM OrderItemSerialNo s WHERE s.serialNo LIKE :datePrefix% ORDER BY s.serialNo DESC")
	Optional<String> findLatestSerialNoForToday(@Param("datePrefix") String datePrefix);
	/* -------------------------------------------------------------- */
}
