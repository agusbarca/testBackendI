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

public class DomicilioDAOH2 implements IDao<Domicilio> {
    public static final Logger LOGGER = Logger.getLogger(DomicilioDAOH2.class);

    @Override
    public Domicilio guardar(Domicilio domicilio) {
        Connection connection = null;

        try{
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement ps = connection.prepareStatement("INSERT INTO DOMICILIOS (CALLE, NUMERO, LOCALIDAD, PROVINCIA) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS); // le pido que me genere una key/id
            ps.setString(1, domicilio.getCalle());
            ps.setInt(2, domicilio.getNumero());
            ps.setString(3, domicilio.getLocalidad());
            ps.setString(4, domicilio.getProvincia());
            ps.execute();

            //aca le pido que me lo guarde
            ResultSet rs = ps.getGeneratedKeys();
            while(rs.next()){
                domicilio.setId(rs.getInt(1));
            }

            connection.commit();
            // lo retornamos aca para estar seguros de que se guardó
            LOGGER.info("DOMICILIO GUARDADO: " + domicilio);

        }catch (Exception e) {
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
        }finally {
            try {
                connection.close();
            }catch (Exception exp){
                LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
                exp.printStackTrace();
            }
        }
        return domicilio;
    }



    @Override
    public List<Domicilio> listarTodos() {
        Connection connection = null;
        List<Domicilio> domicilios = new ArrayList<>();

        try{
            connection = H2Connection.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM DOMICILIOS");

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Domicilio domicilio = crearObjetoDomicilio(rs);
                domicilios.add(domicilio);
            }

            LOGGER.info("LISTADO DE TODOS LOS DOMICILIOS: " + domicilios);

            // ¡SIN COMMIT PQ NO GUARDAMOS NADA, SOLO BUSCAMOS!

        }catch(Exception e){
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            // SIN LO DEL ROLLBACK PQ NO GUARDAMOS NADA, SOLO BUSCAMOS
        }finally {
            try {
                connection.close();
            }catch (Exception exp){
                LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
                exp.printStackTrace();
            }
        }
        return domicilios;
    }

    @Override
    public Domicilio buscarPorId(int id) {
        Connection connection = null;
        Domicilio domicilio = null;

        try {
            connection = H2Connection.getConnection();


            PreparedStatement ps = connection.prepareStatement("SELECT * FROM DOMICILIOS WHERE ID = ?");
            ps.setInt(1, id);


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                domicilio = crearObjetoDomicilio(rs);
            }


            if (domicilio != null)
                LOGGER.info("DOMICILIO SOLICITADO: " + domicilio);
            else LOGGER.info("El DOMICILIO SOLICITADO (con id: "+ id + ") no fue hallado");


            // ¡SIN COMMIT PQ NO GUARDAMOS NADA, SOLO BUSCAMOS!

            }catch(Exception e){
                LOGGER.error(e.getMessage());
                e.printStackTrace();
                // SIN LO DEL ROLLBACK PQ NO GUARDAMOS NADA, SOLO BUSCAMOS
            }finally{
                try {
                    connection.close();
                } catch (Exception exp) {
                    LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
                    exp.printStackTrace();
                }
            }
        return domicilio;
    }

    @Override
    public void eliminar(int id) {
        Connection connection = null;

        try{
            connection = H2Connection.getConnection();
            connection.setAutoCommit(false);

            // NUEVO!! no se lo asignamos con ps.setInt(1, id), SINO QUE lo asignamos al final
            PreparedStatement ps = connection.prepareStatement("DELETE FROM DOMICILIOS WHERE ID = ?");
            ps.setInt(1, id);
            ps.execute();

            connection.commit();
            LOGGER.info("Se ha ELIMINADO el domicilio con el id: " + id);

        }catch (Exception e) {
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
        }finally {
            try {
                connection.close();
            } catch (Exception exp) {
                LOGGER.error("No se pudo cerrar la conexion: " + exp.getMessage());
                exp.printStackTrace();
            }
        }
    }

    private Domicilio crearObjetoDomicilio(ResultSet rs) throws SQLException {
        int idDomicilio = rs.getInt("id");
        String nombreDomicilio = rs.getString("calle");
        int numeroDomicilio = rs.getInt("numero");
        String localidadDomicilio = rs.getString("localidad");
        String provinciaDomicilio = rs.getString("provincia");
        return new Domicilio(idDomicilio, nombreDomicilio, numeroDomicilio, localidadDomicilio, provinciaDomicilio);
    }
}
