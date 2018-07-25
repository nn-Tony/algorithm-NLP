package com.alg.tony.nativebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class Example_2_2 {
	
	public static Forest zbjsmallDic = new Forest();  // 细粒度词典
	
	public static void main(String[] args) throws FileNotFoundException, IOException {			
		String SmallDicPath = "F:/Dic/zbjsmall.dic";         // 细粒度词典
		String ErrorDicPath = "F:/Dic/zbjaddword.dic";       // 错词词典
		String AmbiguityDicPath = "F:/Dic/zbjambiguity.dic"; // 纠正词典

		insertZbjDic(zbjsmallDic, SmallDicPath); // 加载细粒度词典 
		insertZbjDic(zbjsmallDic, ErrorDicPath); // 加载分词错误的词典
		LoadAmbiguityLibrary(AmbiguityDicPath);  // 加载纠正词典
				
		String trainDataDir = "./TrainningSet/";
		Map<String, Integer> dic = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> CateWord = loadtrainData(trainDataDir, dic);
		
		String text = "微软公司提出以446亿美元的价格收购雅虎中国网2月1日报道 美联社消息，微软公司提出以446亿美元现金加股票的价格收购搜索网站雅虎公司。微软提出以每股31美元的价格收购雅虎。微软的收购报价较雅虎1月31日的收盘价19.18美元溢价62%。微软公司称雅虎公司的股东可以选择以现金或股票进行交易。微软和雅虎公司在2006年底和2007年初已在寻求双方合作。而近两年，雅虎一直处于困境：市场份额下滑、运营业绩不佳、股价大幅下跌。对于力图在互联网市场有所作为的微软来说，收购雅虎无疑是一条捷径，因为双方具有非常强的互补性。(小桥)";
//		String text = "Chinese Chinese Chinese Tokyo Japan";
		
		Result parse = ToAnalysis.parse(text, zbjsmallDic);
		
		int TrainDataWordSum = 0;
		for(Entry<String, Integer> e : dic.entrySet()) {
			TrainDataWordSum += e.getValue();
		}
		
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
			System.out.println(e.getKey() + "\t" + prob);
		}
	}
	
	public static Map<String, Map<String, Integer>> loadtrainData(String trainDataDir, Map<String, Integer> dic) throws IOException, FileNotFoundException {		
		Map<String, Map<String, Integer>> CateWord = new HashMap<String, Map<String, Integer>>();
		
		File cateFileDir = new File(trainDataDir);
		String[] cateFiles = cateFileDir.list();		
		for(String s : cateFiles) {
			Map<String, Integer> WordFreMap = CateWord.get(s);
			if(null == WordFreMap) {
				WordFreMap = new HashMap<String, Integer>();
				CateWord.put(s, WordFreMap);
			}
			File files = new File(trainDataDir + File.separator + s);
			String[] files2 = files.list();
			for(String s2 : files2) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(trainDataDir+File.separator+s+File.separator+s2)), "gbk"));
				String line = null;
				while(null != (line=br.readLine())) {
					Result parse = ToAnalysis.parse(line, zbjsmallDic);
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
						
						if(dic.containsKey(t.getName())) {
							dic.put(t.getName(), WordFreMap.get(t.getName())+1);
						} else {
							dic.put(t.getName(), 1);
						}
					}
				}
				br.close();
			}
		}

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
