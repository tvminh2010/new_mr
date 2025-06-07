package zve.com.vn.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

	/**
     * DataSource chính (Spring JPA) - SQL Server chạy local
     */
    @Primary
    @Bean(name = "mainDataSource")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .url("jdbc:sqlserver://host.docker.internal:1433;databaseName=mr;encrypt=false;trustServerCertificate=true;")
                .username("sa")
                .password("Vietnamnet.vn")
                .build();
    }

    @Primary
    @Bean(name = "mainJdbcTemplate")
    public JdbcTemplate mainJdbcTemplate(@Qualifier("mainDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    /**
     * DataSource phụ (second) - SQL Server ở địa chỉ IP nội bộ
     */
    @Bean(name = "secondDataSource")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .url("jdbc:sqlserver://192.168.15.9:1433;databaseName=mr;encrypt=true;trustServerCertificate=true")
                .username("caputtino")
                .password("caputtino")
                .build();
    }

    @Bean(name = "secondJdbcTemplate")
    public JdbcTemplate secondJdbcTemplate(@Qualifier("secondDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    /**
     * DataSource thứ 3 (third) - PostgreSQL ở địa chỉ IP nội bộ
     */
    @Bean(name = "thirdDataSource")
    public DataSource thirdDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://192.168.15.7:5493/vte2")
                .username("postgres")
                .password("postgres")
                .build();
    }

    @Bean(name = "thirdJdbcTemplate")
    public JdbcTemplate thirdJdbcTemplate(@Qualifier("thirdDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
