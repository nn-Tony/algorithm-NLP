package com.alg.tony.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;

import com.alg.tony.libsvm.svm;
import com.alg.tony.libsvm.svm_model;
import com.alg.tony.libsvm.svm_node;
import com.alg.tony.libsvm.svm_parameter;
import com.alg.tony.libsvm.svm_problem;
import com.alg.tony.utils.LoadFile;
import com.alg.tony.utils.Pair;
import com.alg.tony.utils.VectorModel;

public class SVM_Demo {
	
	private static VectorModel vm = null;
	private static Forest zbjDic = new Forest();
	private static List<Pair<String, Integer>> keywords = new ArrayList<Pair<String, Integer>>();
	
	public static void main(String[] args) throws IOException, FileNotFoundException {
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
				
		String[] paths = {
				"./modelfile/selfbusiness/IntellectualProperty.txt",
				"./modelfile/selfbusiness/CompanyRegister.txt",
				"./modelfile/selfbusiness/KeepAccounts.txt",
				"./modelfile/selfbusiness/Another.txt"
		};
		svm_problem problem = Data2SvmProblem(paths);
		
		/**
		 * 定义svm模型的参数，即定义svm_parameter对象
		 */
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;  // C_SVC的多分类svm模型
        param.kernel_type = svm_parameter.LINEAR;  // 线性核函数
        param.cache_size = 100;  
        param.eps = 0.00001;  // 终止模型训练的精度
        param.C = 2.2;        
        param.probability = 1;  // 输出所属各类别的概率
        
        /**
         * 训练SVM分类模型
         */
        // 如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
        System.out.println(svm.svm_check_parameter(problem, param)); 
        svm_model model = svm.svm_train(problem, param); // svm.svm_train()训练出SVM分类模型
		svm.svm_save_model("./modelfile/svm.model", model);
		        
    	int cor = 0;
    	int Num = 0;
		BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
        	System.out.print("请输入查询词: ");
        	String str = strin.readLine();
//        for(Pair<String, Integer> p : keywords) {
        	Num++;
			float[] sentvec = new float[vm.getVectorSize()];  // 初始化词向量
			Result parse = ToAnalysis.parse(str, zbjDic);
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
			svm_node[] sm = new svm_node[sentvec.length];
			for(int i=0; i<sm.length; i++) {
				sm[i] = new svm_node();
			}
			for(int i=0; i<sentvec.length; i++) {
				sm[i].index = i+1;
				sm[i].value = sentvec[i];
			}						
			
			double[] aaa = new double[model.nr_class];
			// one-class没有概率输出
			double predictLabel1 = svm.svm_predict_probability(model, sm, aaa);
//			if(predictLabel1 == p.v) {
//				cor++;
//			} else {
//				System.out.print(predictLabel1 + "\t" + p.k + "\t" + p.v);
//				for(int i=0; i<aaa.length; ++i) {
//					System.out.print("\t" + aaa[i]);
//				}
//				System.out.println();
//			}
			System.out.print(predictLabel1);
			for(int i=0; i<aaa.length; ++i) {
				System.out.print("\t" + aaa[i]);
			}
			System.out.println();

        }
//        System.out.println(cor*1.0/Num);
	}

	public static svm_problem Data2SvmProblem(String[] paths) throws IOException, FileNotFoundException {
		Map<svm_node[], Integer> TrainData = new LinkedHashMap<svm_node[], Integer>();
		// 定义svm_problem对象
        svm_problem problem = new svm_problem();
		if(paths.length != 0) {
			int LabelIndex = 0;
			for(String datapath : paths) {
				LabelIndex++;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(datapath)), "utf-8"));
				String line = null;
				while((line=br.readLine()) != null) {
					keywords.add(new Pair<String, Integer>(line, LabelIndex));
					float[] sentvec = new float[vm.getVectorSize()]; // 初始化词向量
					Result parse = ToAnalysis.parse(line.trim(), zbjDic);
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
					svm_node[] sm = new svm_node[sentvec.length];
					for(int i=0; i<sm.length; i++) {
						sm[i] = new svm_node();
					}
					for(int i=0; i<sentvec.length; i++) {
						sm[i].index = i+1;
						sm[i].value = sentvec[i];
					}			
					TrainData.put(sm, LabelIndex);					
				}	
				br.close();
			}	
			svm_node[][] data = new svm_node[TrainData.size()][vm.getVectorSize()];
			double[] labels = new double[TrainData.size()];
			int i = 0;
			for(Entry<svm_node[], Integer> e : TrainData.entrySet()) {
				data[i] = e.getKey();
	    		labels[i] = e.getValue();
	    		i++;
			}
	        problem.l = labels.length; // 向量个数
	        problem.x = data;          // 训练集向量表
	        problem.y = labels;        // 对应的label数组
		}
		return problem;
	}

}
