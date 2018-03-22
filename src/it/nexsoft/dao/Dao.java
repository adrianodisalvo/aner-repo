package it.nexsoft.dao;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class Dao<E> implements IDao<E> {
	
	protected Class entityClass;

	@PersistenceContext
	protected EntityManager entityManager;

	public Dao() {
		ParameterizedType genericSuperclass = (ParameterizedType)getClass().getGenericSuperclass();
		this.entityClass = (Class)genericSuperclass.getActualTypeArguments()[1];
	}

	public void persist(E entity) { entityManager.persist(entity); }
}
