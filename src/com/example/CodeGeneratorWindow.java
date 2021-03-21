package com.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;


public class CodeGeneratorWindow {
	
	public static String CodeGeneratorVersion = "v1.0";
	
	public static JFrame mainframe;

	public static JCheckBox dataCodeAutoUpdateCheckBox;
	public static JCheckBox showErrorDialogsCheckBox;
	
	private static JSlider dataCodeSizeSlider;
	
	public static Choice errorCorrectionLevelChoice;
	public static Choice codeFormatChoice;
	private static JLabel errorCorrectionLevelLabel;
	
	private static JTextPane dataCodeLogTextPane;
	private static JTextPane dataCodeInputTextPane;
	
	private static JPanel dataCodeShowPanel;
	private static JPanel dataCodePicPanel;
	private static JTabbedPane dataCodeTabbedPane;
	
	private static JSplitPane horizontalSplitPane;

	public static Preferences preferences = Preferences.userNodeForPackage(com.example.CodeGeneratorWindow.class);
	final static String CODE_FORMAT_KEY = "CODE_FORMAT_KEY";
	final static String ERROR_CORRECTION_KEY = "ERROR_CORRECTION_KEY";
	
	public static void main(String[] args){
		
		mainframe = new JFrame("GAM Code Generator");
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel dataCodePanel = new JPanel(new BorderLayout());
		JPanel settingsPanel = new JPanel();
		
		JPanel dataCodeControlPanel = new JPanel();
		dataCodeLogTextPane = new JTextPane();
		JScrollPane dataCodeLogScrollPane = new JScrollPane(dataCodeLogTextPane);
		dataCodeTabbedPane = new JTabbedPane();

		dataCodeShowPanel = new JPanel();
		dataCodeShowPanel.setBorder(new EmptyBorder(30, 20, 20, 20));
		dataCodePicPanel = new JPanel();
		
		dataCodeInputTextPane = new JTextPane();
		JScrollPane textDataCodeScrollPane = new JScrollPane(dataCodeInputTextPane);
		dataCodeInputTextPane.setMinimumSize(new Dimension(50,50));
		JPanel textDataCodeInputPanel = new JPanel(new BorderLayout());
		
		JButton dataCodeGenerateButton = new JButton("Generate Code");
		JButton dataCodeExportDataButton = new JButton("Export Data");
		JButton dataCodeImportDataButton = new JButton("Import Data");
		JButton dataCodeImportImageDataButton = new JButton("Import Data from Code");
		
		dataCodeSizeSlider = new JSlider(200, 1000, 350);
		dataCodeSizeSlider.setOrientation(JSlider.HORIZONTAL);

		errorCorrectionLevelLabel = new JLabel("Error Correction:");
		JLabel codeFormatLabel = new JLabel("Code Format:");

		setupChoices();
		
		try {
		      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) { 
			System.err.println("Error: " + e.getMessage()); 
		}
		
		dataCodeLogTextPane.setMargin(new Insets(5, 5, 5, 5));
		dataCodeLogTextPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		dataCodeLogScrollPane.setPreferredSize(new Dimension(2000, 150));
		dataCodeLogScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
	
		dataCodeGenerateButton.addActionListener(e -> {
			String dataCodeData = getDataCodeData(dataCodeTabbedPane.getSelectedIndex());
			if(dataCodeData.isEmpty()) {
				showDialog("Fields cant be empty!");
			} else {
				generateDataCode(dataCodeData, dataCodePicPanel);
			}
		});
		dataCodeExportDataButton.addActionListener(e -> {
			String filePath = openFileChooser("Specify how to save");
			if(filePath != null) {
				logCodeGen("Export file: " + filePath, Color.BLACK);
				FileHandler.writeToFile(getDataCodeData(dataCodeTabbedPane.getSelectedIndex()), filePath, false);
			}
		});
		dataCodeImportDataButton.addActionListener(e -> {
			String filePath = openFileChooser("Specify file to import");
			if(filePath != null) {
				String dataString = FileHandler.readFromFile(filePath);
				logCodeGen("Load file: " + filePath + "\nData: " + dataString, Color.BLACK);
				setDataCodeData(dataString, dataCodeTabbedPane.getSelectedIndex());
			}
		});
		dataCodeImportImageDataButton.addActionListener(e -> {
			String filePath = openFileChooser("Specify dataCode Code Image to import");
			if(filePath != null) {
				String dataString = DataCodeCodeHandler.readGAMSetupDataCode(filePath);
				logCodeGen("Load file: " + filePath + "\nData: " + dataString, Color.BLACK);
				setDataCodeData(dataString, dataCodeTabbedPane.getSelectedIndex());
			}
		});
		dataCodeSizeSlider.addChangeListener(e -> {
			dataCodePicPanel.setPreferredSize(new Dimension(dataCodeSizeSlider.getValue(),dataCodeShowPanel.getHeight()));
			horizontalSplitPane.setDividerLocation((int)mainframe.getSize().getWidth() - dataCodeSizeSlider.getValue());
			dataCodeUpdater();
		});
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem savePicItem = new JMenuItem("save");
		savePicItem.addActionListener(e -> {
			String filePath = openFileChooser("Specify how to save");
			if(filePath != null) {
				logCodeGen("Save as file: " + filePath, Color.BLACK);
				copyImageFileTo(filePath);
			}
		});
		popupMenu.add(savePicItem);
		dataCodePicPanel.setComponentPopupMenu(popupMenu);
		
		
		dataCodeInputTextPane.getDocument().addDocumentListener(getdataCodeUpdateDocumentListener());
		
		dataCodeAutoUpdateCheckBox = new JCheckBox("Auto Update");
		dataCodeAutoUpdateCheckBox.setSelected(true);
		showErrorDialogsCheckBox = new JCheckBox("Error Dialogs");
		showErrorDialogsCheckBox.setSelected(false);

		
		textDataCodeInputPanel.add(textDataCodeScrollPane, BorderLayout.CENTER);
		
		dataCodeControlPanel.add(dataCodeAutoUpdateCheckBox);
		dataCodeControlPanel.add(showErrorDialogsCheckBox);
		
		dataCodeControlPanel.add(dataCodeGenerateButton);
		dataCodeControlPanel.add(dataCodeExportDataButton);
		dataCodeControlPanel.add(dataCodeImportDataButton);
		dataCodeControlPanel.add(dataCodeImportImageDataButton);
		dataCodeControlPanel.add(dataCodeSizeSlider);

		dataCodeTabbedPane.addTab("Text", null, textDataCodeInputPanel,
	  			  "Plain Text Code Generator");
		dataCodeTabbedPane.addChangeListener(e -> dataCodeUpdater());
		
		dataCodeShowPanel.add(dataCodePicPanel);

		settingsPanel.add(codeFormatLabel);
		settingsPanel.add(codeFormatChoice);
		settingsPanel.add(errorCorrectionLevelLabel);
		settingsPanel.add(errorCorrectionLevelChoice);
		
		JPanel dataCodeNorthPanel = new JPanel();
		dataCodeNorthPanel.setLayout(new BoxLayout(dataCodeNorthPanel, BoxLayout.Y_AXIS));
		dataCodeNorthPanel.add(dataCodeControlPanel);
		dataCodeNorthPanel.add(settingsPanel);
		
		
		
		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataCodeTabbedPane, dataCodeShowPanel);
		horizontalSplitPane.setResizeWeight(0.3);
		
		horizontalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, pce -> {
			//int width = (int) (mainframe.getSize().getWidth() - horizontalSplitPane.getDividerLocation());
			dataCodeUpdater();
		});

		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, dataCodeLogScrollPane);
		verticalSplitPane.setDividerLocation(625);
		verticalSplitPane.setResizeWeight(0);
		
		dataCodePanel.add(dataCodeNorthPanel, BorderLayout.NORTH);
		dataCodePanel.add(verticalSplitPane, BorderLayout.CENTER);
		
		mainframe.getContentPane().add(dataCodePanel);
		mainframe.setSize(1200, 800);
		mainframe.setVisible(true);
		

		dataCodeLogTextPane.setText("Code Generator Version: " + CodeGeneratorVersion);
	}
	
	
	private static void generateDataCode(String dataString, JPanel dataCodePicPanel) {
		try {
			if(!dataString.isEmpty()) {
				BufferedImage currentDataCodeCodeImage = DataCodeCodeHandler.generateGAMSetupDataCode(dataString, null, dataCodeSizeSlider.getValue(), dataCodeSizeSlider.getValue());
				dataCodePicPanel.removeAll();
				if(currentDataCodeCodeImage != null) {
					dataCodePicPanel.add(new JLabel(new ImageIcon(currentDataCodeCodeImage)));
				}
			} else {
				dataCodePicPanel.removeAll();
			}
			dataCodePicPanel.updateUI();
		} catch (Exception e) {
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
		}
	}

	protected static String openFileChooser(String dialogTitle) {
		try {
			JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
			fileChooser.setDialogTitle(dialogTitle);   
			 
			int userSelection = fileChooser.showSaveDialog(mainframe);
			 
			if (userSelection == JFileChooser.APPROVE_OPTION) {
			    File fileToSave = fileChooser.getSelectedFile();
			    return fileToSave.getAbsolutePath();
			}
		} catch (Exception e) {
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
		}
		return null;
	}

	private static void copyImageFileTo(String absoluteDestinationPath) {
		try {
			String fileExtension = "png";
			String[] pathSubstrings = absoluteDestinationPath.split("\\.");
			if(pathSubstrings.length >= 2) {
				fileExtension = pathSubstrings[1];
			} else {
				absoluteDestinationPath += "." + fileExtension;
			}
			JLabel jLabel = (JLabel) dataCodePicPanel.getComponent(0);
			ImageIcon imageIcon = (ImageIcon) jLabel.getIcon();
			BufferedImage bufferedImage = (BufferedImage) imageIcon.getImage();
			File dest = new File(absoluteDestinationPath);

			ImageIO.write(bufferedImage, fileExtension, dest);

		} catch (Exception e) {
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
		}
	}

	private static void setupChoices() {
		
		codeFormatChoice = new Choice();
		codeFormatChoice.add("QR Code (2D)");
		codeFormatChoice.add("PDF417 (2D)");
		codeFormatChoice.add("AZTEC (2D)");
		codeFormatChoice.add("Data Matrix (2D)");
		codeFormatChoice.add("CODE 128 (1D / upto 80 Characters)");
		codeFormatChoice.add("Code 39 (1D / upto 80 Digits (40 Characters))");
		codeFormatChoice.add("Code 93 (1D / upto 80 Digits (40 Characters))");

		codeFormatChoice.add("CODABAR (1D / NUMERIC)");
		codeFormatChoice.add("UPC-A (1D / 12 Digits)");
		codeFormatChoice.add("UPC-E (1D / 7 Digits)");
		codeFormatChoice.add("ITF (1D / upto 80 Digits / even count)");
		codeFormatChoice.add("EAN-13 (1D / 12 Digits)");
		codeFormatChoice.add("EAN-8 (1D / 7 Digits)");
		// no encoder for:
		// MaxiCode (2D)
		// RSS 14
		// RSS EXPANDED
		// UPC/EAN extension
		String codeFormatPreference = preferences.get(CODE_FORMAT_KEY, "QR Code (2D)");
		codeFormatChoice.select(codeFormatPreference);
		
		errorCorrectionLevelChoice = new Choice();
		errorCorrectionLevelChoice.setPreferredSize(new Dimension(100,10));
		setErrorCorrectionLevels(codeFormatPreference);
		String errorCorrectionLevelPreference = preferences.get(ERROR_CORRECTION_KEY, "L (~7%)");
		errorCorrectionLevelChoice.select(errorCorrectionLevelPreference);
		
		codeFormatChoice.addItemListener(e -> {
			preferences.put(CODE_FORMAT_KEY, codeFormatChoice.getSelectedItem());
			dataCodeAutoUpdater();
			setErrorCorrectionLevels(codeFormatChoice.getSelectedItem());
		});
		errorCorrectionLevelChoice.addItemListener(e -> {
			preferences.put(ERROR_CORRECTION_KEY, errorCorrectionLevelChoice.getSelectedItem());
			dataCodeAutoUpdater();
		});
	}
	
	private static void setErrorCorrectionLevels(String codeFormat) {
		if(codeFormat.contains("QR Code")) {
			errorCorrectionLevelLabel.setVisible(true);
			errorCorrectionLevelChoice.setVisible(true);
			errorCorrectionLevelChoice.removeAll();
			errorCorrectionLevelChoice.add("L (~7%)");
			errorCorrectionLevelChoice.add("M (~15%)");
			errorCorrectionLevelChoice.add("Q (~25%)");
			errorCorrectionLevelChoice.add("H (~30%)");
		} else if (codeFormat.contains("PDF417")) {
			errorCorrectionLevelLabel.setVisible(true);
			errorCorrectionLevelChoice.setVisible(true);
			errorCorrectionLevelChoice.removeAll();
			errorCorrectionLevelChoice.add("0 (ECL0)");
			errorCorrectionLevelChoice.add("1 (ECL1)");
			errorCorrectionLevelChoice.add("2 (ECL2)");
			errorCorrectionLevelChoice.add("3 (ECL3)");
			errorCorrectionLevelChoice.add("4 (ECL4)");
			errorCorrectionLevelChoice.add("5 (ECL5)");
			errorCorrectionLevelChoice.add("6 (ECL6)");
			errorCorrectionLevelChoice.add("7 (ECL7)");
			errorCorrectionLevelChoice.add("8 (ECL8)");
		} else if (codeFormat.contains("AZTEC")) {
			errorCorrectionLevelLabel.setVisible(true);
			errorCorrectionLevelChoice.setVisible(true);
			errorCorrectionLevelChoice.removeAll();
			errorCorrectionLevelChoice.add("1 (~25%)");
			errorCorrectionLevelChoice.add("2 (~40%)");
			errorCorrectionLevelChoice.add("3 (~55%)");
			errorCorrectionLevelChoice.add("4 (~70%)");
			errorCorrectionLevelChoice.add("5 (~85%)");
			errorCorrectionLevelChoice.add("6 (~100%)");
		} else {
			errorCorrectionLevelLabel.setVisible(false);
			errorCorrectionLevelChoice.setVisible(false);
		}
	}
	
	private static DocumentListener getdataCodeUpdateDocumentListener() {
		return new DocumentListener() {
			public void removeUpdate(DocumentEvent e) { dataCodeAutoUpdater(); }
			public void insertUpdate(DocumentEvent e) { dataCodeAutoUpdater(); }
			public void changedUpdate(DocumentEvent e) { dataCodeAutoUpdater(); }
		};
	}
	private static void dataCodeUpdater() {
		new Thread(() -> {
			String dataString = getDataCodeData(dataCodeTabbedPane.getSelectedIndex());
			if(!dataString.isEmpty() && dataString.length() <= DataCodeCodeHandler.maxDataCodeDataLength) {
				generateDataCode(dataString, dataCodePicPanel);
			} else {
				dataCodePicPanel.removeAll();
				dataCodePicPanel.updateUI();
			}
		}).start();
	}
	private static void dataCodeAutoUpdater() {
		if(dataCodeAutoUpdateCheckBox.isSelected()) {
			dataCodeUpdater();
		}
	}
	
	private static void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        //aSet = sc.addAttribute(aSet, StyleConstants.FontFamily, "Lucida Console");
        aSet = sc.addAttribute(aSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aSet, false);
        tp.replaceSelection(msg);
    }
	public static void showDialog(String message) {
		if(showErrorDialogsCheckBox.isSelected()) {
			JOptionPane.showMessageDialog(mainframe, message);
		}
	}
	
	private static String getDataCodeData(int selectedTabIndex) {
		String dataCodeData = "";
		switch (selectedTabIndex) {
		case 0:
			dataCodeData = dataCodeInputTextPane.getText();
			break;
		case 1:
			dataCodeData = ""; //TODO add more features
			break;
		default:
			break;
		}
		return dataCodeData;
	}

	private static void setDataCodeData(String dataCodeInputData, int selectedTabIndex) {
		switch (selectedTabIndex) {
		case 0:
			dataCodeInputTextPane.setText(dataCodeInputData.trim());
			break;
		case 1:
			dataCodeInputTextPane.setText(""); //TODO add more features
			break;
		default:
			break;
		}
		new Timer().schedule(new TimerTask() {
			public void run() {
				dataCodeAutoUpdater();
			}
		}, 1000);
	}

	public static void logCodeGen(String message, Color color) {
		appendToPane(dataCodeLogTextPane, "\n" + message, color);
	}
}
