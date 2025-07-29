package zve.com.vn.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import zve.com.vn.dto.order.response.ResponseOrderDto;

@Repository
public class ResponseOrderRepository  {
	private final JdbcTemplate jdbc;
	/* ---------------------------------------------------------- */
	public ResponseOrderRepository(@Qualifier("secondJdbcTemplate") JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	/* ---------------------------------------------------------- */
	public List<ResponseOrderDto> findAllItems(String model, Integer plan) {
	    String sql = """
	        SELECT 
	            pt.pt_desc2 AS category,
	            ps.ps_comp AS itemCode,
	            pt.pt_desc1 AS itemName,
	            pk.pt_package AS donggoi,
	            ps.ps_qty_per AS qtyPer,
	            COALESCE(ttoh.ld_total_oh, 0) AS instock
	        FROM ps_mstr ps
	        LEFT JOIN pt_mstr_package pk ON ps.ps_comp = pk.pt_part
	        LEFT JOIN pt_mstr pt ON ps.ps_comp = pt.pt_part
	        LEFT JOIN total_oh_v300_v350 ttoh ON ps.ps_comp = ttoh.ld_part
	        WHERE ps.ps_par = (
	            SELECT pt_part FROM pt_mstr WHERE pt_desc1 = ?
	        )
	        ORDER BY pt.pt_desc2, instock DESC
	    """;

	    BigDecimal fgPlan = (plan != null) ? BigDecimal.valueOf(plan) : BigDecimal.ZERO;

	    return jdbc.query(sql, (rs, rowNum) -> {
	        BigDecimal qtyPer = rs.getBigDecimal("qtyPer");
	        BigDecimal qtyPlan = (qtyPer != null) ? qtyPer.multiply(fgPlan).setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO;

	        return new ResponseOrderDto(
	            rs.getString("category"),       // pt.pt_desc2
	            rs.getString("itemCode"),       // ps.ps_comp
	            rs.getString("itemName"),       // pt.pt_desc1
	            rs.getInt("donggoi"),           // pk.pt_package
	            qtyPlan,                        // qtyPlan (tính từ qtyPer * plan)
	            BigDecimal.ZERO,                // qtyReceive (ban đầu 0)
	            BigDecimal.ZERO,                // qtyrequest (ban đầu 0)
	            BigDecimal.ZERO, 				// qtyreturn (ban đầu 0)
	            rs.getInt("instock")            // tồn kho
	        );
	    }, model);
	}
	/* ---------------------------------------------------------- */
}
