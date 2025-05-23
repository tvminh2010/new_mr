package zve.com.vn.repository;

import java.util.List;
import zve.com.vn.dto.yeucaunvl.YeucauNvlItemProjection;

public interface YeuCauNVLItemRepositoryCustom {
	List<YeucauNvlItemProjection> findAllItems (String model, Integer plan);
}
