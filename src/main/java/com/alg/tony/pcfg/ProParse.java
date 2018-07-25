package com.alg.tony.pcfg;

import java.util.ArrayList;
import java.util.List;

public class ProParse {
//
	static List<ProEdge> edges = new ArrayList<ProEdge>();
	
	public static void parsing(String s,List<Rule> rules){
		double result = 0.0;
		edges = new ArrayList<ProEdge>();
		int wn = 0;
		String w = "";
		while(s.length()>0){
			wn++;
			int i = s.indexOf(" ");
			if(i<0){
				w = s;
				s = "";
			}else{
				w = s.substring(0, i);
				s = s.substring(i).trim();
			}
//			System.out.println("单词："+w);
			ProEdge pe = new ProEdge(w,wn);
			edges.add(pe);
			int pid = edges.size()-1;
			int rid = getRule(pe.getRoot(),"",rules);
			if(rid>=0){
				Rule r = rules.get(rid);
				edges.add(new ProEdge(pe,pid,rid,rules));
			}
		}
//		System.out.println("edges ok\t"+edges.size());
//		for(int i=edges.size()-1; i>=0; i--){
//			System.out.println(edges.get(i).root);
//		}
		for(int j=1;j<=wn;j++){ // 原书j=1
			for(int i=1;i+j<=wn;i++) {// 原书i=1
//				System.out.println("j "+j+" i "+i+" InsertEdges( "+i+" "+(i+j)+" )");
				InsertEdges(i,i+j,rules);
//				System.out.println("edges ok\t"+edges.size());
//				for(int k=edges.size()-1; k>=0; k--){
//					System.out.println(edges.get(k).root);
//				}
				/*未完待续*/
			}
		}
//		System.out.println("InsertEdges ok");
		for(int i=edges.size()-1; i>=0; i--){
			ProEdge pe = edges.get(i);
			if(pe.root=="S" && pe.first==1 && pe.last==wn){
				result += pe.InsideProb;
			}else{
				break;
			}
		}
//		System.out.println("^-^ ok\t"+result);
		if(result>0.0){	
			GetOutsideProb(wn,rules);
			GetDesireCount(result,rules);
		}
//		System.out.println("^-^ ok ^^");
		String resultstr = GetProbTrees(wn);
		System.out.println(resultstr);
	}
	
	private static void InsertEdges(int first,int last,List<Rule>rules)
	{
		int i = 0;
		int j = 0;
		int rid = 0;
		ProEdge e = null;
		ProEdge e1 = null;
		ProEdge e2 = null;
		ProEdge e0 = null;

		int edgesize = edges.size();
		for(i=0;i<edges.size();i++) {
			e1= edges.get(i);
			if(e1.first!=first)
				continue;
			if(e1.last==last && (rid=getRule(e1.getRoot(),"",rules))>=0) {
				e=new ProEdge(e1,i,rid,rules);
				edges.add(e);
				for(int k=edges.size()-2;k>=0;k--) {
					e0= edges.get(k);
					if(e0.first!=e.first || e0.last !=e.last)
						break;
					if(e0.getRoot()==e.getRoot()) {
						e0.InProbAddUp+=e.InsideProb;
						e.InProbAddUp+=e0.InsideProb;
					}
				}
				continue;
			}

//			System.out.println(i+"\t"+edges.size());
			for(j=0;j<edges.size();j++) {
				e2= edges.get(j);
				if(e2.last != last || e1.last+1 != e2.first)
					continue;
				rid=getRule(e1.getRoot(),e2.getRoot(),rules);
				if(rid<0)
					continue;
				e=new ProEdge(e1,e2,i,j,rid,rules);
				edges.add(e);
				for(int k=edges.size()-2;k>=0;k--) {
					e0= edges.get(k);
					if(e0.first != e.first || e0.last != e.last)
						break;
					if(e0.getRoot() == e.getRoot()) {
						e0.InProbAddUp+=e.InsideProb;
						e.InProbAddUp+=e0.InsideProb;
					}
				}
			}
		}
	}
	
