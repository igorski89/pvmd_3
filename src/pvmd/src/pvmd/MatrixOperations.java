/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pvmd;

/**
 * клас для різних операцій з матрицями та векторами
 * @author igorevsukov
 */
public class MatrixOperations {
    /**
     * перевіряє рівність двох векторів
     * @param v1 перший вектор
     * @param v2 другий вектор
     * @return true якщо рівні, false якщо є різні елементи
     */
    public static Boolean isEqual(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            return false;
        
        for (int i=0; i<v1.length; i++)
            if (v1[i] != v2[i])
                return false;
        
        return true;
    }

    /**
     * перевіряє рівність двох матриць
     * @param m1 перша матриця
     * @param m2 дурга матриця
     * @return true якщо рівні, false якщо є різні елементи
     */
    public static Boolean isEqual(double[][] m1, double[][] m2) {
        if (m1.length != m2.length)
            return false;

        for (int i=0; i<m1.length; i++){
            if (!isEqual(m1[i], m2[i]))
                return false;
        }

        return true;
    }

    /**
     * сума двох векторів(поелементно)
     * @param v1 перший вектор
     * @param v2 другий вектор
     * @return вектор суми
     */
    public static double[] sum(double[] v1, double[] v2) throws Exception {
        if (v1.length != v2.length)
            throw new Exception("length of vectors are different");
        final int len = v1.length;
        double[] sum = new double[len];
        for(int i=0; i<len; i++)
            sum[i] = v1[i]+v2[i];
        
        return sum;
    }

    /**
     * різния двох векторів(поелементно)
     * @param v1 перший вектор
     * @param v2 другий вектор
     * @return вектор сум
     * @throws java.lang.Exception якщо вектори різної довжини
     */
    public static double[] sub(double[] v1, double[] v2) throws Exception {
        if (v1.length != v2.length)
            throw new Exception("length of vectors are different");
        final int len = v1.length;
        double[] sum = new double[len];
        for(int i=0; i<len; i++)
            sum[i] = v1[i]-v2[i];

        return sum;
    }

    /**
     * добуток елементів вектору на задане число
     * @param v вектор
     * @param t число, на яке необхідно помножити
     * @return новий вектор добутку
     */
    public static double[] times(double[] v, double t) {
    	final int v_len = v.length;
        double [] res = new double[v_len];
        for (int i=0; i<v_len; i++)
            res[i] = v[i]*t;

        return res;
    }

    /**
     * сума добутку відповідних елементів векторів
     * @param v1 перший вектор
     * @param v2 другий вектор
     * @return
     * @throws java.lang.Exception якщо вектори різної довжини
     */
    public static double mult(double[] v1, double[] v2) throws Exception{
        double mult = 0.0;
        if (v1.length != v2.length)
            throw new Exception("length of vectors are different");
        for(int i=0; i<v1.length; i++)
            mult += v1[i]*v2[i];

        return mult;
    }

    /**
     * повертає мінор матриці
     * @param m матриця
     * @param row номер строки
     * @param column номер стовпця
     * @return мінор(нова матриця)
     */
    public static double[][] minor(double[][] m, int row, int column) {
        int n = m.length;
        double[][] minor = new double[n-1][n-1];
        int indexRow = 0;
        for(int i=0; i<n; i++){
            if (i == row) continue;
            int indexColumn = 0;
            for(int j=0; j<n; j++){
                if (j == column) continue;
                minor[indexRow][indexColumn] = m[i][j];
                indexColumn++;
            }
            indexRow++;
        }

        return minor;
    }

    /**
     * визначає детермінант матриці
     * @param m матрця
     * @return детермінант
     */
    public static double det(double[][] m){
        double ret = 0.0;
        if (m.length == 1)
            return m[0][0];

        for(int i=0; i < m.length; i++){
            if (i % 2 == 0)
                ret += m[i][0]*det(minor(m,i,0));
            else
                ret -= m[i][0]*det(minor(m,i,0));
        }
        
        return ret;
    }

    /**
     * множить матрцию на задане число
     * @param m матриця
     * @param times множник
     * @return нова матриця
     */
    public static double[][] times(double[][] m, double times){
        double[][] res = new double[m.length][m[0].length];
        for(int i=0; i<m.length; i++) 
            for(int j=0; j<m[0].length; j++) 
                res[i][j] = m[i][j]*times;

        return res;
    }

    /**
     * @param m матриця
     * @return інвертована матриця
     */
    public static double[][] inverse(double[][] m){
        double[][] res = new double[m.length][m.length];
        for(int i=0; i<m.length; i++) {
            for(int j=0; j<m.length; j++) {
                if( (i + j) % 2 == 0 )
                    res[i][j] = det(minor(m,j,i));
                else
                    res[i][j] = (-1)*det(minor(m,j,i));
            }
        }
        return times(res, 1.0/det(m));
    }

    /**
     * добуток вектора на матрицю
     * @param v вектор
     * @param A матриця
     * @return вектор-результат
     */
    public static double[] mult(double[] v, double[][] A){
        double[] ret = new double[v.length];
        for(int i=0; i<v.length; i++){
            ret[i] = 0;
            for(int j=0; j<v.length; j++)
                ret[i] += v[j]*A[i][j];
        }

        return ret;
    }
}
