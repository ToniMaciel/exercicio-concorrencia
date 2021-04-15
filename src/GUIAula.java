import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUIAula implements ActionListener, ListSelectionListener, WindowConstants {

	Lock lock = new ReentrantLock();
	Condition songslistOperation = lock.newCondition(); 

	int idx, progBarIdx = 0;
	boolean paused = true;
	ArrayList<String>  title = new ArrayList<>();
	ArrayList<Integer> duration = new ArrayList<>();

	//	Inicializa os componentes do JavaSwing
	private JButton addMusicButton, fwdMusicButton, stpMusicButton, bckMusicButton, rmvMusicButton;
	private JLabel playingSong;
	private JList musicTitlesList;
	private JProgressBar musicProgressBar;
	private SwingWorker ProgressBarUpdate = new SwingWorker(){

		@Override
		protected Object doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}};
	JFrame frame;

	public GUIAula() {

		musicProgressBar = new JProgressBar();
		musicProgressBar.setStringPainted(true);
		musicProgressBar.setValue(0);
		musicProgressBar.setSize(100, 25);

		playingSong = new JLabel("None");
		
		//Variáveis de teste-----------
		title.add("teste - Teste");
		title.add("teste1 - Teste1");
		title.add("teste2 - Teste2");

		duration.add(25);
		duration.add(25);
		duration.add(25);
		//------------------------------

		musicTitlesList = new JList(title.toArray());
		musicTitlesList.setSelectedIndex(0);
		musicTitlesList.addListSelectionListener(this);
		musicTitlesList.setAutoscrolls(true);

		bckMusicButton = new JButton("<<");
		bckMusicButton.addActionListener(this);
		bckMusicButton.setActionCommand("bck");
		
		stpMusicButton = new JButton("|>");
		stpMusicButton.addActionListener(this);
		stpMusicButton.setActionCommand("stp");

		fwdMusicButton = new JButton(">>");
		fwdMusicButton.addActionListener(this);
		fwdMusicButton.setActionCommand("fwd");

		addMusicButton = new JButton("ADD");
		addMusicButton.addActionListener(this);
		addMusicButton.setActionCommand("add");

		rmvMusicButton = new JButton("RMV");
		rmvMusicButton.addActionListener(this);
		rmvMusicButton.setActionCommand("rmv");

		JPanel songFunc = new JPanel();
		songFunc.setLayout(new GridLayout(1, 0));
		songFunc.add(bckMusicButton);
		songFunc.add(stpMusicButton);
		songFunc.add(fwdMusicButton);


		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		panel.setLayout(new GridLayout(0, 1));

		panel.add(musicProgressBar);
		panel.add(playingSong);
		panel.add(musicTitlesList);
		panel.add(songFunc);
		panel.add(addMusicButton);
		panel.add(rmvMusicButton);

		frame = new JFrame();
		frame.add(panel);
		frame.setTitle("GUI Aula");
		frame.setSize(300, 500);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new GUIAula();
	}

	private void callProgBar(boolean pause) {

		if (!ProgressBarUpdate.isDone())
			ProgressBarUpdate.cancel(true);

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}

		if (!pause){
			paused = false;
			progBarIdx = 0;
			musicProgressBar.setValue(0);
			stpMusicButton.setText("||");
		}

		ProgressBarUpdate = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				while (progBarIdx <= 100) {
					musicProgressBar.setValue(progBarIdx++);

					if (isCancelled())
						return 0;

					try {
						Thread.sleep(10 * duration.get(idx));
					} catch (InterruptedException e1) {}
				}

				musicProgressBar.setValue(0);
				return 0;
			}

		};

		ProgressBarUpdate.execute();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		// Caso a ação "deposit_act" seja detecada no clique de algum botão.
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
				
				if (idx != -1){
					playingSong.setText(title.get(idx));
					callProgBar(false);
				}
				else {
					if (!ProgressBarUpdate.isDone())
						ProgressBarUpdate.cancel(true);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) { }
					musicProgressBar.setValue(0);
				}
				break;
			case "fwd":
				idx = (idx + 1) % title.size();
				playingSong.setText(title.get(idx));
				callProgBar(false);
				break;
			case "bck":
				idx = (idx + title.size() - 1) % title.size();
				playingSong.setText(title.get(idx));
				callProgBar(false);
				break;
			case "stp":
				paused = !paused;

				if (paused){
					stpMusicButton.setText("|>");
					ProgressBarUpdate.cancel(true);
				}
				else{
					stpMusicButton.setText("||");
					callProgBar(true);
				}
				break;
		}


	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		/*
		 * Aqui criamos uma função, que é acionada todas as vezes que uma nova célula
		 * da lista é clicada. Quando isso acontece, o valor da célula é capturado,
		 * convertido e colocado em nossa variável handleDepositValue.
		 */
		String selectedOption = (String) musicTitlesList.getSelectedValue();
		int tempIdx = title.indexOf(selectedOption);

		if (tempIdx != -1){
			idx = tempIdx;

			playingSong.setText(title.get(idx));
			callProgBar(false);
		}
		System.out.println(idx);

	}

}