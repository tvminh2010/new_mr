package zve.com.vn.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import zve.com.vn.dto.order.request.WeightDto;
import zve.com.vn.dto.order.response.DetailItemBySerialWMSDto;
import zve.com.vn.dto.order.response.SerialScanResponseDto;
import zve.com.vn.entity.OrderItemSerialNo;
import zve.com.vn.repository.MaterialCoreRepository;
import zve.com.vn.repository.OrderItemSerialNoRepository;
import zve.com.vn.repository.ResponseItemBySerialRepository;
import zve.com.vn.service.WorkOrderService;

@Controller
public class HoantraNVLController {

	private final WorkOrderService workOrderService;
	private final ResponseItemBySerialRepository itemBySerialRepository;
	private final MaterialCoreRepository materialCoreRepository;
	private final OrderItemSerialNoRepository orderItemSerialNoRepository;
	private final SimpMessagingTemplate messagingTemplate;

	/* ---------------------------------------------------- */
	public HoantraNVLController(WorkOrderService workOrderService,
			ResponseItemBySerialRepository itemBySerialRepository, MaterialCoreRepository materialCoreRepository,
			OrderItemSerialNoRepository orderItemSerialNoRepository, SimpMessagingTemplate messagingTemplate) {
		this.workOrderService = workOrderService;
		this.itemBySerialRepository = itemBySerialRepository;
		this.materialCoreRepository = materialCoreRepository;
		this.orderItemSerialNoRepository = orderItemSerialNoRepository;
		this.messagingTemplate = messagingTemplate;
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

		if (!orderItemSerialNoRepository.existsBySerialNoAndWorkOrderNumber(serialNo, workOrderCode)) {
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
		messagingTemplate.convertAndSend("/topic/scale-weight", dto.getWeight());
		return ResponseEntity.ok().build();
	}

	/* ------------------------------------------------- */
	@PostMapping("/hoantra-nvl/print_and_save")
	@ResponseBody
	public ResponseEntity<?> printAndSaveLabel(@RequestBody Map<String, Object> payload) {
		try {
			String serialNo = payload.get("serialNo").toString().trim();
			Object quantityObj = payload.get("quantity");

			System.out.println("REturn Quantiy: " + quantityObj);

			if (serialNo == null || serialNo.isEmpty()) {
				return ResponseEntity.badRequest().body("SerialNo không được để trống");
			}

			BigDecimal quantity = BigDecimal.ZERO;
			if (quantityObj != null) {
				quantity = new BigDecimal(quantityObj.toString());
			}

			if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
				return ResponseEntity.badRequest().body("Trọng lượng cân phải lớn hơn 0");
			}

			Optional<OrderItemSerialNo> serialOpt = orderItemSerialNoRepository.findBySerialNo(serialNo);
			if (serialOpt.isEmpty()) {
				return ResponseEntity.badRequest().body("Không tìm thấy serial: " + serialNo);
			}

			String newSerialNo = generateNextSerialNo();
	        while (orderItemSerialNoRepository.findByNewSerialNo(newSerialNo).isPresent()) {
	            newSerialNo = generateNextSerialNo(); 
	        }
	        
			OrderItemSerialNo serialEntity = serialOpt.get();
			
			serialEntity.setReturnQty(quantity);
			serialEntity.setNewSerialNo(newSerialNo);

			orderItemSerialNoRepository.save(serialEntity);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "In và lưu thành công new serial: " + newSerialNo);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý dữ liệu: " + e.getMessage());
		}

	}
	/* ------------------------------------------------- */
	private String generateNextSerialNo() {
	    String datePart = new SimpleDateFormat("ddMMyy").format(new Date());
	    Optional<String> lastSerialOpt = orderItemSerialNoRepository.findLatestSerialNoForToday(datePart);
	    String lastSerialNo = lastSerialOpt.orElse(datePart + "000000");
	    int lastSerialNumber = Integer.parseInt(lastSerialNo.substring(6)); 
	    int newSerialNumber = lastSerialNumber + 1;
	    String newSerialNumberPart = String.format("%06d", newSerialNumber);
	    return datePart + newSerialNumberPart;
	}

	/* ------------------------------------------------- */
}
