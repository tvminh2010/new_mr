package zve.com.vn.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import zve.com.vn.entity.MaterialCore;

@Component
public class MaterialCoreExcelImporter {

	private final MaterialCoreService materialCoreService;

	/* ---------------------------------------------------- */
	public MaterialCoreExcelImporter(MaterialCoreService materialCoreService) {
		this.materialCoreService = materialCoreService;
	}

	/* ---------------------------------------------------- */
	public String importExcel(MultipartFile file) {
	    List<MaterialCore> materialCores = new ArrayList<>();

	    try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
	        Sheet sheet = workbook.getSheetAt(0);
	        int rowStart = 1;

	        Set<String> existingItemCodes = materialCoreService.getExistingItemCodes();

	        for (int i = rowStart; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            if (row == null) continue;

	            String itemCode = getCellString(row.getCell(1));
	            if (itemCode == null || itemCode.isEmpty()) continue;
	            if (existingItemCodes.contains(itemCode)) continue;

	            String itemName  = getCellString(row.getCell(2));
	            String unit      = getCellString(row.getCell(4));
	            String coreType  = getCellString(row.getCell(5));
	            BigDecimal coreWeight = getCellDecimal(row.getCell(6));
	            BigDecimal rate = Optional.ofNullable(getCellDecimal(row.getCell(7)))
	                                      .orElse(getCellDecimal(row.getCell(8)));
	            String vendor = getCellString(row.getCell(9));

	            MaterialCore materialCore = new MaterialCore();
	            materialCore.setItemCode(itemCode);
	            materialCore.setItemName(itemName);
	            materialCore.setUnit(unit);
	            materialCore.setCoreType(coreType);
	            materialCore.setCoreWeight(coreWeight);
	            materialCore.setRate(rate);
	            materialCore.setVendor(vendor);
	            materialCores.add(materialCore);
	        }

	        materialCoreService.saveAll(materialCores);
	        return "Đã nhập thành công tổng số: " + materialCores.size() + " bản ghi.";

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Lỗi khi đọc file Excel: " + e.getMessage();
	    }
	}
	/* ---------------------------------------------------- */
	private String getCellString(Cell cell) {
		if (cell == null)
			return null;
		return switch (cell.getCellType()) {
		case STRING -> cell.getStringCellValue().trim();
		case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
		case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
		default -> null;
		};
	}

	/* ---------------------------------------------------- */
	/*
	 * private Integer getCellInteger(Cell cell) { try { if (cell == null) return
	 * null; if (cell.getCellType() == CellType.NUMERIC) { return (int)
	 * cell.getNumericCellValue(); } else { return
	 * Integer.parseInt(cell.getStringCellValue().trim()); } } catch (Exception e) {
	 * return null; } }
	 */
	/* ---------------------------------------------------- */
	private BigDecimal getCellDecimal(Cell cell) {
		try {
			if (cell == null)
				return null;
			if (cell.getCellType() == CellType.NUMERIC) {
				return BigDecimal.valueOf(cell.getNumericCellValue());
			} else {
				String value = cell.getStringCellValue().trim();
				return new BigDecimal(value);
			}
		} catch (Exception e) {
			return null;
		}
	}
	/* ---------------------------------------------------- */
}
