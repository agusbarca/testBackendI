package codigo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Connection {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        //indicar qué driver voy a usar
        Class.forName("org.h2.Driver");
        // crear la conexion con la base de datos de h2, si no existe la va a crear
        return DriverManager.getConnection("jdbc:h2:~/clase15clinicadental", "sa", "sa");
    }
}

