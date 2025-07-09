package zve.com.vn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import zve.com.vn.service.MaterialCoreService;

@Controller
public class MaterialCoreController {
	
	private final MaterialCoreService service;
	/* ------------------------------------------------- */
	public MaterialCoreController(MaterialCoreService service) {
	    this.service = service;
	}
	/* ------------------------------------------------- */
	@GetMapping("/materialcore")
	public String materialCoreIndex(Model model,
			RedirectAttributes redirectAttributes) {
		return "materialcore";
	}
	/* ------------------------------------------------- */
	@PostMapping("/materialcore/import")
	public String handleWorkOrderImport(@RequestParam("file") MultipartFile file, Model model,
			RedirectAttributes redirectAttributes) {
		return null;
	}
	/* ------------------------------------------------- */

}
