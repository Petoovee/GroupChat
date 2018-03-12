package GroupChat;

import java.awt.EventQueue;
import java.awt.Label;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ClientUI extends JFrame {
	private BufferedReader in;
	private PrintWriter out;
	private User user;

	private JPanel contentPane = new JPanel();
	private JTextArea messageArea = new JTextArea();
	private JTextArea textArea = new JTextArea();
	private JLabel lblOnline;

	private ArrayList<User> userList = new ArrayList<User>();
	// private ArrayList<User> onlineUserList = new ArrayList<User>();
	private JLabel lblOnlone;
	private Label label;
	private List onlineList;
	private List contactList;
	private JButton btnImage;
	private JButton sendButton;
saasaffasadafafsf
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientUI frame = new ClientUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 575, 514);
		setContentPane(contentPane);

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		sendButton = new JButton("Send");
		sendButton.setBounds(368, 409, 81, 57);
		contentPane.add(sendButton);
		
		messageArea.setEditable(false);
		messageArea.setBounds(0, 36, 356, 363);
		contentPane.add(messageArea);

		btnImage = new JButton("Image");
		btnImage.setBounds(461, 409, 84, 57);
		contentPane.add(btnImage);

		contactList = new List();
		contactList.setBounds(365, 36, 182, 130);
		contentPane.add(contactList);

		onlineList = new List();
		onlineList.setBounds(365, 210, 182, 190);
		contentPane.add(onlineList);

		label = new Label("Antoine Rebelo");
		label.setAlignment(Label.CENTER);
		label.setBounds(10, -2, 346, 35);
		contentPane.add(label);

		textArea.setEditable(false);
		textArea.setBounds(0, 409, 356, 57);
		contentPane.add(textArea);

		lblOnlone = new JLabel("Contacts");
		lblOnlone.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnlone.setBounds(368, 0, 177, 30);
		contentPane.add(lblOnlone);

		lblOnline = new JLabel("Online users");
		lblOnline.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnline.setBounds(365, 169, 180, 35);
		contentPane.add(lblOnline);
	}

	private void sendButtonActionListener(ActionEvent e) {
		String nothing = "";

		if ((textArea.getText()).equals(nothing)) {
			textArea.setText(nothing);
			textArea.requestFocus();
		} else {
			try {
				out.println(user.getName() + " " + textArea.getText() + " Chat");
				out.flush();
			} catch (Exception ex) {
				messageArea.append("Message was not sent \n");
			}
			textArea.setText(nothing);
			textArea.requestFocus();
		}

		textArea.setText(nothing);
		textArea.requestFocus();
	}

}
