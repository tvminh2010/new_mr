package zve.com.vn.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

    /* DataSource chính (dùng cho JPA) */
    @Primary
    @Bean(name = "mainDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().build();
    }

    /* JdbcTemplate cho DataSource chính */
    @Primary
    @Bean(name = "mainJdbcTemplate")
    public JdbcTemplate mainJdbcTemplate(@Qualifier("mainDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    /* DataSource phụ (dùng cho JdbcTemplate, remote SQL Server) */
    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "second.datasource")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }

    /* JdbcTemplate cho DataSource phụ */
    @Bean(name = "secondJdbcTemplate")
    public JdbcTemplate secondJdbcTemplate(@Qualifier("secondDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
