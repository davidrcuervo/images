package com.laetienda.images.utilities;

import javax.persistence.EntityManager;

import com.laetienda.images.entities.Setting;
import com.laetienda.images.entities.User;

public class DbTransaction {
	
	private EntityManager em;
	private Logger log;
	
	public DbTransaction(EntityManager em, Logger log){
		this.em = em;
		this.log = log;
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
	
	
}