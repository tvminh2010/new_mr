package zve.com.vn.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import zve.com.vn.entity.WorkOrder;

@Component
public class WorkOrderExcelImporter {

    private final WorkOrderService workOrderService;

    /* ---------------------------------------------------- */
    public WorkOrderExcelImporter(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }
    
    /* ---------------------------------------------------- */
    public String importExcel(MultipartFile file) {
        List<WorkOrder> workOrders = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); 
            int rowStart = 1; 

            for (int i = rowStart; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String woNumber = getCellString(row.getCell(0));
                String line = getCellString(row.getCell(1));
                String model = getCellString(row.getCell(2));
                Integer plan = getCellInteger(row.getCell(3));

                if (woNumber == null || woNumber.isEmpty()) continue;
                if (workOrderService.existsByWoNumber(woNumber)) {
                    continue;
                }

                WorkOrder workOrder = new WorkOrder();
                workOrder.setWoNumber(woNumber);
                workOrder.setLine(line);
                workOrder.setModel(model);
                workOrder.setQty(plan);
                workOrders.add(workOrder);
            }
            workOrderService.saveAll(workOrders);
            return "Đã nhập thành công tổng số: " + workOrders.size() + " bản ghi.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi đọc file Excel: " + e.getMessage();
        }
    }

    /* ---------------------------------------------------- */
    private String getCellString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    /* ---------------------------------------------------- */
    private Integer getCellInteger(Cell cell) {
        try {
            if (cell == null) return null;
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else {
                return Integer.parseInt(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            return null;
        }
    }
    /* ---------------------------------------------------- */
}
