package it.nexsoft.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class Dao<E> implements IDao<E> {
	
	//protected Class entityClass;

	protected EntityManager entityManager;

	public Dao() {
		//ParameterizedType genericSuperclass = (ParameterizedType)getClass().getGenericSuperclass();
		//this.entityClass = (Class)genericSuperclass.getActualTypeArguments()[1];
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AutomaticNexsoftEmailResponder");
		entityManager = entityManagerFactory.createEntityManager();
	}

	public void persist(E entity) {
		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
	}
}
