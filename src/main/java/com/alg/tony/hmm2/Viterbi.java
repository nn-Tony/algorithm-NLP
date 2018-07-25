package com.alg.tony.hmm2;

import java.util.ArrayList;
import java.util.List;

public class Viterbi extends HMM{
	
	public Viterbi(int stateNum,int observationSymbolNum)
	{
		super(stateNum,observationSymbolNum);
	}
	//ob 已知的观察序列；probability 可能性最大的隐藏状态序列的概率；返回 可能性最大的隐藏状态序列
	public List viterbi(int[] ob, double probability)
    {
        double[][] delta = null;
        int[][] psi = null;
        return viterbi(ob, delta, psi, probability);
    }
	//delta 输出中间结果，局部最大概率；psi 输出中间结果，反向指针指示最可能路径；返回可能性最大的隐藏状态序列的概率
	public List viterbi(int[] ob, double[][] delta, int[][] psi,double probability)
    {   
		delta = new double[ob.length][N];   // 局部概率
        psi = new int[ob.length][N];      // 反向指针

        System.out.println("1.初始化：");
        // 1. 初始化
        for (int j = 0; j < N; j++)
        {
        	delta[0][j] = PI[j] * B[j][ob[0]];
        	System.out.println("delta[0]["+j+"]:"+delta[0][j]);
        }

        // 2. 递归
        System.out.println("2.归纳：");
        for (int t = 1; t < ob.length; t++)
        {
            for (int j = 0; j < N; j++)
            {
            	double MaxValue = delta[t - 1][0] * A[0][j];//初始，设第0个状态处的值为最大值
                int MaxValueIndex = 0;//存储取得最大值处的状态的索引
                for (int i = 1; i < N; i++)
                {
                	double Value = delta[t - 1][i] * A[i][j];
                    if (Value > MaxValue)
                    {
                        MaxValue = Value;
                        MaxValueIndex = i;
                    }
                }

                delta[t][j] = MaxValue * B[j][ob[t]];
                System.out.println("delta["+t+"]["+j+"]:"+delta[t][j]);
                psi[t][j] = MaxValueIndex; // 记录下最有可能到达此状态的上一个状态
            }
        }

        // 3. 终止
        System.out.println("3.终止，回溯求最佳路径");
        int[] q= new int[ob.length];   // 定义最佳路径
        probability = delta[ob.length - 1][0];//将第0个定义为最大的权值
        q[ob.length - 1] = 0;
        for (int i = 1; i < N; i++)
        {
            if (delta[ob.length - 1][i] > probability)
            {
                probability = delta[ob.length - 1][i];
                q[ob.length - 1] = i;//最后一个时间点的最优状态
            }
        }

        // 4. 路径回溯
        for (int t = ob.length - 2; t >= 0; t--)
        {
            q[t] = psi[t + 1][q[t + 1]];
        }

        List list=new ArrayList();
        list.add(q);
        list.add(probability);
        return list;
    }
}
