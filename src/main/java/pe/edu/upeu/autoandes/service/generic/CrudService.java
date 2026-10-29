package pe.edu.upeu.autoandes.service.generic;

public interface CrudService<REQ, RES, ID> {
    RES create(REQ dto);

    RES update(ID id, REQ dto);

    RES read(ID id);

    void delete(ID id);

    Iterable<RES> readAll();
}
