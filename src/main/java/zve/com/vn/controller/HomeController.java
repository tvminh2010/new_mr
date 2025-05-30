package zve.com.vn.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import zve.com.vn.entity.WorkOrder;
import zve.com.vn.service.WorkOrderService;

@Controller
public class HomeController {
	
	private final WorkOrderService service;
	/* ------------------------------------------------- */
	public HomeController(WorkOrderService service) {
	    this.service = service;
	}
	/* ------------------------------------------------- */
	@GetMapping("/")
	public String index(Model model) {
		WorkOrder workOrder = new WorkOrder();
		List<WorkOrder> workOrderList = service.findAll();
		model.addAttribute("workOrder", workOrder);
		model.addAttribute("workOrderList", workOrderList);
		return "index";
	}
	/* ------------------------------------------------- */
}
