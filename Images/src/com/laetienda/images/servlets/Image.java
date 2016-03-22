package com.laetienda.images.servlets;

import java.io.IOException;
import java.io.File;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import com.laetienda.images.utilities.DB;
import com.laetienda.images.utilities.Logger;
import com.laetienda.images.utilities.Imagen;

public class Image extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Logger log;
	private Imagen image;
	//private DB db;
       
	public void init(ServletConfig config) throws ServletException {
		log = (Logger)config.getServletContext().getAttribute("Logger");
		//db = (DB)config.getServletContext().getAttribute("db");
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Executing Image servlet.");
		
		File imageFolder = (File)request.getServletContext().getAttribute("imageFolder");
		String imageName = request.getPathInfo().substring(request.getPathInfo().lastIndexOf("/") + 1);
		
		try{
			image = new Imagen(new File(imageFolder.getAbsolutePath() + "/" + imageName), log);
			
			String mimeType = request.getServletContext().getMimeType(image.getName());
			
			if(mimeType == null){
				log.error("Could not get MIME type of " + image.getAbsolutePath());
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				
				//set content type
				response.setContentType(mimeType);
				
				if(request.getParameter("height") != null){
					image.setHeight(request.getParameter("height"));
				}
				
				if(request.getParameter("width") != null){
					image.setWidth(request.getParameter("width"));
				}
				
				OutputStream out = response.getOutputStream();
				ImageIO.write(image.getImage(), image.getExtension(), out);
			}	
		}catch (Exception ex){
			log.error(ex.getMessage().toString());
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Executing Image servlet.");
		doGet(request, response);
	}
}
