package codigo.service;

import codigo.dao.impl.PacienteDAOH2;
import codigo.entity.Domicilio;
import codigo.entity.Paciente;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PacienteServiceTest {
    private static Connection connection = null;
    private PacienteService pacienteService = new PacienteService(new PacienteDAOH2());

    @Test
    public void deberiaAgregarUnPaciente(){
        // creamos el paciente
        Paciente pacienteTest = new Paciente("Nombre", "Apellido", 123456, LocalDate.of(2023, 05, 02), new Domicilio("Calle", 13, "Localidad", "Provincia"));

        //lo insetamos en el H2
        Paciente pacienteResult = pacienteService.guardarPaciente(pacienteTest);

        assertNotNull(pacienteResult);
        assertEquals(123456, pacienteResult.getDni());
    }

    @Test
    public void deberiaEliminarElPacienteConId3(){
        pacienteService.eliminarPaciente(3);
        assertNull(pacienteService.buscarPacientePorID(3));
    }

    @Test
    public void listarTodosLosPacientes(){
        // creamos una lista de pacientes
        List<Paciente> pacientesTest = pacienteService.listarPacientes();

        // nos tiene que dar false porque la lista no deberia de estar vacia
        assertFalse(pacientesTest.isEmpty());
        assertTrue(pacientesTest.size()>=3);
    }



    /* --- ESTA ES OTRA VARIABLE DEL Application --> irian antes de el @Test y esto solo se ejecutaria en el test, no en todo (creo, eso entendí)
    @BeforeAll
    static void doBefore() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/clase15pruebas;INIT=RUNSCRIPT FROM 'create.sql'", "sa", "sa");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }*/
}