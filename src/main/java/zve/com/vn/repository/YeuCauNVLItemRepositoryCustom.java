package zve.com.vn.repository;

import java.util.List;

import zve.com.vn.dto.order.response.ResponseOrderDto;

public interface YeuCauNVLItemRepositoryCustom {
	List<ResponseOrderDto> findAllItems (String model, Integer plan);
}
