/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.client.activity.utils;

import java.util.Vector;
public class Autocorrelation {

    private static double[] calcMeanAndSTD(Double[] numbers)
    {
        int n = numbers.length;
        double mean = 0;
        double std = 0;
        double squares = 0;
        for(int i = 0; i < n; i++)
        {
            mean += numbers[i];
            squares += numbers[i]*numbers[i];
        }
        mean /= n;
        squares /= n;
        double meanSquare = mean * mean;
        std = Math.sqrt(squares - meanSquare);
        double[] result = {mean,std};
        return result;
        
    }
    
    public static double calcAutocorrelation(Double[] sample, Double[] data)
    {
        double result = 0;
        if(sample.length == data.length)
        {
            double[] meanStdSample = calcMeanAndSTD(sample);
            double[] meanStdData = calcMeanAndSTD(data);
            for(int j = 0; j < sample.length; j++)
            {
                result+= (sample[j]-meanStdSample[0])*(data[j]-meanStdData[0]);

            }
            result /= sample.length;
            result /= meanStdSample[1]*meanStdData[1];
        }
        return result;
    }

    public static double calcAutocorrelationVector(Vector<Double> sample, Vector<Double> data)
    {
        Double[] sampleVec = new Double[sample.size()];
        Double[] dataVec = new Double[data.size()];
        sample.toArray(sampleVec);
        data.toArray(dataVec);
        return calcAutocorrelation(sampleVec, dataVec);
    }

}
