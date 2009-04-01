package pvmd;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = 2949687444255909471L;
	
	private GauissianCommonWeb commonWeb = new GauissianCommonWeb();
	
	private JButton openFileButton;
	private JTextField n0TextF;
	private JTextField kMaxTextF;
	private JButton studyButton;
	private JButton examButton;
	private JLabel reliabilityLabel;
	
	private JTable studyTable;
	private AbstractTableModel studyTableModel;
	private XYSeriesCollection studyGraphDataset;
	
	private JTable examTable;
	private AbstractTableModel examTableModel;
	private XYSeriesCollection examGraphDataset;
	public MainPanel(){
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		studyTableModel = new AbstractTableModel() {
			private static final long serialVersionUID = -7228306584948379443L;

			@Override
			public void addTableModelListener(TableModelListener l) {}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == commonWeb.getStudy().getDimension()) return Integer.class;
				else return Double.class;
			}

			@Override
			public int getColumnCount() {
				if (commonWeb.getStudy() == null) return 0;
				else return commonWeb.getStudy().getDimension()+1;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex < commonWeb.getStudy().getDimension()) return String.valueOf(columnIndex);
				else return "#";
			}

			@Override
			public int getRowCount() {
				if (commonWeb.getStudy() == null) return 0;
				else return commonWeb.getStudy().size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex < commonWeb.getStudy().getDimension())
					return new Double(commonWeb.getStudy().get(rowIndex).getParams()[columnIndex]);
				else
					return new Integer(commonWeb.getStudy().get(rowIndex).getClassN());
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }

			@Override
			public void removeTableModelListener(TableModelListener l) {}

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {}
			
		};
		
		//кнопка открытия файла
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		openFileButton = new JButton("Open File");
		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser openFileChooser = new JFileChooser();
				openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFileChooser.setMultiSelectionEnabled(false);
				// TODO:убрать в релизе, а то будет падать
				openFileChooser.setCurrentDirectory(new File("/Users/igorevsukov/Documents/DNU/PVMD/PVMD_3_data/"));
				if (openFileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION){
					try {
						commonWeb.load(openFileChooser.getSelectedFile().getAbsolutePath());
						studyTable.tableChanged(null);
						examTable.tableChanged(null);
						refreshStudyGraphData();
						examButton.setEnabled(false);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		add(openFileButton,c);
		
		//панель с параметрами обучения и кнопкой Ыкзамин
		JPanel examParamsPanel = new JPanel();
		examParamsPanel.setLayout(new FlowLayout());
		n0TextF = new JTextField("10");
//		n0TextF.setMinimumSize(new Dimension(80,20));
//		n0TextF.setText("      ");
		kMaxTextF = new JTextField("15");
		studyButton = new JButton("study");
		examButton = new JButton("exam");
		examParamsPanel.add(new JLabel("  n0 ="));
		examParamsPanel.add(n0TextF);
		examParamsPanel.add(new JLabel("  kMax ="));
		examParamsPanel.add(kMaxTextF);
		examParamsPanel.add(studyButton);
		examParamsPanel.add(examButton);
		reliabilityLabel = new JLabel();
		examParamsPanel.add(reliabilityLabel);
		studyButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//проводим обучение
				commonWeb.educate(Integer.valueOf(n0TextF.getText()), Integer.valueOf(kMaxTextF.getText()));
				examButton.setEnabled(true);
			}
			
		});
		examButton.setEnabled(false);
		examButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Ыкзамен
				double reliability = commonWeb.exam();
				reliabilityLabel.setText("   reliability = "+String.valueOf(reliability));
				examTable.tableChanged(null);
				//обновляем график экзаменационной выборки
				refreshExamGraphData();
			}
		});
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		add(examParamsPanel,c);
		
		//таблица на обучение
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.LINE_START;
		studyTable = new JTable(studyTableModel);
		JScrollPane studyTableScrollPane = new JScrollPane(studyTable);
		studyTableScrollPane.setMinimumSize(new Dimension(300,200));
		add(studyTableScrollPane,c);
		
		//график обучающей выборки
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		studyGraphDataset = new XYSeriesCollection();
		JFreeChart studyChart = ChartFactory.createScatterPlot("study samples", "", "", studyGraphDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel studyChartPanel = new ChartPanel(studyChart);
		studyChartPanel.setVerticalAxisTrace(true);
		studyChartPanel.setHorizontalAxisTrace(true);
		add(studyChartPanel,c);
		
		//таблица экзаменационной выборки
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 1.0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.LINE_START;
		examTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1099503804870204904L;
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == commonWeb.getExam().getDimension()) return Integer.class;
				else return Double.class;
			}
			
			@Override
			public int getColumnCount() {
				if (commonWeb.getExam() == null) return 0;
				else return commonWeb.getExam().getDimension()+2;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex < commonWeb.getExam().getDimension()) return String.valueOf(columnIndex);
				else if (columnIndex == commonWeb.getExam().getDimension()) return "#";
				else return "exam #";
			}

			@Override
			public int getRowCount() {
				if (commonWeb.getExam() == null) return 0;
				else return commonWeb.getExam().size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex < commonWeb.getExam().getDimension())
					return new Double(commonWeb.getExam().get(rowIndex).getParams()[columnIndex]);
				else if (columnIndex == commonWeb.getExam().getDimension())
					return new Integer(commonWeb.getExam().get(rowIndex).getClassN());
				else
					return new Integer(commonWeb.getExam().get(rowIndex).getExamClassN());
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {}
		};
		
		examTable = new JTable(examTableModel);
		JScrollPane examTableScrollPane = new JScrollPane(examTable);
		examTableScrollPane.setMinimumSize(new Dimension(300,200));
		add(examTableScrollPane,c);
		
		//график экзаменационной выборки
		c.gridx = 1;
