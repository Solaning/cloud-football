package com.kinth.football.bean;

import java.util.List;

public class JsonBean {
	public String a;
	
	public List<B> b;

	public List<B> getB() {
		return b;
	}

	public void setB(List<B> b) {
		this.b = b;
	}

	public static class B {
		public String id;
		public String name;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}

}
