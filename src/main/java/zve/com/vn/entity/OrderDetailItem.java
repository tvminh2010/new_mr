package zve.com.vn.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_order_dtl_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	String id;
	
	String itemcode;
	String serialNo;
	BigDecimal stockQty;
	BigDecimal pickingQty;
	BigDecimal receivedQty;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_dtl_id")
	OrderDetail orderDetail;
}
