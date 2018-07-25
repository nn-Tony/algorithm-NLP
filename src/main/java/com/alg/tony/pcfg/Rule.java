package com.alg.tony.pcfg;

import java.util.HashSet;
import java.util.Set;

public class Rule {
//
	public String ls;
	public String rs1;
	public String rs2;
	public double score;
	public double desireCount;
	public Set<String> natureSet;
	
	public Rule(){
		ls = "";
		rs1 = "";
		rs2 = "";
		score = 0.0;
		desireCount =0.0;
		natureSet = new HashSet<String>();
	}
	
	public Rule(String str){
		int index = str.indexOf("->");
		ls = str.substring(0,index).trim();
		String line = str.substring(index+2).trim();
		String[] seg = line.split(" ");
		rs1 = seg[0].trim();
		rs2 = seg[1].trim();
		score = Double.parseDouble(seg[seg.length-1].replaceAll(";", "").trim());
		desireCount = 0.0;
		natureSet = new HashSet<String>();
		if((rs1.toLowerCase().equals(rs1)&&rs1.trim().length()>0) || rs1.equals("BB"))natureSet.add(rs1);
		if((rs2.toLowerCase().equals(rs2)&&rs2.trim().length()>0) || rs2.equals("BB"))natureSet.add(rs2);
	}
	
	
	
	
	public static void main(String[] srgs){
		String str = "S -> NP      0.1;";
		int index = str.indexOf("->");
		String ls = str.substring(0,index).trim();
		System.out.println(ls);
		
		String line = str.substring(index+2).trim();
		String[] seg = line.split(" ");
		String rs1 = seg[0].trim();
		String rs2 = seg[1].trim();
		double s = Double.parseDouble(seg[seg.length-1].replaceAll(";", "").trim());
		System.out.println(rs1+",\t,"+rs2+",\t,"+s);
	}
}
