package com.alg.tony.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class VectorModel {

	private Map<String, float[]> wordMap = new HashMap<String, float[]>();
	private int vectorSize = 300; // 特征数
	private int topNSize = 100;

	public Map<String, float[]> getWordMap() {
		return wordMap;
	}

	public void setWordMap(Map<String, float[]> wordMap) {
		this.wordMap = wordMap;
	}

	/**
	 * 获取相似词的数量
	 * 
	 * @return 最相似词的数量
	 */
	public int getTopNSize() {
		return topNSize;
	}

	/**
	 * 设置最相似词的数量
	 * 
	 * @param topNSize
	 *            数量
	 */
	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public int getVectorSize() {
		return vectorSize;
	}

	public void setVectorSize(int vectorSize) {
		this.vectorSize = vectorSize;
	}

	/**
	 * 私有构造函数
	 * 
	 * @param wordMap
	 *            词向量哈希表
	 * @param vectorSize
	 *            词向量长度
	 * */
	public VectorModel(Map<String, float[]> wordMap, int vectorSize) {

		if (wordMap == null || wordMap.isEmpty()) {
			throw new IllegalArgumentException("word2vec的词向量长度为空，请先训练模型");
		}
		if (vectorSize <= 0) {
			throw new IllegalArgumentException("词向量长度（layerSize)应大于0");
		}

		this.wordMap = wordMap;
		this.vectorSize = vectorSize;
	}

	/**
	 * 使用Word2Vec保存的模型加载词向量模型
	 * 
	 * @param path
	 *            模型文件路径
	 * @return 词向量模型
	 */
	public static VectorModel loadFromFile(String path) {

		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("模型路径可以为null或空");
		}

		DataInputStream dis = null;
		int wordCount, layerSizeLoaded = 0;
		Map<String, float[]> wordMapLoaded = new HashMap<String, float[]>();
		try {
			dis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(path)));

			wordCount = dis.readInt();
			layerSizeLoaded = dis.readInt();

			float vector;

			String key;
			float[] value;
			for (int i = 0; i < wordCount; i++) {				
				key = dis.readUTF();
				value = new float[layerSizeLoaded];
				double len = 0;
				for (int j = 0; j < layerSizeLoaded; j++) {
					vector = dis.readFloat();
					len += vector * vector;
					value[j] = vector;
				}
				len = Math.sqrt(len);
				for (int j = 0; j < layerSizeLoaded; j++) {
					value[j] /= len;
				}
				wordMapLoaded.put(key, value);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (dis != null) {
					dis.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return new VectorModel(wordMapLoaded, layerSizeLoaded);
	}

	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}

	public static class WordScore implements Comparable<WordScore> {

		public String name;
		public float score;

		public WordScore(String name, float score) {
			this.name = name;
			this.score = score;
		}

		@Override
		public String toString() {
			return this.name + "\t" + score;
		}

		public int compareTo(WordScore o) {
			if (this.score < o.score) {
				return 1;
			} else {
				return -1;
			}
		}
	}
	
	
	
	public Set<WordScore> similar(String queryWord){

        float[] center = wordMap.get(queryWord);
        if (center == null){
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize + 1;
        TreeSet<WordScore> result = new TreeSet<WordScore>();
        for (int i = 0; i < resultSize; i++){
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()){
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++){
                dist += center[i] * vector[i];
            }
            if (dist > minDist){
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }
        result.pollFirst();

        return result;
    }
	
	
	

    public Set<WordScore> newsimilar(float[] center) {
        if (center == null || center.length != vectorSize) {
            return Collections.emptySet();
        }

        int TOPNSize = 5;
        int resultSize = wordMap.size() < TOPNSize ? wordMap.size() : TOPNSize;
        TreeSet<WordScore> result = new TreeSet<WordScore>();
        for (int i = 0; i < resultSize; i++) {
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++) {
                dist += center[i] * vector[i];
            }
            if (dist > minDist) {
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }

        return result;
    }

    public Set<WordScore> bestCategory(float[] center) {
        if (center == null || center.length != vectorSize) {
            return Collections.emptySet();
        }

        int TOPNSize = 5;
        int resultSize = wordMap.size() < TOPNSize ? wordMap.size() : TOPNSize;
        TreeSet<WordScore> result = new TreeSet<WordScore>();
        for (int i = 0; i < resultSize; i++) {
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++) {
                dist += center[i] * vector[i];
            }
            if (dist > minDist) {
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }
        return result;
    }


}
