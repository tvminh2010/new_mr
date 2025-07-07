package zve.com.vn.dto.order.response;

import java.math.BigDecimal;
import java.util.List;

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
public class PickingItemDto {
	String itemCategory;
	String itemCode;
	String itemName;
	BigDecimal requestqty;
	BigDecimal pickingqty;
	List<String> suggestedLocations;
}