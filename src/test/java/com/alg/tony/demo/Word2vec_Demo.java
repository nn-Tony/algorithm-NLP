package com.alg.tony.demo;

import java.io.*;
import java.util.Collections;
import java.util.Set;

import com.alg.tony.word2vec.Tokenizer;
import com.alg.tony.word2vec.VectorModel;
import com.alg.tony.word2vec.Word2Vec;


public class Word2vec_Demo {

	public static void readByJava(String textFilePath, String modelFilePath) {
		Word2Vec wv = new Word2Vec.Factory()
				.setMethod(Word2Vec.Method.Skip_Gram).setNumOfThread(20)
				.build();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(textFilePath), "utf-8"));
			int lineCount = 0;
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				String[] segline = line.split("\t");
				wv.readTokens(new Tokenizer(segline[0], " "));//segline[7]
				// System.out.println(line);
				lineCount = lineCount + 1;
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		wv.training();
		wv.saveModel(new File(modelFilePath));
	}

	public static void testVector(String modelFilePath) {

		VectorModel vm = VectorModel.loadFromFile(modelFilePath);
		Set<VectorModel.WordScore> result1 = Collections.emptySet();

		result1 = vm.similar("职务");
		int count = 0;
		for (VectorModel.WordScore we : result1) {
			if(count++ >10){
				break;
			}
			System.out.println(we.name + " :\t" + we.score);
		}
	}

	public static void main(String[] args) {
//		if (args.length <= 1) {
//			System.out.printf("App:训练词向量模型——args[1]：分词语料路径；args[2]：模型保存路径");
//		}
//		String textFilePath = args[1];
//		String modelFilePath = args[2];
		String textFilePath = "./modelfile/word2vecfile/my_test.utf8";
		String modelFilePath = "./modelfile/word2vecfile/word2vec.model";
		readByJava(textFilePath, modelFilePath);
		testVector(modelFilePath);
	}

}
