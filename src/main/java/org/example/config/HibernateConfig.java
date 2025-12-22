package org.example.config;

import lombok.Getter;
import org.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

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

            System.out.println("Фабрика сессий Hibernate успешно создана");
        } catch (Exception e) {
            System.out.println("Ошибка создания фабрики сессий!" + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("Фабрика сессий закрыта");
        }
    }
}
