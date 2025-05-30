package zve.com.vn.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zve.com.vn.entity.WorkOrder;
import zve.com.vn.service.WorkOrderExcelImporter;
import zve.com.vn.service.WorkOrderService;

@Controller
public class WorkOrderController {

	private final WorkOrderExcelImporter excelImporter;
	private final WorkOrderService service;
	public WorkOrderController(WorkOrderExcelImporter excelImporter, WorkOrderService service) {
	    this.excelImporter = excelImporter;
	    this.service = service;
	}
	/* ------------------------------------------------- */
	@GetMapping("/workorder")
	public String index(Model model) {
		WorkOrder workOrder = new WorkOrder();
		List<WorkOrder> workOrderList = service.findAll();
		model.addAttribute("workOrder", workOrder);
		model.addAttribute("workOrderList", workOrderList);
		return "index";
	}
	/* ------------------------------------------------- */
	@GetMapping("/workorder/edit")
	public String editWorkOrder(@RequestParam(value = "wo_id", required = false) String woNumber, Model model) {

		WorkOrder workOrder = new WorkOrder();
		List<WorkOrder> workOrderList = service.findAll();

		if (woNumber != null) {
			Optional<WorkOrder> workOrderOpt = service.findByWoNumber(woNumber);
			if (workOrderOpt.isPresent()) {
				workOrder = workOrderOpt.get();
			}
		}
		model.addAttribute("workOrder", workOrder);
		model.addAttribute("workOrderList", workOrderList);
		return "index";
	}

	/* ---------------------------------------------------- */
	@PostMapping("/workorder/import")
	public String handleWorkOrderImport(@RequestParam("file") MultipartFile file, Model model,
			RedirectAttributes redirectAttributes) {

		String result = excelImporter.importExcel(file);
		redirectAttributes.addFlashAttribute("message", result);

		WorkOrder workOrder = new WorkOrder();
		List<WorkOrder> workOrderList = service.findAll();
		model.addAttribute("workOrder", workOrder);
		model.addAttribute("workOrderList", workOrderList);
		return "redirect:/workorder";
	}

	/* ------------------------------------------------- */
	@PostMapping("/workorder/save")
	public String saveOrEditWorkOrder(@ModelAttribute("workOrder") WorkOrder workOrderForm, BindingResult result,
	        RedirectAttributes redirectAttributes, Model model) {

	    if (result.hasErrors()) {
	        model.addAttribute("workOrder", workOrderForm);
	        model.addAttribute("message", "Lưu không thành công!");
	        return "index"; // Trả lại view form để người dùng sửa
	    }

	    WorkOrder workOrder = service.findByWoNumber(workOrderForm.getWoNumber())
	            .map(existing -> {
	                existing.setLine(workOrderForm.getLine());
	                existing.setModel(workOrderForm.getModel());
	                existing.setWoQty(workOrderForm.getWoQty());
	                return existing;
	            })
	            .orElseGet(() -> {
	                WorkOrder newOrder = new WorkOrder();
	                newOrder.setWoNumber(workOrderForm.getWoNumber());
	                newOrder.setLine(workOrderForm.getLine());
	                newOrder.setModel(workOrderForm.getModel());
	                newOrder.setWoQty(workOrderForm.getWoQty());
	                newOrder.setStatus(1);
	                return newOrder;
	            });

	    service.save(workOrder);
	    redirectAttributes.addFlashAttribute("message", "Lưu thành công!");
	    return "redirect:/workorder";
	}
	/* ------------------------------------------------- */
	@GetMapping("/workorder/del")
	public String deleteWorkOrder(@RequestParam("wo_id") String woNumber, RedirectAttributes redirectAttributes) {
		Optional<WorkOrder> workOrderOpt = service.findByWoNumber(woNumber);
		if (workOrderOpt.isPresent()) {
			service.delete(workOrderOpt.get());
			redirectAttributes.addFlashAttribute("message", "Xóa thành công!");
		} else {
			redirectAttributes.addFlashAttribute("message", "Không tìm thấy Work Order!");
		}
		return "redirect:/workorder";
	}
	/* ------------------------------------------------- */
}
