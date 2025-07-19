package zve.com.vn.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import zve.com.vn.dto.order.request.WeightDto;
import zve.com.vn.dto.order.response.DetailItemBySerialWMSDto;
import zve.com.vn.dto.order.response.SerialScanResponseDto;
import zve.com.vn.repository.MaterialCoreRepository;
import zve.com.vn.repository.OrderItemSerialNoRepository;
import zve.com.vn.repository.ResponseItemBySerialRepository;
import zve.com.vn.service.ScaleWeightStorage;
import zve.com.vn.service.WorkOrderService;

@Controller
public class HoantraNVLController {

	private final WorkOrderService workOrderService;
	private final ResponseItemBySerialRepository itemBySerialRepository;
	private final MaterialCoreRepository materialCoreRepository;
	private final OrderItemSerialNoRepository orderItemSerialNoRepository;
	private final ScaleWeightStorage scaleWeightStorage;

	/* ---------------------------------------------------- */
	public HoantraNVLController(WorkOrderService workOrderService,
			ResponseItemBySerialRepository itemBySerialRepository,
			MaterialCoreRepository materialCoreRepository,
			OrderItemSerialNoRepository orderItemSerialNoRepository,
			ScaleWeightStorage scaleWeightStorage) {
		this.workOrderService = workOrderService;
		this.itemBySerialRepository = itemBySerialRepository;
		this.materialCoreRepository = materialCoreRepository;
		this.orderItemSerialNoRepository = orderItemSerialNoRepository;
		this.scaleWeightStorage = scaleWeightStorage;
	}

	/* ---------------------------------------------------- */
	@GetMapping("/hoantra-nvl")
	public String ycnvl(Model model) {
		List<String> lines = workOrderService.getAllLine();
		model.addAttribute("lines", lines);
		return "hoantranvl";
	}
	/* ------------------------------------------------- */
	@PostMapping("/hoantra-nvl/serial-scan")
	@ResponseBody
	public ResponseEntity<?> scan(@RequestBody Map<String, String> body) {
	    String serialNo = body.get("serialNo");
	    String workOrderCode = body.get("workOrderCode"); 
	    SerialScanResponseDto serialScanResponseDto = new SerialScanResponseDto();

	    if (serialNo == null || serialNo.trim().isEmpty()) {
	        serialScanResponseDto.setMessage("Chưa quét số serial");
	        serialScanResponseDto.setMessageType("error");
	        return ResponseEntity.ok(serialScanResponseDto);
	    }

	    if (!orderItemSerialNoRepository.existsBySerialNo(serialNo)) {
	        serialScanResponseDto.setMessage("Không tìm thấy số Serial vừa quét trong hệ thống");
	        serialScanResponseDto.setMessageType("error");
	        return ResponseEntity.ok(serialScanResponseDto);
	    }
	    
	    if(!orderItemSerialNoRepository.existsBySerialNoAndWorkOrderNumber(serialNo, workOrderCode)) {
	    	serialScanResponseDto.setMessage("Số Serial vừa quét và Workorder không khớp");
	    	 serialScanResponseDto.setMessageType("error");
	        return ResponseEntity.ok(serialScanResponseDto);
	    }

	    DetailItemBySerialWMSDto itemDto = itemBySerialRepository.detailItemBySerial(serialNo);
	    if (itemDto == null) {
	        serialScanResponseDto.setMessage("Không tồn tại số serial trong WMS");
	        serialScanResponseDto.setMessageType("error");
	        return ResponseEntity.ok(serialScanResponseDto); 
	    }

	    BeanUtils.copyProperties(itemDto, serialScanResponseDto);
	    materialCoreRepository.findByItemCode(itemDto.getItemCode()).ifPresent(coreInfo -> {
	        serialScanResponseDto.setVendor(coreInfo.getVendor());
	        serialScanResponseDto.setCoreType(coreInfo.getCoreType());
	        serialScanResponseDto.setCoreWeight(coreInfo.getCoreWeight());
	        serialScanResponseDto.setRate(coreInfo.getRate());
	    });

	    serialScanResponseDto.setMessage("Quét thành công số serial: '" + serialNo + "'");
	    serialScanResponseDto.setMessageType("success");
	    return ResponseEntity.ok(serialScanResponseDto);
	}

	/* ------------------------------------------------- */
	@PostMapping("/hoantra-nvl/weight")
	@ResponseBody
	public ResponseEntity<?> receiveWeight(@RequestBody WeightDto dto) {
		System.out.println("[RECEIVED] Weight received: " + dto.getWeight());
		
		scaleWeightStorage.setLatestWeight(dto.getWeight());
		return ResponseEntity.ok(dto);
	}
	/* ------------------------------------------------- */
	@GetMapping("/hoantra-nvl/weight-latest")
	@ResponseBody
	public ResponseEntity<String> getLatestWeight() {
	    return ResponseEntity.ok(scaleWeightStorage.getLatestWeight());
	}
	/* ------------------------------------------------- */
}
