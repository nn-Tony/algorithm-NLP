package com.alg.tony.pcfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rules {
//
	public List<Rule> rules;
	public Set<String> natures;
	public Rules(){
		rules = new ArrayList<Rule>();
		natures = new HashSet<String>();
	}
	
	public Rules(String path){
		rules = new ArrayList<Rule>();
		natures = new HashSet<String>();
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "utf-8"));	
			String line = "";
			
			while((line=br.readLine())!=null){
				Rule rule = new Rule(line);
				natures.addAll(rule.natureSet);
				rules.add(rule);
				line = "";
			}

			br.close();
			
		}catch(IOException e){
			System.err.println("rules read fail");
		}
	}
	
	
	
}
