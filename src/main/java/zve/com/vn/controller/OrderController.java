package zve.com.vn.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import zve.com.vn.dto.order.response.PickingItemDto;
import zve.com.vn.dto.order.response.PickingSerialNo;
import zve.com.vn.dto.order.response.PickingSuggestionGroupDto;
import zve.com.vn.entity.Order;
import zve.com.vn.entity.OrderItem;
import zve.com.vn.entity.OrderItemSerialNo;
import zve.com.vn.repository.OrderItemRepository;
import zve.com.vn.repository.OrderItemSerialNoRepository;
import zve.com.vn.service.OrderService;
import zve.com.vn.service.PickingSuggestionService;

@Controller
public class OrderController {

	private final OrderService service;
	private final PickingSuggestionService pickingSuggestionService;
	private final OrderItemSerialNoRepository orderItemSerialNoRepository;
	private final OrderItemRepository orderItemRepository;
	/* ------------------------------------------------- */
	public OrderController(
			OrderService service, 
			PickingSuggestionService pickingSuggestionService, 
			OrderItemSerialNoRepository orderItemSerialNoRepository,
			OrderItemRepository orderItemRepository) {
		this.service = service;
		this.pickingSuggestionService = pickingSuggestionService;
		this.orderItemSerialNoRepository = orderItemSerialNoRepository;
		this.orderItemRepository = orderItemRepository;
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
	   
		  if (orderId == null) {
		        model.addAttribute("message", "Chưa chọn order để nhặt hàng");
		        return "orderpicking";
		    }
		
		Order order = service.findById(orderId).orElse(null);
	    if (order == null) {
	        model.addAttribute("message", "Không tìm thấy đơn hàng");
	        return "orderpicking";
	    }

	    List<OrderItem> orderItems = order.getOrderItems();
	    List<String> productNos = orderItems.stream()
	        .map(OrderItem::getItemcode)
	        .distinct()
	        .toList();
	    
	    List<PickingSuggestionGroupDto> pickingSuggestions = pickingSuggestionService
	            .findSuggestionsByProductNos(productNos);

	    List<PickingItemDto> pickingItems = orderItems.stream()
	    	.sorted(Comparator.comparing(OrderItem::getItemcode))
	        .map(detail -> new PickingItemDto(
	            detail.getItemcode(),   		   
	            detail.getItemname(),  			                             
	            detail.getQtyrequest(), 
	            detail.getTotalPickingQtyByItemCode(detail.getItemcode())
	        ))
	        .toList();

	    model.addAttribute("order", order);
	    model.addAttribute("pickingItems", pickingItems);
	    model.addAttribute("pickingSuggestions", pickingSuggestions);
	    return "orderpicking";
	}

	/* ------------------------------------------------- */
	@PostMapping("/picking/scan-serial")
	@ResponseBody
	public Map<String, Object> scanSerial(@RequestBody Map<String, Object> request) {
	    String serial = (String) request.get("serial");
	    List<String> itemCodes = (List<String>) request.get("itemCodes");

	    Map<String, Object> response = new HashMap<>();
	    
	    if (orderItemSerialNoRepository.existsBySerialNo(serial)) {
	        response.put("success", false);
	        response.put("message", "⚠️ Số Serial '" + serial + "' đã được quét vào hệ thống!");
	        return response;
	    }
	    
	    
	    PickingSerialNo pickingSerialNo = pickingSuggestionService.getItemBySerialNo(serial);
	    if (pickingSerialNo == null) {
	        response.put("success", false);
	        response.put("message", "❌ Không tìm thấy số Serial: '" + serial + "' trong wms.");
	        return response;
	    }

	    String itemCode = pickingSerialNo.getItemCode(); 
	    if (itemCodes == null || itemCodes.stream().noneMatch(code -> code.equalsIgnoreCase(itemCode))) {
	        response.put("success", false);
	        response.put("message", "⚠️ SerialNo " + serial + " có mã itemCode `" + itemCode + "` không khớp với danh sách cần nhặt.");
	        return response;
	    }

	    String message = "✅ SerialNo " + serial + ", với Số lượng: " + pickingSerialNo.getPickingqty() + " đã được quét.";
	    response.put("success", true);
	    response.put("message", message);
	    response.put("pickingSerialNo", pickingSerialNo);
	    return response;
	}
	/* ------------------------------------------------- */
	@PostMapping("/picking/save-serials")
	@ResponseBody
	public Map<String, Object> saveScannedSerials(@RequestBody List<PickingSerialNo> scannedSerials) {
	    List<OrderItemSerialNo> entities = new ArrayList<>();
	    for (PickingSerialNo dto : scannedSerials) {
	        OrderItemSerialNo existingEntity = orderItemSerialNoRepository.findBySerialNo(dto.getSerialNo()).orElse(null);

	        if (existingEntity == null) {
	        	List<OrderItem> orderItemList = orderItemRepository.findByItemcode(dto.getItemCode());
	        	if (orderItemList.isEmpty()) {
	                continue;
	            }
	        	OrderItem orderItem = orderItemList.get(0);
	            existingEntity = OrderItemSerialNo.builder()
	                    .itemcode(dto.getItemCode())
	                    .serialNo(dto.getSerialNo())
	                    .pickingQty(dto.getPickingqty())
	                    .receivedQty(BigDecimal.ZERO)
	                    .orderItem(orderItem)
	                    .build();
	        } else {
	            existingEntity.setPickingQty(dto.getPickingqty());
	            existingEntity.setReceivedQty(BigDecimal.ZERO); 
	        }

	        entities.add(existingEntity);
	    }
	    orderItemSerialNoRepository.saveAll(entities);
	    return Map.of(
	        "success", true,
	        "saved", entities.size(),
	        "message", "Đã lưu " + entities.size() + " serial vào hệ thống."
	    );
	}
	/* ------------------------------------------------- */
	@PostMapping("/orders/{id}/end-picking")
	@ResponseBody
	public ResponseEntity<String> endPicking(@PathVariable Long id) {
		Optional<Order> orderOpt = service.findById(id);
		if (orderOpt.isPresent()) {
			Order order = orderOpt.get();
			if (order.getStatus() != 2) {
				return ResponseEntity.badRequest().body("Order không ở trạng thái PICKING.");
			}
			order.setStatus(3);
			service.save(order);
			return ResponseEntity.ok("Hoàn tất kết thúc nhặt hàng, đã ở trạng thái đang giao!");
		}
		return ResponseEntity.notFound().build();
	}
	/* ------------------------------------------------- */
}
