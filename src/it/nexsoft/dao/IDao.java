package it.nexsoft.dao;

public interface IDao<E> {
    void insertOrUpdate(E entity);
}
