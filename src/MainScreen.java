import gov.nih.nci.ncicb.xmiinout.handler.XmiException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.jdesktop.swingx.prompt.PromptSupport;

import orbital.logic.imp.Formula;
import orbital.logic.sign.ParseException;

class MainScreen extends JFrame {
	private JTabbedPane tabbedPane;
	private JPanel mainScreen;
	private JPanel condition_consequence;
	private JPanel artifact;
	private List<Artifact> artifactList = new ArrayList<Artifact>();
	private JPanel topPanel;
	private InputAttribute inputScreen1;
	private InputState inputSceen2;
	private InputDefined inputScreen3;
	// screen 1
	private JTextField textFilePath;
	private List<int[]> pathList;
	private List<BusinessTask> BTList;
	private JComboBox<BusinessTask> businessTaskCB;
	private JLabel lblBusinessTask;
	private JLabel lblPreCondition;
	private JLabel lblPostCondition;
	private JLabel lblService;
	private JTextArea textPreCondition;
	private JTextArea textPostCondition;
	private JTextField textService;
	private JButton btnSubmit;
	private String tableHeader1[] = { "Attribute", "Operator", "Value" };
	private String tableHeader2[] = { "State" };
	private String tableHeader3[] = { "Is Defined" };
	private DefaultTableModel preAttrModel;
	private JTable preAttrTable;
	private DefaultTableModel postAttrModel;
	private JTable postAttrTable;
	private DefaultTableModel preStateModel;
	private JTable preStateTable;
	private DefaultTableModel postStateModel;
	private JTable postStateTable;
	private DefaultTableModel preDefinedModel;
	private JTable preDefinedTable;
	private DefaultTableModel postDefinedModel;
	private JTable postDefinedTable;
	private JButton preStateAddBtn;
	private JButton preAttrAddBtn;
	private JButton postStateAddBtn;
	private JButton postAttrAddBtn;
	private JButton preStateDelBtn;
	private JButton preAttrDelBtn;
	private JButton postStateDelBtn;
	private JButton postAttrDelBtn;
	private JButton preDefinedAddBtn;
	private JButton preDefinedDelBtn;
	private JButton postDefinedAddBtn;
	private JButton postDefinedDelBtn;
	// screen 2
	private JTextArea textCondStr;
	private JTextArea textConsqStr;
	private JLabel lblCondStr;
	private JLabel lblConsqStr;

	// screen 3
	private JButton classDiagramBtn;
	private JTextField classDiagramPath;
	private DefaultTableModel artifactsModel;
	private JTable artifactsTable;

	void setPanelEnabled(JPanel panel, Boolean isEnabled) {
		panel.setEnabled(isEnabled);

		Component[] components = panel.getComponents();

		for (int i = 0; i < components.length; i++) {
			if (components[i].getClass().getName() == "javax.swing.JPanel") {
				setPanelEnabled((JPanel) components[i], isEnabled);
			}

			components[i].setEnabled(isEnabled);
		}
	}

	public MainScreen() {

		setTitle("NCKH");
		setSize(1024, 768);
		setBackground(Color.gray);

		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);

		// Create the tab pages
		createMainScreen();
		createConScreen();
		createArtifactScreen();

