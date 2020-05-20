package com.github.mob41.osums.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.mob41.osums.Osums;
import com.github.mob41.osums.search.SearchResult;
import com.github.mob41.osums.search.SongResult;
import com.google.gson.Gson;

public class QueryServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9033797298845660820L;
	
	private Osums osums;
	
	private OsumsServer srv;
	
	public QueryServlet(Osums osums, OsumsServer srv) {
		this.osums = osums;
		this.srv = srv;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String itemsStr = req.getParameter("i");
		String pageStr = req.getParameter("p");
		String key = req.getParameter("k");
		
		resp.setContentType("application/json");
		
		QueryOutput output = new QueryOutput();
		
		if (itemsStr == null || pageStr == null) {
			output.result = -1;
			output.msg = "Invalid request";
			resp.setStatus(400);
			end(resp, output);
			return;
		}
		
		if (key == null) {
			key = "";
		}
		
		int items = -1;
		int page = -1;
		try {
			items = Integer.parseInt(itemsStr);
			page = Integer.parseInt(pageStr);
		} catch (NumberFormatException e) {
			output.result = -1;
			output.msg = "Invalid request";
			resp.setStatus(400);
			end(resp, output);
			return;
		}
		/*
		int max = (int) Math.ceil(results.length / items);
		
		if (page > max) {
			output.result = -1;
			output.msg = "Invalid request";
			resp.setStatus(400);
			end(resp, output);
			return;
		}
		*/
		
		SearchResult result = null;
		try {
			result = srv.searchSong(key, null, items, page);
		} catch (Exception e) {
			e.printStackTrace();
			output.result = -2;
			output.msg = "Server Error";
			resp.setStatus(503);
			end(resp, output);
			return;
		}
		
		SongResult[] results = result.getResult();
		
		if (results == null) {
			output.result = -3;
			output.msg = "No result";
			end(resp, output);
			return;
		}

		output.currentPage = page;
		output.totalPages = result.getTotalPages();
		output.result = 0;
		output.output = results;
		end(resp, output);
		return;
	}
	
	private void end(HttpServletResponse resp, QueryOutput output) throws IOException {
		Gson gson = new Gson();
		resp.getWriter().println(gson.toJson(output));
		resp.getWriter().flush();
	}

}
