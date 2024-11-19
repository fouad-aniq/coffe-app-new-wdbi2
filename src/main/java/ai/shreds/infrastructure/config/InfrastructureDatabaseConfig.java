package ai.shreds.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * Configuration class for setting up the database DataSource.
 */
@Configuration
@EnableJpaRepositories(basePackages = "ai.shreds.infrastructure.repositories")
public class InfrastructureDatabaseConfig {

    /**
     * Configures and provides the DataSource bean.
     * The DataSource properties are loaded from application properties with prefix 'spring.datasource'.
     * 
     * @return DataSource configured for the application.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource configureDataSource() {
        return DataSourceBuilder.create().build();
    }
}