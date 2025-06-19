package zve.com.vn.dto.order.response;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Dùng để nhận dữ liệu từ query hiển thị trên màn hình Picking trên tablet
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceivingItemDto {
	String itemCode;
	String itemName;
	BigDecimal requestqty;
	BigDecimal pickingqty;
	BigDecimal receivingqty;
}
