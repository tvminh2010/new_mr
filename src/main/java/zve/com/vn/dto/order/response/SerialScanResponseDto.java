package zve.com.vn.dto.order.response;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SerialScanResponseDto {

	String category;
    String itemCode;
    String itemName;
    String serialNo;
    String location;
    String lotNo;
    BigDecimal qty;
    Date receivingDate;
    String unit;
    
    String vendor;
    String coreType;
    BigDecimal coreWeight;
    BigDecimal rate;
    
    String message;
    String messageType;      // "success" | "error"
}
