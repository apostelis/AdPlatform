package com.example.adplatform.infrastructure.persistence.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple read/write routing datasource.
 * Routes read-only transactions to the read replica when configured; otherwise falls back to the write datasource.
 */
@Configuration
public class ReadWriteDataSourceConfig {

    static class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            return readOnly ? "read" : "write";
        }
    }

    @Bean(name = "writeDataSource")
    public DataSource writeDataSource(org.springframework.core.env.Environment env) {
        return DataSourceBuilder.create()
                .url(env.getProperty("app.datasource.write.url"))
                .username(env.getProperty("app.datasource.write.username"))
                .password(env.getProperty("app.datasource.write.password"))
                .driverClassName(Objects.requireNonNullElse(env.getProperty("app.datasource.write.driver-class-name"), "org.h2.Driver"))
                .build();
    }

    @Bean(name = "readDataSource")
    @ConditionalOnProperty(prefix = "app.datasource.read", name = "url")
    public DataSource readDataSource(org.springframework.core.env.Environment env) {
        String url = env.getProperty("app.datasource.read.url");
        if (url == null || url.isBlank()) {
            return null;
        }
        return DataSourceBuilder.create()
                .url(url)
                .username(env.getProperty("app.datasource.read.username"))
                .password(env.getProperty("app.datasource.read.password"))
                .driverClassName(env.getProperty("app.datasource.read.driver-class-name", "org.h2.Driver"))
                .build();
    }

    @Bean
    public DataSource dataSource(@Qualifier("writeDataSource") DataSource write,
                                 @Qualifier("readDataSource") DataSource read) {
        ReadWriteRoutingDataSource routing = new ReadWriteRoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();
        targets.put("write", write);
        // Fallback to write if read is null
        targets.put("read", read != null ? read : write);
        routing.setTargetDataSources(targets);
        routing.setDefaultTargetDataSource(write);
        return routing;
    }
}
