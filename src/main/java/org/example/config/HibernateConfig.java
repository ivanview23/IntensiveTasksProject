package org.example.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@Slf4j
public class HibernateConfig {

    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            Properties properties = new Properties();
            Configuration configuration = new Configuration()
                    .setProperties(properties)
                    .addAnnotatedClass(User.class);

            sessionFactory = configuration.buildSessionFactory();

            log.warn("Фабрика сессий Hibernate успешно создана");
        } catch (Exception e) {
            log.error("Ошибка создания фабрики сессий! {}", e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            log.warn("Фабрика сессий закрыта");
        }
    }
}
