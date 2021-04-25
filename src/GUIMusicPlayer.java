import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class GUIMusicPlayer extends MusicPlayer implements ActionListener, ListSelectionListener, WindowConstants {

	//	idx guarda o valor do indice na lista da música que está em execução e progBarIdx guarda o valor que a musicProgressBar possui
	int idx, progBarIdx = 0;
	//	Variável auxiliar na definição de tempo de música
	long songTime = 0;
	//	Variáveis auxiliares de controle de botão pause/play(JBUtton stpMusicButton), ao executar um shuffle na lista e na reprodução em looping
	boolean paused = true, shuffle = false, loop = false;
	

	//	Inicializa os componentes do JavaSwing
	private JButton addMusicButton, fwdMusicButton, stpMusicButton, bckMusicButton, rmvMusicButton, shuMusicButton, seqMusicButton, loopMusicButton;
	private JLabel playingSong, currentTime, totalTime, looping;
	private JList musicTitlesList;
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
		bckMusicButton.setBounds(20, 100, 60, 25);

		stpMusicButton = new JButton("|>");
		stpMusicButton.addActionListener(this);
		stpMusicButton.setActionCommand("stp");
		stpMusicButton.setBounds(85, 100, 50, 25);

		fwdMusicButton = new JButton(">>");
		fwdMusicButton.addActionListener(this);
		fwdMusicButton.setActionCommand("fwd");
		fwdMusicButton.setBounds(140, 100, 60, 25);

		addMusicButton = new JButton("ADD");
		addMusicButton.addActionListener(this);
		addMusicButton.setActionCommand("add");
		addMusicButton.setBounds(380, 100, 60, 25);

		//-------------------------------------
			songsList.addElement("01-01");
			songsList.addElement("02-01");
			songsList.addElement("03-01");
			songsList.addElement("04-01");
			songsList.addElement("05-01");

			songsListAux.add("01-01");
			songsListAux.add("02-01");
			songsListAux.add("03-01");
			songsListAux.add("04-01");
			songsListAux.add("05-01");

			duration.add(15);
			duration.add(20);
			duration.add(25);
			duration.add(30);
			duration.add(35);

			durationAux.add(15);
			durationAux.add(20);
			durationAux.add(25);
			durationAux.add(30);
			durationAux.add(35);
		//-------------------------------------

		musicTitlesList = new JList();
		musicTitlesList.setModel(songsList);
		musicTitlesList.addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(musicTitlesList);
		scrollPane.setViewportView(musicTitlesList);
		scrollPane.setBounds(20, 135, 420, 95);

		looping = new JLabel("Looping: Disable");
		looping.setBounds(20, 230, 420, 25);

		shuMusicButton = new JButton("Shuffle");
		shuMusicButton.addActionListener(this);
		shuMusicButton.setActionCommand("shu");
		shuMusicButton.setBounds(20, 265, 75, 25);

		seqMusicButton = new JButton("Serial");
		seqMusicButton.addActionListener(this);
		seqMusicButton.setActionCommand("ser");
		seqMusicButton.setBounds(100, 265, 75, 25);

		loopMusicButton = new JButton("Loop");
		loopMusicButton.addActionListener(this);
		loopMusicButton.setActionCommand("loop");
		loopMusicButton.setBounds(180, 265, 75, 25);

		rmvMusicButton = new JButton("RMV");
		rmvMusicButton.addActionListener(this);
		rmvMusicButton.setActionCommand("rmv");
		rmvMusicButton.setBounds(380, 265, 60, 25);

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
		panel.add(looping);
		panel.add(shuMusicButton);
		panel.add(seqMusicButton);
		panel.add(loopMusicButton);
		panel.add(rmvMusicButton);

		frame = new JFrame();
		frame.add(panel);
		frame.setTitle("MusicPlayer");
		frame.setSize(480, 340);
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

				addThread(musicName + " - " + musicArtist, Integer.parseInt(musicDuration));
				
				break;
			case "rmv":
				if(thereIsMusicSelected()){ //	Só executa se alguma música da lista estiver selecionada
					removeThread(idx);

					if (!progressBarUpdate.isDone())
						progressBarUpdate.cancel(true);

					try {
						Thread.sleep(5);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					resetExecution();
				}
				break;
			case "fwd": //	Executa próxima música de forma circular
				if(thereIsMusicSelected()){
					if(songsList.getSize() == 1){
						musicTitlesList.clearSelection();
						musicTitlesList.setSelectedIndex(0);
					} else {
						idx = (idx + 1) % songsList.size();
						selectNewMusic();
					}
				}
				break;
			case "bck": // Executa música anterior de forma circular
				if(thereIsMusicSelected()){
					if(songsList.getSize() == 1){
						musicTitlesList.clearSelection();
						musicTitlesList.setSelectedIndex(0);
					} else {
						idx = (idx + songsList.size() - 1) % songsList.size();
						selectNewMusic();
					}
				}
				break;
			case "stp":
				paused = !paused;

				if (paused){
					stpMusicButton.setText("|>");
					progressBarUpdate.cancel(true); //Cancela a thread que estava executando a musica selecionada no momento
				}
				else{
					stpMusicButton.setText("||");
					if (thereIsMusicSelected()) //Só dá paly em uma música, se houver alguma selecionada
						callProgBar();
				}
				break;
			case "shu":
				if(songsList.size() > 0){
					String aux = (String)musicTitlesList.getSelectedValue(); // Pega o nome da música em execução
					shuffleTread();
					selectMusicInExecutionBefore(aux);
				}	
				break;
			case "ser":
				if(songsList.size() > 0){				
					String aux = (String)musicTitlesList.getSelectedValue(); // Pega o nome da música em execução
					sequentialTread();
					selectMusicInExecutionBefore(aux);
				}
				break;
			case "loop":
				loop = !loop;

				if (loop) 
					looping.setText("Looping: Enable");
				else 
					looping.setText("Looping: Disable");				
				break;
		}
	}

	
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		if(shuffle)
			shuffle = false;
		else { // Caso o valor selecioando não tenha mudado em virtude do shuffle na lista
			if(thereIsMusicSelected()){ //Verifica se o valor trocado é uma música da lista (Pode ser null caso remova a primeira música da lista, pois idx fica -1)
				idx = musicTitlesList.getSelectedIndex();
				
				startNewMusic();
			}	
		}
		
		
	}
	
	private boolean thereIsMusicSelected() {
		return musicTitlesList.getSelectedValue() != null;
	}
	
	private void selectNewMusic() {
		musicTitlesList.setSelectedIndex(idx);
	}
	
	/**
	 * 	Seta as variavéis necessárias para inicialização da música selecionada, como reinicialização do tempo de execução e da progressBar
	 * 	bem como altera o botão de play/pause, a label que indica a música atual e o tempo total para sua execução.
	 * 	Por último, chama a thread que vai executar a música.
	 */
	private void startNewMusic() {
		
		paused = false;
		songTime = 0;
		progBarIdx = 0;
		musicProgressBar.setValue(0);
		stpMusicButton.setText("||");
		playingSong.setText("Now Playing: " + songsList.get(idx));
		totalTime.setText(timeToString(duration.get(idx)));
		callProgBar();
		
	}
	
	//	Thread resposável por executar a música, alterando a progressBar e o tempo atual de execução da música
	private void callProgBar() {
		
		if (!progressBarUpdate.isDone())
		progressBarUpdate.cancel(true);
		
		progressBarUpdate = new SwingWorker<Object, Object>() {
			
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
				
				if(!isCancelled()){
					if(loop){ // Caso o loop esteja em execução, toca a próxima música de forma circular
						idx = (idx + 1) % songsList.size();
						selectNewMusic();
					} else{ // Caso contrário, executa a próxima música na sequência ou finaliza as execuções
						if(idx < songsList.getSize() - 1){
							idx++;
							selectNewMusic();
						} else
							resetExecution();
					}
				}
				
				return 0;
			}
			
		};
		progressBarUpdate.execute();
	}
	
	private String timeToString(int time){
		return String.format("%d:%02d", time/60, time%60);
	}
	
	// Reseta alguns elementos da GUI que exibiam informações de execução, como progressBar, list, labels, etc.
	private void resetExecution() {
		musicTitlesList.clearSelection();
		totalTime.setText("0:00");
		currentTime.setText("0:00");
		playingSong.setText("Now Playing: None");
		stpMusicButton.setText("|>");
		musicProgressBar.setValue(0);
	}
	
	// Seleciona na lista a música que estava em execução antes de aplicar as mudanças na ordem de execução na lista
	private void selectMusicInExecutionBefore(String musicName) {
		try {
			Thread.sleep(5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		shuffle = true;
		
		idx = songsList.indexOf(musicName); // O novo indíce da música que estava em execução quando foi dado o shuffle
		musicTitlesList.setSelectedIndex(idx); // Seleciona novamente a música que estava em execução
	}
}