package zve.com.vn.dto.order.response;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Dùng để nhận dữ liệu từ query hiển thị trên màn hình yêu cầu NVL
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseOrderDto {
	String category;
	String itemCode;
	String itemName;
	Integer donggoi;
	BigDecimal qtyPlan;
	BigDecimal qtyReceive;
	BigDecimal qtyrequest;
	BigDecimal qtyreturn;
	Integer instock;
}