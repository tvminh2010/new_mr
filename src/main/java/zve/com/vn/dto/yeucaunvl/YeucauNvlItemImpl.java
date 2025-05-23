package zve.com.vn.dto.yeucaunvl;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YeucauNvlItemImpl implements YeucauNvlItemProjection {
	String category;
	String itemCode;
	String model;
	Integer donggoi;
	BigDecimal plan;
	Integer receive;
	Integer instock;
}
