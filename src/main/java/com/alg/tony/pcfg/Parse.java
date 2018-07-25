package com.alg.tony.pcfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parse {
//
	public List<Edge> edges = new ArrayList<Edge>();
	public int widN = 0;
	
	public Parse(String line,Rules rulesc){
		parsing(line,rulesc);
	}
	
	private List<Edge> parsing(String line,Rules rulesc){
		widN = 0;
		edges = new ArrayList<Edge>();
		List<Rule> rules = rulesc.rules;
		Set<String> natureSet = rulesc.natures;
		String[] seg = line.split(" ");
		for(int i=0; i<seg.length; i++){
			String t = seg[i];
			try{
				if(t.trim().length()<1 || (t.split("/")[0].length()<2&&!t.split("/")[1].equals("w"))) continue;
			}catch(ArrayIndexOutOfBoundsException e){
				continue;
			}
			String[] wn = t.split("/");
			if(!natureSet.contains(wn[1])) continue;
			widN ++;
			Edge e = new Edge(t, widN);
			edges.add(e);
			expend(rules);
			if(edges.size()>150) 
				break;
		}
//		String result = Tree.show(edges,widN);
//		return result;
		return edges;
	}

	private void expend(List<Rule> rules){
		int i = edges.size()-1;
		int j = 0;
		String nr = "";
		Edge e = null;
		Edge e2 = null;
		boolean raising = false;
		while(i<edges.size()){
			e = edges.get(i);
			e2 = edges.get(j);
			nr = getRule(e.getRoot(),"",rules);
			if(!raising && nr.length()>0){
				edges.add(new Edge(e,i,nr));
				raising = true;
			}
			nr = getRule(e2.getRoot(),e.getRoot(),rules);
			if(e2.last+1==e.first && nr.length()>0){
				edges.add(new Edge(e2,e,j,i,nr));
			}
			j++;
			if(j>=i){
				i++;
				j=0;
				raising = false;
			}
		}
	}
	
	private String getRule(String rs1, String rs2,List<Rule> rules){
		String ls = "";
		for(int i=0; i<rules.size(); i++){
			Rule r = rules.get(i);
			if(r.rs1.equals(rs1)&&r.rs2.equals(rs2)){
				ls = r.ls;
				return ls;
			}
		}
		return ls;
	}

	
	
}
