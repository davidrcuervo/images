package com.laetienda.images.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import javax.persistence.PersistenceException;

import com.laetienda.images.entities.Log;
import com.laetienda.images.entities.User;
import com.laetienda.images.entities.Setting;


public class Logger {
	
	public static final String[] LEVELS = {"DEBUG", "INFO", "NOTICE", "WARNING", "ERROR", "CRITICAL", "ALERT", "EMERGENCY"};
	public static final String DEFAULT_LEVEL = "DEBUG";
	public static final String DEFAULT_USER = "images";
	public static int counter = 0;
	
	private File file;
	private int level;
	private int tempLevel;
	private User user;
		
	private DbTransaction trans;
	private Log message;
	
	public Logger(){
		this.setLevel(DEFAULT_LEVEL);
		this.file = null;
		this.message = new Log();
		this.trans = null;
		
		user = new User();
		user.setUsername("Logger");
		message.setUser(user);
	}
		
	public void setFile(String fileName){
		
		try{
			
			file = new File(fileName);
			
			if(this.file.exists()){
				if(!file.canRead()){
					System.out.println("The log file is not readable -> FILE: " + file.getAbsolutePath());
					file = null;
				}
				
				if(!file.canWrite()){
					System.out.println("The log file is not writable -> FILE: " + file.getAbsolutePath());
					file = null;
				}
				
				if(!file.isFile()){
					System.out.println("The log file is not a file  -> FILE: " + file.getAbsolutePath());
					file = null;
				}
			}else{
				String header = "";
				header = "COUNTER\t";
				header += "DATE\t";
				header += "LEVEL\t";
				header += "USER\t";
				header += "CLASS\t";
				header += "METHOD\t";
				header += "LINE\t\t";
				header += "MESSAGE\n";
				
				FileWriter out = new FileWriter(this.file, true);
				out.write(header);
				out.close();
			}
			
		}catch(NullPointerException ex){
			System.out.println("\nThere is not a valid file for logs");
			System.out.println("The file paht is: " + file.getAbsolutePath());
			System.out.println(ex.getMessage());
			file = null;
		}catch(SecurityException ex){
			System.out.println("Security manager denies access to the file");
		}catch(IOException ex){
			System.out.println("\n" + ex.getMessage() + " FILE PATH: " + file.getAbsolutePath());
			file = null;
		}
	}
	
	public void setDb(EntityManager em){
		
		this.trans = new DbTransaction(em, this);
		
		try{
			Setting logger = trans.getSetting("default_logger_user");
			User logUser = trans.getUserByUsername(logger.getValue());
			message.setUser(logUser);
		}catch (Exception ex){
			this.critical("The user for logger has not been found");
		}finally{
			trans.getEm().clear();
		}
	}
	
	public EntityManager getEm(){
		return trans.getEm();
	}
	
	//This method is like the table of contents to save the log.
	//It calls each of the method (steps) to save the log.
	private void prePrintLog(String message, String logLevel){
		
		this.message.setLog(message);
		this.findTempLevel(logLevel);
		this.message.setLevel(LEVELS[this.tempLevel]);
		this.setClassDetails();
		
		if(trans == null && this.level <= this.tempLevel){
			this.saveToFile();
		}else{
			try{
				this.setFile(trans.getSetting("logger_file").getValue());
				this.setLevel(trans.getSetting("logger_level").getValue());
			}catch(Exception ex){
				String temp = this.message.getLog();
				this.message.setLog(temp + " ERROR: " + ex.getMessage());
				
			}finally{
				String temp;
				if(this.level <= this.tempLevel){
					try{
						EntityManager em = trans.getEm();
						
						try{
							em.getTransaction().begin();
							em.persist(this.message);
							em.getTransaction().commit();
						}catch(IllegalStateException ex){
							temp = this.message.getLog();
							this.message.setLog(temp + " ERROR: " + "Not possible to save log in database. -> ERROR: " + ex.getMessage());
							this.saveToFile();
						}catch(RollbackException ex){
							this.message.setLog(this.message.getLog() + " ERROR: " + "Not possible to save log in database. -> ERROR: " + ex.getMessage());
							
							try{
								em.getTransaction().rollback();
							}catch(IllegalStateException e){
								this.message.setLog(this.message.getLog() + " ERROR: " + "Not possible to save log in database. -> ERROR: " + ex.getMessage());
							}catch(PersistenceException e){
								this.message.setLog(this.message.getLog() + " ERROR: " + "Not possible to save log in database. -> ERROR: " + ex.getMessage());
							}finally{
								this.saveToFile();
							}
			
						}finally{
							em.clear();
							em.flush();
						}
						
					}catch(IllegalStateException ex){
						this.message.setLog(this.message.getLog() + " ERROR: " + "Entity manager has been closed. -> ERROR: " + ex.getMessage());
						this.saveToFile();
					}
				}
			}
		}
		this.message = new Log();
	}
	
	public void setLevel(String level){
		int temp = getLevel(level);
		
		if(temp >= LEVELS.length){
			this.setLevel(DEFAULT_LEVEL);
		}else{
			this.level = temp;
		}
	}
	
	private void findTempLevel(String logLevel){
		this.tempLevel = this.getLevel(logLevel);
	}
	
	private int getLevel(String level){
		
		int c;
		for(c = 0; c < LEVELS.length; c++){
			if(level.equals(LEVELS[c])){
				return c;
			} 
		}
		return LEVELS.length;
	}
	
	private void setClassDetails(){
		
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		
		for(int c=1; c < stackTraceElements.length; c++){
			if(stackTraceElements[c].getClassName().equals(Logger.class.getName()) && stackTraceElements[c].getClassName().indexOf("java.lang.Thread") != 0){	
			
			} else{
				this.message.setClass_(stackTraceElements[c].getClassName());
				this.message.setMethod(stackTraceElements[c].getMethodName());
				this.message.setLine(stackTraceElements[c].getLineNumber());
				break;
			}
		}
	}
	
	private void saveToFile(){
		Logger.counter++;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		String logLine = "";
		logLine += new Integer(Logger.counter).toString() + "\t";
		logLine += dateFormat.format(date) + "\t";
		logLine += LEVELS[this.tempLevel] + "\t";
		logLine += this.message.getClass_() + "\t";
		logLine += this.message.getMethod() + "\t";
		logLine += new Integer(this.message.getLine()).toString() + "\t";
		logLine += this.message.getLog() + "\t";
		
		if(this.file == null){
			System.out.println(logLine);
		} else {
			try{
				FileWriter out = new FileWriter(this.file, true);
				out.write(logLine + "\n");
				out.close();
			}catch(IOException ex){
				System.out.println(logLine);
				System.out.println("");
				System.out.println(ex.getMessage());
			}
		}
	}
	
	public String getFilePath(){
		
		if(this.file == null){
			return "";
		}else{
			return this.file.getAbsolutePath();
		}
	}
	
	public void debug(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
	
	public void info(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
	
	public void notice(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
	
	public void warning(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
	
	public void error(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
	
	public void critical(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
	
	public void alert(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
	
	public void emergency(String message){
		this.prePrintLog(message, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
	}
}

