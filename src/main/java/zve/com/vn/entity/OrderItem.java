package zve.com.vn.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String itemcode;
	String itemname;
	//BigDecimal qtyreceived;
	BigDecimal qtyrequest;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	Order order;
	
	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
	List<OrderItemSerialNo> orderItemSerialNos = new ArrayList<>();
	
	/* ------------------------------------------------- */
	public BigDecimal getTotalPickingQtyByItemCode(String itemCode) {
	    return orderItemSerialNos.stream()
	            .filter(orderItemSerialNo -> orderItemSerialNo.getItemcode().equals(itemCode))  // Lọc theo itemCode
	            .map(OrderItemSerialNo::getPickingQty)  // Lấy pickingQty
	            .filter(pickingQty -> pickingQty != null)  // Loại bỏ null
	            .reduce(BigDecimal.ZERO, BigDecimal::add);  // Tính tổng
	}
	/* ------------------------------------------------- */
	/* ------------------------------------------------- */
	public BigDecimal getTotalReceivingQtyByItemCode(String itemCode) {
	    return orderItemSerialNos.stream()
	            .filter(orderItemSerialNo -> orderItemSerialNo.getItemcode().equals(itemCode))  
	            .map(OrderItemSerialNo::getReceivedQty) 
	            .filter(receivedQty -> receivedQty != null)  
	            .reduce(BigDecimal.ZERO, BigDecimal::add);  
	}


}
