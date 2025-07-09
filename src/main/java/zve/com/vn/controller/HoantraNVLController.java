package zve.com.vn.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import zve.com.vn.dto.order.response.DetailItemBySerialDto;
import zve.com.vn.repository.ResponseItemBySerialRepository;
import zve.com.vn.service.WorkOrderService;

@Controller
public class HoantraNVLController {


	private final WorkOrderService workOrderService;
	private final ResponseItemBySerialRepository itemBySerialRepository;
	/* ---------------------------------------------------- */
	public HoantraNVLController(WorkOrderService workOrderService, ResponseItemBySerialRepository itemBySerialRepository) {
		this.workOrderService = workOrderService;
		this.itemBySerialRepository = itemBySerialRepository;
	}
	/* ---------------------------------------------------- */
	@GetMapping("/hoantra-nvl")	public String ycnvl(Model model) {
		List<String> lines = workOrderService.getAllLine();
		model.addAttribute("lines", lines);
		return "hoantranvl";
	}
	/* ------------------------------------------------- */
	@PostMapping("/hoantra-nvl/serial-scan")
	@ResponseBody
	public ResponseEntity<?> scan(@RequestBody Map<String,String> body) {
	    String serialNo = body.get("serialNo");
	    DetailItemBySerialDto result = itemBySerialRepository.detailItemBySerial(serialNo);
	    return ResponseEntity.ok(result);
	}
	/* ------------------------------------------------- */
}
