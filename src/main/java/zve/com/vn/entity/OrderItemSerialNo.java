package zve.com.vn.entity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import jakarta.persistence.Column;
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
@Table(name = "tbl_item_by_serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemSerialNo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	Long id;
	
	String itemcode;
	String serialNo;
	String newSerialNo;
	String lotNo;
	String unit;
	String vendor;
	Date receivingDate;
	
	
	@Column(precision = 38, scale = 6)
	BigDecimal pickingQty;
	
	@Column(precision = 38, scale = 6)
	BigDecimal receivedQty;
	
	@Column(precision = 38, scale = 6)
	BigDecimal returnQty;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	OrderItem orderItem;
	
    public String generateNewSerialNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String datePart = sdf.format(new Date());
        Random random = new Random();
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomPart.append(random.nextInt(10)); 
        }
        return datePart + randomPart.toString();
    }
}
