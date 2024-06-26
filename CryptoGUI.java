// imports
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;


// Create a GUI to encrypt and decrypt files
public class CryptoGUI extends JPanel {
	private JPanel keyPanel, keyPanel2, inPanel, buttonPanel, encryptPanel, decryptPanel;
	private JLabel keyLabel, inLabel, statusLabel;
	private JTextArea keyArea, inArea;
	private JScrollPane keyPane, inPane;
	private JButton inButton, encryptButton, decryptButton;
	private File inFile, outFile;
	private FileWriter fw;
	private BufferedWriter bw;
	
	// create GridBagConstraints
	private GridBagConstraints createGBC(int x, int y) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
		return c;
	}

	// create CryptoGUI
	public CryptoGUI() {
		// init files
		this.inFile = null;
		this.outFile = null;

		// init panels
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		this.keyPanel = new JPanel(new GridBagLayout());
		this.keyPanel2 = new JPanel(new BorderLayout());
		this.inPanel = new JPanel(new GridBagLayout());
		this.buttonPanel = new JPanel(new GridLayout(1, 2));
		this.encryptPanel = new JPanel(new FlowLayout());
		this.decryptPanel = new JPanel(new FlowLayout());

		// init labels
		this.keyLabel = new JLabel("Key:");
		this.inLabel = new JLabel("Input File:");
		this.statusLabel = new JLabel("Status: Waiting", JLabel.CENTER);
		
		// init textareas
		this.keyArea = new JTextArea(1, 28);
		this.inArea = new JTextArea(2, 21);
		this.inArea.setEditable(false);

		// init scrollpanes
		this.keyPane = new JScrollPane(this.keyArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.inPane = new JScrollPane(this.inArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// init buttons
		this.inButton = new JButton("Select");
		this.encryptButton = new JButton("Encrypt");
		this.decryptButton = new JButton("Decrypt");

		this.inButton.addActionListener(new InListener());
		this.encryptButton.addActionListener(new EncryptListener());
		this.decryptButton.addActionListener(new DecryptListener());

		// assemble
		GridBagConstraints c;

		c = createGBC(0, 0);
		this.keyPanel.add(this.keyLabel, c);
		
		c = createGBC(0, 1);
		this.keyPanel.add(this.keyPane, c);

		this.keyPanel2.add(this.keyPanel, BorderLayout.WEST);
		
		c = createGBC(0, 0);
		add(keyPanel2, c);

		c = createGBC(0, 0);
		this.inPanel.add(this.inLabel, c);
		
		c = createGBC(0, 1);
		this.inPanel.add(this.inPane, c);

		c = createGBC(1, 1);
		this.inPanel.add(this.inButton, c);
		
		c = createGBC(0, 1);
		add(this.inPanel, c);

		this.encryptPanel.add(this.encryptButton);
		this.decryptPanel.add(this.decryptButton);
		this.buttonPanel.add(this.encryptPanel);
		this.buttonPanel.add(this.decryptPanel);
		
		c = createGBC(0, 3);
		add(this.buttonPanel, c);

		c = createGBC(0, 4);
		add(this.statusLabel, c);

		// log
		log("Initialize Session");
	}

	private static void log(String s) {
		try {
			File logFile = new File("log.txt");
			logFile.createNewFile();
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("[" + LocalDateTime.now() + "] " + s + System.getProperty("line.separator"));
			bw.close();
		} catch(IOException ioe) {}
	}
	
	// get file path
	private static String getPath(File f) {
		String s = f.getAbsolutePath();
		int sep = s.lastIndexOf(File.separator);
		return s.substring(0, sep) + File.separator;
	}	

	// sets error messages
	private static String getError(Exception e) {
		if(e.getClass().getSimpleName().equals("CustomException")) {
			String s = e.getMessage();
			switch(s) {
				case "BadPaddingException":
					return "Incorrect Key";
				case "IllegalBlockSizeException":
					return "File Not Encrypted";
				default:
					return s;
			}
		} else {
			String s = e.getClass().getSimpleName();
			switch(s) {
				case "NullPointerException":
					return "No Files Selected";
				default:
					return s;
			}
		}
	}
	
	// listener for the input file button
	private class InListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// init JFileChooser
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int val = fc.showOpenDialog(CryptoGUI.this);
			if(val == JFileChooser.APPROVE_OPTION) {
				CryptoGUI.this.inFile = fc.getSelectedFile();
				CryptoGUI.this.inArea.setText(CryptoGUI.this.inFile.getAbsolutePath());
			}
		}
	}

	
// listener for the encrypt button
// listener for the encrypt button
private class EncryptListener implements ActionListener {
    private int count;

