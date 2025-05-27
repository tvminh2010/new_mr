package zve.com.vn.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import zve.com.vn.dto.order.response.ResponseOrderDto;

@Repository
public class YeuCauNVLItemRepositoryImpl implements YeuCauNVLItemRepositoryCustom {
	
	private final JdbcTemplate secondJdbcTemplate;
	/* ---------------------------------------------------------- */
	public YeuCauNVLItemRepositoryImpl(@Qualifier("secondJdbcTemplate") JdbcTemplate secondJdbcTemplate) {
		this.secondJdbcTemplate = secondJdbcTemplate;
	}

	/* ---------------------------------------------------------- */
	@Override
	public List<ResponseOrderDto> findAllItems(String model, Integer plan) {
		String sql = """
		        SELECT pt.pt_desc2, ps.ps_comp, pt.pt_desc1, pk.pt_package, ps.ps_qty_per, ttoh.ld_total_oh
		        FROM ps_mstr ps
		        LEFT JOIN pt_mstr_package pk ON ps.ps_comp = pk.pt_part
		        LEFT JOIN pt_mstr pt ON ps.ps_comp = pt.pt_part
		        LEFT JOIN total_oh_v300_v350 ttoh ON ps.ps_comp = ttoh.ld_part
		        WHERE ps.ps_par = (
		            SELECT pt_part FROM pt_mstr WHERE pt_desc1 = ?
		        )
		        ORDER BY pt.pt_desc2, ld_total_oh DESC
		    """;
	   
	   return secondJdbcTemplate.query(
			    sql,
			    (rs, rowNum) -> {

			        BigDecimal psQtyPer = rs.getBigDecimal("ps_qty_per");
			        BigDecimal fgPlanValue = (plan != null) ? BigDecimal.valueOf(plan) : BigDecimal.ZERO;

			        BigDecimal kehoach = (psQtyPer != null) ? psQtyPer.multiply(fgPlanValue) : BigDecimal.ZERO;
			        kehoach = kehoach.setScale(3, RoundingMode.HALF_UP);  
			        
			        return new ResponseOrderDto(
			            rs.getString("pt_desc2"),
			            rs.getString("ps_comp"),
			            rs.getString("pt_desc1"),
			            rs.getInt("pt_package"),
			            kehoach,
			            BigDecimal.ZERO,
			            BigDecimal.ZERO,
			            rs.getInt("ld_total_oh")
			        );
			    },
			    model
		);
	}
	
	/* ---------------------------------------------------------- */
}
