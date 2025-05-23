package zve.com.vn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import zve.com.vn.dto.yeucaunvl.YeucauNvlItemProjection;
import zve.com.vn.entity.WorkOrder;
import zve.com.vn.repository.YeuCauNVLItemRepositoryCustom;
import zve.com.vn.service.WorkOrderExcelImporter;
import zve.com.vn.service.WorkOrderService;

@Controller
public class HomeController {
	
	@Autowired
	private WorkOrderExcelImporter excelImporter;
	
	@Autowired
	private WorkOrderService service;
	
	@Autowired
	private YeuCauNVLItemRepositoryCustom nvlRepository;
	
	/* ------------------------------------------------- */
	@GetMapping({"/", "/workorder"})
	public String index(Model model) {
		
		WorkOrder workOrder = new WorkOrder();
	    List<WorkOrder> workOrderList = service.findAll();
	    model.addAttribute("workOrder", workOrder);
	    model.addAttribute("workOrderList", workOrderList);
		    
		return "index";
	}
	/* ------------------------------------------------- */
	@GetMapping("/workorder/edit")
	 public String editWorkOrder(
			 @RequestParam(value = "wo_id", required = false) String woNumber, 
			 Model model) {
		
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
	public String handleWorkOrderImport(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
		
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
	public String saveOrEditWorkOrder(@ModelAttribute("workOrder") WorkOrder workOrderForm,
	        BindingResult result,
	        RedirectAttributes redirectAttributes) {

	    if (result.hasErrors()) {
	        redirectAttributes.addFlashAttribute("message", "Lưu không thành công!");
	        return "redirect:/workorder";
	    }

	    Optional<WorkOrder> existingWorkOrderOpt = service.findByWoNumber(workOrderForm.getWoNumber());
	    WorkOrder workOrder;
	    if (existingWorkOrderOpt.isPresent()) {
	        workOrder = existingWorkOrderOpt.get();
	        workOrder.setLine(workOrderForm.getLine());
	        workOrder.setModel(workOrderForm.getModel());
	        workOrder.setQty(workOrderForm.getQty());
	    } else {
	        workOrder = new WorkOrder();
	        workOrder.setWoNumber(workOrderForm.getWoNumber());
	        workOrder.setLine(workOrderForm.getLine());
	        workOrder.setModel(workOrderForm.getModel());
	        workOrder.setQty(workOrderForm.getQty());
	        workOrder.setStatus(1);
	    }

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
	/* ---------------------------------------------------- */
	@GetMapping("/ycnvl")
	public String ycnvl(Model model) {
	    List<String> lines = service.getAllLine();
	    model.addAttribute("lines", lines);
		return "ycnvl";
	}
	/* ------------------------------------------------- */
	@GetMapping("/ycnvl/by-line")
	@ResponseBody
	public List<String> getWorkOrdersByLine(@RequestParam("line") String line) {
	    return service.getAllWoNumberByLine(line);
	}
	/* ------------------------------------------------- */
	@GetMapping("/ycnvl/workorder-info")
	@ResponseBody
	public Map<String, Object> getWorkOrderInfo(@RequestParam("woNumber") String woNumber) {
	    WorkOrder workOrder = service.findByWoNumber(woNumber).orElse(null);

	    Map<String, Object> result = new HashMap<>();
	    if (workOrder != null) {
	        result.put("model", workOrder.getModel());
	        result.put("plan", String.valueOf(workOrder.getQty()));
	        List<YeucauNvlItemProjection> nvlList = nvlRepository.findAllItems(workOrder.getModel(), workOrder.getQty());
	        result.put("materials", nvlList);
	    }
	    return result;
	}
	/* ------------------------------------------------- */
}
