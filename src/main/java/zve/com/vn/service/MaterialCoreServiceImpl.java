package zve.com.vn.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import zve.com.vn.entity.MaterialCore;
import zve.com.vn.repository.MaterialCoreRepository;

@Service
public class MaterialCoreServiceImpl implements MaterialCoreService{
	
	private final MaterialCoreRepository repository;
	/* ---------------------------------------------------- */
	public MaterialCoreServiceImpl(MaterialCoreRepository repository) {
        this.repository = repository;
    }
	/* ---------------------------------------------------- */
	@Override
	public void saveAll(List<MaterialCore> materialCores) {
		repository.saveAll(materialCores);
	}
	/* ---------------------------------------------------- */
	@Override
	public List<MaterialCore> findAll() {
		return repository.findAll();
	}
	/* ---------------------------------------------------- */
	@Override
	public Optional<MaterialCore> findById(Long coreId) {
		return repository.findById(coreId);
	}
	/* ---------------------------------------------------- */
	@Override
	public MaterialCore save(MaterialCore materialCore) {
		return repository.save(materialCore);
	}
	/* ---------------------------------------------------- */
	@Override
	public void delete(MaterialCore materialCore) {
		repository.delete(materialCore);	
	}
	/* ---------------------------------------------------- */
	/*
	@Override
	public boolean existsByItemCode(String itemCode) {
		return repository.existsByItemCode(itemCode);
	} */
	/* ---------------------------------------------------- */
	@Override
	public Set<String> getExistingItemCodes() {
		return new HashSet<>(repository.findAllItemCodes());
	}
	/* ---------------------------------------------------- */
}
