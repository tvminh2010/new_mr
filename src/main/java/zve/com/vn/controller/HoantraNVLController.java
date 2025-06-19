package zve.com.vn.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import zve.com.vn.repository.ResponseOrderRepository;
import zve.com.vn.service.OrderService;
import zve.com.vn.service.WorkOrderService;

@Controller
public class HoantraNVLController {

	private final OrderService orderService;
	private final WorkOrderService workOrderService;
	private final ResponseOrderRepository nvlRepository;
	/* ---------------------------------------------------- */
	public HoantraNVLController(OrderService orderService, ResponseOrderRepository nvlRepository, WorkOrderService workOrderService) {
		this.orderService = orderService;
		this.nvlRepository = nvlRepository;
		this.workOrderService = workOrderService;
	}
	/* ---------------------------------------------------- */
	@GetMapping("/hoantra-nvl")	public String ycnvl(Model model) {
		List<String> lines = workOrderService.getAllLine();
		model.addAttribute("lines", lines);
		return "hoantranvl";
	}
	/* ------------------------------------------------- */
}
