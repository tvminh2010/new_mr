package zve.com.vn.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
@Table(name = "tbl_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	Integer status;
	String createUserId;
	String updateUserId;
	Date createdDate;
	Date updateDate;
	Integer orderNumber;
	
	@Column(unique = true)
	String orderName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workorder_id")
	WorkOrder workOrder;
	
	@Builder.Default
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	List<OrderItem> orderItems = new ArrayList<>();
}
