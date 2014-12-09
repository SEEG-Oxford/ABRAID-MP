package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
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
 * @param <I> the class of the entity's identifier
 */
public abstract class AbstractDao<E, I extends Serializable> {
    private SessionFactory sessionFactory;

    private final Class<?> entityClass;

    public AbstractDao(SessionFactory sessionFactory) {
        ParameterizedType superType = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class) superType.getActualTypeArguments()[0];
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
     * Convenience method to return a single instance that matches the named query with the specified parameters,
     * or null if the query returns no results.
     * @param namedQuery the named query to run
     * @param parameterNamesAndValues the names and values of the parameters. These must be in the format
     * name1, value1, name2, value2, ...
     * @return the single result or {@code null}
     * @throws HibernateException if there is more than one matching result
     */
    @SuppressWarnings("unchecked")
    protected final E uniqueResultNamedQuery(String namedQuery, Object... parameterNamesAndValues)
            throws HibernateException {
        Query query = getParameterisedNamedQuery(namedQuery, parameterNamesAndValues);
        return uniqueResult(query);
    }

    /**
     * Convenience method to execute an update or delete named query with the specified parameters.
     * @param namedQuery the named query to run
     * @param parameterNamesAndValues the names and values of the parameters. These must be in the format
     * name1, value1, name2, value2, ...
     * @throws HibernateException if the query can not be executed
     */
    protected final void noResultNamedQuery(String namedQuery, Object... parameterNamesAndValues)
            throws HibernateException {
        Query query = getParameterisedNamedQuery(namedQuery, parameterNamesAndValues);
        query.executeUpdate();
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
     * Get the results of a named query with the specified parameters.
     * @param namedQuery the named query to run
     * @param parameterNamesAndValues the names and values of the parameters. These must be in the format
     * name1, value1, name2, value2, ...
     * @return the list of matched query results
     * @throws HibernateException Indicates a problem executing the SQL or processing the SQL results.
     */
    @SuppressWarnings("unchecked")
    protected final List<E> listNamedQuery(String namedQuery, Object... parameterNamesAndValues)
            throws HibernateException {
        Query query = getParameterisedNamedQuery(namedQuery, parameterNamesAndValues);
        return list(query);
    }

    /**
     * Get a page of the results of a named query with the specified parameters.
     * @param namedQuery the named query to run
     * @param pageNumber the number of the page to return
     * @param pageSize the size of the pages to split the result into
     * @param parameterNamesAndValues the names and values of the parameters. These must be in the format
     * name1, value1, name2, value2, ...
     * @return the list of matched query results
     * @throws HibernateException Indicates a problem executing the SQL or processing the SQL results.
     */
    @SuppressWarnings("unchecked")
    protected final List<E> listPageOfNamedQuery(
            String namedQuery, int pageNumber, int pageSize, Object... parameterNamesAndValues)
            throws HibernateException {
        Query query = getParameterisedNamedQuery(namedQuery, parameterNamesAndValues);
        query.setMaxResults(pageSize);
        query.setFirstResult((pageNumber - 1) * pageSize);
        return list(query);
    }


    /**
     * Get all entities of the given type.
     * @return the list of entities
     * @throws HibernateException Indicates a problem executing the SQL or processing the SQL results.
     * @see Query#list()
     */
    @SuppressWarnings("unchecked")
    public List<E> getAll() throws HibernateException {
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
     * @throws HibernateException Indicates a problem executing the SQL or processing the SQL results.
     * @see Session#saveOrUpdate(Object)
     */
    public final void save(E entity) throws HibernateException {
        currentSession().save(entity);
    }

    /**
     * Gets a named query, and sets the specified parameters.
     * @param namedQuery the named query.
     * @param parameterNamesAndValues the names and values of the parameters. These must be in the format
     * name1, value1, name2, value2, ...
     * @return the named query.
     */
    public Query getParameterisedNamedQuery(String namedQuery, Object... parameterNamesAndValues) {
        Query query = namedQuery(namedQuery);
        for (int i = 0; i < parameterNamesAndValues.length; i += 2) {
            String parameterName = (String) parameterNamesAndValues[i];
            Object parameterValue = parameterNamesAndValues[i + 1];

            if (parameterValue instanceof Collection) {
                query.setParameterList(parameterName, (Collection) parameterValue);
            } else {
                query.setParameter(parameterName, parameterValue);
            }
        }
        return query;
    }
}
