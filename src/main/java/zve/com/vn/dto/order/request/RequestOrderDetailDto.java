package zve.com.vn.dto.order.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RequestOrderDetailDto {
	private String itemCategory;
	private String itemCode;
	private String itemName;
    private BigDecimal qtyrequest;
}
