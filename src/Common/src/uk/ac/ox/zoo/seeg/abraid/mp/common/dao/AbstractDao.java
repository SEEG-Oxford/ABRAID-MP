package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.*;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * A base class for Hibernate Data Access Object classes.
 *
 * Adapted from AbstractDAO in Dropwizard, developed by Coda Hale and Yammer, Inc.
 * Copyright 2010-2013 Coda Hale and Yammer, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2014 University of Oxford
 *
 * @param <E> the class which this DAO manages
 */
public abstract class AbstractDao<E, I extends Serializable> {
    private SessionFactory sessionFactory;

    private final Class<?> entityClass;

    /**
     * Creates a new DAO.
     */
    public AbstractDao() {
        ParameterizedType superType = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class) superType.getActualTypeArguments()[0];
    }

    /**
     * Sets the session factory.
     * @param sessionFactory The session factory
     */
    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Returns the current session.
     * @return the current session
     */
    protected final Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Creates a new {@link Criteria} query for {@code <E>}.
     * @return a new {@link Criteria} query
     * @see Session#createCriteria(Class)
     */
    protected final Criteria criteria() {
        return currentSession().createCriteria(entityClass);
    }

    /**
     * Returns a named {@link Query}.
     * @param queryName the name of the query
     * @return the named query
     * @throws HibernateException if named query is unknown
     * @see Session#getNamedQuery(String)
     */
    protected final Query namedQuery(String queryName) throws HibernateException {
        return currentSession().getNamedQuery(queryName);
    }

    /**
     * Returns the entity class managed by this DAO.
     * @return the entity class managed by this DAO
     */
    @SuppressWarnings("unchecked")
    public final Class<E> getEntityClass() {
        return (Class<E>) entityClass;
    }

    /**
     * Convenience method to return a single instance that matches the criteria, or null if the
     * criteria returns no results.
     * @param criteria the {@link Criteria} query to run
     * @return the single result or {@code null}
     * @throws HibernateException if there is more than one matching result
     * @see Criteria#uniqueResult()
     */
    @SuppressWarnings("unchecked")
    protected final E uniqueResult(Criteria criteria) throws HibernateException {
        return (E) criteria.uniqueResult();
    }

    /**
     * Convenience method to return a single instance that matches the query, or null if the query
     * returns no results.
     * @param query the query to run
     * @return the single result or {@code null}
     * @throws HibernateException if there is more than one matching result
     * @see Query#uniqueResult()
     */
    @SuppressWarnings("unchecked")
    protected final E uniqueResult(Query query) throws HibernateException {
        return (E) query.uniqueResult();
    }

    /**
     * Get the results of a {@link Criteria} query.
     * @param criteria the {@link Criteria} query to run
     * @return the list of matched query results
     * @throws HibernateException Indicates a problem either translating the criteria to SQL,
     * executing the SQL or processing the SQL results.
     * @see Criteria#list()
     */
    @SuppressWarnings("unchecked")
    protected final List<E> list(Criteria criteria) throws HibernateException {
        return criteria.list();
    }

    /**
     * Get the results of a query.
     * @param query the query to run
     * @return the list of matched query results
     * @throws HibernateException Indicates a problem executing the SQL or processing the SQL results.
     * @see Query#list()
     */
    @SuppressWarnings("unchecked")
    protected final List<E> list(Query query) throws HibernateException {
        return query.list();
    }

    /**
     * Get all entities of the given type.
     * @return the list of entities
     * @throws HibernateException Indicates a problem executing the SQL or processing the SQL results.
     * @see Query#list()
     */
    @SuppressWarnings("unchecked")
    public final List<E> getAll() throws HibernateException {
        return currentSession().createCriteria(entityClass).list();
    }

    /**
     * Return the persistent instance of {@code <E>} with the given identifier, or {@code null} if
     * there is no such persistent instance. (If the instance, or a proxy for the instance, is
     * already associated with the session, return that instance or proxy.)
     * @param id an identifier
     * @return a persistent instance or {@code null}
     * @throws HibernateException
     * @see Session#get(Class, Serializable)
     */
    @SuppressWarnings("unchecked")
    public final E getById(I id) {
        return (E) currentSession().get(entityClass, id);
    }

    /**
     * Either save or update the given instance, depending upon resolution of the unsaved-value
     * checks (see the manual for discussion of unsaved-value checking).
     * <p/>
     * This operation cascades to associated instances if the association is mapped with
     * <tt>cascade="save-update"</tt>.
     * @param entity a transient or detached instance containing new or updated state
     * @throws HibernateException
     * @see Session#saveOrUpdate(Object)
     */
    public final void save(E entity) throws HibernateException {
        currentSession().save(entity);
    }

    /**
     * Force initialization of a proxy or persistent collection.
     * <p/>
     * Note: This only ensures initialization of a proxy object or collection;
     * it is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
     * @param proxy a persistable object, proxy, persistent collection or {@code null}
     * @throws HibernateException if we can't initialize the proxy at this time, eg. the {@link Session} was closed
     */
    protected final <T> T initialize(T proxy) throws HibernateException {
        if (!Hibernate.isInitialized(proxy)) {
            Hibernate.initialize(proxy);
        }
        return proxy;
    }
}
