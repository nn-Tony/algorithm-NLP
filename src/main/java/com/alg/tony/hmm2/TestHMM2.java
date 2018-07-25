package com.alg.tony.hmm2;

import java.util.List;

public class TestHMM2 {
	enum Box {one,two,three };  // 隐藏状态（箱子编号）
    enum Color {red,yellow,blue,green };  // 观察状态（观测到的颜色值）

    public static void main(String[] args)
    {
        // 测试前向算法和后向算法
        CheckForwardAndBackward();
//        Console.WriteLine();
        // 测试维特比算法
        CheckViterbi();
//        Console.WriteLine();
    }

    // 测试前向算法和后向算法
    static void CheckForwardAndBackward()
    {
        // 状态转移矩阵
        double[][] A = 
        {
            {0.500, 0.375, 0.125},
            {0.250, 0.125, 0.625},
            {0.250, 0.375, 0.375}
        };

        // 混淆矩阵
        double[][] B = 
        {
            {0.60, 0.20, 0.15, 0.05},
            {0.25, 0.25, 0.25, 0.25},
            {0.05, 0.10, 0.35, 0.50}
        };

        // 初始概率向量
        double[] PI = {0.63,0.17,0.20};

        // 观察序列
        int[] OB = {Color.red.ordinal(), Color.yellow.ordinal(), Color.blue.ordinal()};
        System.out.println("状态转移概率矩阵：");
        for(int i=0;i<A.length;i++)
        {
        	for(int j=0;j<A[0].length;j++)
        	{
        		System.out.print(A[i][j]+"\t");
        	}
        	System.out.println();
        }
        System.out.println("符号观测概率矩阵：");
        for(int i=0;i<B.length;i++)
        {
        	for(int j=0;j<B[0].length;j++)
        	{
        		System.out.print(B[i][j]+"\t");
        	}
        	System.out.println();
        }
        System.out.println("初始概率向量：{"+PI[0]+" "+PI[1]+" "+PI[2]+"}");
        System.out.println("隐藏状态序列：{"+Box.one+" "+Box.two+" "+Box.three+"}");
        System.out.println("观测序列：{"+Color.red+" "+Color.yellow+" "+Color.blue+"}");

        // 初始化HMM模型
        Forward forward = new Forward(A.length, B[0].length);
        forward.A=A;
        forward.B=B;
        forward.PI=PI;   

        // 观察序列的概率
        System.out.println("------------前向算法-----------------");
        double probability = forward.forward(OB);
        
        Backward backWard = new Backward(A.length, B[0].length);
        backWard.A=A;
        backWard.B=B;
        backWard.PI=PI;
        // 观察序列的概率
        System.out.println("------------后向算法-----------------");
        probability = backWard.backward(OB);
    }       

    // 测试维特比算法
    static void CheckViterbi()
    {
        // 状态转移矩阵
        double[][] A = 
        {
            {0.500, 0.250, 0.250},
            {0.375, 0.125, 0.375},
            {0.125, 0.675, 0.375}
        };

        // 混淆矩阵
        double[][] B = 
        {
            {0.60, 0.20, 0.15, 0.05},
            {0.25, 0.25, 0.25, 0.25},
            {0.05, 0.10, 0.35, 0.50}
        };

        // 初始概率向量
        double[] PI = { 0.63, 0.17, 0.20 };
        // 观察序列
        int[] OB = {Color.red.ordinal(),Color.yellow.ordinal(), Color.blue.ordinal(), Color.yellow.ordinal(),Color.green.ordinal() };
        System.out.println("状态转移概率矩阵：");
        for(int i=0;i<A.length;i++)
        {
        	for(int j=0;j<A[0].length;j++)
        	{
        		System.out.print(A[i][j]+"\t");
        	}
        	System.out.println();
        }
        System.out.println("符号观测概率矩阵：");
        for(int i=0;i<B.length;i++)
        {
        	for(int j=0;j<B[0].length;j++)
        	{
        		System.out.print(B[i][j]+"\t");
        	}
        	System.out.println();
        }
        System.out.println("初始概率向量：{"+PI[0]+" "+PI[1]+" "+PI[2]+"}");
        System.out.println("隐藏状态序列：{"+Box.one+" "+Box.two+" "+Box.three+"}");
        System.out.println("观测序列：{"+Color.red+" "+Color.yellow+" "+Color.blue+" "+Color.yellow+" "+Color.green+"}");
        
        // 初始化HMM模型
        Viterbi viterbi = new Viterbi(A.length, B[0].length);
        viterbi.A=A;
        viterbi.B=B;
        viterbi.PI=PI;           

        // 找出最有可能的隐藏状态序列
        double probability = 0;

        List list=viterbi.viterbi(OB,probability);
        int[] Q = (int[]) list.get(0);//返回隐藏状态序列
        System.out.print("最可能的隐藏状态序列为：{");
        for(int value:Q)
        {
        	System.out.print(Box.values()[value]+" ");
        }
        System.out.println("}");
        System.out.println("最大可能性为："+list.get(1));
    }
}
