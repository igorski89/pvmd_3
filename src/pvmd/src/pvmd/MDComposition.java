/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pvmd;

import java.util.ArrayList;

/**
 * представляє суміш нормальних розподілів
 * @author igorevsukov
 */
public class MDComposition {
    private ArrayList<double[]> M;
    private ArrayList<double[][]> Sigma;
    private ArrayList<Double> c;
    private int classN;

    /**
     * 
     * @param x дані
     * @param n0 найменша кількість елементів в компоненті
     * @param kMax початкова кількість компонент
     */
    public MDComposition(ArrayList<MDObject> x, int n0, int kMax, int keyClass){
        classN = keyClass;
        separate(x, n0, kMax);
    }

    /**
     * розділяє суміш нормальних розподілів
     * @param x дані
     * @param n0 найменша кількість елементів в компоненті
     * @param kMax початкова кількість компонент
     */
    public void separate(ArrayList<MDObject> x, int n0, int kMax){
        ArrayList<double[]> doubleList = new ArrayList<double[]>();
        for(MDObject obj:x)
            doubleList.add(obj.getParams());

        SEM.Execute(doubleList, n0, kMax);
        M = SEM.getMDeepCopy();
        Sigma = SEM.getSigmaDeepCopy();
        c = SEM.getCDeepCopy();
    }

    /**
     * @param x об'єкт, для якого необхідно підрахувати ймовірність
     * @return вираховує ймовірність об'єкту в суміші
     */
    public double calculateProbability(MDObject x){
        double prob = 0.0;
        final int c_size = c.size();
        for(int i=0; i<c_size; i++)
            prob += c.get(i)*SEM.fx_norm(x.getParams(),M.get(i), Sigma.get(i));

        return prob;
    }

    /**
     * @return the key
     */
    public int getClassN() {
        return classN;
    }
}
