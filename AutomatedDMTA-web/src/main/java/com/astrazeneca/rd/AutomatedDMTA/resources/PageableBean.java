package com.astrazeneca.rd.AutomatedDMTA.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableBean {
	@QueryParam("pageNumber") 
	private int pageNumber;
	
	@QueryParam("pageSize") 
	private int pageSize;
	
	@QueryParam("sort") 
	private String sort;

	public int getPageNumber() {
		return pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getSort() {
		return sort;
	}
	
	public Pageable getPageRequest() {
		Pageable pageable = null;
		//handles no sort:
		if (sort == null ) {
			pageable = new PageRequest(pageNumber, pageSize);
		}
		else {
			pageable = new PageRequest(pageNumber, pageSize, getSortOrders(sort));
		}
		return pageable;
	}
	
	//Helper methods:
	private Sort getSortOrders(String sort) {	
		String[] sortRequirements = sort.split(",");
		List<Sort.Order> queryOrders = new ArrayList<Sort.Order>();
		
		for (String sortRequirement : sortRequirements) {			
			//if it is a descending order ('-'):
			if (sortRequirement.charAt(0) == '-')
				queryOrders.add(new Sort.Order(Sort.Direction.DESC, sortRequirement.substring(1)));
			else {
				queryOrders.add(new Sort.Order(Sort.Direction.ASC, sortRequirement));
			}
		}
		return new Sort(queryOrders);
	}
	
}