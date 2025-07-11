package zve.com.vn.service;import java.util.List;
import java.util.Optional;
import java.util.Set;

import zve.com.vn.entity.MaterialCore;

public interface MaterialCoreService {
	void saveAll(List<MaterialCore> materialCores);
	List<MaterialCore> findAll();
	Optional<MaterialCore> findById (Long coreId);
	MaterialCore save(MaterialCore materialCore);
	void delete(MaterialCore materialCore);
	//boolean existsByItemCode(String itemCode);
	public Set<String> getExistingItemCodes();
}
