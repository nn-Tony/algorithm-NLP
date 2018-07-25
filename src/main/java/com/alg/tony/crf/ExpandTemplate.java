package com.alg.tony.crf;

import java.util.HashMap;
import java.util.Map;

public class ExpandTemplate {
	//	15892 U07:_B-1/棋牌/辅助 [id tag]
	
	// ** Unigram  
	Map<String, Map<String, Integer>> tempFeatureIds = new HashMap<String, Map<String, Integer>>();
	
	// ** Bigram 
	int idBigram = 0;
	
	public void addBigramTag(String line){
		String[] seg = line.split(" ", 2);
		idBigram = Integer.parseInt(seg[0]);
	}
	
	// 240 U00:组装
	// 4 U01:_B-1
	public void addBigramTag1(String line){
		String[] seg = line.split(" ", 2);
		int id = Integer.parseInt(seg[0]);
		String[] strs = seg[1].split(":", 2);
		String temp = strs[0];    // U00
		String feature = strs[1]; // 组装
		Map<String,Integer> featureIds = new HashMap<String,Integer>();
		if(tempFeatureIds.containsKey(temp)){
			featureIds = tempFeatureIds.get(temp);
		}
		//Map<String,Integer> featureIds = tempFeatureIds.getOrDefault(temp, new HashMap<String,Integer>());
		featureIds.put(feature, id);
		tempFeatureIds.put(temp, featureIds);
	}
	
}
