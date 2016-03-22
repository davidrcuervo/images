package com.laetienda.images.utilities;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.laetienda.images.entities.*;

public class DB {
	
	public static void main(String[] args) {
		
		EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("Images");
		DB db = new DB(emfactory);
		
		System.out.println("Testing connection to DB by printing setting logger file: ");
		System.out.println("Logger file: " + db.getSetting("logger_file").getValue());
		
		db.close();
	}
	
	private EntityManagerFactory emfactory;
    private Logger log;

    public DB(EntityManagerFactory emfactory){
    	log = new Logger();
    	this.emfactory = emfactory;
    }
    
    public DB(Logger log, EntityManagerFactory emfactory){
    	this.log = log;
    	this.emfactory = emfactory; 
    }
    
    public void close(){
    	log.info("Closing database connection");
    	
    	emfactory.close();
    }
    
    public EntityManager getEntityManagerForLogger() throws IllegalStateException {
    	
		EntityManager em = emfactory.createEntityManager();
		return em;
    }
    
    public Setting getSettingForLogger(String settingParam) throws IOException{
    	
    	Setting setting = new Setting();
    	
    	try{
    		EntityManager em = emfactory.createEntityManager();
    		setting = em.createNamedQuery("Setting.findBySetting", Setting.class).setParameter("setting", settingParam).getSingleResult();
    		em.clear();
    		em.close();	
    	}catch(Exception ex){
    		throw new IOException(ex.getMessage());
    	}
    	
    	return setting;
    }
    
    public Setting getSetting(String settingParam){
    	log.info("Getting setting. $setting: " + settingParam);
    	Setting setting = new Setting();
    	
    	try{
    		EntityManager em = emfactory.createEntityManager();
    		setting = em.createNamedQuery("Setting.findBySetting", Setting.class).setParameter("setting", settingParam).getSingleResult();
    		em.clear();
    		em.close();	
    	}catch(Exception ex){
    		log.error("Setting has not been found in the database");
    		log.error(ex.getMessage());
    	}
    	
    	return setting;
    }
    
    public User getUser(String username){
    	log.info("Getting user from username: $username: " + username);
    	User user = new User();
    	
    	try{
    		EntityManager em = emfactory.createEntityManager();
    		user = em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
    		em.clear();
    		em.close();
    	}catch(Exception ex){
    		log.error("User has not been found in the database");
    		log.error(ex.getMessage());
    	}
    	
    	return user;
    }
    
    
}
