package com.alg.tony.maxent_iis;


/**
 * 实例
 */
public class Instance
{
    /**
     * 标签
     */
    public int label;
    /**
     * 特征
     */
    public Feature feature;

    public Instance(int label, int[] xs)
    {
        this.label = label;
        this.feature = new Feature(xs);
    }

    public int getLabel()
    {
        return label;
    }

    public Feature getFeature()
    {
        return feature;
    }

    @Override
    public String toString()
    {
        return "Instance{" +
                "label=" + label +
                ", feature=" + feature +
                '}';
    }
}
