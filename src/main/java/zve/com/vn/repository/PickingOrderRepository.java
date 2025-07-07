package zve.com.vn.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import zve.com.vn.dto.order.response.PickingItemDto;

@Repository
public class PickingOrderRepository {
	private final JdbcTemplate jdbc;

	/* ---------------------------------------------------------- */
	public PickingOrderRepository(@Qualifier("thirdJdbcTemplate") JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/* ---------------------------------------------------------- */
	public PickingItemDto findPickingItemBySerial(String serialNo) {
		String sql = """
				SELECT pi.product_no, pi.loc_code, pi.serial_no, pi.qty
				FROM public.product_instance pi
				WHERE pi.serial_no = ?
			""";
		
		return null;
	}
	
	/* ---------------------------------------------------------- */
	public List<PickingItemDto> findAllItems(String productNo) {
		String sql = """
					SELECT pi.category_code, pi.product_no, pm.product_name, pi.loc_code, pi.serial_no, pi.qty
					FROM public.product_instance pi
					LEFT JOIN public.m_product_master pm
					ON pi.product_no = pm.product_no
					WHERE pi.status = '1'
					  AND pi.qty > 0
					  AND pi.product_no = ?
					ORDER BY pi.date_in
				""";

		return jdbc.query(sql,
				(rs, rowNum) -> new PickingItemDto(
						rs.getString("category_code"), 
						rs.getString("product_no"), 
						rs.getString("product_name"), 
						rs.getBigDecimal("qty"), 
						BigDecimal.ZERO,
						null),
				productNo);
	}
	/* ---------------------------------------------------------- */
}
