package zve.com.vn.entity;



import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "tbl_wo_ycnvl_session") 
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkOrderYcnvlSession {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;
	String itemCode;
	BigDecimal requiredQty;
	BigDecimal receiveQty;
	Integer status;
	String userId;
	Date createdDate;
}
