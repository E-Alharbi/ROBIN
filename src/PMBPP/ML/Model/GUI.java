package PMBPP.ML.Model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import PMBPP.Data.Preparation.Features;
import PMBPP.Data.Preparation.GetFeatures;
import PMBPP.Log.Log;
import PMBPP.Utilities.MTZReader;

public class GUI {
	static String MTZ = "";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
		//Parameters.setLoadAllMLModelsAtOnce("T");
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		JFrame frame = new JFrame("PMBPP");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		JLabel Mtzlabel = new JLabel("MTZ");
		Mtzlabel.setBounds(5, 6, 30, 40);
		panel.add(Mtzlabel);
		JTextField MatPath = new JTextField(16);
		MatPath.setBounds(50, 10, 380, 30);
		MatPath.setEnabled(false);
		panel.add(MatPath);
		// JPanel panel = new JPanel(new GridLayout(12,12,12,12));
		JLabel Reso = new JLabel();
		Reso.setBounds(10, 30, 70, 50);
		Reso.setText("Resolution");
		panel.add(Reso);

		JTextField ResoVal = new JTextField(16);
		ResoVal.setBounds(80, 40, 70, 30);
		ResoVal.setText("");
		panel.add(ResoVal);

		JLabel RMSD = new JLabel();
		RMSD.setBounds(160, 30, 70, 50);
		RMSD.setText("RMSD");
		panel.add(RMSD);

		JTextField RMSDVal = new JTextField(16);
		RMSDVal.setBounds(200, 40, 70, 30);
		RMSDVal.setText("");
		panel.add(RMSDVal);

		JLabel Skew = new JLabel();
		Skew.setBounds(290, 30, 70, 50);
		Skew.setText("Skew");
		panel.add(Skew);

		JTextField SkewVal = new JTextField(16);
		SkewVal.setBounds(320, 40, 70, 30);
		SkewVal.setText("");
		panel.add(SkewVal);

		JLabel Min = new JLabel();
		Min.setBounds(390, 30, 70, 50);
		Min.setText("Min");
		panel.add(Min);

		JTextField MinVal = new JTextField(16);
		MinVal.setBounds(410, 40, 70, 30);
		MinVal.setText("");
		panel.add(MinVal);

		JLabel Max = new JLabel();
		Max.setBounds(480, 30, 70, 50);
		Max.setText("Max");
		panel.add(Max);

		JTextField MaxVal = new JTextField(16);
		MaxVal.setBounds(510, 40, 70, 30);
		MaxVal.setText("");
		panel.add(MaxVal);

		
		JComboBox<String> Phases = new JComboBox<String>();
		Phases.setBounds(10, 50, 250, 70);
		
		panel.add(Phases);
		
		
		JComboBox<String> FP = new JComboBox<String>();
		FP.setBounds(260, 50, 100, 70);
		
		panel.add(FP);
		
		JComboBox<String> SIGFP = new JComboBox<String>();
		SIGFP.setBounds(370, 50, 100, 70);
		
		panel.add(SIGFP);
		
		
		
		panel.setBounds(40, 80, 200, 200);
		JButton button = new JButton("Choose");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File(MTZ).getAbsoluteFile());
				int result = chooser.showOpenDialog(null);
				if (JFileChooser.APPROVE_OPTION == result) {
					File file = chooser.getSelectedFile();
					MTZ = file.getAbsolutePath();
					
					System.out.println(file.getAbsolutePath());
					MatPath.setText(MTZ);
					Vector<String> P=new Vector<String>();
					Vector<String> FPV=new Vector<String>();
					Vector<String> SIGFPV=new Vector<String>();
					try {
						P = new MTZReader(MTZ).GetColLabels().get("A");
						FPV = new MTZReader(MTZ).GetColLabels().get("F");
						SIGFPV = new MTZReader(MTZ).GetColLabels().get("Q");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Phases.removeAllItems();
					for(int i=0 ; i < P.size();++i) {
						
						Phases.addItem(P.get(i));
					}
					FP.removeAllItems();
					for(int i=0 ; i < FPV.size();++i) {
						FP.addItem(FPV.get(i));
					}
					SIGFP.removeAllItems();
					for(int i=0 ; i < SIGFPV.size();++i) {
						SIGFP.addItem(SIGFPV.get(i));
					}
				}
			}
		});
		button.setBounds(430, 10, 80, 30);
		// button.setPreferredSize(new Dimension(40,50));
		panel.add(button);
		
		
		
		
		// frame.getContentPane().add(panel); // Adds Button to content pane of frame

		button = new JButton("Predict");

		JTextArea jt = new JTextArea(100, 100);
		String[] columnNames = { "Pipeline", "R-free", "R-work", "Completeness" };
		//DefaultTableModel model = new DefaultTableModel(null, columnNames);
		
		DefaultTableModel model = new DefaultTableModel(null, columnNames) {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};

		
		
		
		JTable j = new JTable(model);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("MTZ " + MTZ);
				Parameters.setAttCSV( "/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/target/classes/att.csv");
				Parameters.setPhases(Phases.getSelectedItem().toString());
				Parameters.setColinfo(FP.getSelectedItem().toString()+","+SIGFP.getSelectedItem().toString());
				String[] arg = { MTZ };
				try {
					Parameters.setUsecfft ( true);
					Predict Pre = new Predict();
					Pre.PredictMultipleModles(arg);
					 Pre.Print(Pre.PipelinesPredictions);
					jt.setText(Pre.PredictionTable);
					DefaultTableModel model1 = new DefaultTableModel(Pre.RowData, columnNames){

					    @Override
					    public boolean isCellEditable(int row, int column) {
					       //all cells false
					       return false;
					    }
					};
					
					j.setModel(model1);
					Features cfftM = new GetFeatures().Get(MTZ);
					ResoVal.setText(String.valueOf(cfftM.Resolution));
					RMSDVal.setText(String.valueOf(cfftM.RMSD));
					SkewVal.setText(String.valueOf(cfftM.Skew));
					MinVal.setText(String.valueOf(cfftM.Min));
					MaxVal.setText(String.valueOf(cfftM.Max));

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		j.setBounds(10, 200, 400, 400);
		button.setBounds(500, 10, 80, 30);
		panel.add(button);

		// adding it to JScrollPane
		JScrollPane sp = new JScrollPane(j);
		sp.setBounds(10, 100, 550, 400);
		panel.add(sp);

		/*
		button = new JButton("Predict again");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("MTZ " + MTZ);
				Parameters.setAttCSV( "/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/target/classes/att.csv");
				String[] arg = { MTZ };
				try {
					Parameters.setUsecfft (false);
					Parameters.setInstanceValue1 ( new double[] { Double.parseDouble(RMSDVal.getText()),
							Double.parseDouble(SkewVal.getText()), Double.parseDouble(ResoVal.getText()),
							Double.parseDouble(MaxVal.getText()), Double.parseDouble(MinVal.getText()) });
					Predict Pre = new Predict();
					Pre.main(arg);
					System.out.println("Pre " + Pre.PredictionTable);
					jt.setText(Pre.PredictionTable);
					DefaultTableModel model1 = new DefaultTableModel(Pre.RowData, columnNames);

					j.setModel(model1);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		button.setBounds(20, 70, 110, 30);
		panel.add(button);
		*/
		
		
		frame.add(panel);
		// frame.add(panel2);
		// frame.getContentPane().add(panel); // Adds Button to content pane of frame
		// frame.getContentPane().add(panel2);
		frame.setVisible(true);

	}
}
