package com.alg.tony.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;

import com.utils.LoadFile;
import com.utils.VectorModel;

public class KmeansTest {
	
	private static int vecsize = 300;
	private static VectorModel vm = null; 
	private static Forest zbjDic = null;
	
	public static void main(String[] args) throws IOException {
		String SmallDicPath = "./modelfile/dic/zbjsmall.dic";         // 细粒度词典
		String ErrorDicPath = "./modelfile/dic/zbjaddword.dic";       // 错词词典
		String AmbiguityDicPath = "./modelfile/dic/zbjambiguity.dic"; // 纠正词典		
		// 加载猪八戒字典
		LoadFile.insertZbjDic(zbjDic, SmallDicPath); // 加载细粒度词典 
		LoadFile.insertZbjDic(zbjDic, ErrorDicPath); // 加载分词错误的词典
		LoadFile.LoadAmbiguityLibrary(AmbiguityDicPath);  // 加载纠正词典
		System.out.println("zhubajie.dic加载完毕.");
    	
    	String baseModelRealPath = "./modelfile/cbow.model.filter";
		// 加载词向量模型
		vm = VectorModel.loadFromFile(baseModelRealPath);
		System.out.println("VectorModel加载完毕.");
		
		
		String input = "./modelfile/kmeans/ZhubajiePhraseLablePOS";
		String output = "./modelfile/kmeans/ZhubajiePhraseLablePOS.txt";
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(input)), "utf-8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(new File(output)), "utf-8"), true);
		
		String line = null;
		int cnt = 0;
		Map<String, float[]> POSvec = new LinkedHashMap<String, float[]>();
        while ((line = br.readLine()) != null) {
        	String[] seg = line.split("\t");
        	if(seg.length ==3) {
        		System.out.println(cnt++);        		
        		if(!POSvec.containsKey(seg[0])) {
        			float[] tmp = GetKeywordVector(seg[0]);
        			POSvec.put(seg[0], tmp);
        		}
        		if(cnt > 100) {
        			break;
        		}
        	}
        }
        br.close();
        
        float[][] center = KmeansInit(POSvec, 6);
    	List<List<String>> results=KmeansComput(POSvec, center, 6);
        
    	for(List<String> o : results) {
    		pw.println("Cluster1");
    		for(String oo : o) {
    			pw.print(oo + "\t");
    		}
    		pw.println();
    	}
    	pw.close();
	}

	
	private static List<List<String>> KmeansComput(Map<String, float[]> vectors, float[][] center, int K) {  
//		List<String>[] results = new ArrayList[K];
		List<List<String>> results = new ArrayList<List<String>>();
		
        boolean flagCenter = true;  
        int time=0;
        while (flagCenter) { // 簇中心是否还有变化
        	time += 1;
        	if (time>1000) { // 迭代终止条件，迭代1000次
        		break;
        	}
        	flagCenter = false;  
//            for (int i = 0; i < K; i++) {  
//                results[i] = new ArrayList<String>();  
//            }     
        	results.clear();
            for(int i=0; i<K; i++) {            	
    			results.add(new ArrayList<String>());
    		}
            Iterator<Map.Entry<String, float[]>> it = vectors.entrySet().iterator();
            for (int i = 0; i < vectors.size(); i++) {
            	Map.Entry<String, float[]> ME = it.next();
            	String word = ME.getKey();  
            	float[] wordvec = ME.getValue();
            	
                double[] dists = new double[K];                  
                for (int j = 0; j < center.length; j++) {
                	float[] initP = new float[vecsize];                	
                	for(int k=0; k<vecsize; k++){
                		initP[k] = center[j][k]; 
                	}                                  
                	// 计算每个词到每个簇中心的距离，判断词属于哪个簇
                    dists[j] = distance(initP, wordvec); 
                }  
                // 将每个词存放到其所属簇的List中
                int dist_index = computOrder(dists); 
//                results[dist_index].add(word);  
                results.get(dist_index).add(word);
            }

            for (int i = 0; i < K; i++) {  
            	// 计算新簇中心（簇中每个词对应维度之和的平均值）
//                float[] center_new = findNewCenter(vectors, results[i]);  
                float[] center_new = findNewCenter(vectors, results.get(i));
                // 保存上一次的旧簇中心
                float[] center_old = new float[vecsize];
                for (int j=0; j<vecsize; j++){
                	center_old[j] = center[i][j];
                }
                // 判断是否满足收敛条件（新旧簇位置相同），只要有一个不收敛，则继续迭代
                if (!IsConvergence(center_new, center_old)) {  
                	flagCenter = true;  
                    for (int j=0; j<vecsize; j++){
                    	center[i][j] = center_new[j];
                    } 
                }  
            }  
        }  
        return results;  
    }
	
	// 计算新簇中心（簇中每个词对应维度之和的平均值）
	private static float[] findNewCenter(Map<String, float[]> vectors, List<String> ps) {  
        float[] t = new float[vecsize];  
        if (ps != null && ps.size() != 0) {                 
            for (String word : ps){
            	float[] midle = vectors.get(word);
            	for (int i=0; i<vecsize; i++){
            		t[i] += midle[i];
            	}
            }
            for (int i=0; i<vecsize; i++){
            	t[i] /= ps.size();
            }	
        }
        return t;   
    }
	
	// 计算余弦距离
	private static Double distance(float[] p1, float[] p2) {  
        double dis = 0;  
        if(p1.length != p2.length) {
        	return null;
        }
    	for (int i=0; i<vecsize; i++){
    		dis += p1[i]*p2[i];
    	}  
        return Math.sqrt(dis);  
  
    }
	
	
	private static boolean IsConvergence(float[] p1, float[] p2) {  
        boolean result = true;
        for (int i=0; i<vecsize; i++) {
        	if (!(p1[i] == p2[i])) {
        		result = false;
        	}
        }
        return result; 
    }
	

	private static int computOrder(double[] dists) {  
        double min = 0;  
        int index = 0;  
        for (int i = 0; i < dists.length - 1; i++) {  
            double dist0 = dists[i];  
            if (i == 0) {  
                min = dist0;  
                index = 0;  
            }  
            double dist1 = dists[i + 1];  
            if (min < dist1) {  
                min = dist1;  
                index = i + 1;  
            }  
        }  
        return index;  
    }
	
	// Kmeans的初始化，选取前K个词所对应的词向量作为初始簇中心
	private static float[][] KmeansInit(Map<String, float[]> vectors, int K) {
        if(vectors.size() == 0) {
    		return null;
    	}
        Iterator<Map.Entry<String, float[]>> it = vectors.entrySet().iterator();
        float[][] InitCenter = new float[K][vecsize];  
        for (int i=0; i<K; i++) {
        	float[] middle = it.next().getValue();
        	for (int j=0; j<middle.length; j++){
        		InitCenter[i][j] = middle[j];  
        	}
        }
        return InitCenter;
    }
	
	public static float[] GetKeywordVector(String keyword) {
		String sentence = keyword.trim();
		float[] sentvec = new float[vm.getVectorSize()];
		Result parse = ToAnalysis.parse(sentence, UserDefineLibrary.FOREST, zbjDic);
		for (Term term : parse) {
			String word = term.getName().trim();
			// 未登录词跳过
			if (!vm.getWordMap().containsKey(word)) {
				continue;
			}
			// 句子向量由词向量累加求和
			for (int i = 0; i < sentvec.length; i++) {
				sentvec[i] += vm.getWordVector(word)[i];
			}
		}
		return sentvec;
	}
}
