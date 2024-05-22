package com.userservice.database;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MultiDbManager {

    private final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();
    private AbstractRoutingDataSource routingDataSource;
    private final DataSourceProperties properties;


    public MultiDbManager(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DataSource dataSourceClient() {
        routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return currentTenant.get();
            }
        };
        routingDataSource.setTargetDataSources(tenantDataSources);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource());
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    public void addTenant(String tenantId, String url, String username, String password,String driverClassName) throws SQLException {
        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();

        // Check that new connection is 'live'. If not - throw exception
        try(Connection c = dataSource.getConnection()) {
            tenantDataSources.put(tenantId, dataSource);
            routingDataSource.afterPropertiesSet();
            System.out.println("Tenant Client added - "+tenantId);
        }
    }

    public void setCurrentTenant(String tenantId){
        currentTenant.set(tenantId);
        System.out.println("Tenant set as current - "+tenantId);
    }

    private DataSource defaultDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    /*
    public void clear() {
        currentTenant.remove();
    }

    public DataSource removeTenant(String tenantId) {
        Object removedDataSource = tenantDataSources.remove(tenantId);
        multiTenantDataSource.afterPropertiesSet();
        return (DataSource) removedDataSource;
    }

    public boolean tenantIsAbsent(String tenantId) {
        return !tenantDataSources.containsKey(tenantId);
    }

    public Collection<Object> getTenantList() {
        return tenantDataSources.keySet();
    }

     private DriverManagerDataSource defaultDataSource() {
        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
        defaultDataSource.setDriverClassName(properties.getDriverClassName());
        defaultDataSource.setUrl(properties.getUrl());
        defaultDataSource.setUsername(properties.getUsername());
        defaultDataSource.setPassword(properties.getPassword());
        return defaultDataSource;
    }

    */

}
