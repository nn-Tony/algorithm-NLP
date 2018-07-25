package com.alg.tony.nativebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

public class CategoryPredict_2_1 {
	
	public static Forest zbjsmallDic = new Forest();  // 细粒度词典
	
	public static void main(String[] args) throws FileNotFoundException, IOException {			
		String SmallDicPath = "F:/Dic/zbjsmall.dic";         // 细粒度词典
		String ErrorDicPath = "F:/Dic/zbjaddword.dic";       // 错词词典
		String AmbiguityDicPath = "F:/Dic/zbjambiguity.dic"; // 纠正词典

		insertZbjDic(zbjsmallDic, SmallDicPath); // 加载细粒度词典 
		insertZbjDic(zbjsmallDic, ErrorDicPath); // 加载分词错误的词典
		LoadAmbiguityLibrary(AmbiguityDicPath);  // 加载纠正词典
				
		String path = "F:/UserPortrait/topic4TagResource/myCategoryPredict.txt";
		Map<String, Integer> dic = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> CateWord = loadtrainData(path, dic);
		
		BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));	
		
		while(true) {
			System.out.println("qing: ");
			String text = strin.readLine();
	//		String text = "Chinese Chinese Chinese Tokyo Japan";
			
			Result parse = ToAnalysis.parse(text, zbjsmallDic);
			
			int TrainDataWordSum = 0;
			for(Entry<String, Integer> e : dic.entrySet()) {
				TrainDataWordSum += e.getValue();
			}
			
			Map<String, Double> res = new HashMap<String, Double>();
			for(Entry<String, Map<String, Integer>> e : CateWord.entrySet()) {
				int CateWordSum = 0;
				for(Entry<String, Integer> ee : e.getValue().entrySet()) {
					CateWordSum += ee.getValue();
				}
				double prob = 1.0;
				for(Term t : parse) {
					if(t.getNatureStr().equals("null") ||
						t.getNatureStr().equals("w") ||
						t.getNatureStr().equals("m") ||
						t.getName().length() < 2) {
						continue;
					}				
					if(e.getValue().containsKey(t.getName())) {
						double tmp = (e.getValue().get(t.getName())+1)*1.0 / (CateWordSum+dic.size());
						prob *= tmp;
					} else {
						double tmp = 1.0 / (CateWordSum+dic.size());
						prob *= tmp;
					}
				}
				prob *= CateWordSum*1.0 / TrainDataWordSum;
				res.put(e.getKey(), prob);
	//			System.out.println(e.getKey() + "\t" + prob);
			}
			
			List<Entry<String, Double>> resSort = new ArrayList<Entry<String, Double>>(res.entrySet());
			Collections.sort(resSort,
					new Comparator<Entry<String, Double>>() {
						public int compare(Entry<String, Double> e1, Entry<String, Double> e2) {
							if(e2.getValue() > e1.getValue()) {
								return 1;
							} else if(e2.getValue() < e1.getValue()) {
								return -1;
							} else {
								return 0;
							}
						}
			});
			int num = 0;
			for(Entry<String, Double> e : resSort) {
				if(num++ > 3) {
					break;
				}
				System.out.println(e.getKey() + "\t" + e.getValue());
			}
		}
	}
	
	public static Map<String, Map<String, Integer>> loadtrainData(String path, Map<String, Integer> dic) throws IOException, FileNotFoundException {		
		Map<String, Map<String, Integer>> CateWord = new HashMap<String, Map<String, Integer>>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(path)), "utf-8"));		
		
		String line = null;
		while(null != (line=br.readLine())) {
			String[] parts = line.split("\t");
			if(parts.length == 3) {
				String cat = parts[0];
				Map<String, Integer> WordFreMap = CateWord.get(cat);
				if(null == WordFreMap) {
					WordFreMap = new HashMap<String, Integer>();
					CateWord.put(cat, WordFreMap);
				}
				if(WordFreMap.containsKey(parts[1])) {
					WordFreMap.put(parts[1], WordFreMap.get(parts[1])+Integer.parseInt(parts[2]));
				} else {
					WordFreMap.put(parts[1], Integer.parseInt(parts[2]));
				}
				
				if(dic.containsKey(parts[1])) {
					dic.put(parts[1], WordFreMap.get(parts[1])+1);
				} else {
					dic.put(parts[1], 1);
				}
			}
		}
		br.close();
		return CateWord;
	} 
	
	/***
	 * 导入zhubajie.dic
	 * @param zbjdicPath
	 * @throws IOException
	 */
	public static void insertZbjDic(Forest dic, String zbjdicPath) throws IOException {
		BufferedReader ar = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(zbjdicPath)), "utf-8"));
		String Line = null;
		while ((Line = ar.readLine()) != null) {
			String[] seg = Line.split("\t");
			if (seg.length == 3) {
				Library.insertWord(dic, new Value(seg[0], seg[1], seg[2]));
			} else if (seg.length == 2) {
				Library.insertWord(dic, new Value(seg[0], seg[1], "2000"));
			} else if (seg.length == 1) {
				Library.insertWord(dic, new Value(Line.toLowerCase(), "zbj", "2000"));
			}
		}
		ar.close();		
		System.out.println(ToAnalysis.parse("download over"));
	}
	
	
	/**
	 * 加载纠正词典
	 * @param path 纠正词典所在的路径
	 * @throws IOException
	 */
    public static void LoadAmbiguityLibrary(String path) throws IOException {

    	UserDefineLibrary.ambiguityForest = new Forest();
    	BufferedReader ar = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(path)), "utf-8"));
    	String temp = null;
		while ((temp = ar.readLine()) != null) {
			String[] split = temp.split("\t");
            StringBuilder sb = new StringBuilder();
            if (split.length % 2 != 0) {
                continue;
            }
            for (int i = 0; i < split.length; i += 2) {
                sb.append(split[i]);
            }
            UserDefineLibrary.ambiguityForest.addBranch(sb.toString(), split);            
		}
		ar.close();	
    }
}
