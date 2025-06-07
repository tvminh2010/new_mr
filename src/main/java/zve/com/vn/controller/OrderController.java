package zve.com.vn.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import zve.com.vn.dto.order.response.PickingItemDto;
import zve.com.vn.dto.order.response.PickingSuggestionGroupDto;
import zve.com.vn.entity.Order;
import zve.com.vn.entity.OrderDetail;
import zve.com.vn.repository.PickingOrderRepository;
import zve.com.vn.service.OrderService;
import zve.com.vn.service.PickingSuggestionService;

@Controller
public class OrderController {

	private final OrderService service;
	private final PickingOrderRepository pickingrepository;
	private final PickingSuggestionService pickingSuggestionService;
	
	/* ------------------------------------------------- */
	public OrderController(OrderService service, PickingOrderRepository pickingrepository, PickingSuggestionService pickingSuggestionService) {
		this.service = service;
		this.pickingrepository = pickingrepository;
		this.pickingSuggestionService = pickingSuggestionService;
	}
	/* ------------------------------------------------- */
	@GetMapping("/quanlyorder")
	public String quanlyOrderIndex(Model model) {
		List<Order> orderLists = service.fileAllOrder();
		model.addAttribute("orderLists", orderLists);
		return "quanlyorder";
	}
	/* ------------------------------------------------- */
	@GetMapping("/materialorder")
	public String materialOrderIndex(Model model) {
		List<Order> orderLists = service.fileAllOrder();
		model.addAttribute("orderLists", orderLists);
		return "materialorder";
	}
	/* ------------------------------------------------- */
	@PostMapping("/order/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateOrderStatus(@RequestParam Long id) {
	    try {
	        int updated = service.updateOrderByStatus(id, 2); // Trả về số dòng bị ảnh hưởng
	        if (updated > 0) {
	            Map<String, Object> result = new HashMap<>();
	            result.put("id", id);
	            result.put("status", 2); // Cập nhật trạng thái mới
	            return ResponseEntity.ok(result);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found"));
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Update failed"));
	    }
	}
	/* ------------------------------------------------- */
	@GetMapping("/order/picking")
	public String orderPicking(@RequestParam(value = "id", required = false) Long orderId, Model model) {
	    Order order = service.findById(orderId).orElse(null);
	    if (order == null) {
	        model.addAttribute("message", "Không tìm thấy đơn hàng");
	        return "orderpicking";
	    }

	    List<OrderDetail> orderDetails = order.getOrderDetails();
	    
	    // Lấy danh sách mã SP
	    List<String> productNos = orderDetails.stream()
	        .map(OrderDetail::getItemcode)
	        .distinct()
	        .toList();
	    
	    List<PickingSuggestionGroupDto> pickingSuggestions = pickingSuggestionService
	            .findSuggestionsByProductNos(productNos);

	    List<PickingItemDto> pickingItems = orderDetails.stream()
	        .map(detail -> new PickingItemDto(
	            detail.getItemcode(),   		//Item Code     
	            detail.getItemname(),  			//Item Name 
	            null,							//SerialNo 
	            null,    						//location                     
	            BigDecimal.ZERO,  				//Balance            
	            detail.getQtyrequest(),  		//Balance    
	            BigDecimal.ZERO     			//PickingQty         
	        ))
	        .toList();

	    model.addAttribute("order", order);
	    model.addAttribute("pickingItems", pickingItems);
	    model.addAttribute("pickingSuggestions", pickingSuggestions);
	    return "orderpicking";
	}

	/* ------------------------------------------------- */
}
