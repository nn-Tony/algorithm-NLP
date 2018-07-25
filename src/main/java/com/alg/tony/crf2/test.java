package com.alg.tony.crf2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class test {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String CRFmodelRealpath = "F:/CRFLabelWord/new/model.txt";		
		CRFmodel crf_model = new CRFmodel();
		crf_model.loadCRFmodel(CRFmodelRealpath); // CRF模型解析
		for(String s : crf_model.labels) {
			System.out.println(s);
		}
		
		String text = "商品包装系统设计";
		Result parse = ToAnalysis.parse(text);
		List<String> in = new ArrayList<String>();
		for(Term t : parse) {
			in.add(t.getName() + "&&" + t.getNatureStr());
		}
		List<String> out = crf_model.getCRFlabel(in);
		for(int i=0; i<in.size(); ++i) {
			System.out.println(in.get(i) + "\t" + out.get(i));
		}
	}
}
