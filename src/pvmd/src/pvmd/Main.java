/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pvmd;

import java.awt.BorderLayout;

import javax.swing.JFrame;



/**
 *
 * @author igorevsukov
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        try {
//            for(int i = 0; i < 150; i++){
//            	GaussianCommonWeb commonWeb = new GaussianCommonWeb();
//            	commonWeb.load("/Users/igorevsukov/Documents/DNU/PVMD/lab2_claster/4_new.txt");
//            	commonWeb.educate(10, 15);
//            	double reliability = commonWeb.exam();
//            	System.out.println(reliability);
//            }
//        }
//        catch(Exception ex) {
//            ex.printStackTrace();
//        }
    	
    	JFrame mainWindow = new JFrame("main window");
    	mainWindow.setLocation(100, 100);
    	mainWindow.setSize(800, 600);
    	mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    	mainWindow.getContentPane().setLayout(new FlowLayout());
//    	JTable table = new JTable(new Object[][] { {"0.0", "0.1"},{"1.0","1.1"} }, new Object[] {"col 1","col 2"});
//    	table.getColumnModel().getColumn(0).setMaxWidth(100);
//    	table.getColumnModel().getColumn(1).setMaxWidth(100);
//    	JScrollPane sp = new JScrollPane(table);
//    	sp.setPreferredSize(new Dimension(200,300));
//    	mainWindow.getContentPane().add(sp);
//    	mainWindow.getContentPane().add(new JScrollPane(new JTable(new Object[][] { {"0.0", "0.1"},{"1.0","1.1"} }, new Object[] {"col 1","col 2"})));
//    	mainWindow.getContentPane().add(new JButton("some button"));
    	mainWindow.getContentPane().setLayout(new BorderLayout());
    	mainWindow.getContentPane().add(new MainPanel(), BorderLayout.CENTER);

    	
    	mainWindow.setVisible(true);
    }

}
