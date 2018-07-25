package com.alg.tony.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

public class LoadFile {
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
