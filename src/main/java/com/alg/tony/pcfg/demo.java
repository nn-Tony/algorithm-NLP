package com.alg.tony.pcfg;

import java.util.List;

public class demo {

	public static void main(String[] args){
		String path = "D:/learnData/语法分析/一个简单的PCFG分析器/PCFGParser.exe/";
//		List<Rule> rules = new Rules(path+"rules1.txt").rules;
		Rules rules = new Rules(path+"rules1.txt");
		String str = "孩子/noun 喜欢/vt 狗/noun ";
		str = "设计/vt 一个/adj logo/noun";
		Parse parse = new Parse(str,rules);
		String result = Tree.show(parse.edges,parse.widN);
//		Parse.parsing(str,rules);
//		Parse.parsing("穿/vt 衣服/noun ", rules);
//		Parse.parsing("穿/vt 好/adj 衣服/noun", rules);
	}
}
