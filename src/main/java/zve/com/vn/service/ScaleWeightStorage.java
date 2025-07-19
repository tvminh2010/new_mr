package zve.com.vn.service;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Component
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScaleWeightStorage {
	 volatile String latestWeight = "";
}
