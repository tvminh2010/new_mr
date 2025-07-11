package zve.com.vn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import zve.com.vn.entity.MaterialCore;

public interface MaterialCoreRepository extends JpaRepository<MaterialCore, Long> {
	 boolean existsByItemCode(String itemCode);
	 
	 @Query("SELECT m.itemCode FROM MaterialCore m")
	 List<String> findAllItemCodes();
	 Optional<MaterialCore> findByItemCode(String itemCode);
}