		// Create a tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Main", mainScreen);
		tabbedPane.addTab("Condition & Consequence", condition_consequence);
		tabbedPane.addTab("Artifact", artifact);
		topPanel.add(tabbedPane, BorderLayout.CENTER);
	}

	public void createMainScreen() {

		mainScreen = new JPanel();
		pathList = new ArrayList<int[]>();
		BTList = new ArrayList<BusinessTask>();
		mainScreen.setLayout(null);

		JButton btnReadFile = new JButton("Read BPMN");
		btnReadFile.setBounds(20, 30, 100, 30);
		btnReadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser bpmnFile = new JFileChooser();
				FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(".bpmn", "bpmn");
				bpmnFile.setFileFilter(fileFilter);
				int returnVal = bpmnFile.showOpenDialog(mainScreen);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = bpmnFile.getSelectedFile();
					textFilePath.setText(file.getName());
					BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
					Collection<Task> tempListTask = modelInstance.getModelElementsByType(Task.class);
					// Remove All element in businessTaskCB and Table
					businessTaskCB.removeAllItems();

					try {
						for (Task i : tempListTask) {

							BusinessTask n = new BusinessTask();
							n.name = i.getName();
							BTList.add(n);
							businessTaskCB.addItem(n);
						}
						pathList = BusinessProcessRepository.getListPath(file);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mainScreen.validate();
			}
		});
		mainScreen.add(btnReadFile);

		textFilePath = new JTextField();
		textFilePath.setBounds(150, 30, 320, 30);
		mainScreen.add(textFilePath);
		textFilePath.setColumns(10);

		lblBusinessTask = new JLabel("Business Task:");
		lblBusinessTask.setBounds(500, 30, 150, 30);
		mainScreen.add(lblBusinessTask);

		businessTaskCB = new JComboBox<BusinessTask>();
		businessTaskCB.setBounds(600, 30, 350, 30);
		mainScreen.add(businessTaskCB);

		// Attribute Table
		preAttrModel = new DefaultTableModel(tableHeader1, 0);
		preAttrTable = new JTable(preAttrModel);
		JScrollPane scrollpane1 = new JScrollPane(preAttrTable);
		scrollpane1.setBounds(20, 80, 450, 100);
		mainScreen.add(scrollpane1);

		preAttrAddBtn = new JButton("Add");
		preAttrAddBtn.setBounds(20, 190, 100, 30);

		preAttrAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Add new Attr Precond
				if (artifactList == null || artifactList.isEmpty() || businessTaskCB.getItemCount() == 0) {
					JOptionPane.showMessageDialog(MainScreen.this, "Please Input Artifacts and Business Tasks.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				inputScreen1 = new InputAttribute(artifactList, preAttrModel, textPreCondition);
				inputScreen1.run();
			}
		});
		mainScreen.add(preAttrAddBtn);

		preAttrDelBtn = new JButton("Delete");
		preAttrDelBtn.setBounds(130, 190, 100, 30);
		preAttrDelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (preAttrTable.getSelectedRow() >= 0) {
					preAttrModel.removeRow(preAttrTable.getSelectedRow());
				}
			}
		});
		mainScreen.add(preAttrDelBtn);

		postAttrModel = new DefaultTableModel(tableHeader1, 0);
		postAttrTable = new JTable(postAttrModel);
		JScrollPane scrollpane2 = new JScrollPane(postAttrTable);
		scrollpane2.setBounds(500, 80, 450, 100);
		mainScreen.add(scrollpane2);

		postAttrAddBtn = new JButton("Add");
		postAttrAddBtn.setBounds(500, 190, 100, 30);
		postAttrAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Add new Attr Postcond
				if (artifactList == null || artifactList.isEmpty() || businessTaskCB.getItemCount() == 0) {
					JOptionPane.showMessageDialog(MainScreen.this, "Please Input Artifacts and Business Tasks.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				inputScreen1 = new InputAttribute(artifactList, postAttrModel, textPostCondition);
				inputScreen1.run();
			}
		});
		mainScreen.add(postAttrAddBtn);

		postAttrDelBtn = new JButton("Delete");
		postAttrDelBtn.setBounds(610, 190, 100, 30);
		postAttrDelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (postAttrTable.getSelectedRow() >= 0) {
					postAttrModel.removeRow(postAttrTable.getSelectedRow());
				}
			}
		});
		mainScreen.add(postAttrDelBtn);

		// State Table
		preStateModel = new DefaultTableModel(tableHeader2, 0);
		preStateTable = new JTable(preStateModel);
		JScrollPane scrollpane3 = new JScrollPane(preStateTable);
		scrollpane3.setBounds(20, 250, 225, 100);
		mainScreen.add(scrollpane3);

		preStateAddBtn = new JButton("Add");
		preStateAddBtn.setBounds(20, 360, 100, 30);
		preStateAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Add new State Precond
				if (artifactList == null || artifactList.isEmpty() || businessTaskCB.getItemCount() == 0) {
					JOptionPane.showMessageDialog(MainScreen.this, "Please Input Artifacts and Business Tasks.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				inputSceen2 = new InputState(artifactList, preStateModel, textPreCondition);
				inputSceen2.run();
			}
		});
		mainScreen.add(preStateAddBtn);

		preStateDelBtn = new JButton("Delete");
		preStateDelBtn.setBounds(130, 360, 100, 30);
		preStateDelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (preStateTable.getSelectedRow() >= 0) {
					preStateModel.removeRow(preStateTable.getSelectedRow());
				}
			}
		});
		mainScreen.add(preStateDelBtn);

		postStateModel = new DefaultTableModel(tableHeader2, 0);
		postStateTable = new JTable(postStateModel);
		JScrollPane scrollpane4 = new JScrollPane(postStateTable);
		scrollpane4.setBounds(500, 250, 225, 100);
		mainScreen.add(scrollpane4);

		postStateAddBtn = new JButton("Add");
		postStateAddBtn.setBounds(500, 360, 100, 30);
		postStateAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Add new State Postcond
				if (artifactList == null || artifactList.isEmpty() || businessTaskCB.getItemCount() == 0) {
					JOptionPane.showMessageDialog(MainScreen.this, "Please Input Artifacts and Business Tasks.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				inputSceen2 = new InputState(artifactList, postStateModel, textPostCondition);
				inputSceen2.run();
			}
		});
		mainScreen.add(postStateAddBtn);

		postStateDelBtn = new JButton("Delete");
		postStateDelBtn.setBounds(610, 360, 100, 30);
		postStateDelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (postStateTable.getSelectedRow() >= 0) {
					postStateModel.removeRow(postStateTable.getSelectedRow());
				}
			}
		});
		mainScreen.add(postStateDelBtn);

		// Defined Table
		preDefinedModel = new DefaultTableModel(tableHeader3, 0);
		preDefinedTable = new JTable(preDefinedModel);
		JScrollPane scrollpane5 = new JScrollPane(preDefinedTable);
		scrollpane5.setBounds(245, 250, 225, 100);
		mainScreen.add(scrollpane5);

		preDefinedAddBtn = new JButton("Add");
		preDefinedAddBtn.setBounds(245, 360, 100, 30);
		preDefinedAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Add new Defined Precond
				if (artifactList == null || artifactList.isEmpty() || businessTaskCB.getItemCount() == 0) {
					JOptionPane.showMessageDialog(MainScreen.this, "Please Input Artifacts and Business Tasks.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				inputScreen3 = new InputDefined(artifactList, preDefinedModel, textPreCondition);
				inputScreen3.run();
			}
		});
		mainScreen.add(preDefinedAddBtn);

		preDefinedDelBtn = new JButton("Delete");
		preDefinedDelBtn.setBounds(355, 360, 100, 30);
		preDefinedDelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (preDefinedTable.getSelectedRow() >= 0) {
					preDefinedModel.removeRow(preDefinedTable.getSelectedRow());
				}
			}
		});
		mainScreen.add(preDefinedDelBtn);

		postDefinedModel = new DefaultTableModel(tableHeader3, 0);
		postDefinedTable = new JTable(postDefinedModel);
		JScrollPane scrollpane6 = new JScrollPane(postDefinedTable);
		scrollpane6.setBounds(725, 250, 225, 100);
		mainScreen.add(scrollpane6);

		postDefinedAddBtn = new JButton("Add");
		postDefinedAddBtn.setBounds(725, 360, 100, 30);
		postDefinedAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Add new State Postcond
				if (artifactList == null || artifactList.isEmpty() || businessTaskCB.getItemCount() == 0) {
					JOptionPane.showMessageDialog(MainScreen.this, "Please Input Artifacts and Business Tasks.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				inputScreen3 = new InputDefined(artifactList, postDefinedModel, textPostCondition);
				inputScreen3.run();
			}
		});
		mainScreen.add(postDefinedAddBtn);

		postDefinedDelBtn = new JButton("Delete");
		postDefinedDelBtn.setBounds(835, 360, 100, 30);
		postDefinedDelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (postDefinedTable.getSelectedRow() >= 0) {
					postDefinedModel.removeRow(postDefinedTable.getSelectedRow());
				}
			}
		});
		mainScreen.add(postDefinedDelBtn);

		// Result
		lblPreCondition = new JLabel("Pre Condition :");
		lblPreCondition.setBounds(20, 430, 150, 30);
		mainScreen.add(lblPreCondition);

		lblPostCondition = new JLabel("Post Condition :");
		lblPostCondition.setBounds(500, 430, 150, 30);
		mainScreen.add(lblPostCondition);

		lblService = new JLabel("Service : ");
		lblService.setBounds(20, 530, 150, 30);
		mainScreen.add(lblService);

		textPreCondition = new JTextArea();
		textPreCondition.setEditable(false);
		textPreCondition.setLineWrap(true);
		textPreCondition.setWrapStyleWord(true);
		JScrollPane spPreCondtion = new JScrollPane(textPreCondition);
		spPreCondtion.setBounds(120, 430, 350, 90);
		mainScreen.add(spPreCondtion);
		textPreCondition.setColumns(10);

		textPostCondition = new JTextArea();
		textPostCondition.setEditable(false);
		textPostCondition.setLineWrap(true);
		textPostCondition.setWrapStyleWord(true);
		JScrollPane spPostCondtion = new JScrollPane(textPostCondition);
		spPostCondtion.setBounds(600, 430, 350, 90);
		textPostCondition.setColumns(10);
		mainScreen.add(spPostCondtion);

		textService = new JTextField();
		textService.setBounds(120, 530, 500, 50);
		textService.setColumns(10);
		mainScreen.add(textService);

		btnSubmit = new JButton("Submit");
		btnSubmit.setBounds(20, 600, 100, 30);
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BusinessTask sBT = (BusinessTask) businessTaskCB.getSelectedItem();
				try {
					sBT.preCondition = (Formula) Algorithm.logic.createExpression(textPreCondition.getText());
					String[] preCondList = textPreCondition.getText().split(" & ");
					for (int i = 0; i < preCondList.length; i++) {
						sBT.addLiteral2Pre(preCondList[i]);
					}
					sBT.postCondition = (Formula) Algorithm.logic.createExpression(textPostCondition.getText());
					String[] postCondList = textPostCondition.getText().split(" & ");
					for (int i = 0; i < postCondList.length; i++) {
						sBT.addLiteral2Post(postCondList[i]);
					}

					// postComma and postInstance not done yet

					sBT.service = textService.getText();
					// TODO: related artifacts of task
					// sBT.artifacts = textRelatedArtifact.getText();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mainScreen.add(btnSubmit);
		preAttrTable.setDefaultEditor(Object.class, null);
		preDefinedTable.setDefaultEditor(Object.class, null);
		preStateTable.setDefaultEditor(Object.class, null);
		postAttrTable.setDefaultEditor(Object.class, null);
		postDefinedTable.setDefaultEditor(Object.class, null);
		postStateTable.setDefaultEditor(Object.class, null);
	}

	public void createConScreen() {
		condition_consequence = new JPanel();
		condition_consequence.setLayout(null);

		lblCondStr = new JLabel("Condition String :");
		lblCondStr.setBounds(20, 130, 150, 30);
		condition_consequence.add(lblCondStr);

		lblConsqStr = new JLabel("Consequence String :");
		lblConsqStr.setBounds(20, 330, 150, 30);
		condition_consequence.add(lblConsqStr);

		textCondStr = new JTextArea();
		textCondStr.setBounds(150, 130, 500, 90);
		condition_consequence.add(textCondStr);
		textCondStr.setColumns(10);
		PromptSupport.setPrompt("condition1 & condition2 & condition3..........", textCondStr);

		textConsqStr = new JTextArea();
		textConsqStr.setBounds(150, 330, 500, 90);
		textConsqStr.setColumns(10);
		condition_consequence.add(textConsqStr);
	}

	public void createArtifactScreen() {
		artifact = new JPanel();
		artifact.setLayout(null);
		classDiagramBtn = new JButton("Read Class Diagram");
		classDiagramBtn.setBounds(20, 30, 200, 30);
		classDiagramBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser classDiagramFile = new JFileChooser();
				FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(".xmi", "xmi");
				classDiagramFile.setFileFilter(fileFilter);
				int returnVal = classDiagramFile.showOpenDialog(artifact);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = classDiagramFile.getSelectedFile();
					classDiagramPath.setText(file.getAbsolutePath());

					try {
						artifactList = (List<Artifact>) ArtifactRepository.getListArtifact(file.getAbsolutePath());
						for (Artifact i : artifactList) {
							String attributeList = "";
							String stateList = "";
							for (ArtifactAttribute j : i.getListAttribute()) {
								attributeList += j.toString() + ",";
							}
							for (ArtifactState j : i.getListState()) {
								stateList += j.toString() + ",";
							}

							String temp[] = { i.getName(), attributeList, stateList };
							artifactsModel.addRow(temp);
						}
					} catch (XmiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		artifact.add(classDiagramBtn);

		classDiagramPath = new JTextField();
		classDiagramPath.setBounds(300, 30, 320, 30);
		artifact.add(classDiagramPath);
		classDiagramPath.setColumns(10);
		String header[] = { "Name", "Attributes", "States" };
		artifactsModel = new DefaultTableModel(header, 0);
		artifactsTable = new JTable(artifactsModel);
		artifactsTable.setEnabled(false);
		JScrollPane scrollpane = new JScrollPane(artifactsTable);
		scrollpane.setBounds(150, 100, 700, 500);
		artifact.add(scrollpane);

	}

	// Main method to get things started
	public static void main(String args[]) {
		// Create an instance of the test application
		MainScreen mainFrame = new MainScreen();
		mainFrame.setVisible(true);
	}
}