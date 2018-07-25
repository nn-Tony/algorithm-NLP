package com.alg.tony.crf2;

import java.util.HashMap;
import java.util.Map;

public class ExpandTemplate {
	
	// ** Unigram  
	Map<String, Map<String, Integer>> templateFeatureId = new HashMap<String, Map<String, Integer>>();
	
	// ** Bigram 
	int idBigram = 0;
	
	// 120 B
	public void addBigramTag(String line){
		String[] seg = line.split(" ", 2);
		idBigram = Integer.parseInt(seg[0]);
	}
	
	// 240 U00:组装
	// 4 U01:_B-1
	public void addUnigramTag(String line){
		String[] seg = line.split(" ", 2);
		int id = Integer.parseInt(seg[0]);
		String[] strs = seg[1].split(":", 2);
		String template = strs[0];   // U00
		String feature = strs[1];    // 组装
		
		Map<String,Integer> featureId = new HashMap<String,Integer>();
		if(templateFeatureId.containsKey(template)){
			featureId = templateFeatureId.get(template);
		}
		//Map<String,Integer> featureIds = tempFeatureIds.getOrDefault(temp, new HashMap<String,Integer>());
		featureId.put(feature, id);
		templateFeatureId.put(template, featureId);
	}
	
}
