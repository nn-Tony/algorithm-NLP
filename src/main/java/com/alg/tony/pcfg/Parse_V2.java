package com.alg.tony.pcfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parse_V2 {
//解析（版本1：增加抽取结构截止条件,在上一个版本的基础上增加了对全句子进行语法分析）
	private List<Edge> edges = new ArrayList<Edge>();
	private int widN = 0;
	boolean fullTree = false;
	public List<Edge> edgesOld = new ArrayList<Edge>();
	public int widNOld = 0;
	public int wordendIndex = 0;
	public int wordendOldIndex = 0;
	
	public Parse_V2(String line,Rules rulesc, int type){
		if(type==1){
			parsing(line,rulesc);
		}else{
			parsing_attr(line,rulesc);
		}
		
	}
	private List<Edge> parsing_attr(String line,Rules rulesc){
		widN = 0;
		List<Rule> rules = rulesc.rules;
		Set<String> natureSet = rulesc.natures;
		String[] seg = line.split(" ");
		boolean begin = false;
		for(int i=0; i<seg.length; i++){
			String t = seg[i];
			wordendIndex+=t.length()+1;
			try{
				if(t.trim().length()<1 || (t.split("/")[0].length()<2&&
						!t.split("/")[1].equals("w")/*&&!t.split("/")[1].equals("v")*/)) continue;
			}catch(ArrayIndexOutOfBoundsException e){
				continue;
			}
			String[] wn = t.split("/");
			if(wn[0].equals("主营")) begin = true;
			if(!begin)continue;
			if(!natureSet.contains(wn[1])) continue;
			widN ++;
			Edge e = new Edge(t, widN);
			edges.add(e);
			expend(rules);
			String result = "";
			if(fullTree){
				if((result=Tree.show(edges, widN)).trim().length()<1){
					return edgesOld;
				}
			}else{
				if((result=Tree.show(edges, widN)).length()>0){
					fullTree = true;
				}
			}
			for(int j=edgesOld.size(); j<edges.size(); j++){
				edgesOld.add(edges.get(j));
			}
			widNOld++;
			wordendOldIndex = wordendIndex;
			if(edges.size()>150) 
				break;
		}
//		String result = Tree.show(edges,widN);
//		return result;
		return edges;
	}
	private List<Edge> parsing(String line,Rules rulesc){
		widN = 0;
		List<Rule> rules = rulesc.rules;
		Set<String> natureSet = rulesc.natures;
		String[] seg = line.split(" ");
		for(int i=0; i<seg.length; i++){
			String t = seg[i];
			wordendIndex+=t.length()+1;
//			try{
//				if(t.trim().length()<1 || (t.split("/")[0].length()<2&&!t.split("/")[1].equals("w")&&!t.split("/")[1].equals("v"))) continue;
//			}catch(ArrayIndexOutOfBoundsException e){
//				continue;
//			}
			String[] wn = t.split("/");
			if(wn.length < 2 || !natureSet.contains(wn[1])) {
//				widNOld++;
				wordendOldIndex = wordendIndex;
				break;
			}
			widN ++;
			Edge e = new Edge(t, widN);
			edges.add(e);
			expend(rules);
			String result = "";
			if(fullTree){
				if((result=Tree.show(edges, widN)).trim().length()<1){
					return edgesOld;
				}
			}else{
				if((result=Tree.show(edges, widN)).length()>0){
					fullTree = true;
				}
			}
			for(int j=edgesOld.size(); j<edges.size(); j++){
				edgesOld.add(edges.get(j));
			}
			widNOld++;
			wordendOldIndex = wordendIndex;
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
