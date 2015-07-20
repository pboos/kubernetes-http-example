package ch.pboos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pboos on 16/07/15.
 */
public class Database {

    private Connection db;

    public void incrementCounter() {
        ensureConnected();
        try {
            db.createStatement().execute("UPDATE counter SET count=count+1;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCounter() {
        ensureConnected();
        try {
            ResultSet resultSet = db.createStatement().executeQuery("SELECT count FROM counter;");
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    private void ensureConnected() {
        try {
            if (db == null || db.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String host = System.getenv("POSTGRES_HOST");
        String port = System.getenv("POSTGRES_PORT");
        String database = System.getenv("POSTGRES_DATABASE");

        String username = System.getenv("POSTGRES_USERNAME");
        String password = System.getenv("POSTGRES_PASSWORD");

//
//        export POSTGRES_HOST=192.168.59.103
//        export POSTGRES_PORT=5432
//        export POSTGRES_DATABASE=test
//
//        export POSTGRES_USERNAME=postgres
//        export POSTGRES_PASSWORD=test

        try {
            db = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, username, password);

            try {
                db.createStatement().execute("SELECT * FROM counter;");
            } catch (SQLException e) {
                db.createStatement().execute("CREATE TABLE counter (count integer NOT NULL);");
                db.createStatement().execute("INSERT INTO counter (count) VALUES (0) ;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
