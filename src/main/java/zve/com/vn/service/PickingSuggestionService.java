package zve.com.vn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import zve.com.vn.dto.order.request.PickingSerialNo;
import zve.com.vn.dto.order.response.PickingSuggestionGroupDto;
import zve.com.vn.repository.PickingSuggestionRepository;

@Service
public class PickingSuggestionService {
	/* ---------------------------------------------------------- */
	private final PickingSuggestionRepository repository;
    public PickingSuggestionService(PickingSuggestionRepository repository) {
        this.repository = repository;
    }
    /* ---------------------------------------------------------- */
    public List<PickingSuggestionGroupDto> findSuggestionsByProductNos(List<String> productNos) {
        return repository.findSuggestionsByProductNos(productNos);
    }
    /* ---------------------------------------------------------- */
    public PickingSerialNo getItemBySerialNo(String serialNo) {
    	return repository.getItemBySerialNo(serialNo);
    }
    /* ---------------------------------------------------------- */
}
