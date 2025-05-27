package zve.com.vn.dto.yeucaunvl;
import java.util.List;
import lombok.Data;

@Data
public class WorkOrderYcnvlRequest {
	private String woNumber;
    private List<WorkOrderYcnvlSessionDto> items;
}
