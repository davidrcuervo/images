package com.laetienda.images.utilities;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;

public class Imagen {
	
	private File file;
	private BufferedImage image;
	private int type;
	private int origWidth;
	private int width;
	private int origHeight;
	private int height;
	private int origRelation;
	private Logger log;
	private String extension;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Imagen (File file, Logger log) throws IOException{
		this.log = log;
		
		if(file.exists() && file.canRead() && file.isFile()){
			this.file = file;
			findExtension();
			image = ImageIO.read(this.file);
			type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
			height = origHeight = image.getHeight();
			width = origWidth = image.getWidth();
			origRelation = width / height;
			
		}else{
			log.info("File does not exist or is not readable.");
			this.file = null;
			throw new IOException("File does not exist or is not readable.");
		}
	}
	
	private void findExtension() throws IOException{
		log.info("Finding extension for image");
		
		if(file.getName().lastIndexOf(".") != -1 && file.getName().lastIndexOf(".") != 0){
			extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			
			if(extension.equals("png")){
				log.debug("valid extension. $extension: " + extension);
			}else if(extension.equals("jpeg")){
				log.debug("valid extension. $extension: " + extension);
			}else if(extension.equals("jpg")){
				log.debug("valid extension. $extension: " + extension);
			}else{
				throw new IOException("." + extension + " is not valid");
			}
		}else{
			throw new IOException("The file exension is invalid");
		}
	}
	
	public String getExtension(){
		return this.extension;
	}
	
	//Magic of the entire app is in this method
	public BufferedImage getImage() throws IOException{
		
		BufferedImage result;
		int newW;
		int newH;
		
		if(origHeight == height && origWidth == width){
			result = this.image;
		}else{
			
			if(origWidth != width && origHeight != height){
				newW = width;
				newH = height;
			}else if(origWidth != width){
				newW = width;
				newH = newW / origRelation;
			}else if(origHeight != height){
				newH = height;
				newW = newH * origRelation;
			}else{
				throw new IOException("Internal error, sizes of original image and desired image does not fit");
			}
			
			result = new BufferedImage(newW, newH, type);
			Graphics2D g = result.createGraphics();
			g.drawImage(image, 0, 0, newW, newH, null);
			
		}
		
		return result;
	}
	
	public void setHeight(String height){
		log.info("Setting height. $height: " + height);
		
		try{
			this.height = Integer.parseInt(height);
		}catch(NumberFormatException ex){
			log.error(height + "is not recognized as valid int");
		}
	}
	
	public void setWidth(String width){
		log.info("Setting width. $width: "+ width);
		
		try{
			this.width = Integer.parseInt(width);
		}catch(NumberFormatException ex){
			log.error(width + "is not recognized as valid int");
		}
	}
	
	public String getName(){
		
		return file.getName();
	}
	
	public String getAbsolutePath(){
		
		return file.getAbsolutePath();
	}
	
	public int getType(){
		return this.type;
	}
}
