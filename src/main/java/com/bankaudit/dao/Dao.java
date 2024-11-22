package com.bankaudit.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 * Base interface for CRUD operations and common queries
 * @author amit.patel
 * @version 1.0
 */
public interface Dao {

	/**
     * This method used to save Object entity 
     * @param entity specifies that the method is able to accept all model class's object to save
     *
     */
	void save(Object entity);
	
	void saveOrUpdate(Object entity);
	
	/**
     * This method used to update Object entity 
     * @param entity specifies that the method is able to accept all model class's object to update 
     *
     */
	void update(Object entity);
	
    /**
     * This method used to delete Object entity 
     * @param entity specifies that the method is able to accept all model class's object to delete 
     *
     */
	void delete(Object entity);
	
	 /**
     * This method used to delete Object entity from given class based on given id 
     * @param classInstance specifies that the method is able to accept all model class's Class object to delete
     * @param id specifies that, id is able to accept Serializable id  to delete object 
     * @return Object specifies the deleted entity 
     *
     */
	Object deleteById(Class<?> classInstance, Serializable id);
	
	/**
     * This method used to get the Object of given class based on given id
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @param id specifies the object to be retrieve 
     * @return Object
     *
     */
	@SuppressWarnings("rawtypes")
	Object get(Class classInstance, Serializable id);
	
	/**
     * This method used to get the list of objects based on given class instance
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @return List
     *
     */
	@SuppressWarnings("rawtypes")
	List getAll(Class classInstance);
	
	/**
     * This method used to get the unique Object of given class based on given properties
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @param properties specifies the properties based on that ,object will be retrieved
     * @return Object 
     *
     */
	@SuppressWarnings("rawtypes")
	Object getUniqueEntityByMatchingProperties(Class classInstance, Map<String, Object> properties);
	
	/**
     * This method used to get the List of Objects of given class based on given properties
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @param properties specifies the properties based on that ,objects will be retrieved
     * @return Object 
     *
     */
	@SuppressWarnings("rawtypes")
	List getEntitiesByMatchingProperties(Class classInstance, Map<String, Object> properties);
	
	void flushSession();
	
}
