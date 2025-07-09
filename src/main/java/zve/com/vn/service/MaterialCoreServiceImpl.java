package zve.com.vn.service;

import java.util.List;

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
}
