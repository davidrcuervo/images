package com.laetienda.images.listeners;

import java.io.File;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.laetienda.images.utilities.Logger;
import com.laetienda.images.utilities.DB;


public class LoadUtilities implements ServletContextListener {

	private Logger log;
	private DB db;
	private EntityManagerFactory emfactory;
	private File imageFolder;
	
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	log.info("Destroying servlet context");
    	
    	db.close();
    }

    public void contextInitialized(ServletContextEvent arg0)  { 
         
    	ServletContext sc = arg0.getServletContext();
    	
    	initializeLogger(sc);
    	intializeDB(sc);
    	intializeImageFolder(sc);
    }	
    
    private void initializeLogger(ServletContext sc){
    	String logsFile = sc.getRealPath((String)sc.getInitParameter("logs-file"));
    	
    	log = new Logger();
    	log.setFile(logsFile);
    	log.debug("Logs file is: " + log.getFilePath());
    	
    	sc.setAttribute("Logger", log);
    	log.debug("Logger has been initialized");
    }
    
    private void intializeDB(ServletContext sc){
    	log.info("Initializing database....");
    	
    	String persistenceUnitName = (String)sc.getInitParameter("persistence-unit-name");
    	
    	try{
    		emfactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    		db = new DB(log, emfactory);
    		log.setDatabaseConnection(db);
    		    		
    		log.debug("The application has been succesfully conected to the database");
    		
    		sc.setAttribute("db", db);
    		
    	}catch (IllegalStateException ex){
    		log.emergency("Can not create connection to the database because entity manager has been closed.");
    		System.exit(1);
    	}catch(PersistenceException ex){
    		log.emergency(ex.getMessage());
    		System.exit(1);
    	}
    }
    
    private void intializeImageFolder(ServletContext sc){
    	log.info("Initiliazing image folder");
    	
    	String temp = (String)db.getSetting("images_path").getValue();
    	imageFolder = new File(temp);
    	log.debug("$images_path: " + temp);
    	
    	if(imageFolder.exists() && imageFolder.isDirectory() && imageFolder.canRead()){
    		sc.setAttribute("imageFolder", imageFolder);
    	}else{
    		log.emergency("Image folder does not exist, or it is not a directory or can't be read");
    		log.debug("Image fotder is: " + imageFolder.getPath());
    		log.debug("Does folder exists? " + (imageFolder.getAbsoluteFile().exists() ? "true" : "false"));
    		log.debug("is folder a directory? " + (imageFolder.isDirectory() ? "true" : "false"));
    		log.debug("can folder be read? " + (imageFolder.canRead() ? "true" : "false"));
    		
    		System.exit(1);
    	}
    }
}
