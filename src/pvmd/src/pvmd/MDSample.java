/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pvmd;

import java.util.ArrayList;
//import java.util.Enumeration;
import java.util.Hashtable;
/**
 * клас, що описує вибірку
 * @author igorevsukov
 */
public class MDSample {
    private ArrayList<MDObject> data;
//    private ArrayList<MDObject> backup;

    /**
     * розмірність елементів у вибірці
     */
    private int dimension;
    /**
     * @return the dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * стрворює нову вибірку з елементами заданої розмірності
     * @param d розмірність елементів вибірки
     */
    public MDSample(int d) {
        dimension = d;
        data = new ArrayList<MDObject>();
//        backup = new ArrayList<MDObject>();
    }

    /**
     * повертає елемент вибірки
     * @param i порядковий номер необхідного елементу
     * @return
     */
    public MDObject get(int i) {
        return data.get(i);
    }

    /**
     * розмір вибірки
     * @return кількість елементів у вибірці
     */
    public int size() {
        return data.size();
    }

    /**
     * додає об'єкт до вибірки
     * @param newObj новий об'єкт
     */
    public void add(MDObject newObj) throws Exception {
        if (newObj.getParams().length != dimension)
            throw new Exception("MDobject dimension is different to this MDSample");
        
        data.add(newObj);
    }

    /**
     * видаляє об'єкт з вибірки
     * @param i індекс об'єкту
     */
    public void remove(int i) {
        data.remove(i);
    }

    /**
     * видаляє об'єкт з вибірки
     * @param obj об'єкт, що видаляється
     */
    public void remove(MDObject obj) {
        data.remove(obj);
    }

    /**
     * видаляє всі об'єкти з вибірки
     */
    public void removeAll() {
        data.clear();
    }

//    /**
//     * відновлює дані з бекапу
//     */
//    public void restore() {
//        data.clear();
//        for(MDObject obj:backup) {
//            data.add(obj.clone());
//        }
//    }
    
//    /**
//     * створює бекап з поточних даних вибірки
//     */
//    public void backup() {
//        backup.clear();
//        for (MDObject obj:data) {
//            backup.add(obj.clone());
//        }
//    }

//    /**
//     * вираховує характеристики вибірки: середнє, дисперсію та ін.
//     */
//    public void calculateParams() {
//        //присваиваем начальные значения
//        for (int j=0; j<dimension; j++) {
//            double tmp = data.get(0).getParams()[j];
//            mean[j] = tmp;
//            xmin[j] = tmp;
//            xmax[j] = tmp;
//        }
//
//        //вычисляем реальные значения
//        for(int i=1; i<data.size(); i++) {
//            for (int j=0; j<dimension; j++) {
//                double tmp = data.get(i).getParams()[j];
//                mean[j] += tmp;
//                if (tmp < xmin[j])
//                    xmin[j] = tmp;
//                else if (tmp > xmax[j])
//                    xmax[j] = tmp;
//            }
//        }
//
//        for (int j=0; j<dimension; j++) {
//            mean[j] /= data.size();
//        }
//
//        //если размер выборки == 1, то дисперсия нах не нужна
//        if (data.size() > 1) {
//            for (int i=0; i<data.size(); i++)
//                for (int j=0; j<dimension; j++)
//                    dispersion[j] += Math.pow(data.get(i).getParams()[j]-mean[j], 2);
//
//            for (int j=0; j<dimension; j++)
//                dispersion[j] = Math.sqrt(dispersion[j]/(data.size()-1.0));
//        }
//    }

//    /**
//     * @return вибірка у якій параметри об'єктів згруповані по номеру классу
//     */
//    public Hashtable<Integer,ArrayList<double[]>> dataGroupedByClass() {
//        Hashtable<Integer, ArrayList<double[]>> ret = new Hashtable<Integer, ArrayList<double[]>>();
//        for (int i=0; i<data.size(); i++) {
//            //вот не доверяю я что-то equals()
//            for(Enumeration<Integer> e = ret.keys(); e.hasMoreElements(); ){
//                Integer key = (Integer) e.nextElement();
//                if (key.intValue()==get(i).getClassN()){
//                    ret.get(key).add(get(i).getParams());
//                }
//                else {
//                    ret.put(get(i).getClassN(), new ArrayList<double[]>());
//                    ret.get(key).add(get(i).getParams());
//                }
//            }
//        }
//        return ret;
//    }

    /**
     * @return hash-таблция, у якій об'єкти згруповані за номером классу
     */
    public Hashtable<Integer,ArrayList<MDObject>> objectsGroupedByClass() {
        Hashtable<Integer, ArrayList<MDObject>> ret = new Hashtable<Integer, ArrayList<MDObject>>();
        int dataSize = data.size();
        for (int i=0; i<dataSize; i++) {
            if(ret.containsKey(get(i).getClassN())){
                ret.get(get(i).getClassN()).add(get(i));
            }
            else {
                ret.put(get(i).getClassN(), new ArrayList<MDObject>());
                ret.get(get(i).getClassN()).add(get(i));
            }
        }
        return ret;
    }
}
