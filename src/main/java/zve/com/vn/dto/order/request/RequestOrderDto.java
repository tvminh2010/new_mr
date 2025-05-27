package zve.com.vn.dto.order.request;
import java.util.List;
import lombok.Data;

@Data
public class RequestOrderDto {
	private String woNumber;
    private List<RequestOrderDetailDto> items;
}
