package GroupChat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ServerUI extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3447627342492082985L;
	private JList list;
	private DefaultListModel model;

	private JPanel southPanel = new JPanel();
	private final TextArea logArea = new TextArea();
	private JLabel startDateLabel = new JLabel("          Visa logg från: yyyy/mm/dd");
	private JLabel endDateLabel = new JLabel("           Visa logg till: yyyy/mm/dd");
	private JLabel emptyLabel = new JLabel("");
	private final TextArea startDateField = new TextArea();
	private final TextArea endDateField = new TextArea();
	private JButton buttonShowLogg = new JButton("Visa logg");
	private Server server;

	public ServerUI(Server server) {
		this.server = server;
		server.setUI(this);
		setLayout(new BorderLayout());
		southPanel.setPreferredSize(new Dimension(600, 100));
		GridLayout southGrid = new GridLayout(2, 3);
		model = new DefaultListModel();
		list = new JList(model);
		JScrollPane pane = new JScrollPane(list);
		pane.setPreferredSize(new Dimension(150, 500));
		
		southPanel.setLayout(southGrid);
		add(southPanel, BorderLayout.SOUTH);
		add(pane, BorderLayout.EAST);
		setUserList();
		startDateLabel.setPreferredSize(new Dimension(50, 70));
		startDateLabel.setPreferredSize(new Dimension(50, 70));
		startDateField.setPreferredSize(new Dimension(50, 40));
		endDateField.setPreferredSize(new Dimension(50, 40));
		buttonShowLogg.setPreferredSize(new Dimension(150, 100));
		southGrid.setHgap(20);

		southPanel.add(startDateLabel);
		southPanel.add(endDateLabel);
		southPanel.add(emptyLabel);
		southPanel.add(startDateField);
		southPanel.add(endDateField);
		southPanel.add(buttonShowLogg);

		buttonShowLogg.addActionListener(this);

		logArea.setEditable(false);
		logArea.setBounds(105, 37, 312, 364);
		add(logArea);

	}

	public void setUserList() {
		ArrayList<User> allUsers = server.getCurrentUsers();
		model.removeAllElements();
		for (int i = 0; i < allUsers.size(); i++) {
			model.addElement(allUsers.get(i).getName());
		}
	}
	
	public void setLogArea(String log) {
		logArea.setText(log);
	}
	
	public void updateLog() {
		logArea.setText("");
		String startPoint = startDateField.getText();
		String endPoint = endDateField.getText();
		ArrayList<String> messagesList = server.getMessages(startPoint, endPoint);
		for(int i = 0; i<messagesList.size(); i++) {
			logArea.append(messagesList.get(i) +"\n");
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {
		updateLog();
	}
}