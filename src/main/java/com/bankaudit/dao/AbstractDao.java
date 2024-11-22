package com.bankaudit.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bankaudit.model.*;
/**
 * AbstractDao class implements the {@link Dao} interface and implements the {@link Dao} interface's methods
 * for CRUD operations and common queries
 *  
 * @author amit.patel
 * @version 1.0
 */
public abstract class AbstractDao implements Dao {
 
    private static final Order Order = null;
	@Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;
    /**
     * This method provides the  current {@link Session}
     * to provide access to persist methods
     * @return Session
     * @see Session 
     * 
     */
    protected Session getSession() {    
        return sessionFactory.getCurrentSession();
    }
 
    /**
     * This method used to save Object entity 
     * @param entity specifies that the method is able to accept all model class's object to save
     * 
     */
    public void save(Object entity) {
    	
        getSession().save(entity);
        
    }
    
    public void saveOrUpdate(Object entity) {
    	
        getSession().saveOrUpdate(entity);
        
    }
    
    /**
     * This method used to update Object entity 
     * @param entity specifies that the method is able to accept all model class's object to update 
     * 
     */
    public void update(Object entity) {
        getSession().update(entity);
    }
 
    
    public void merge(Object entity) {
    	getSession().merge(entity);
	}
    
    
    /**
     * This method used to delete Object entity 
     * @param entity specifies that the method is able to accept all model class's object to delete 
     * 
     */
    public void delete(Object entity) {
        getSession().delete(entity);
    }
    
    /**
     * This method used to delete Object entity from given class based on given id 
     * @param classInstance specifies that the method is able to accept all model class's Class object to delete
     * @param id specifies that, id is able to accept Serializable id  to delete object 
     * @return Object specifies the deleted entity 
     * 
     */
    public Object deleteById(Class<?> classInstance, Serializable id) {
    	Session session = getSession();
    	Object entity = session.get(classInstance, id);
    	if(entity != null){
         session.delete(entity);
         return entity;
    	}else{
         return entity;
    	}
    }
    
    
    
    /**
     * This method used to get the Object of given class based on given id
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @param id specifies the object to be retrieve 
     * @return Object
     * 
     */
    @SuppressWarnings("rawtypes")
	public Object get(Class classInstance, Serializable id) {
       return getSession().get(classInstance, id);
    }
    
    /**
     * This method used to get the list of objects based on given class instance
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @return List
     * 
     */
    @SuppressWarnings("rawtypes")
	public List getAll(Class classInstance) {
		
		Session session = getSession();
		
		//For the early fetching of child entities, we need to instruct Hibernate Criteria to use distinct root entities
		@SuppressWarnings("deprecation")
        Criteria criteria = session.createCriteria(classInstance).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		
		return criteria.list();
	}
    
    /**
     * This method used to get the unique Object of given class based on given properties
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @param properties specifies the properties based on that ,object will be retrieved
     * @return Object 
     * 
     */
    @SuppressWarnings("rawtypes")
	public Object getUniqueEntityByMatchingProperties(Class classInstance, 
    											Map<String, Object> properties) {
    	
    	Session session = getSession();
		
		Criteria criteria = session.createCriteria(classInstance).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		
		for(Map.Entry<String,Object> entry :properties.entrySet()) {
			criteria.add(Restrictions.eq(entry.getKey(), entry.getValue()));
		}
	
		
		return criteria.uniqueResult();
    }
    
    /**
     * This method used to get the List of Objects of given class based on given properties
     * 
     * @param classInstance specifies that the method is able to accept all model class's Class object to retrieve.
     * @param properties specifies the properties based on that ,objects will be retrieved
     * @return Object 
     * 
     */
    @SuppressWarnings("rawtypes")
    public List getEntitiesByMatchingProperties(Class classInstance, Map<String, Object> properties) {

    	Session session = getSession();

    	Criteria criteria = session.createCriteria(classInstance);

    	criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
    	for(Map.Entry<String,Object> entry :properties.entrySet()) {
    		criteria.add(Restrictions.eq(entry.getKey(), entry.getValue()));
    	}
    	if(classInstance == EntityLevelCodeDesc.class) {
			criteria.addOrder(Order.asc("levelCode").ignoreCase());  
		}
    	// if(classInstance == MaintAuditActivity.class) {
		// 	criteria.addOrder(Order.asc("activityName").ignoreCase());  
		// }
    	// if(classInstance == MaintAuditFinding.class) {
		// 	criteria.addOrder(Order.asc("findingName").ignoreCase());  
		// }
    	// if(classInstance == MaintAuditGroup.class) {
		// 	criteria.addOrder(Order.asc("auditGroupName").ignoreCase());  
		// }
    	// if(classInstance == MaintAuditProcess.class) {
    	// 	criteria.addOrder(Order.asc("processName").ignoreCase());  
    	// }
    	if(classInstance == MaintAuditSubgroup.class) {
    		criteria.addOrder(Order.asc("auditSubGroupName").ignoreCase());  
    	}
    	if(classInstance == MaintAuditTypeDesc.class) {
    		criteria.addOrder(Order.asc("auditTypeDesc").ignoreCase());  
    	}
    	// if(classInstance == MaintCriticality.class) {
    	// 	criteria.addOrder(Order.asc("criticalityDesc").ignoreCase());  
    	// }
    	if(classInstance == MaintEntity.class) {
    		criteria.addOrder(Order.asc("unitName").ignoreCase());  
    	}
    	if(classInstance == MaintUsergroupRoles.class) {
    		criteria.addOrder(Order.asc("ugRoleName").ignoreCase());  
    	}
    	if(classInstance == MaintUsergroupRolesWrk.class) {
    		criteria.addOrder(Order.asc("ugRoleName").ignoreCase());  
    	}
    	// if(classInstance == RiskScorePercentageWrk.class) {
    	// 	criteria.addOrder(Order.asc("riskCategoryDesc").ignoreCase());  
    	// }
    	// if(classInstance == RiskScorePercentage.class) {
    	// 	criteria.addOrder(Order.asc("riskCategoryDesc").ignoreCase());  
    	// }
    	
    	return criteria.list();
    }
    

	@Override
	public void flushSession() {
		Session session=getSession();
		session.flush();
		session.clear();
	}


	

	
}
