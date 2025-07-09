package zve.com.vn.repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import zve.com.vn.dto.order.response.DetailItemBySerialDto;

@Repository
public class ResponseItemBySerialRepository  {
	private final JdbcTemplate jdbc;
	/* ---------------------------------------------------------- */
	public ResponseItemBySerialRepository(@Qualifier("thirdJdbcTemplate") JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	/* ---------------------------------------------------------- */
	public DetailItemBySerialDto detailItemBySerial(String serial) {
	    String sql = """
	        SELECT pm.category_code, 
	               pi.product_no, 
	               pm.product_name, 
	               pi.serial_no, 
	               pi.loc_code, 
	               pi.lot_no, 
	               pi.qty,
	               pi.date_in, 
	               pm.unit
	        FROM public.product_instance pi
	        INNER JOIN public.m_product_master pm
	          ON pi.product_no = pm.product_no
	        WHERE pi.serial_no = ?
	          AND pi.qty > 0
	    """;

	    return jdbc.query(sql, (rs, rowNum) -> {
	        DetailItemBySerialDto dto = new DetailItemBySerialDto();
	        dto.setCategory(rs.getString("category_code"));
	        dto.setItemCode(rs.getString("product_no"));
	        dto.setItemName(rs.getString("product_name"));
	        dto.setSerialNo(rs.getString("serial_no"));
	        dto.setLotNo(rs.getString("lot_no"));
	        dto.setLocation(rs.getString("loc_code"));
	        dto.setQty(rs.getBigDecimal("qty"));
	        dto.setReceivingDate(rs.getDate("date_in"));
	        dto.setUnit(rs.getString("unit"));
	        return dto;
	    }, serial).stream().findFirst().orElse(null);
	}
	/* ---------------------------------------------------------- */
}