	private static void GetDesireCount(double sProb,List<Rule>rules)
	{ // 计算规则的期望次数
		Rule r;
		ProEdge e,e1,e2;
		double dCount;

		for(int i=0;i<edges.size();i++) {
			e=edges.get(i);
			if(e.outsideProb==0.0)
				continue;
			r=rules.get(e.ruleId);
			dCount=r.score*e.outsideProb;
			if(e.sub1>=0) {
				e1=edges.get(e.sub1);
				dCount *= e1.InProbAddUp;
			}
			if(e.sub2>=0) {
				e2=edges.get(e.sub2);
				dCount *=e2.InProbAddUp;
			}
			dCount/=sProb;
			r.desireCount+=dCount;
		}
	}
	
	private static void GetOutsideProb(int wn,List<Rule>rules)
	{// 向外算法
		int j = 0;
		int pid = 0;
		int bid = 0;
		String tmp;
		ProEdge e = null;
		ProEdge pe = null; 
		ProEdge be = null ;
		Rule r;

		for(int i=edges.size()-1; i>=0; i--) {
			e=edges.get(i);
			e.parent.trim();
			if(e.first==1 && e.last==wn && e.root .equals("S") )
				e.outsideProb=1.0;
			else 
				if(e.sub1<0 || e.parent.isEmpty())
					continue;
				else
					while(!(e.parent.isEmpty())) {
						j=e.parent.indexOf(' ');
						if(j>0) {
							tmp=e.parent.substring(0, j);
							e.parent=e.parent.substring(j+1);
						}
						else {
							tmp=e.parent;
							e.parent="";
						}
						pid = Integer.parseInt(tmp);
						pe= edges.get(pid);
						r=rules.get(pe.ruleId);
						double outProb=(pe.outsideProb) * (r.score);
						if(pe.sub1>=0 && pe.sub1!=i)
							bid=pe.sub1;
						else
							if(pe.sub2>=0 && pe.sub2!=i)
								bid=pe.sub2;
							else
								bid=-1;
							if(bid>=0) {
								be=edges.get(bid);
								outProb *=be.InProbAddUp;
							}
							e.outsideProb+=outProb;
					}
					if(e.sub1>=0) {
						ProEdge  s1= edges.get(e.sub1);
						tmp = String.valueOf(i);
						s1.parent=tmp; // 原书为+=
					}
					if(e.sub2>=0) {
						ProEdge s2= edges.get(e.sub2);
						tmp = String.valueOf(i);
						s2.parent=tmp;
					}
			}
	}
	
	private static String GetProbTrees(int wn)
	{ // 获取概率分析的结果树
//		System.out.println("edges ok\t"+edges.size());
//		for(int i=edges.size()-1; i>=0; i--){
//			System.out.println(edges.get(i).root);
//		}
		String s="";
		for(int i=0;i<edges.size();i++) {
			ProEdge e=edges.get(i);
			if(e.first==1 && e.last==wn && e.getRoot().equals("S")) { // 如果想输出每一个局部分析结果，把这个 { 去掉即可
				String tmpprob = String.valueOf(e.InsideProb);
				s+=tmpprob+GetOneProbTree(e)+"\n\n";
			} // 如果想输出每一个局部分析结果，把这个 } 去掉即可
		}
		String tmp = String.valueOf(edges.size());
		if(s.isEmpty())
			s=tmp+"句子不合语法"+"\n\n";
		else
			s=tmp+s;
		return s;
	}

	private static String GetOneProbTree(ProEdge e)
	{
		if(e.sub1==-1 && e.sub2==-1)
			return e.root;
		String s=e.root+'(';
		ProEdge e1=edges.get(e.sub1);
		s+=GetOneProbTree(e1);
		if(e.sub2>=0) {
			ProEdge e2=edges.get(e.sub2);
			s+='+'+GetOneProbTree(e2);
		}
		s+=')';
		return s;
	}
	
	private static int getRule(String rs1, String rs2,List<Rule> rules){
		int ls = -1;
		for(int i=0; i<rules.size(); i++){
			Rule r = rules.get(i);
			if(r.rs1.equals(rs1)&&r.rs2.equals(rs2)){
				ls = i;
				return ls;
			}
		}
		return ls;
	}

	
	public static void main(String[] args){
		String path = "D:/学习资源/语法分析/一个简单的PCFG分析器/PCFGParser.exe/";
		List<Rule> rules = new Rules(path+"rules1.txt").rules;
		
		String str = "老虎/noun 咬/vt 死/adj 了/utl 猎人/noun 的/de 狗/noun";
		str = "设计/vt 一个/adj logo/noun";
		ProParse.parsing(str,rules);
	}
}
