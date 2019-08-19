package com.github.mob41.osums.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.github.mob41.osumer.debug.DumpManager;
import com.github.mob41.osums.Osums;

public class Main {

	public static void main(String[] args) throws Exception {
		Osums osums = new Osums();
		
		Server server = new Server(6099);
		
		FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
	    filterHolder.setInitParameter("allowedOrigins", "https://www.osumer.ml");
	    filterHolder.setInitParameter("allowedHeaders", "X-Requested-With");
	    filterHolder.setInitParameter("allowedMethods", "GET");
		
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/");
        servletHandler.addServlet(new ServletHolder(new QueryServlet(osums)), "/query");
        servletHandler.addFilter(filterHolder, "/query", null);
        
        server.setHandler(servletHandler);
        
		server.start();
		server.join();
	}

}
