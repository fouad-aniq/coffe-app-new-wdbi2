package ai.shreds.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import javax.sql.DataSource;

import ai.shreds.infrastructure.config.DatabaseProperties;

@Configuration
@EnableJpaRepositories(basePackages = "ai.shreds.infrastructure.repositories")
public class InfrastructureDatabaseConfig {

    private final DatabaseProperties databaseProperties;

    @Autowired
    public InfrastructureDatabaseConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Bean
    public DataSource configureDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(databaseProperties.getDriverClassName())
                .url(databaseProperties.getUrl())
                .username(databaseProperties.getUsername())
                .password(databaseProperties.getPassword())
                .build();
    }
}