package zve.com.vn.dto.order.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PickingSuggestionGroupDto {
	String itemCategory;
	String itemCode;
	String productName;
	List<PickingSuggestionItem> items;
}
