package com.laetienda.images.utilities;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import com.laetienda.images.entities.Setting;
import com.laetienda.images.entities.User;

public class DbTransaction {
	
	private EntityManager em;
		
	public DbTransaction(EntityManager em, Logger log){
		this.em = em;
	}
	
	public EntityManager getEm(){
		return this.em;
	}
	
	public Setting getSetting(String settingParam) throws Exception{
    	
    	Setting setting = new Setting();
    	
    	try{
    		setting = em.createNamedQuery("Setting.findBySetting", Setting.class).setParameter("setting", settingParam).getSingleResult();
    		
    	}catch(Exception ex){
    		throw ex;
    	}finally{
    		em.clear();
    		
    	}
    	
    	return setting;
    }
	
	public User getUserByUsername(String username) throws Exception{
		
    	User user = new User();
    	
    	try{
    		
    		user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
    		
    	}catch(Exception ex){
    		throw ex;
    	}finally{
    		em.clear();
    		
    	}
    	
    	return user;
	}
	
	public boolean begin() throws Exception {
		
		boolean result = false;
		
		if(begin(em)){
			result = true;
		}
		
		return result;
	}
	
	public boolean begin(EntityManager em) throws Exception {
		
		boolean result = false;
		
		try{
			em.clear();
			em.getTransaction().begin();
			result = true;
			
		}catch(IllegalStateException ex){
			em.clear();
			throw ex;
		}finally{
			
		}
		
		return result;
	}
	
	public boolean save(Object entity) throws Exception {
		boolean result = false;
		
		if(save(em, entity)){
			result = true;
		}
		
		return result;
	}
	
	public boolean save(EntityManager em, Object entity) throws Exception {
		boolean result = false;
		
		try{
			em.persist(entity);
			em.getTransaction().commit();
			result = true;
		}catch(IllegalStateException ex){
			throw ex;
		}catch(RollbackException ex){
			
			try{
				em.getTransaction().rollback();
			}catch(IllegalStateException e){
				throw e;
			}finally{
				
			}
			throw ex;
			
		}finally{
			em.clear();
		}
		
		return result;
	}
}