//		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		examGraphDataset = new XYSeriesCollection();
		JFreeChart examChart = ChartFactory.createScatterPlot("exam samples", "", "", examGraphDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel examChartPanel = new ChartPanel(examChart);
		examChartPanel.setHorizontalAxisTrace(true);
		examChartPanel.setVerticalAxisTrace(true);
		add(examChartPanel,c);

	}
	
	private void refreshStudyGraphData() {
		studyGraphDataset.removeAllSeries();
		Hashtable<Integer, ArrayList<MDObject>> classedObjects =  commonWeb.getStudy().objectsGroupedByClass();
		Enumeration<Integer> enum_obj = classedObjects.keys();
		while(enum_obj.hasMoreElements()){
			Integer index = enum_obj.nextElement();
			ArrayList<MDObject> objects = classedObjects.get(index);
			XYSeries series = new XYSeries("class "+index.toString());
			final int objects_size = objects.size();
			for(int i=0; i<objects_size; i++)
				series.add(objects.get(i).getParams()[0], objects.get(i).getParams()[1]);
			studyGraphDataset.addSeries(series);
			
		}
	}

	private void refreshExamGraphData() {
		examGraphDataset.removeAllSeries();
		Hashtable<Integer, ArrayList<MDObject>> classedObjects =  commonWeb.getExam().objectsGroupedByClass();
		Enumeration<Integer> enum_obj = classedObjects.keys();
		ArrayList<MDObject> errors = new ArrayList<MDObject>();
		while(enum_obj.hasMoreElements()){
			Integer index = enum_obj.nextElement();
			ArrayList<MDObject> objects = classedObjects.get(index);
			XYSeries series = new XYSeries("class "+index.toString());
			final int objects_size = objects.size();
			for(int i=0; i<objects_size; i++) {
				MDObject obj = objects.get(i);
				if (obj.getClassN() == obj.getExamClassN())
					series.add(objects.get(i).getParams()[0], objects.get(i).getParams()[1]);
				else
					errors.add(obj);
			}
			examGraphDataset.addSeries(series);
		}
		XYSeries error_serie = new XYSeries("errors");
		final int errors_size = errors.size();
		for(int i=0; i<errors_size; i++){
			MDObject err_obj = errors.get(i);
			error_serie.add(err_obj.getParams()[0], err_obj.getParams()[1]);
		}
		examGraphDataset.addSeries(error_serie);
	}
}
