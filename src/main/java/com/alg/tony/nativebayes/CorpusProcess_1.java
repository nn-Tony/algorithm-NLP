package com.alg.tony.nativebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

public class CorpusProcess_1 {
	
	public static Forest zbjsmallDic = new Forest();  // 细粒度词典
	
	public static void main(String[] args) throws FileNotFoundException, IOException {			
		String SmallDicPath = "F:/Dic/zbjsmall.dic";         // 细粒度词典
		String ErrorDicPath = "F:/Dic/zbjaddword.dic";       // 错词词典
		String AmbiguityDicPath = "F:/Dic/zbjambiguity.dic"; // 纠正词典

		insertZbjDic(zbjsmallDic, SmallDicPath); // 加载细粒度词典 
		insertZbjDic(zbjsmallDic, ErrorDicPath); // 加载分词错误的词典
		LoadAmbiguityLibrary(AmbiguityDicPath);  // 加载纠正词典
				
		loadtrainData();
		
		
	}
	
	public static void loadtrainData() throws IOException, FileNotFoundException {		
		Map<String, Map<String, Integer>> CateWord = new HashMap<String, Map<String, Integer>>();
		
		String inpath = "F:/UserPortrait/topic4TagResource/serviceCategory";
		String outpath = "F:/UserPortrait/topic4TagResource/myCategoryPredict.txt";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inpath)), "utf-8"));		
		PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(new File(outpath)), "utf-8"), true);
		
		String line = null;
		while(null != (line=br.readLine())) {
			String[] parts = line.split("\t");
			if(parts.length == 7) {
				String cat = parts[1] + "&" + parts[2] + "&" + parts[3];
				Map<String, Integer> WordFreMap = CateWord.get(cat);
				if(null == WordFreMap) {
					WordFreMap = new HashMap<String, Integer>();
					CateWord.put(cat, WordFreMap);
				}
				
				Result parse = ToAnalysis.parse(parts[6], zbjsmallDic);
				for(Term t : parse) {
					if(t.getNatureStr().equals("null") ||
						t.getNatureStr().equals("w") ||
						t.getNatureStr().equals("m") ||
						t.getName().length() < 2) {
						continue;
					}
					if(WordFreMap.containsKey(t.getName())) {
						WordFreMap.put(t.getName(), WordFreMap.get(t.getName())+1);
					} else {
						WordFreMap.put(t.getName(), 1);
					}
				}
			}
		}
		br.close();
		
		for(Entry<String, Map<String, Integer>> e1 : CateWord.entrySet()) {
			for(Entry<String, Integer> e2 : e1.getValue().entrySet()) {
				pw.println(e1.getKey() + "\t" + e2.getKey() + "\t" + e2.getValue());
			}
		}
		pw.close();
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
