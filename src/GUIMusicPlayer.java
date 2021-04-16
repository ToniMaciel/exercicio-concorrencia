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

public class GUIMusicPlayer implements ActionListener, ListSelectionListener, WindowConstants {

	int idx, progBarIdx = 0;
	long songTime = 0;
	boolean paused = true;
	
	ArrayList<String>  title = new ArrayList<>();
	ArrayList<Integer> duration = new ArrayList<>();

	//	Inicializa os componentes do JavaSwing
	private JButton addMusicButton, fwdMusicButton, stpMusicButton, bckMusicButton, rmvMusicButton;
	private JLabel playingSong, currentTime, totalTime;
	private JList<Object> musicTitlesList;
	private JProgressBar musicProgressBar;
	private JFrame frame;
	private SwingWorker<Object, Object> progressBarUpdate = new SwingWorker<Object, Object>(){
		@Override
		protected Object doInBackground() throws Exception {
			return null;
		}
	};

	public GUIMusicPlayer() {

		musicProgressBar = new JProgressBar();
		musicProgressBar.setValue(0);
		musicProgressBar.setBounds(20, 20, 420, 20);

		currentTime = new JLabel("0:00");
		currentTime.setBounds(20, 45, 40, 10);
		totalTime = new JLabel("0:00");
		totalTime.setBounds(410, 45, 40, 10);

		playingSong = new JLabel("Now Playing: None");
		playingSong.setBounds(20, 60, 420, 25);

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
		
		//Variáveis de teste-----------
		title.add("teste - Teste");
		title.add("teste1 - Teste1");
		title.add("teste2 - Teste2");

		duration.add(25);
		duration.add(25);
		duration.add(25);
		//------------------------------

		musicTitlesList = new JList<Object>(title.toArray());
		musicTitlesList.addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(musicTitlesList);
		scrollPane.setViewportView(musicTitlesList);
		scrollPane.setBounds(20, 150, 420, 110);

		rmvMusicButton = new JButton("RMV");
		rmvMusicButton.addActionListener(this);
		rmvMusicButton.setActionCommand("rmv");
		rmvMusicButton.setBounds(380, 270, 60, 40);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.add(musicProgressBar);
		panel.add(currentTime);
		panel.add(totalTime);
		panel.add(playingSong);
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
		new GUIMusicPlayer();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		switch (command) {
			case "add":
				String musicName     = JOptionPane.showInputDialog(frame, "Title", "Music Input", -1);
				String musicArtist   = JOptionPane.showInputDialog(frame, "Artist", "Music Input", -1);
				String musicDuration = JOptionPane.showInputDialog(frame, "Duration (in seconds)", "Music Input", -1);
				title.add(musicName + " - " + musicArtist);
				duration.add(Integer.parseInt(musicDuration));
				musicTitlesList.setListData(title.toArray());
				break;
			case "rmv":
				if(thereIsMusicSelected()){
					title.remove(idx);
					duration.remove(idx);
					musicTitlesList.setListData(title.toArray());

					idx = Math.min(idx, title.size() - 1);

					if (!title.isEmpty())
						selectNewMusic();
					else {
						if (!progressBarUpdate.isDone())
							progressBarUpdate.cancel(true);

						totalTime.setText("0:00");
						currentTime.setText("0:00");
						playingSong.setText("Now Playing: None");
						musicProgressBar.setValue(0);
					}
				}
				break;
			case "fwd":
				if(thereIsMusicSelected()){
					idx = (idx + 1) % title.size();
					selectNewMusic();
				}
				break;
			case "bck":
				if(thereIsMusicSelected()){
					idx = (idx + title.size() - 1) % title.size();
					selectNewMusic();
				}
				break;
			case "stp":
				paused = !paused;

				if (paused){
					stpMusicButton.setText("|>");
					progressBarUpdate.cancel(true);
				}
				else{
					stpMusicButton.setText("||");
					if (thereIsMusicSelected())
						callProgBar();
				}
				break;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if(thereIsMusicSelected()){
			String selectedOption = (String) musicTitlesList.getSelectedValue();
			idx = title.indexOf(selectedOption);

			startNewMusic();
		}

	}

	private void callProgBar() {

		if (!progressBarUpdate.isDone())
			progressBarUpdate.cancel(true);

		progressBarUpdate = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				while (progBarIdx <= 100 && !isCancelled()) {
					musicProgressBar.setValue(progBarIdx);

					long now = System.currentTimeMillis();
					System.out.println(now);
					while (System.currentTimeMillis() < now + 10 * duration.get(idx))
						currentTime.setText(timeToString((int)(System.currentTimeMillis() + songTime - now)/1000));
					
					songTime += 10 * duration.get(idx);
					progBarIdx++;
				}

				return 0;
			}

		};
		progressBarUpdate.execute();
	}

	private String timeToString(int time){
		return String.format("%d:%02d", time/60, time%60);
	}

	private void selectNewMusic() {
		musicTitlesList.setSelectedIndex(idx);
	}

	private boolean thereIsMusicSelected() {
		return musicTitlesList.getSelectedValue() != null;
	}

	private void startNewMusic() {
		paused = false;
		songTime = 0;
		progBarIdx = 0;
		musicProgressBar.setValue(0);
		stpMusicButton.setText("||");
		playingSong.setText("Now Playing: " +title.get(idx));
		totalTime.setText(timeToString(duration.get(idx)));
		callProgBar();
	}

}