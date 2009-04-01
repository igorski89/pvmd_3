
package pvmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Клас, що описує простий обєкт
 * @author I-Evsukov
 */
public class MDObject implements Cloneable {
    /**
     * номер классу, до якого належить обєкт
     */
    private int classN;
    public int getClassN() {
        return classN;
    }
    public void setClassN(int newClassN) {
        classN = newClassN;
    }
    
    /**
     * номер классу, що присвоюється на екзамені
     */
    private int examClassN;
    public int getExamClassN(){
    	return examClassN;
    }
    public void setExamClassN(int newExamClassN){
    	examClassN = newExamClassN;
    }

    /**
     * параметри обєкту(x1,x2,..,xp)
     */
    private double params[];
    public double[] getParams() {
        return params;
    }
    public void setParams(double[] newParams) {
        params = newParams;
    }

    /**
     * створює новий обєкт з масиву параметрів та номеру классу
     * @param objParams масив параметрів
     * @param objClassN номер классу
     */
    public MDObject(double[] objParams, int objClassN){
        classN = objClassN;
        params = new double[objParams.length];
        for (int i = 0; i < objParams.length; i++) {
            params[i] = objParams[i];
        }
        examClassN = 0;
    }

    /**
     * створює новий обєкт зчитуючи параметри та номер классу з строки(для
     * завантаження з файлу)
     * @param s строка, в якій записані параметри і номер классу(останнім)
     */
    public MDObject(String s) throws Exception{
//        Scanner sc = new Scanner(s);
//
//        // считываем параметры
//        ArrayList<Double> tempParams = new ArrayList<Double>();
//        //вот такая подпорка, а то эта сцуко считает последнее число тоже даблом
//        while(sc.hasNextDouble() && !sc.hasNextInt()){
//            tempParams.add(sc.nextDouble());
//        }
//        if (tempParams.isEmpty()) {
//            throw new Exception("there is no params of double type in "+s);
//        }
//
//        params = new double[tempParams.size()];
//        for (int i = 0; i < tempParams.size(); i++) {
//            params[i] = tempParams.get(i);
//        }
//
//        // считываем номер класса
//        if (!sc.hasNextInt()) {
//            throw new Exception("there is no classNumber of int type in "+s);
//        }
//        classN = sc.nextInt();
//
//        sc.close();
    	
//    	s = s.trim();
    	
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(s.trim());
        String[] str = matcher.replaceAll(" ").split(" ");
        params = new double[str.length-1];
        for(int i = 0; i< str.length - 1; i++)
        	params[i] = Double.parseDouble(str[i]);
        
        classN =  (int)Double.parseDouble(str[str.length-1]);
        
        examClassN = 0;
    }

    /**
     * повертає параметри та номер классу об'єкту у вигляді строки
     * @return (x1,x2,...,xn,classN)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
            sb.append(",");
        }
        sb.delete(sb.length()-2, sb.length()-1);
        sb.append(classN);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public MDObject clone() {
        double[] cloneParams = new double[params.length];
        for (int i=0; i<params.length; i++) 
            cloneParams[i] = params[i];
        
        int cloneClass = classN;
        return new MDObject(cloneParams, cloneClass);
    }
}
