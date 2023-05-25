package codigo.dao;

import java.util.List;

public interface IDao<T> {
    // alta
    T guardar (T t);

    // buscarlos (a todos)
    List<T> listarTodos();

    // buscarlos (por id)
    T buscarPorId(int id);

    // eliminar (por id)
    void eliminar(int id);

    // modificar (por id) --> lo sacaron, pero podria practicarlo
    // T update(int id);
}
