import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUIAula implements ActionListener, ListSelectionListener{

	int idx;
	boolean paused = true;
	ArrayList<String>  title = new ArrayList<>();
	ArrayList<Integer> duration = new ArrayList<>();

	//	Inicializa os componentes do JavaSwing
	private JButton addMusicButton, fwdMusicButton, stpMusicButton, bckMusicButton;
	private JLabel playingSong;
	private JList musicTitlesList;
	private JProgressBar musicProgressBar;
	private SwingWorker ProgressBarUpdate;
	JFrame frame;

	public GUIAula() {

		musicProgressBar = new JProgressBar();
		musicProgressBar.setStringPainted(true);
		musicProgressBar.setValue(0);
		musicProgressBar.setSize(100, 25);

		playingSong = new JLabel("None");
		
		String[] T = {"A", "B", "C", "D", "E"};
		musicTitlesList = new JList(T);
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

		JPanel songFunc = new JPanel();
		songFunc.setLayout(new GridLayout(1, 0));
		songFunc.add(bckMusicButton);
		songFunc.add(stpMusicButton);
		songFunc.add(fwdMusicButton);


		JPanel panelTest = new JPanel();
		panelTest.setLayout(new GridLayout(1,0));
		panelTest.add(musicProgressBar);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		panel.setLayout(new GridLayout(0, 1));

		panel.add(musicProgressBar);
		panel.add(playingSong);
		panel.add(musicTitlesList);
		panel.add(songFunc);
		panel.add(addMusicButton);
		panel.add(panelTest);

		frame = new JFrame();
		frame.add(panel);
		frame.setTitle("GUI Aula");
		frame.setSize(300, 500);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new GUIAula();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		// Caso a ação "deposit_act" seja detecada no clique de algum botão.
		switch (command) {
			case "deposit_act":
				/*
				* Aqui, criamos um SwingWorker (Thread especial do JavaSwing) que cria
				* o efeito de "loading" de nossa barra, altera nossas variáveis internas
				* e atualiza o texto de nossa label.
				*/

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
				break;
			case "fwd":
				idx = (idx + 1) % title.size();
				break;
			case "bck":
				idx = (idx + title.size() - 1) % title.size();
				break;
			case "stp":
				paused = !paused;
				if (paused)	stpMusicButton.setText("|>");
				else        stpMusicButton.setText("||");

				if (!paused && title.size() > 0 && musicProgressBar.getValue() == 0){
					ProgressBarUpdate = new SwingWorker() {

						@Override
						protected Object doInBackground() throws Exception {
							for (int i = 0; i <= 100; i++) {
								musicProgressBar.setValue(i);

								while (paused);

								try {
									Thread.sleep(10 * duration.get(idx));
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}

							musicProgressBar.setValue(0);

							return 0;
						}

					};
					ProgressBarUpdate.execute();
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

		idx = title.indexOf(selectedOption);
		if (idx != -1){
			playingSong.setText(title.get(idx));
			ProgressBarUpdate.cancel(true);
			musicProgressBar.setValue(0);
		}
		System.out.println(idx);

	}

}