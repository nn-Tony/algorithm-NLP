package com.alg.tony.pcfg;

import java.util.List;

public class Tree {

	public static String show(List<Edge> edges, int widN){
		String result = "";
		for(int i=0; i<edges.size(); i++){
			Edge e = edges.get(i);
			if(e.first==1 && e.last==widN && e.root.equals("S")){
				result = getOneTree(e,edges);
				return result;
//				System.out.println(str);
			}
		}
		return result;
	}
	private static String getOneTree(Edge e, List<Edge> edges){
		String result = "";
		if(e.sub1==-1 && e.sub2==-1)
			return e.root;
		result += e.root+"(";
		Edge e1 = edges.get(e.sub1);
		result += getOneTree(e1, edges);
		if(e.sub2>=0){
			Edge e2 = edges.get(e.sub2);
			result += "+"+getOneTree(e2, edges);
		}
		result += ")";
		return result;
	}
}
