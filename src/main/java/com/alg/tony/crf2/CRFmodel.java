package com.alg.tony.crf2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alg.tony.crf.Tables;

public class CRFmodel implements Serializable {

	private static final long serialVersionUID = -6941624873387908111L;
	
	public String version;
	public String cost_factor;
	public int weightMatricNum;
	public int xsize;
	
	public String[] labels;
	
	public Template template = new Template();
	
	public ExpandTemplate expand_template = new ExpandTemplate();
		
	public double[] weight_matrix;
	
	public int templateNum;
	public int extendtemplateNum; // 扩展模板个数
	
	public CRFmodel loadCRFmodel(String path) throws IOException, FileNotFoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
 				new FileInputStream(new File(path)),"utf-8"));
		
		String version = br.readLine();
		this.version = version.split(":")[1].trim();
		
		String costfactor = br.readLine();
		this.cost_factor = costfactor.split(":")[1].trim();
		
		String weightMatricNum = br.readLine();
		this.weightMatricNum = Integer.parseInt(weightMatricNum.split(":")[1].trim());
		weight_matrix = new double[this.weightMatricNum];
		
		String xsize = br.readLine();
		this.xsize = Integer.parseInt(xsize.split(":")[1].trim());
		
		//标签
		br.readLine();
		String line = "";
		String labelstr = "";
		while((line=br.readLine()).length() > 0){
			labelstr += line + "\t";
		}
		labels = labelstr.split("\t");
		
		int labelNum = labels.length;
		extendtemplateNum = (this.weightMatricNum-labelNum*labelNum)/labelNum+1;
		
		// 模板
		int weightindex = 0;
		int hang = 0;
		while((line=br.readLine()) != null) {
			hang++;
			if(line.split(" ").length>1&&line.split(" ")[1].toLowerCase().equals("b")&&line.length()>1) {
				expand_template.addBigramTag(line);
				continue;
			}
			if(line.toLowerCase().contains("u")) {
				if(line.toLowerCase().contains("%x[")) { // 模板
					template.addTemplate(line);
				} else { // 扩展模板
					expand_template.addUnigramTag(line);
				}
			}else if(hang>extendtemplateNum && !line.equals("")){
				weight_matrix[weightindex] = Double.parseDouble(line);
				weightindex++;
			}
		}
		br.close();
		
		return this;
	}
	
	public List<String> getCRFlabel(List<String> words) {
		List<String> labelList = new ArrayList<String>();
		if(words.size() < 1) {
			return labelList;
		}
		Map<String,Map<String,Integer>> tempFeatureIds = expand_template.templateFeatureId;
		Map<String,List<String>> featureTemplatesplit = template.featureTemplate;
		int idBigram = expand_template.idBigram; // Bigram的id
		Tables table = new Tables(words);
		int labelNum = labels.length;
		// Viterbi 权重和路径
		double[] Viscoreag = new double[labelNum]; // Viterbi
		Map<Integer, String> Vipath = new HashMap<Integer, String>(); //Viterbi
		
		double[][] bgramBEM = new double[labelNum][labelNum];
		for(int i=0; i<labelNum; i++) {
			for(int j=0; j<labelNum; j++){
				bgramBEM[i][j] = weight_matrix[idBigram+(i*labelNum)+j];
			}
		}
		
		//transform
		Map<String, List<Double>> weightBEM = new HashMap<String, List<Double>>();
		for(int i=0; i<labels.length; i++){
			String str = labels[i];
			weightBEM.put(str, new ArrayList<Double>());
		}
		
		for(int i=0,length=words.size(); i<length; i++){
			
			Map<String,List<Double>> weightBEMtmp = new HashMap<String,List<Double>>();
			for(int j=0; j<labels.length; j++){
				String str = labels[j];
				weightBEMtmp.put(str, new ArrayList<Double>());
			}
			
			for(Map.Entry<String,List<String>> entry:featureTemplatesplit.entrySet()){
				String tagsu = entry.getKey();//U00
				List<String> templates = entry.getValue();
				StringBuffer sb = new StringBuffer(); 
				String template = "";
				for(int j=0,length1=templates.size(); j<length1; j++){
					template = templates.get(j);
					String xy = template.replace("[", "").replace("]", "");;
					int x = Integer.parseInt(xy.split(",")[0]);
					int y = Integer.parseInt(xy.split(",")[1]);
					if(!sb.toString().equals("")){
						sb.append("/");
					}
					sb.append(table.getTag(i+x , y));
				}
				//Map<String,Integer>featureIds = tempFeatureIds.getOrDefault(tagsu, new HashMap<String,Integer>());
				//
				Map<String,Integer> featureIds = new HashMap<String,Integer>();
				if(tempFeatureIds.containsKey(tagsu)){
					featureIds = tempFeatureIds.get(tagsu);
				}
				if(featureIds.size()>0){
					String tagFeature = sb.toString();
					if(featureIds.containsKey(tagFeature)){
						int id = featureIds.get(tagFeature);
								
						for(int j=0; j<labels.length; j++){
							String str = labels[j];
							List<Double> score = weightBEMtmp.get(str);
							score.add(weight_matrix[id+j]);
							weightBEMtmp.put(str, score);
						}
					}
				}
			}
			
			double[] sunBEM = new double[labels.length];
			for(int j=0; j<labels.length; j++){
				sunBEM[j] = 0;
				String str = labels[j];
				List<Double> score = weightBEMtmp.get(str);
				for(int k=0; k<score.size(); k++){
					sunBEM[j] += score.get(k);
				}
			}
			
			
			//---------------------  Viterbi  ---------------------------
			if(i<1){
				for(int j=0; j<labelNum; j++){
					Viscoreag[j] = sunBEM[j];
					Vipath.put((j+1), labels[j]);
				}

			}else{
				double[] presBEM = new double[labelNum];
				for(int j=0; j<labelNum; j++){
					presBEM[j] = Viscoreag[j];
				}
				
				//transform
				Map<Integer,String> Vipathsaved = new HashMap<Integer,String>();
				for(int j=0; j<labelNum; j++){
					Vipathsaved.put((j+1), Vipath.get((j+1)));
				}
				for(int j=0; j<labelNum; j++){//到达状态j所有路径
					double[] skj = new double[labelNum];
					for(int k=0; k<labelNum; k++){
						skj[k] = presBEM[k] + bgramBEM[k][j] + sunBEM[j];
					}
					double maxScore = skj[0];
					int maxIdex = 0;
					for(int k=1; k<labelNum; k++){
						if(skj[k]>maxScore){
							maxScore = skj[k];
							maxIdex = k;
						}
					}
					String pathPre = Vipathsaved.get((maxIdex+1));
					Vipath.put((j+1), pathPre+"\t"+labels[j]);
					Viscoreag[j] = maxScore;
				}
			}
			//------------------------------------------------------------
			
		}
		String tags = "";
		double maxScore = Viscoreag[0];
		int maxIndex = 0;
		for(int i=1; i<labelNum; i++){
			if(maxScore<Viscoreag[i]){
				maxScore = Viscoreag[i];
				maxIndex = i;
			}
		}
		tags = Vipath.get(maxIndex+1);
		String[] tag = tags.split("\t");
		for(String s : tag) {
			labelList.add(s);
		}
		return labelList;
		
	}

	public void saveCRFmodel_serial(String path) {
		try {
			ObjectOutputStream os = new ObjectOutputStream(
					new FileOutputStream(new File(path)));
			os.writeObject(this);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CRFmodel loadCRFmodel_serial(String path) {
		try {
			ObjectInputStream os = new ObjectInputStream(
					new FileInputStream(new File(path)));
			CRFmodel crf = (CRFmodel) os.readObject();
			os.close();
			return crf;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
