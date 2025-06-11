package zve.com.vn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zve.com.vn.entity.OrderItemSerialNo;
import zve.com.vn.repository.OrderItemSerialNoRepository;

@Service
@RequiredArgsConstructor
public class OrderDetailItemService {
    private final OrderItemSerialNoRepository itemRepository;
    /* ---------------------------------------------------- */
    public void saveScannedSerials(List<OrderItemSerialNo> items) {
        itemRepository.saveAll(items);
    }
    /* ---------------------------------------------------- */
}
