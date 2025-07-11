package zve.com.vn.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_material_core_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialCore {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	Long id;
	String itemCode;
	
	@Column(columnDefinition = "NVARCHAR(50)")
	String itemName;
	String unit;
	String coreType;
	@Column(precision = 18, scale = 6)
	BigDecimal coreWeight;
	@Column(precision = 18, scale = 10) 
	BigDecimal rate;
	
	@Column(columnDefinition = "NVARCHAR(50)")
	String vendor;
}
