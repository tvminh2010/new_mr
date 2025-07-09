package zve.com.vn.dto.order.response;
import java.math.BigDecimal;
import java.util.Date;

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
public class DetailItemBySerialDto {
	String category;
	String itemCode;
	String itemName;
	String serialNo;
	String location;
	String lotNo;
	BigDecimal qty;
	Date receivingDate;
	String unit;
}