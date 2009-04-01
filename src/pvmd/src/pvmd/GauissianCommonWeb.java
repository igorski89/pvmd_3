/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pvmd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

/**
 * Гаусівська загальна мережа
 * @author igorevsukov
 */
public class GauissianCommonWeb {
    private MDSample study;
    public MDSample getStudy(){
    	return this.study;
    }
    private MDSample exam;
    public MDSample getExam() {
    	return this.exam;
    }
    private ArrayList<MDComposition> commonWeb = new ArrayList<MDComposition>();
    
    /**
     * априорная вероятность
     */
    Hashtable<Integer, Double> p = new Hashtable<Integer, Double>();
    /**
     * завантажує вибірку з файлу
     * @param fileName повний шлях до файлу
     */
    public void load(String fileName) throws Exception {
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));

            try {
                String line = input.readLine();
                if (line == null)
                    throw new Exception("Can't read data: file "+fileName+" is empty");
                MDObject first_obj = new MDObject(line);
                //опеределяем какова размерность объекта
                int dimension = first_obj.getParams().length;
                //исходя из размерности создаем обучающюю и экз выборки
                study = new MDSample(dimension);
                exam = new MDSample(dimension);
                study.add(first_obj);
                while ((line = input.readLine())!= null) {
                    try {
                    	MDObject obj = new MDObject(line);
                    	if (p.containsKey(obj.getClassN())) p.put(obj.getClassN(), p.get(obj.getClassN())+1);
                    	else p.put(obj.getClassN(), new Double(1));
                    	
                    	study.add(obj);
                    	
                    }
                    catch(Exception ex){
                        System.out.println("can't add object to studySample:" + ex.getMessage());
                    }
                };
                Enumeration<Integer> e = p.keys();
                while(e.hasMoreElements()){
                	Integer key = e.nextElement();
                	p.put(key, p.get(key)/study.size());
                }
                mergeToExam();
            }
            finally{
                input.close();
            }
            
        }
        catch(Exception ex){
            throw ex;
        }
    }
    /**
     * переносить із обучаючої вибірки до екзаменаційної половину своїх
     * елементів, при цьому елементи екзаменаційної вибірки з неї видаляються.
     * Елементи, які переносяться вибираються випадково
     */
    public void mergeToExam(){
        int toMerge = study.size()/2;
        Random rand = new Random();
        //удаляем все из экз выборки
        exam.removeAll();
        for (int i = 0; i < toMerge; i++) {
            int index = rand.nextInt(study.size());
            try {
                exam.add(study.get(index));
                study.remove(index);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
//        for(int i = 0; i< toMerge; i++){
//        	try {
//        		exam.add(study.get(i));
//        		study.remove(i);
//        	}catch (Exception e) {
//				e.printStackTrace();
//			}
//        }
    }

    /**
     * проводить процесс навчання
     * @param n0 найменша кількість елементів в компоненті
     * @param kMax початкова кількість компонент
     */
    public void educate(int n0, int kMax){
        commonWeb.clear();
        Hashtable<Integer, ArrayList<MDObject>> samplesGroupedByClass = study.objectsGroupedByClass();

        Enumeration<Integer> e = samplesGroupedByClass.keys();
//        ArrayList<Integer> tmp = new ArrayList<Integer>();
        while (e.hasMoreElements()) {
            Integer key = e.nextElement();
//            tmp.add(key);
            commonWeb.add(new MDComposition(samplesGroupedByClass.get(key), n0, kMax, key.intValue()));
        }
//        for(int i=tmp.size()-1; i>=0; i--){
//        	Integer key = tmp.get(i);
//            commonWeb.add(new MDComposition(samplesGroupedByClass.get(key), n0, kMax, key.intValue()));
//        }
    }

    /**
     * проводить екзамен
     * @return достовірність
     */
    public double exam(){
        int correct = 0;
        double reliability = 0.0;
        final int commonWeb_size = commonWeb.size();
        for(int i=0; i<exam.size(); i++){
        	MDObject curr_exam = exam.get(i);
        	double t = p.get(commonWeb.get(0).getClassN());

            double max = t*commonWeb.get(0).calculateProbability(curr_exam);
            int classN = commonWeb.get(0).getClassN();

            for(int j=1; j<commonWeb_size; j++){
            	double tj = p.get(commonWeb.get(j).getClassN());
                double tmp = tj*commonWeb.get(j).calculateProbability(curr_exam);            	
                if (tmp > max){
                    max = tmp;
                    classN = commonWeb.get(j).getClassN();
                }
            }

            exam.get(i).setExamClassN(classN);
            if (classN == exam.get(i).getClassN()) correct++;
        }
        reliability = (double)correct/(double)exam.size();
        return reliability;
    }
}
