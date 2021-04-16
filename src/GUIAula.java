import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.lang.System;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUIAula implements ActionListener, ListSelectionListener, WindowConstants {

	int idx, progBarIdx = 0;
	boolean paused = true;
	ArrayList<String>  title = new ArrayList<>();
	ArrayList<Integer> duration = new ArrayList<>();

	//	Inicializa os componentes do JavaSwing
	private JButton addMusicButton, fwdMusicButton, stpMusicButton, bckMusicButton, rmvMusicButton;
	private JLabel playingSong, currentTime, totalTime;
	private JList musicTitlesList;
	private JProgressBar musicProgressBar;
	private SwingWorker ProgressBarUpdate = new SwingWorker(){
		@Override
		protected Object doInBackground() throws Exception {
			return null;
		}
	};
	long songTime = 0;
	JFrame frame;

	public GUIAula() {

		musicProgressBar = new JProgressBar();
		musicProgressBar.setValue(0);
		musicProgressBar.setBounds(20, 20, 420, 20);

		playingSong = new JLabel("None");
		playingSong.setBounds(20, 60, 420, 25);
		currentTime = new JLabel("0:00");
		currentTime.setBounds(20, 45, 40, 10);
		totalTime = new JLabel("0:00");
		totalTime.setBounds(410, 45, 40, 10);

		
		//Variáveis de teste-----------
		/*title.add("teste - Teste");
		title.add("teste1 - Teste1");
		title.add("teste2 - Teste2");

		duration.add(25);
		duration.add(25);
		duration.add(25);*/
		//------------------------------

		musicTitlesList = new JList(title.toArray());
		musicTitlesList.addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(musicTitlesList);
		scrollPane.setViewportView(musicTitlesList);
		scrollPane.setBounds(20, 150, 420, 110);

		bckMusicButton = new JButton("<<");
		bckMusicButton.addActionListener(this);
		bckMusicButton.setActionCommand("bck");
		bckMusicButton.setBounds(20, 100, 60, 40);
		
		stpMusicButton = new JButton("|>");
		stpMusicButton.addActionListener(this);
		stpMusicButton.setActionCommand("stp");
		stpMusicButton.setBounds(85, 100, 50, 40);

		fwdMusicButton = new JButton(">>");
		fwdMusicButton.addActionListener(this);
		fwdMusicButton.setActionCommand("fwd");
		fwdMusicButton.setBounds(140, 100, 60, 40);

		addMusicButton = new JButton("ADD");
		addMusicButton.addActionListener(this);
		addMusicButton.setActionCommand("add");
		addMusicButton.setBounds(380, 100, 60, 40);

		rmvMusicButton = new JButton("RMV");
		rmvMusicButton.addActionListener(this);
		rmvMusicButton.setActionCommand("rmv");
		rmvMusicButton.setBounds(380, 270, 60, 40);


		JPanel panel = new JPanel();
		//panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		panel.setLayout(null);

		panel.add(musicProgressBar);
		panel.add(playingSong);
		panel.add(currentTime);
		panel.add(totalTime);
		panel.add(bckMusicButton);
		panel.add(stpMusicButton);
		panel.add(fwdMusicButton);
		panel.add(addMusicButton);
		panel.add(scrollPane);
		panel.add(rmvMusicButton);

		frame = new JFrame();
		frame.add(panel);
		frame.setTitle("MusicPlayer");
		frame.setSize(480, 360);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new GUIAula();
	}

	private String timeToString(int time){
		return String.format("%d:%02d", time/60, time%60);
	}

	private void callProgBar() {

		if (!ProgressBarUpdate.isDone())
			ProgressBarUpdate.cancel(true);

		ProgressBarUpdate = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				while (progBarIdx <= 100 && !isCancelled()) {
					musicProgressBar.setValue(progBarIdx);

					long now = System.currentTimeMillis();
					while (System.currentTimeMillis() < now + 10 * duration.get(idx))
						currentTime.setText(timeToString((int)(System.currentTimeMillis() + songTime - now)/1000));
					
					songTime += 10 * duration.get(idx);
					progBarIdx++;
				}

				return 0;
			}

		};
		ProgressBarUpdate.execute();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		switch (command) {
			case "add":
				String name   = JOptionPane.showInputDialog(frame, "Title");
				String singer = JOptionPane.showInputDialog(frame, "Singer");
				String dur    = JOptionPane.showInputDialog(frame, "Duration");
				title.add(name + " - " + singer);
				duration.add(Integer.parseInt(dur));
				musicTitlesList.setListData(title.toArray());
				break;
			case "rmv":
				title.remove(idx);
				duration.remove(idx);
				musicTitlesList.setListData(title.toArray());

				idx = Math.min(idx, title.size() - 1);
				
				if (idx != -1)
					musicTitlesList.setSelectedIndex(idx);
				else {
					if (!ProgressBarUpdate.isDone())
						ProgressBarUpdate.cancel(true);

					totalTime.setText("0:00");
					currentTime.setText("0:00");
					playingSong.setText("None");
					musicProgressBar.setValue(0);
				}
				break;
			case "fwd":
				idx = (idx + 1) % title.size();
				musicTitlesList.setSelectedIndex(idx);
				playingSong.setText(title.get(idx));
				break;
			case "bck":
				idx = (idx + title.size() - 1) % title.size();
				playingSong.setText(title.get(idx));
				musicTitlesList.setSelectedIndex(idx);
				break;
			case "stp":
				paused = !paused;

				if (paused){
					stpMusicButton.setText("|>");
					ProgressBarUpdate.cancel(true);
				}
				else{
					stpMusicButton.setText("||");
					if (musicTitlesList.getSelectedValue() != null)
						callProgBar();
				}
				break;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		String selectedOption = (String) musicTitlesList.getSelectedValue();
		int tempIdx = title.indexOf(selectedOption);

		if (tempIdx != -1){
			idx = tempIdx;
			paused = false;
			songTime = 0;
			progBarIdx = 0;
			musicProgressBar.setValue(0);
			stpMusicButton.setText("||");
			playingSong.setText(title.get(idx));
			totalTime.setText(timeToString(duration.get(idx)));
			callProgBar();
		}
		System.out.println(idx);

	}

}