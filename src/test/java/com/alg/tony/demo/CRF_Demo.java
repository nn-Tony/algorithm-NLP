package com.alg.tony.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.alg.tony.crf.ModelParse;

public class CRF_Demo {
	
	private static ModelParse MP = new ModelParse();
	
	public static void main(String[] args) {
		String CRFmodelRealpath = "./modelfile/crfmodel.txt";//D:/workspace/algorithm/modelfile/crfmodel.txt		
		MP.parse(CRFmodelRealpath); // CRF模型解析
		
		String query = "猪八戒网络有限公司logo设计";
		Map<Term, String> resMap = getWordLabel(query);
		System.out.println(resMap);
	}

	/***
	 * @param query 查询词
	 * @return queryAttribute 查询词分词后由词，词性，属性
	 * @attribute TH-主题   ID-身份   DE-要求   ST-风格   TY-类型   OT-其他
	 */
	public static Map<Term, String> getWordLabel(String query) {
		Map<Term,String> queryAttribute = new LinkedHashMap<Term,String>();
		List<String> queryNature = new ArrayList<String>();
		String newquery = query.replaceAll("\\s*|\t|\r|\n", "");
		
		Result parse = ToAnalysis.parse(newquery);
		for(Term t : parse) {
			queryNature.add(t.getName() + "&&" + t.getNatureStr());
		}
		
		String labelstr = MP.useTestViterbi(queryNature); // 对query分词后的各个词打标签
		if(labelstr == null) {
			return queryAttribute;
		}
		String[] labels = labelstr.split("\t");
		for(int i=0,length=labels.length; i<length; i++){
			Term term = parse.get(i);
			if(!queryAttribute.containsKey(term)){
				queryAttribute.put(term, labels[i]);
			}
		}
		
		return queryAttribute;
	}
}
