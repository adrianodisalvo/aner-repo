package it.nexsoft.dao;

public interface IDao<E> {
    void persist(E entity);
}
