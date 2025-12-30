package org.example.config;

import org.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestHibernateConfig {

    private static PostgreSQLContainer<?> postgresContainer;
    private static SessionFactory sessionFactory;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgresContainer.start();
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                        .applySetting("hibernate.connection.url", postgresContainer.getJdbcUrl())
                        .applySetting("hibernate.connection.username", postgresContainer.getUsername())
                        .applySetting("hibernate.connection.password", postgresContainer.getPassword())
                        .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                        .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                        .applySetting("hibernate.show_sql", "true")
                        .applySetting("hibernate.format_sql", "true")
                        .build();

                sessionFactory = new MetadataSources(registry)
                        .addAnnotatedClass(User.class)
                        .buildMetadata()
                        .buildSessionFactory();

            } catch (Exception e) {
                throw new RuntimeException("Failed to create test session factory", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
    }

    public static String getJdbcUrl() {
        return postgresContainer.getJdbcUrl();
    }
}