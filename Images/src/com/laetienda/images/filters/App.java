package com.laetienda.images.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.laetienda.images.utilities.Logger;
import com.laetienda.images.utilities.DB;

public class App implements Filter {
	
	private Logger log;
	private DB db;

    public App() {
        
    }

	public void destroy() {
		db.closeEm(log.getEm());
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest)request;
		
		log = new Logger(); 
		log.setDb(db.getNewEm());
		httpReq.setAttribute("Logger", log);
				
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		db = (DB)fConfig.getServletContext().getAttribute("db");
	}
}
