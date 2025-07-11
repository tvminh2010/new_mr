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

import zve.com.vn.entity.MaterialCore;
import zve.com.vn.entity.WorkOrder;
import zve.com.vn.service.MaterialCoreExcelImporter;
import zve.com.vn.service.MaterialCoreService;

@Controller
public class MaterialCoreController {
	
	private final MaterialCoreService service;
	private final MaterialCoreExcelImporter excelImporter;
	/* ------------------------------------------------- */
	public MaterialCoreController(MaterialCoreService service, MaterialCoreExcelImporter excelImporter) {
	    this.service = service;
	    this.excelImporter = excelImporter;
	}
	/* ------------------------------------------------- */
	@GetMapping("/materialcore")
	public String materialCoreIndex(Model model,
			RedirectAttributes redirectAttributes) {
		
		MaterialCore materialCore = new MaterialCore();
		List<MaterialCore> materialCoreList = service.findAll();
		model.addAttribute("materialCoreList", materialCoreList);
		model.addAttribute("materialCore", materialCore);
		return "materialcore";
	}
	/* ------------------------------------------------- */
	@GetMapping("/materialcore/edit")
	public String editMaterialCore(@RequestParam(value = "core_id", required = false) Long coreId, Model model) {

		MaterialCore materialCore = new MaterialCore();
		List<MaterialCore> materialCoreList = service.findAll();

		if (coreId != null) {
			Optional<MaterialCore> materialCoreOpt = service.findById(coreId);
			if (materialCoreOpt.isPresent()) {
				materialCore = materialCoreOpt.get();
			}
		}
		model.addAttribute("materialCore", materialCore);
		model.addAttribute("materialCoreList", materialCoreList);
		return "materialcore";
	}
	/* ---------------------------------------------------- */
	@PostMapping("/materialcore/save")
	public String saveOrEditMaterialCore(@ModelAttribute("materialCore") MaterialCore materialCoreForm,
	                                     BindingResult result,
	                                     RedirectAttributes redirectAttributes,
	                                     Model model) {
	    try {
	        MaterialCore materialCore;
	        if (materialCoreForm.getId() != null) {
	            materialCore = service.findById(materialCoreForm.getId())
	                                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bản ghi để cập nhật."));
	        } else {
	            materialCore = new MaterialCore();
	        }
	        materialCore.setItemCode(materialCoreForm.getItemCode());
	        materialCore.setItemName(materialCoreForm.getItemName());
	        materialCore.setCoreType(materialCoreForm.getCoreType());
	        materialCore.setCoreWeight(materialCoreForm.getCoreWeight());
	        materialCore.setRate(materialCoreForm.getRate());
	        materialCore.setVendor(materialCoreForm.getVendor());
	        service.save(materialCore);
	        redirectAttributes.addFlashAttribute("message", "Lưu thành công!");
	        return "redirect:/materialcore";

	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("message", "Lưu không thành công: " + e.getMessage());
	        return "materialcore";
	    }
	}
	/* ------------------------------------------------- */
	@GetMapping("/materialcore/del")
	public String deleteMaterialCore(@RequestParam("core_id") Long coreId, RedirectAttributes redirectAttributes) {
		Optional<MaterialCore> materialCoreOpt = service.findById(coreId);
		if (materialCoreOpt.isPresent()) {
			service.delete(materialCoreOpt.get());
			redirectAttributes.addFlashAttribute("message", "Xóa thành công!");
		} else {
			redirectAttributes.addFlashAttribute("message", "Không tìm thấy Work Order!");
		}
		return "redirect:/materialcore";
	}
	/* ------------------------------------------------- */
	@PostMapping("/materialcore/import")
	public String handleWorkOrderImport(@RequestParam("file") MultipartFile file, Model model,
			RedirectAttributes redirectAttributes) {
		
		String result = excelImporter.importExcel(file);
		redirectAttributes.addFlashAttribute("message", result);
		
		MaterialCore materialcore = new MaterialCore();
		List<MaterialCore> materialCoreList = service.findAll();
		
		model.addAttribute("materialcore", materialcore);
		model.addAttribute("materialCoreList", materialCoreList);
		return "redirect:/materialcore";
		
	}
	/* ------------------------------------------------- */
}
