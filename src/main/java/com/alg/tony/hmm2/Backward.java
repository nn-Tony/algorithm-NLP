package com.alg.tony.hmm2;

public class Backward extends HMM{
	
	public Backward(int stateNum,int observationSymbolNum)
	{
		super(stateNum,observationSymbolNum);
	}
	//ob 已知的观察序列
	public double backward(int[] ob)
    {
        double[][] beta = null;     // 只声明，不定义
        return backward(ob, beta);
    }
	//ob 已知的观察序列；beta 后向变量；返回观测序列的概率
	public double backward(int[] ob,double[][] beta)
    {            
		
        beta = new double[ob.length][N];
        // 初始化
        System.out.println("1.初始化：");
        for (int i = 0; i < N; i++)
        {
        	beta[ob.length - 1][i] = 1.0;
        	System.out.println("beta["+(ob.length - 1)+"]["+i+"]:"+beta[ob.length - 1][i]);
        }

        // 归纳
        System.out.println("2.归纳：");
        for (int t = ob.length - 2; t >= 0; t--)
        {
            for (int j = 0; j < N; j++)
            {
                double Sum = 0;
                for (int i = 0; i < N; i++)
                {
                    Sum += A[j][i] * B[i][ob[t + 1]] * beta[t + 1][i];
                }

                beta[t][j] = Sum;
                System.out.println("beta["+t+"]["+j+"]:"+beta[t][j]);
            }
        }

        // 终止
        String s="P(red,yellow,blue)=";
		System.out.println("3.终止，求和：");
        double probability = 0;
        for (int i = 0; i < N; i++)
        {
            probability += beta[0][i];
            s+="beta[0]["+i+"]";
        	if(i!=N-1)
        		s+="+";
        }
        System.out.println(s+"="+probability);
        return probability;
    }
}
