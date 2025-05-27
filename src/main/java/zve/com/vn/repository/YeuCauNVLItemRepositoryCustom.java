package zve.com.vn.repository;

import java.util.List;

import zve.com.vn.dto.yeucaunvl.YeucauNvlItemDto;

public interface YeuCauNVLItemRepositoryCustom {
	List<YeucauNvlItemDto> findAllItems (String model, Integer plan);
}
