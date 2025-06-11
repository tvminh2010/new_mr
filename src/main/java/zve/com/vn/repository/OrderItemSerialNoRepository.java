package zve.com.vn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zve.com.vn.entity.OrderItemSerialNo;

@Repository
public interface OrderItemSerialNoRepository extends JpaRepository<OrderItemSerialNo, Long> {
	Optional<OrderItemSerialNo> findBySerialNo(String serialNo);
}
