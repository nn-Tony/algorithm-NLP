package com.alg.tony.hmm2;

public class Forward extends HMM{
	
	public Forward(int stateNum,int observationSymbolNum)
	{
		super(stateNum,observationSymbolNum);
	}
	//ob是已知的观察序列；返回结果是观察序列的概率
	public double forward(int[] ob)	
	{
		double[][] alpha = null;
		return forward(ob,alpha);
	}
	
	//ob:已知的观察序列；alpha：输出中间结果，局部概率；返回观察序列的概率
	public double forward(int[] ob,double[][] alpha)
	{
		alpha=new double[ob.length][N];
		//1.初始化，计算初始时刻所有状态的局部概率
		System.out.println("1.初始化：");
		for(int i=0;i<N;i++)
		{
			alpha[0][i]=PI[i]*B[i][ob[0]];
			System.out.println("alpha[0]["+i+"]:"+alpha[0][i]);
		}
		//2.归纳，递归计算每个时间点的局部概率
		System.out.println("2.归纳：");
		for (int i = 1; i < ob.length; i++)//从第一个观测值开始循环，
        {
            for (int j = 0; j < N; j++)//对于每个状态
            {
                double sum = 0;
                for (int k = 0; k < N; k++)
                {
                	sum += alpha[i - 1][k] * A[k][j];
                }
                alpha[i][j] = sum * B[j][ob[i]];
                System.out.println("alpha["+i+"]["+j+"]:"+alpha[i][j]);
            }
        }
		//3.终止，观察序列的概率等于最终时刻所有局部概率之和
		String s="P(red,yellow,blue)=";
		System.out.println("3.终止，求和：");
		double probability = 0;
        for (int i = 0; i < N; i++)
        {
        	probability += alpha[ob.length - 1][i];
        	s+="alpha["+(ob.length - 1)+"]["+i+"]";
        	if(i!=N-1)
        		s+="+";
        }
        System.out.println(s+"="+probability);
        return probability;
	}
}