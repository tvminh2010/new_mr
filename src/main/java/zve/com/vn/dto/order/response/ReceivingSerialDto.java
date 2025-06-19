package zve.com.vn.dto.order.response;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceivingSerialDto {
	String itemcode;
	String serialNo;
	BigDecimal pickingQty;
	BigDecimal receivedQty;
}