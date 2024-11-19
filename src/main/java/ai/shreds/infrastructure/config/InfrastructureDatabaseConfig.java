package ai.shreds.infrastructure.config;

import javax.sql.DataSource;

import ai.shreds.infrastructure.repositories.InfrastructureRepositoryCategoryImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
@EnableJpaRepositories(basePackageClasses = InfrastructureRepositoryCategoryImpl.class)
public class InfrastructureDatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource configureDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
