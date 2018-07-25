package com.alg.tony.tfidf;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class TFIDF {
	public static void main(String[] args) throws IOException, FileNotFoundException {
		String dirPath = "./modelfile/tfidffiles";
		String resPath = "./modelfile/TFIDFresult.txt";
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
    			new FileOutputStream(new File(resPath)), "utf-8"), true);
		Set<String> FileList = readDirs(dirPath);
		
		Set<String> dicSet = new HashSet<String>();  // 记录全部文档中所有的词
		Map<String, Map<String, Double>> TF = 
				new HashMap<String, Map<String, Double>>();
		
		/******************* 计算TF *******************/
		for(String file : FileList) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(file)), "gbk"));
			Map<String, Integer> SigalFileWordsFre = new HashMap<String, Integer>();
			String line = null;
			int wordFre = 0; // 记录各文档中词的总数
			while(null != (line=br.readLine())) {
				Result parse = ToAnalysis.parse(line);
				for(Term t : parse) {
					if(SigalFileWordsFre.containsKey(t.getName())) {
						SigalFileWordsFre.put(t.getName(), SigalFileWordsFre.get(t.getName())+1);
					} else {
						SigalFileWordsFre.put(t.getName(), 1);
					}
					wordFre++;
					dicSet.add(t.getName());
				}
			}
			br.close();
			Map<String, Double> tmpMap = new HashMap<String, Double>();
			for(Entry<String, Integer> sfw : SigalFileWordsFre.entrySet()) {
				tmpMap.put(sfw.getKey(), SigalFileWordsFre.get(sfw.getKey())*1.0 / wordFre);
			}
			TF.put(file, tmpMap); // TF
		}
		
		pw.println("-----------------------------------------");
        for (Entry<String, Map<String, Double>> e1 : TF.entrySet()) {
        	pw.println("fileName " + e1.getKey());
        	for(Entry<String, Double> e2 : e1.getValue().entrySet()) {
        		pw.print("TF " + e2.getKey() + "=" + e2.getValue() + ", ");
        	}
        	pw.println();
        }
        pw.println("-----------------------------------------");
        
        /******************* 计算IDF *******************/
        Map<String, Double> IDF = new HashMap<String, Double>();
        for(String word : dicSet) {
        	int textNum = 0;
        	for(Entry<String, Map<String, Double>> e : TF.entrySet()) {
        		if(e.getValue().containsKey(word)) {
        			textNum++;
        		}
        	}
        	IDF.put(word, Math.log10(FileList.size()*1.0 / textNum));
        }
        for (String word : IDF.keySet()) {
            pw.println("keyword :" + word + " idf: " + IDF.get(word));
        }
        pw.println("-----------------------------------------");
        
        /******************* 计算TFIDF *******************/
        Map<String, Map<String, Double>> TFIDF = 
				new HashMap<String, Map<String, Double>>();
        for(Entry<String, Map<String, Double>> e1 : TF.entrySet()) {
        	Map<String, Double> tmp = TFIDF.get(e1.getKey());
        	if(null == tmp) {
        		tmp = new HashMap<String, Double>();
        		TFIDF.put(e1.getKey(), tmp);
        	}
        	for(Entry<String, Double> e2 : e1.getValue().entrySet()) {
        		tmp.put(e2.getKey(), e2.getValue()*IDF.get(e2.getKey()));
        	}
        }
        for (String filename : TFIDF.keySet()) {
            pw.println("fileName " + filename);
            pw.println(TFIDF.get(filename));
        }
        pw.close();
	}
	
	public static Set<String> readDirs(String filepath) throws FileNotFoundException, IOException {
		Set<String> fileList = new HashSet<String>();
		try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                System.out.println("输入的参数应该为[文件夹名]");
                System.out.println("filepath: " + file.getAbsolutePath());
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        //System.out.println("filepath: " + readfile.getAbsolutePath());
                        fileList.add(readfile.getAbsolutePath());
                    } else if (readfile.isDirectory()) {
                        readDirs(filepath + File.separator + filelist[i]);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return fileList;
    }
}
