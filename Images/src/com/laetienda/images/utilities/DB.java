package com.laetienda.images.utilities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.ArrayList;

public class DB {
	
	private EntityManagerFactory emfactory;
    private Logger log;
    private List<EntityManager> ems;

    public DB(EntityManagerFactory emfactory, Logger log){
    	this.log = log;
    	this.emfactory = emfactory;
    	this.ems = new ArrayList<EntityManager>();
    }
    
    public void close(){
    	log.info("Closing database connection");
    	
    	for(EntityManager temp : getEms()){
    		
    		if(temp.isOpen()){
    			temp.close();
    		}
    	}
    	
    	emfactory.close();
    }
    
    private List<EntityManager> getEms(){
    	return this.ems;
    }
    
    public EntityManager getNewEm(){
    	
    	EntityManager em = emfactory.createEntityManager();
    	getEms().add(em);
    	return em;
    }
}
