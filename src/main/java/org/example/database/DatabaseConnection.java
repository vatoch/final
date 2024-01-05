package org.example.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties properties = new Properties();

    public  Connection getConnection() throws SQLException {
        try{
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(properties.getProperty("database.url"), properties.getProperty("database.user"), properties.getProperty("database.password"));
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }
    static{
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")){
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void prepareTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS music ("
                + "id SERIAL PRIMARY KEY,"
                + "name VARCHAR(255) NOT NULL,"
                + "artist_name VARCHAR(255) NOT NULL,"
                + "minio_object VARCHAR(255) NOT NULL"
                + ")";
        try( Connection connection = DriverManager.getConnection(properties.getProperty("database.url"), properties.getProperty("database.user"), properties.getProperty("database.password"))) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
