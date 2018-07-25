package com.alg.tony.demo;

import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.SmartForest;

public class testForest {

	public static void main(String[] args){
		Forest forest = new Forest();
		SmartForest<String[]> branch = forest;
		String str = "麻辣小龙虾";
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars.length == i + 1) {
				branch.add(new Forest(chars[i], 3, null));
			} else {
				branch.add(new Forest(chars[i], 1, null));
			}
			branch = branch.getBranch(chars[i]);
		}
		branch = forest;
		str = "麻辣谭";
		chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars.length == i + 1) {
				branch.add(new Forest(chars[i], 3, null));
			} else {
				branch.add(new Forest(chars[i], 1, null));
			}
			branch = branch.getBranch(chars[i]);
		}
	}
}
