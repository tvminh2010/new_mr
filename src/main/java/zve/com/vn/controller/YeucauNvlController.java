package zve.com.vn.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import zve.com.vn.dto.order.request.RequestOrderDetailDto;
import zve.com.vn.dto.order.request.RequestOrderDto;
import zve.com.vn.dto.order.response.ResponseOrderDto;
import zve.com.vn.entity.Order;
import zve.com.vn.entity.OrderItem;
import zve.com.vn.entity.WorkOrder;
import zve.com.vn.repository.ResponseOrderRepository;
import zve.com.vn.service.OrderService;
import zve.com.vn.service.WorkOrderService;

@Controller
public class YeucauNvlController {
	private final OrderService orderService;
	private final WorkOrderService workOrderService;
	private final ResponseOrderRepository nvlRepository;
	/* ---------------------------------------------------- */
	public YeucauNvlController(OrderService orderService, ResponseOrderRepository nvlRepository, WorkOrderService workOrderService) {
		this.orderService = orderService;
		this.nvlRepository = nvlRepository;
		this.workOrderService = workOrderService;
	}
	/* ---------------------------------------------------- */
	@GetMapping("/ycnvl")
	public String ycnvl(Model model) {
		List<String> lines = workOrderService.getAllLine();
		model.addAttribute("lines", lines);
		return "ycnvl";
	}
	/* ------------------------------------------------- */
	@GetMapping("/ycnvl/by-line")
	@ResponseBody
	public List<String> getWorkOrdersByLine(@RequestParam("line") String line) {
		return workOrderService.getAllWoNumberByLine(line);
	}
	/* ------------------------------------------------- */
	@GetMapping("/ycnvl/workorder-info")
	@ResponseBody
	public Map<String, Object> getWorkOrderInfo(@RequestParam("woNumber") String woNumber) {
	    Map<String, Object> result = new HashMap<>();
	    WorkOrder workOrder = workOrderService.findByWoNumber(woNumber).orElse(null);
	    if (workOrder == null) {
	        result.put("error", "WorkOrder not found");
	        return result;
	    }
	    result.put("model", workOrder.getModel());
	    result.put("plan", String.valueOf(workOrder.getWoQty()));
	    
	    List<ResponseOrderDto> nvlList = nvlRepository.findAllItems(workOrder.getModel(), workOrder.getWoQty());
	    Optional<Order> orderOpt = orderService.findByWoNumberandStatus(woNumber);
	    if (orderOpt.isPresent()) {					//Nếu order vẫn ở trạng thái ycnvl
	        Order order = orderOpt.get();
	        List<OrderItem> orderItems = order.getOrderItems();
	        Map<String, OrderItem> orderItemMap = orderItems.stream()
	            .collect(Collectors.toMap(OrderItem::getItemcode, orderItem -> orderItem));
	        
	        
	        for (ResponseOrderDto item : nvlList) {
	            String itemCode = item.getItemCode();
	            BigDecimal receivedQtyByItemCode = workOrderService.calculateTotalReceivedQtyByItemCode(workOrder, itemCode);
	            OrderItem orderItem = orderItemMap.get(itemCode);
	            if (orderItem != null) {
	                item.setQtyReceive(receivedQtyByItemCode);
	                item.setQtyrequest(orderItem.getQtyrequest());
	            } else {
	                item.setQtyReceive(BigDecimal.ZERO);
	                item.setQtyrequest(BigDecimal.ZERO);
	            }
	        }
	        
	    } else {									//Nếu order đã chuyển sang trạng thái nhặt hàng.
	    	for (ResponseOrderDto item : nvlList) {
	    		String itemCode = item.getItemCode();
	    		BigDecimal receivedQtyByItemCode = workOrderService.calculateTotalReceivedQtyByItemCode(workOrder, itemCode);
	    		item.setQtyReceive(receivedQtyByItemCode);
	    	}
	    }

	    // Trả về kết quả với danh sách materials
	    result.put("materials", nvlList);
	    return result;
	}

	/* ------------------------------------------------- */
	@PostMapping("/ycnvl/workorder-info")
	@ResponseBody
	public String saveWorkOrderWithBOM(@RequestBody RequestOrderDto request) {
	    String woNumber = request.getWoNumber();
	    List<RequestOrderDetailDto> items = request.getItems();
	    WorkOrder workOrder = workOrderService.findByWoNumber(woNumber).orElse(null);
	    if (workOrder == null) {
	        return "WorkOrder không tồn tại: " + woNumber;
	    }
	    Order order = orderService.findByWoNumberandStatus(woNumber).orElse(null);

	    if (order == null) {
	    	
	    	int nextOrderNumber = workOrder.getOrders().stream()
	    	        .map(Order::getOrderNumber)
	    	        .filter(Objects::nonNull)
	    	        .max(Integer::compareTo)
	    	        .orElse(0) + 1;
	        order = Order.builder()
	        	.orderName(orderService.generateOrderName())
	            .status(1)
	            .createUserId("V03510") 
	            .createdDate(new Date())
	            .orderNumber(nextOrderNumber)
	            .workOrder(workOrder)
	            .orderItems(new ArrayList<>())
	            .build();
	        workOrder.getOrders().add(order);
	    } else {
	    	 order.setUpdateUserId("V03510");
	         order.setUpdateDate(new Date());
	    }

	    // Cập nhật chi tiết NVL
	    Map<String, OrderItem> detailMap = order.getOrderItems().stream()
	        .collect(Collectors.toMap(OrderItem::getItemcode, d -> d, (a, b) -> b));

	    for (RequestOrderDetailDto dto : items) {
	        if (dto.getItemCode() == null || dto.getQtyrequest() == null) continue;

	        OrderItem existing = detailMap.get(dto.getItemCode());

	        if (existing != null) {
	            existing.setQtyrequest(dto.getQtyrequest());
	        } else {
	            OrderItem newDetail = OrderItem.builder()
	            	.itemcategory(dto.getItemCategory())
	                .itemcode(dto.getItemCode())
	                .itemname(dto.getItemName())
	                .qtyrequest(dto.getQtyrequest())
	                //.qtyreceived(BigDecimal.ZERO)
	                .order(order)
	                .build();
	            order.getOrderItems().add(newDetail);
	        }
	    }

	    workOrderService.save(workOrder);
	    return "Đã lưu thành công " + items.size() + " dòng cho WorkOrder " + woNumber;
	}

	/* ------------------------------------------------- */
	
}
