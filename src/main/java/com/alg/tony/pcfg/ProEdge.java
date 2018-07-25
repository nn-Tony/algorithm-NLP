package com.alg.tony.pcfg;

import java.util.List;

public class ProEdge {

	public String root;
	public int first;
	public int last;
	public int sub1;
	public int sub2;
	public int ruleId;
	public double outsideProb;
	public Rule rule ;
	public double InsideProb;
	public double InProbAddUp;
	public String parent;
	
	
	public ProEdge(){
		root = "";
		first = 0;
		last = 0;
		sub1 = -1;
		sub2 = -1;
		ruleId = 0;
		outsideProb = 0.0;
		rule = new Rule();
		InsideProb = 0.0;
		InProbAddUp = 0.0;
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
	public ProEdge(String wstr, int wid){
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
	public ProEdge(ProEdge p, int pid, int rid, List<Rule> rules){
		ruleId = rid;
		outsideProb=0.0;
		parent="";
		rule = rules.get(rid);
		root=rule.ls;
		first=p.first;
		last=p.last;
		sub1=pid;
		sub2=-1;
		InsideProb = rule.score*p.InProbAddUp;
		InProbAddUp=InsideProb;
	}
	public ProEdge(ProEdge p1, ProEdge p2,int pid1,int pid2,int Rid,List<Rule> rules)
	{
		ruleId=Rid;
		outsideProb=0.0;
		parent="";
		rule=rules.get(Rid);;
		root=rule.ls;
		first=p1.first;
		last=p2.last;
		sub1=pid1;
		sub2=pid2;
		InsideProb = rule.score*p1.InProbAddUp*p2.InProbAddUp;
		InProbAddUp = InsideProb;
	}
	
}
