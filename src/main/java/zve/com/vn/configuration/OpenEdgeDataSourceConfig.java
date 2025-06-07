package zve.com.vn.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

//@Configuration
public class OpenEdgeDataSourceConfig {


    @Bean(name = "forthDataSource")
    public DataSource forthDataSource() {
        HikariDataSource ds = new HikariDataSource();    
        ds.setJdbcUrl("jdbc:datadirect:openedge://192.168.15.5:9000;databaseName=mfgvte");
        ds.setUsername("sysprogress");
        ds.setPassword("sysprogress");
        ds.setDriverClassName("com.ddtek.jdbc.openedge.OpenEdgeDriver");
        return ds;
    }
    @Bean(name = "forthJdbcTemplate")
    public JdbcTemplate forthJdbcTemplate(DataSource forthDataSource) {
        return new JdbcTemplate(forthDataSource);
    }
}


