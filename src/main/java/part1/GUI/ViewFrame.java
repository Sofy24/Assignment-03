package part1.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;


import akka.actor.ActorRef;
import part1.Utils.ComputedFile;

public class ViewFrame extends JFrame implements ActionListener {
	private final JButton startButton;
	private final JButton stopButton;
	private final JButton chooseDir;
	private final JTextField nMaxFilesToRank;
	private final JTextField state;
	private final JLabel selectedDir;
	private final JTextField nBands;
	private final JTextField maxLoc;
	private final JTextArea sourceListArea;
	private JFileChooser startDirectoryChooser;
	private final JLabel numSrcProcessed;
	private final JPanel filesElabPanel;
	private File dir;
	private String selectedDirFullPath;
	private String defStartDir;
	private int defaultMaxFileToRank;
	private int defaultNumBands;
	private int defaultMaxLoc;
	private ActorRef actorView;

	public ViewFrame(ActorRef actorView) {
		super(".:: Test Swing | Actors interaction ::.");
		setSize(800,400);
		this.actorView = actorView;
		startButton = new JButton("start");
		stopButton = new JButton("stop");
		chooseDir = new JButton("select dir");
		defStartDir = "C:\\Users\\Sofia\\Documents\\Programmazione concorrente e distribuita (Ricci)\\f2";
		defaultMaxFileToRank = 5;
		defaultNumBands = 5;
		defaultMaxLoc = 200;
		selectedDirFullPath = defStartDir;
		selectedDir = new JLabel(defStartDir);
		selectedDir.setSize(200,14);

		nMaxFilesToRank = new JTextField("" + defaultMaxFileToRank);
		nBands = new JTextField("" + defaultNumBands);
		maxLoc = new JTextField("" + defaultMaxLoc);

		numSrcProcessed = new JLabel("0");

		JPanel controlPanel1 = new JPanel();
		controlPanel1.add(chooseDir);
		controlPanel1.add(selectedDir);
		controlPanel1.add(Box.createRigidArea(new Dimension(20,0)));
		controlPanel1.add(new JLabel("Num sources to view: "));
		controlPanel1.add(nMaxFilesToRank);
		controlPanel1.add(Box.createRigidArea(new Dimension(20,0)));
		controlPanel1.add(new JLabel("Num Bands: "));
		controlPanel1.add(nBands);
		controlPanel1.add(Box.createRigidArea(new Dimension(20,0)));
		controlPanel1.add(new JLabel("Max LoC: "));
		controlPanel1.add(maxLoc);

		JPanel controlPanel2 = new JPanel();
		controlPanel2.add(startButton);
		controlPanel2.add(stopButton);

		filesElabPanel = new JPanel();
		filesElabPanel.add(new JLabel("Num Sources Processed: "));
		filesElabPanel.add(numSrcProcessed);
		filesElabPanel.setEnabled(false);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(controlPanel1);
		controlPanel.add(controlPanel2);
		controlPanel.add(filesElabPanel);


		JPanel sourcesListPanel = new JPanel();
		sourceListArea = new JTextArea(15,40);
		sourcesListPanel.add(sourceListArea);
		sourceListArea.setEditable(false);
		JScrollPane scrollPane=new JScrollPane(sourcesListPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel infoPanel = new JPanel();
		state = new JTextField("ready.",40);
		state.setSize(700, 14);
		infoPanel.add(state);

		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);
		cp.add(BorderLayout.NORTH,controlPanel);
		cp.add(BorderLayout.CENTER,scrollPane);
		cp.add(BorderLayout.SOUTH, infoPanel);
		setContentPane(cp);

		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		chooseDir.addActionListener(this);

		this.startButton.setEnabled(true);
		this.stopButton.setEnabled(false);
		chooseDir.setEnabled(true);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent ev){
		Object src = ev.getSource();
		if (src == chooseDir) {
			startDirectoryChooser = new JFileChooser(new File("."));
			startDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = startDirectoryChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				dir = startDirectoryChooser.getSelectedFile();
				selectedDirFullPath = dir.getAbsolutePath();
				selectedDir.setText(dir.getName());
			}

		} else if (src == startButton) {
			this.stopButton.setEnabled(true);
			this.state.setText("Processing...");
			filesElabPanel.setEnabled(true);
			this.startButton.setEnabled(false);
			chooseDir.setEnabled(false);
			File dir = new File(selectedDirFullPath);
			int n = Integer.parseInt(nMaxFilesToRank.getText());
			int nBands = Integer.parseInt(this.nBands.getText());
			int maxLocInBand = Integer.parseInt(this.maxLoc.getText());
			actorView.tell(new GUIMessageProtocol.StartMessage(dir.getAbsolutePath(),
					n, nBands, maxLocInBand, this), ActorRef.noSender());

		} else if (src == stopButton) {
			actorView.tell(new GUIMessageProtocol.StopMessage(), ActorRef.noSender());
			this.state.setText("Stopped.");
			this.startButton.setEnabled(true);
			this.stopButton.setEnabled(false);
			chooseDir.setEnabled(true);
			filesElabPanel.setEnabled(false);
		}

	}

	public void update(List<ComputedFile> files, int nLongest) {
		List<String> displayFiles = files.stream()
				.sorted(Comparator.comparing(ComputedFile::getLength).reversed())
				.limit(nLongest)
				.map(f -> f.getFilePath().getCompleteFilePath()
						.substring(f.getFilePath().getCompleteFilePath()
								.lastIndexOf("\\") + 1).concat(f.getLength().toString()))
				.collect(Collectors.toList());
		SwingUtilities.invokeLater(() -> {
			sourceListArea.setText("");
			for(String file : displayFiles){
				sourceListArea.append(file + "\n");
			}
			this.numSrcProcessed.setText(String.valueOf(files.size()));
		});

	}
	
	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}

	public void done() {
		SwingUtilities.invokeLater(() -> {
			this.startButton.setEnabled(true);
			this.stopButton.setEnabled(false);
			chooseDir.setEnabled(true);
			this.state.setText("Done.");
		});

	}
}