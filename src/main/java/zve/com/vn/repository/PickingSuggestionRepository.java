package zve.com.vn.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import zve.com.vn.dto.order.request.PickingSerialNo;
import zve.com.vn.dto.order.response.PickingSuggestionGroupDto;
import zve.com.vn.dto.order.response.PickingSuggestionItem;

@Repository
public class PickingSuggestionRepository {
	/* --------------------------------------------------------------- */
    private final JdbcTemplate jdbc;
    public PickingSuggestionRepository(@Qualifier("thirdJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    /* --------------------------------------------------------------- */
    public List<PickingSuggestionGroupDto> findSuggestionsByProductNos(List<String> productNos) {
        if (productNos == null || productNos.isEmpty()) return List.of();

        String inSql = String.join(",", productNos.stream()
                .map(p -> "'" + p.replace("'", "''") + "'")
                .toList());

        String sql = """
            SELECT pi.product_no,
                   pm.category_code, 
                   pm.product_name,
                   pi.serial_no,
                   pi.loc_code,
                   pi.qty,
                   pi.date_in
            FROM public.product_instance pi
            LEFT JOIN public.m_product_master pm ON pi.product_no = pm.product_no
            WHERE pi.status = '1'
              AND qty > 0
              AND pi.product_no IN (%s)
            ORDER BY pi.date_in, 
                CASE 
                    WHEN pi.loc_code ~ '^[0-9]{2}-([0-9]{2})-[0-9]{2}$' 
                    THEN substring(pi.loc_code FROM 4 FOR 2)::int 
                    ELSE NULL 
                END
        """.formatted(inSql);

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        Map<String, List<Map<String, Object>>> grouped = rows.stream()
                .collect(Collectors.groupingBy(row -> (String) row.get("product_no")));
        
        List<PickingSuggestionGroupDto> result = grouped.keySet().stream()
        	    .sorted()
        	    .map(productNo -> {
        	        List<Map<String, Object>> groupRows = grouped.get(productNo);
        	        String categoryCode = (String) groupRows.get(0).get("category_code");
        	        String productName  = (String) groupRows.get(0).get("product_name");

        	        List<PickingSuggestionItem> items = groupRows.stream()
        	            .map(row -> new PickingSuggestionItem(
        	                (String) row.get("serial_no"),
        	                (String) row.get("loc_code"),
        	                (BigDecimal) row.get("qty"),
        	                row.get("date_in") != null
        	                    ? ((Timestamp) row.get("date_in")).toLocalDateTime().toLocalDate()
        	                    : null
        	            ))
        	            .toList();

        	        return new PickingSuggestionGroupDto(categoryCode, productNo, productName, items);
        	    })
        	    .toList();
        
        return result;
    }
    /* --------------------------------------------------------------- */
    public List<String> findLocationsByProductNoAndRequestQty(String productNo, BigDecimal requestQty) {
        if (productNo == null || requestQty == null || requestQty.compareTo(BigDecimal.ZERO) <= 0) {
            return List.of();
        }
        String sql = """
            SELECT pi.loc_code, pi.qty
            FROM public.product_instance pi
            WHERE pi.product_no = ?
              AND pi.status = '1'
              AND pi.qty > 0
            ORDER BY pi.date_in ASC,
                     CASE 
                         WHEN pi.loc_code ~ '^[0-9]{2}-([0-9]{2})-[0-9]{2}$' 
                         THEN substring(pi.loc_code FROM 4 FOR 2)::int 
                         ELSE NULL 
                     END
        """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql, productNo);
        BigDecimal accumulatedQty = BigDecimal.ZERO;
        List<String> selectedLocations = new java.util.ArrayList<>();

        for (Map<String, Object> row : rows) {
            if (accumulatedQty.compareTo(requestQty) >= 0) break;

            BigDecimal qty = (BigDecimal) row.get("qty");
            String locCode = (String) row.get("loc_code");

            accumulatedQty = accumulatedQty.add(qty);
            selectedLocations.add(locCode);
        }
        return selectedLocations;
    }
    /* --------------------------------------------------------------- */
    public PickingSerialNo getItemBySerialNo(String serialNo) {
        String sql = "SELECT pi.qty, pi.product_no, pi.serial_no " +
                     "FROM public.product_instance pi WHERE pi.serial_no = ? AND pi.qty > 0";
        try {
            return jdbc.queryForObject(sql, (rs, rowNum) -> {
                PickingSerialNo item = new PickingSerialNo();
                item.setItemCode(rs.getString("product_no"));        
                item.setSerialNo(rs.getString("serial_no"));
                item.setPickingqty(rs.getBigDecimal("qty"));          
                return item;
            }, serialNo);
        } catch (EmptyResultDataAccessException e) {
            return null; 
        }
    }
    /* --------------------------------------------------------------- */
}
