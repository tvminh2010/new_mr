package zve.com.vn.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PickingSuggestionItem {
	String serialNo;
	String locCode;
	BigDecimal qty;
	LocalDate dateIn;
}