    public void actionPerformed(ActionEvent e) {
        try {
            // file
            if (CryptoGUI.this.inFile.isFile()) {
                // check file
                if (!CryptoGUI.this.inFile.getName().endsWith(".mao")) {
                    // get output file
                    String encryptedFileName = CryptoGUI.this.inFile.getName() + ".mao";
                    CryptoGUI.this.outFile = new File(
                            CryptoGUI.getPath(CryptoGUI.this.inFile) + encryptedFileName);

                    // encrypt file
                    MyCryptoUtils.encrypt(CryptoGUI.this.keyArea.getText(), CryptoGUI.this.inFile,
                            CryptoGUI.this.outFile);

                    // status report
                    CryptoGUI.this.statusLabel.setText("Status: Encrypting...");
                    Timer timer = new Timer(1000, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            CryptoGUI.this.statusLabel.setText("Status: 1 File Encrypted");
							JOptionPane.showMessageDialog(CryptoGUI.this, "File has been encrypted successfully", "Encryption Complete", JOptionPane.INFORMATION_MESSAGE);

                        }
                    });
                    timer.setRepeats(false);
                    timer.start();

                    // log
                    CryptoGUI.log("Encrypt: " + CryptoGUI.this.inFile.getName());

                } else {
                    CryptoGUI.this.statusLabel.setText("Status: File Already Encrypted");
					JOptionPane.showMessageDialog(CryptoGUI.this, "File is already encrypted", "Encryption Error", JOptionPane.ERROR_MESSAGE);
                }

                // directory
            } else if (CryptoGUI.this.inFile.isDirectory()) {
                File[] fileList = CryptoGUI.this.inFile.listFiles();
                this.count = 0;
                for (int i = 0; i < fileList.length; i++) {
                    if (!fileList[i].getName().endsWith(".mao")) {
                        // bump count
                        this.count++;

                        // get output file
                        String encryptedFileName = fileList[i].getName() + ".mao";
                        CryptoGUI.this.outFile = new File(
                                CryptoGUI.getPath(fileList[i]) + encryptedFileName);

                        // encrypt file
                        MyCryptoUtils.encrypt(CryptoGUI.this.keyArea.getText(), fileList[i], CryptoGUI.this.outFile);

                        // status report
                        CryptoGUI.this.statusLabel.setText("Status: Encrypting...");

                        // log
                        CryptoGUI.log("Encrypt: " + fileList[i].getName());

                    }
                }
                // status report
                Timer timer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        CryptoGUI.this.statusLabel.setText("Status: " + EncryptListener.this.count + " File(s) Encrypted");
						JOptionPane.showMessageDialog(CryptoGUI.this, "File has been encrypted successfully", "Encryption Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        } catch (Exception ex) {
            // status report
            String errorMessage = CryptoGUI.getError(ex);
            CryptoGUI.this.statusLabel.setText("Status: " + errorMessage);

            // log
            CryptoGUI.log("Error: " + errorMessage);
        }
    }
}


// listener for the decrypt button
private class DecryptListener implements ActionListener {
    private int count;

    public void actionPerformed(ActionEvent e) {
        try {
            // file
            if (CryptoGUI.this.inFile.isFile()) {
                // check file
                if (CryptoGUI.this.inFile.getName().endsWith(".mao")) {
                    // get output file
                    String decryptedFileName = CryptoGUI.this.inFile.getName().replace(".mao", "");
                    CryptoGUI.this.outFile = new File(
                            CryptoGUI.getPath(CryptoGUI.this.inFile) + decryptedFileName);

                    // decrypt file
                    MyCryptoUtils.decrypt(CryptoGUI.this.keyArea.getText(), CryptoGUI.this.inFile,
                            CryptoGUI.this.outFile);

                    // status report
                    CryptoGUI.this.statusLabel.setText("Status: Decrypting...");
                    Timer timer = new Timer(1000, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            CryptoGUI.this.statusLabel.setText("Status: 1 File Decrypted");
							JOptionPane.showMessageDialog(CryptoGUI.this, "File has been decrypted successfully", "Decryption Complete", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();

                    // log
                    CryptoGUI.log("Decrypt: " + CryptoGUI.this.inFile.getName());

                } else {
                    CryptoGUI.this.statusLabel.setText("Status: File Not Encrypted");
					JOptionPane.showMessageDialog(CryptoGUI.this, "File is not encrypted", "Decryption Error", JOptionPane.ERROR_MESSAGE);
                }

                // directory
            } else if (CryptoGUI.this.inFile.isDirectory()) {
                File[] fileList = CryptoGUI.this.inFile.listFiles();
                this.count = 0;
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].getName().endsWith(".mao")) {
                        // bump count
                        this.count++;

                        // get output file
                        String decryptedFileName = fileList[i].getName().replace(".mao", "");
                        CryptoGUI.this.outFile = new File(
                                CryptoGUI.getPath(fileList[i]) + decryptedFileName);

                        // decrypt file
                        MyCryptoUtils.decrypt(CryptoGUI.this.keyArea.getText(), fileList[i], CryptoGUI.this.outFile);

                        // status report
                        CryptoGUI.this.statusLabel.setText("Status: Decrypting...");

                        // log
                        CryptoGUI.log("Decrypt: " + fileList[i].getName());

                    }
                }
                // status report
                Timer timer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        CryptoGUI.this.statusLabel.setText("Status: " + DecryptListener.this.count + " File(s) Decrypted");
						JOptionPane.showMessageDialog(CryptoGUI.this, "File has been decrypted successfully", "Decryption Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        } catch (Exception ex) {
            // status report
            String errorMessage = CryptoGUI.getError(ex);
            CryptoGUI.this.statusLabel.setText("Status: " + errorMessage);

            // log
            CryptoGUI.log("Error: " + errorMessage);
        }
    }
}




	public static void onExit() {
		CryptoGUI.log("Close Session");
		System.exit(0);
	}

	// run		
	public static void main(String[] args) {
		// create gui
		JFrame frame = new JFrame("ADET4 : Orpiada Marlon A.");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onExit();
		}});
		frame.setResizable(false);
		frame.add(new CryptoGUI());
		frame.pack();
		frame.setVisible(true);
		frame.setSize(450, 220);
	}
}
		