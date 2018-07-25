package com.alg.tony.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;

import com.alg.tony.cnn.CNN;
import com.alg.tony.cnn.ConcurenceRunner;
import com.alg.tony.cnn.Dataset;
import com.alg.tony.cnn.Layer;
import com.alg.tony.cnn.TimedTest;
import com.alg.tony.cnn.CNN.LayerBuilder;
import com.alg.tony.cnn.Dataset.Record;
import com.alg.tony.cnn.Layer.Size;
import com.alg.tony.cnn.TimedTest.TestTask;
import com.alg.tony.utils.LoadFile;
import com.alg.tony.word2vec.VectorModel;

public class CNNtrain {

	private static VectorModel vm = null;
	private static Forest zbjDic = new Forest();
	
    public static void RunSpamCnn() throws FileNotFoundException, IOException {
        //创建一个卷积神经网络1
        LayerBuilder builder = new LayerBuilder();
        builder.addLayer(Layer.buildInputLayer(new Size(1, 300)));
        builder.addLayer(Layer.buildConvLayer(12, new Size(1, 300)));
        builder.addLayer(Layer.buildSampLayer(new Size(1, 1)));
        builder.addLayer(Layer.buildOutputLayer(4));
        CNN cnn = new CNN(builder, 1);
        
        //导入数据集
        String[] paths = {
				"F:/knowledge/IntellectualProperty.txt",
				"F:/knowledge/CompanyRegister.txt",
				"F:/knowledge/KeepAccounts.txt",
				"F:/knowledge/Another.txt"
		};
        String modelName = "F:/knowledge/SelfBusiness.cnn";
        Dataset trainSet = GetDataset(paths);
        cnn.train(trainSet, 500);
        cnn.saveModel(modelName); 
//        trainSet.clear();
//        trainSet = null;

        CNN SpamCnnModel = CNN.loadModel(modelName);  
        SpamCnnModel.predict(trainSet, modelName + "test.predict");
    }

    public static void main(String[] args) throws IOException {

    	String baseModelRealPath = "F:/UserPortrait/topic4TagResource/cbow.model.filter";
		String ZBJDicRealPath = "F:/UserPortrait/topic4TagResource/zhubajie.dic";
		// 加载词向量模型
		vm = VectorModel.loadFromFile(baseModelRealPath);
		System.out.println("VectorModel加载完毕.");		
		// 加载猪八戒字典		
		LoadFile.insertZbjDic(zbjDic, ZBJDicRealPath);
		System.out.println("zhubajie.dic加载完毕.");
    	
    	
        new TimedTest(new TestTask() {

            public void process() {
                try {
					RunSpamCnn();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }, 1).test();
        ConcurenceRunner.stop();

    }

    
    public static Dataset GetDataset(String[] paths) throws IOException, FileNotFoundException {
    	 Dataset dataset = new Dataset();    	     	     	 
    	 if(paths.length != 0) {
 			int LabelIndex = 0;
 			for(String datapath : paths) {
 				LabelIndex++;
 				BufferedReader br = new BufferedReader(new InputStreamReader(
 						new FileInputStream(new File(datapath)), "utf-8"));
 				String line = null;
 				while((line=br.readLine()) != null) {
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
 					double[] data = new double[sentvec.length];
                    for (int i = 0; i < sentvec.length; i++) {
                        data[i] = sentvec[i];
                    } 					
                    Double label = 1.0*(LabelIndex-1);
 					Record record = dataset.new Record(data, label);
 					dataset.lableIndex = LabelIndex;
                    dataset.append(record); 									
 				}	
 				br.close();
 			}         
    	 }
    	 return dataset;
    }
    
}
