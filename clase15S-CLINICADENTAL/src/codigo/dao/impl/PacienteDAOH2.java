package codigo.dao.impl;

import codigo.dao.H2Connection;
import codigo.dao.IDao;
import codigo.entity.Domicilio;
import codigo.entity.Paciente;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAOH2 implements IDao<Paciente> {

    public static final Logger LOGGER = Logger.getLogger(PacienteDAOH2.class);

    @Override
    public Paciente guardar(Paciente paciente) {
        Connection connection = null;
        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);

            // ACLARACION: Como trabajamos con otra entidad (Domicilio) dentro de la entidad "Paciente", tenemos que crear el domicilio
            // Es decir, el paciente ingresa el domicilio cuando se crea, y nosotros antes de crear al paciente creamos el domicilio
            DomicilioDAOH2 domicilioDAOH2 = new DomicilioDAOH2();
            domicilioDAOH2.guardar(paciente.getDomicilio());


            PreparedStatement ps = connection.prepareStatement("INSERT INTO PACIENTES (NOMBRE, APELLIDO, DNI, FECHA, DOMICILIO_ID) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setInt(3, paciente.getDni());
            ps.setDate(4, Date.valueOf(paciente.getFechaIngreso()));
            ps.setInt(5, paciente.getDomicilio().getId()); // ACLARACION: Respecto del domicilio, trabajamos con el ID del mismo, no con el domicilio completo en si mismo
            ps.execute();


            ResultSet rs = ps.getGeneratedKeys();
            while(rs.next()){
                paciente.setId(rs.getInt(1));
            }

            connection.commit();
            LOGGER.info("PACIENTE GUARDADO: " + paciente);

        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            if (connection != null) {
                try {
                    // vuelve los datos a su origen, previo a ejecutar todo esto
                    connection.rollback();
                    LOGGER.info("Oops, tuvimos un problema");
                    e.printStackTrace();
                } catch (SQLException ex) {
                    LOGGER.error(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }finally {
            try {
                connection.close();
            }catch (Exception exp){
                LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
            }
        }
        return paciente;
    }



    @Override
    public List<Paciente> listarTodos() {
        Connection connection = null;
        List<Paciente> pacientes = new ArrayList();
        try {
            connection = H2Connection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PACIENTES");

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Paciente paciente = crearObjetoPaciente(rs);
                pacientes.add(paciente);
            }

            LOGGER.info("LISTADO DE TODOS LOS PACIENTES: " + pacientes);

        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            }catch (Exception exp){
                LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
            }
        }
        return pacientes;
    }



    @Override
    public Paciente buscarPorId(int id) {
        Connection connection = null;
        Paciente paciente = null;
        try {
            connection = H2Connection.getConnection();

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PACIENTES WHERE ID = ?");
            ps.setInt(1, id);
            ps.execute();

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                paciente = crearObjetoPaciente(rs);
            }

            if (paciente != null)
                LOGGER.info("PACIENTE SOLICITADO: " + paciente);
            else LOGGER.info("El PACIENTE SOLICITADO (con id: "+ id + ") no fue hallado");


        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            }catch (Exception exp){
                LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
            }
        }
        return paciente;
    }



    @Override
    public void eliminar(int id) {
        Connection connection = null;

        try {
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);

            // NUEVO!! no se lo asignamos con ps.setInt(1, id), SINO QUE lo asignamos al final
            PreparedStatement ps = connection.prepareStatement("DELETE FROM PACIENTES WHERE ID = ?");
            ps.setInt(1, id);
            ps.execute();

            connection.commit();

            LOGGER.info("Se ha ELIMINADO el paciente con el id: " + id);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                    LOGGER.info("Oops, tuvimos un problema");
                    e.printStackTrace();
                } catch (SQLException ex) {
                    LOGGER.error(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                connection.close();
            } catch (Exception exp) {
                LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
                exp.printStackTrace();
            }
        }
    }

    // ACLARACION: No es obligatorio usarlo, pero podemos hacer este metodo para no repetirlo siempre
    private  Paciente crearObjetoPaciente(ResultSet rs) throws SQLException {
        int idPaciente = rs.getInt("id");
        String nombrePaciente = rs.getString("nombre");
        String apellidoPaciente = rs.getString("apellido");
        int dniPaciente = rs.getInt("dni");
        LocalDate fechaIngreso = rs.getDate("fecha").toLocalDate();
        Domicilio domicilioPaciente = new DomicilioDAOH2().buscarPorId(rs.getInt("domicilio_id"));
        return new Paciente(idPaciente, nombrePaciente, apellidoPaciente, dniPaciente, fechaIngreso, domicilioPaciente);
    }
}
