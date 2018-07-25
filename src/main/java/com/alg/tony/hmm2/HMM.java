package com.alg.tony.hmm2;

public class HMM {
	protected int N;//状态数
	protected int M;//观察符号数
	protected double[][] A;//状态转移概率
	protected double[][] B;//符号观测概率
	protected double[] PI;//初始状态概率分布
	
	public HMM(){};
	//参数1 状态数目 ；参数2 观察符号数目
	public HMM(int stateNum,int observationSymbolNum)
	{
		N=stateNum;
		M=observationSymbolNum;
		A=new double[N][N];
		B=new double[N][M];
		PI=new double[N];
	}
}
