package com.alg.tony.nativebayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/***
 * 汽车有6个属性，每个属性都有几种类别，根据这6个属性来判断汽车的性价比Classfies如何.</br>
 * Classfies(unacc, acc, good, vgood)</br>
 * 
 * 以下6个特征(属性)</br>
 * buying（ vhigh, high, med, low）</br>
 * maint（vhigh, high, med, low）</br>
 * doors（ 2, 3, 4, 5more）</br>
 * persons（2, 4, more）</br>
 * lug_boot（ small ,med, big）</br>
 * safety（ low, med, high）

 * @author zhuqisi
 *
 */
public class NativeBayesTest {

	// 汽车的6个属性
	private static String[] buyingFeatures = { "vhigh", "high", "med", "low" };
	private static String[] maintFeatures = { "vhigh", "high", "med", "low" };
	private static String[] doorsFeatures = { "2", "3", "4", "5more" };
	private static String[] personsFeatures = { "2", "4", "more" };
	private static String[] lug_bootFeatures = { "small", "med", "big" };
	private static String[] safetyFeatures = { "low", "med", "high" };
	// 汽车的性价比
	private static String[] Classfies = { "unacc", "acc", "good", "vgood" };
	
	private static int[] ClassfiesCount = new int[4];
	private static int[][] buyingCount = new int[4][4];
	private static int[][] maintCount = new int[4][4];
	private static int[][] doorsCount = new int[4][4];
	private static int[][] personsCount = new int[3][4];
	private static int[][] lug_bootCount = new int[3][4];
	private static int[][] safetyCount = new int[3][4];
	
	private static int TrainSampleNum = 0;
	
	public static void main(String[] args) throws IOException {
		
		String trainfilepath = "./modelfile/nativebayesfile/traindata.txt";
		String testfilepath = "./modelfile/nativebayesfile/testdata.txt";
		PriorProb(trainfilepath);		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(testfilepath)), "utf-8"));
		String line = br.readLine();
		while(null != (line=br.readLine())) {			
			String[] parts = line.split(",");
			if(parts.length == 7) {
				Double[] p = new Double[4];
				int rowindex;
				
				for(int i=0; i<4; ++i) {
					rowindex = getIndex(buyingFeatures, parts[0]);
					p[i] = buyingCount[rowindex][i]*1.0 / ClassfiesCount[i];	
					
					rowindex = getIndex(maintFeatures, parts[1]);
					p[i] *= maintCount[rowindex][i]*1.0 / ClassfiesCount[i];	
					
					rowindex = getIndex(doorsFeatures, parts[2]);
					p[i] *= doorsCount[rowindex][i]*1.0 / ClassfiesCount[i];
					
					rowindex = getIndex(personsFeatures, parts[3]);
					p[i] *= personsCount[rowindex][i]*1.0 / ClassfiesCount[i];
					
					rowindex = getIndex(lug_bootFeatures, parts[4]);
					p[i] *= lug_bootCount[rowindex][i]*1.0 / ClassfiesCount[i];
					
					rowindex = getIndex(safetyFeatures, parts[5]);
					p[i] *= safetyCount[rowindex][i]*1.0 / ClassfiesCount[i];
					
					p[i] *= ClassfiesCount[i]*1.0 / TrainSampleNum;
					
					System.out.print(p[i] + "\t");
				}
				System.out.println();						
			}
		}
		br.close();
	}

	public static void PriorProb(String trainfilepath) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(trainfilepath)), "utf-8"));
		String line = br.readLine();
		while(null != (line=br.readLine())) {			
			String[] parts = line.split(",");
			if(parts.length == 7) {
				TrainSampleNum++;
				
				int rowindex;
				int colindex;
				colindex = getIndex(Classfies, parts[6]);				
				ClassfiesCount[colindex]++;
				
				rowindex = getIndex(buyingFeatures, parts[0]);
				buyingCount[rowindex][colindex]++;	
				rowindex = getIndex(maintFeatures, parts[1]);
				maintCount[rowindex][colindex]++;
				rowindex = getIndex(doorsFeatures, parts[2]);
				doorsCount[rowindex][colindex]++;
				rowindex = getIndex(personsFeatures, parts[3]);
				personsCount[rowindex][colindex]++;
				rowindex = getIndex(lug_bootFeatures, parts[4]);
				lug_bootCount[rowindex][colindex]++;
				rowindex = getIndex(safetyFeatures, parts[5]);
				safetyCount[rowindex][colindex]++;

			}
		}
		br.close();
	}
	
	public static int getIndex(String[] type, String str) {
		for(int i=0; i<type.length; i++) {
			if(type[i].equals(str)) {
				return i;
			}
		}
		return -1;
	}
}
