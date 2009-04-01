/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pvmd;

import java.util.ArrayList;
import java.util.Random;

/**
 * в класі реалізован SEM алгоритм
 * @author igorevsukov
 */
public class SEM {

    /**
     * вираховує значення щільності нормального розподілу
     * @param x вектор аргументу
     * @param M центр нормального розподілу
     * @param sigma коваріаційна матриця
     * @return щільність нормального розподілу
     */
    public static double fx_norm(double[] x, double[] M, double[][] sigma) {
        double res = 0.0;
        //вот думаю я, может быть забить на этот JaMa и написать все операции
        //вручную?
//        Matrix sub = new Matrix(x,1);
//        sub.minusEquals(new Matrix(M,1));
//
//        Matrix vect = sub.arrayTimes(sigma.inverse());
//        ret = vect.arrayTimesEquals(sub).get(0, 0);
//        ret = Math.exp(ret);
//        ret /= Math.sqrt(Math.pow(2*Math.PI, x.length)*sigma.det());
//        //пока оставлю JaMa, но если будет получатся херня, напишу вручную
        //как показала практика, с JaMa проблем еще больше
        //переписываю работу с матрицами вручную
        double[] subVec;
        try {
            subVec = MatrixOperations.sub(x, M);
            res = -0.5 * MatrixOperations.mult(MatrixOperations.mult(subVec, MatrixOperations.inverse(sigma)), subVec);
            res = Math.exp(res);
            double det_sigma = MatrixOperations.det(sigma);
            res /= Math.sqrt(Math.pow(2*Math.PI, x.length)*det_sigma);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return res;
    }

    /**
     * задає початкове наближення
     * @param n колличество элементов
     * @param p элементов признаков
     * @return
     */
    private static double[][] startApproximation(int n, int p) {
        double[][] ret = new double[n][p];

        Random rand = new Random();
        for (int i=0; i<n; i++) {
            double s = 0;
            for (int j=0; j<p; j++) {
                ret[i][j] = rand.nextDouble();
                s += ret[i][j];
            }
            //чтобы сумма в строке получилась один
            for(int j=0; j<p; j++)
                ret[i][j] /= s;
        }

        return ret;
    }

    /**
     * крок S
     * @param X
     * @param x
     * @param G
     * @param n0
     * @return допоміжна матриця
     */
    private static double[][] stepS(ArrayList<ArrayList<double[]>> X, ArrayList<double[]> x, double [][] G, int n0) {

        for(ArrayList<double[]> ui:X) ui.clear();

        Random rand = new Random();

        for(int i=0; i<x.size(); i++) {
            double rd = rand.nextDouble();
            double sum = 0;
            int j = -1;
            while(sum <= rd) {
                j++;
                sum += G[i][j];
            }
            X.get(j).add(x.get(i));
        }

        ArrayList<Integer> indexesToDelete = new ArrayList<Integer>();
        //удаляем нах те, в которых мало элементов
        for (int j = X.size() - 1; j>=0; j--) 
        	if (X.get(j).size()<= n0) 
        		indexesToDelete.add(j);
        
        if (indexesToDelete.size() == 0) return G;
        //перераспределяем элементы
        for (Integer i:indexesToDelete) {
            int j = i.intValue();
            while(X.get(j).size() != 0) {
                int k = rand.nextInt(X.size());
                while (k == j)
                    k = rand.nextInt(X.size());

                X.get(k).add(X.get(j).get(0));
                X.get(j).remove(0);
            }
            X.remove(j);
        }
        //пересчитываем матрицу вспомогательных переменных
        double[][] newG = new double[G.length][G[0].length-indexesToDelete.size()];
        
        for (int i=0; i<G.length; i++) {
            double newSum = 0.0;
            int u_size = X.size();
            for (int j=0; j<u_size; j++) 
            	if (!indexesToDelete.contains(j)) 
            		newSum += G[i][j];
            
            int index = 0;
            while (index < u_size) {
                if (indexesToDelete.contains(index)){
                    index++;
                    continue;
                }
                newG[i][index] = G[i][index]/newSum;
                index++;
            }
        }
        
        for(Integer i:indexesToDelete) {
        	int j = i.intValue();
            c.remove(j);
            M.remove(j);
            Sigma.remove(j);
        }

        return newG;
    }

    /**
     * крок E
     * @param x дані
     * @return допоміжна матриця
     */
    private static double[][] stepE(ArrayList<double[]> x){
        final int x_size = x.size();
        final int c_size = c.size();
        double[][] ret = new double[x_size][c_size];

        for (int i=0; i<x_size; i++){
            double sum = 0.0;
            for(int s=0; s<c_size; s++)
                sum += c.get(s)*fx_norm(x.get(i), M.get(s), Sigma.get(s));

            for(int j=0; j<c_size; j++)
                ret[i][j] = c.get(j)*fx_norm(x.get(i), M.get(j), Sigma.get(j))/sum;
            
        }
        return ret;
    }

    /**
     * крок M
     * @param u
     * @param n
     */
    private static void stepM(ArrayList<ArrayList<double[]>> u, double n) {
        final int u_size = u.size();
        for(int j=0; j<u_size; j++){
            final int u_0_0_len = u.get(0).get(0).length;
            
            M.set(j, new double[u_0_0_len]);
            Sigma.set(j, new double[u_0_0_len][u_0_0_len]);

            for (int i=0; i<u.get(j).size(); i++) {
                try {
                    M.set(j, MatrixOperations.sum(M.get(j), u.get(j).get(i)));
                } catch (Exception ex) {
//                    Logger.getLogger(SEMAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
            }
            M.set(j, MatrixOperations.times(M.get(j), 1.0/(double)u.get(j).size()));

            for (int l=0; l<Sigma.get(0).length; l++)
                for(int mu=0; mu<Sigma.get(0).length; mu++)
                    for(int i=0; i<u.get(j).size(); i++) 
                        Sigma.get(j)[l][mu] += (u.get(j).get(i)[l]-M.get(j)[l])*(u.get(j).get(i)[mu]-M.get(j)[mu]);

            Sigma.set(j, MatrixOperations.times(Sigma.get(j), 1.0/(double)u.get(j).size()));
            c.set(j, new Double((double)u.get(j).size()/(double)n));
        }
    }

    /**
     * апріорна вірогідність
     */
    private static ArrayList<Double> c = new ArrayList<Double>();
    /**
     * @return апріорна вірогідність
     */
    public static ArrayList<Double> getC() {
        return c;
    }
    /**
     * @return повертає deep-copy масив апріорних вірогідностей
     */
    public static ArrayList<Double> getCDeepCopy(){
        ArrayList<Double> c_copy = new ArrayList<Double>();
        for (Double tmp:c)
            c_copy.add(new Double(tmp.doubleValue()));
        
        return c_copy;
    }

    /**
     * масив центрів
     */
    private static ArrayList<double[]> M = new ArrayList<double[]>();
    /**
     * @return масив центрів
     */
    public static ArrayList<double[]> getM() {
        return M;
    }
    /**
     * @return deep-копія масиву центрів
     */
    public static ArrayList<double[]> getMDeepCopy(){
        ArrayList<double[]> M_copy = new ArrayList<double[]>();
        for(double[] Mi:M){
            //не знаю делает ли это deep-копию, так что пока вручную
            double[] Mi_copy = new double[Mi.length];
            for (int i=0; i<Mi.length; i++)
                Mi_copy[i] = Mi[i];

            M_copy.add(Mi_copy);
        }
        return M_copy;
    }

    /**
     * масив коваріаційних матриць
     */
    private static ArrayList<double[][]> Sigma = new ArrayList<double[][]>();
    /**
     * @return масив коваріаційних матриць
     */
    public static ArrayList<double[][]> getSigma() {
        return Sigma;
    }
    /**
     * @return deep-копія масиву коваріаційних матриць
     */
    public static ArrayList<double[][]> getSigmaDeepCopy(){
        ArrayList<double[][]> Sigma_copy = new ArrayList<double[][]>();
        for (double[][] Sigma_i:Sigma){
            double[][] Sigma_i_copy = new double[Sigma_i.length][Sigma_i[0].length];

            for(int i=0; i<Sigma_i.length; i++){
//                Sigma_i_copy[i] = Arrays.copyOf(Sigma_i[i], Sigma_i[0].length);
                for (int j=0; j<Sigma_i[0].length; j++)
                    Sigma_i_copy[i][j] = Sigma_i[i][j];
            }
            Sigma_copy.add(Sigma_i_copy);
        }
        return Sigma_copy;
    }

    /**
     * виконує SEM алгоритм.
     * Вихідні дані:апріорну вірогідність, масив центрів та коваріаційних матриць
     * брати після виконання алгоритму з відповідних static змінних
     * @param x дані
     * @param n0 мінімальна кількість елементів в компоненті
     * @param kMax початкова кількість компонент
     */
    public static void Execute(ArrayList<double[]> x,
                               int n0,
                               int kMax) {
        ArrayList<ArrayList<double[]>> X = new ArrayList<ArrayList<double[]>>(kMax);
        c.clear();
        M.clear();
        Sigma.clear();
        for(int i=0; i<kMax; i++){
            X.add(new ArrayList<double[]>());
            c.add(new Double(0.0));
            M.add(new double[x.get(0).length]);
            Sigma.add(new double[x.get(0).length][x.get(0).length]);
        }

        double [][] G = startApproximation(x.size(), kMax);
        int i = 0;
        while(i < 2000) {
            G = stepS(X, x, G, n0);
            stepM(X, x.size());
            G = stepE(x);
            i++;
        }

    }

}
