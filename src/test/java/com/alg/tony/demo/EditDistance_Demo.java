package com.alg.tony.demo;

import com.alg.tony.editDistance.EditDistance;

public class EditDistance_Demo {
	public static void main(String[] args){
		String wordA = "宣传单";
		String wordB = "彩页";
		int distance = EditDistance.caculate(wordB,wordA);
		System.out.println("EditDistance = " + distance);
		System.out.println(1.0/distance);
	}
}
