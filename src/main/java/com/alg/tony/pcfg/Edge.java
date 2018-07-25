package com.alg.tony.pcfg;

public class Edge {

	public String root;
	public int first;
	public int last;
	public int sub1;
	public int sub2;
	public Edge(){
		root = "";
		first = 0;
		last = 0;
		sub1 = -1;
		sub2 = -1;
	}
	public String getRoot(){
		String str = "";
		if(root.contains("(")){
			int index = root.indexOf("(");
			str = root.substring(0,index);
		}else{
			str = root;
		}
		return str;
	}
	public String getWords(){
		String str = "";
		if(root.contains("(")){
			int index = root.indexOf("(");
			str = root.substring(index+1,root.length()-1);
		}
		return str;
	}
	public Edge(String wstr, int wid){
		if(wstr.contains("/")){
			int index = wstr.indexOf("/");
			root = "";
			root+=wstr.substring(index+1)+"("
					+wstr.substring(0, index)+")";
//			root = wstr.substring(index+1);
		}else{
			root = wstr;
		}
		first = wid;
		last = wid;
		sub1 = -1;
		sub2 = -1;
	}
	
	public Edge(Edge e, int pid, String wstr){
		root = wstr;
		first = e.first;
		last = e.last;
		sub1 = pid;
		sub2 = -1;
	}
	
	public Edge(Edge e1, Edge e2, int pid1, int pid2, String wstr){
		root = wstr;
		first = e1.first;
		last = e2.last;
		sub1 = pid1;
		sub2 = pid2;
	}
}
