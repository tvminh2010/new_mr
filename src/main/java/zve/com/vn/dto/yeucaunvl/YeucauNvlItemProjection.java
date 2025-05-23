package zve.com.vn.dto.yeucaunvl;

import java.math.BigDecimal;

public interface YeucauNvlItemProjection {
	String getCategory();
	String getModel();
	String getItemCode();
	Integer getDonggoi();
	BigDecimal getPlan();
	Integer getReceive();
	Integer getInstock();
}
