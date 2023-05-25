package codigo;

import java.sql.Connection;
import java.sql.DriverManager;

public class Application {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            // esto lo que dice es: cuando inicies la conexión, correme el script que te especifico
            // Esto lo estamos haciendo PREVIO a hacer los tests
            connection = DriverManager.getConnection("jdbc:h2:~/clase15clinicadental;INIT=RUNSCRIPT FROM 'create.sql'", "sa", "sa");

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }
}
