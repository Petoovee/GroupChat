package GroupChat;

import java.awt.Label;
import java.awt.TextArea;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

public class ClientUI extends JFrame {
	private ImageIcon image;
	private JPanel contentPane = new JPanel();
	private JLabel lblOnline;
	private JLabel lblOnlone;
	private Label userLabel;
	private JButton btnImage;
	private JButton sendButton;
	private Client client;
	private static Message mess = new Message(null, null, null, null);
	private String text = "";
	private final TextArea contactList = new TextArea();
	private final TextArea chatArea = new TextArea();
	private final TextArea onlineList = new TextArea();
	private final TextArea messageField = new TextArea();
	private JLabel userProfilePic = new JLabel();
	private JLabel sentImage = new JLabel("");
	private final JLabel enemyProfilePic1 = new JLabel("");
	private final JLabel enemyProfilePic2 = new JLabel("");
	private final JLabel enemyProfilePic3 = new JLabel("");
	private final JLabel enemyProfilePic4 = new JLabel("");
	private JLabel[] receiversIcons = new JLabel[4];

	/**
	 * Create the frame.
	 */
	public ClientUI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 658, 565);
		setContentPane(contentPane);

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		sendButton = new JButton("Send");
		sendButton.addActionListener(e -> sendButtonPressed());

		sendButton.setBounds(397, 426, 114, 82);
		contentPane.add(sendButton);

		btnImage = new JButton("Image");
		btnImage.addActionListener(e -> imageButtonPressed());

		btnImage.setBounds(523, 426, 105, 82);
		contentPane.add(btnImage);

		userLabel = new Label();
		userLabel.setAlignment(Label.CENTER);
		userLabel.setBounds(10, -2, 346, 35);
		contentPane.add(userLabel);

		lblOnlone = new JLabel("Contacts");
		lblOnlone.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnlone.setBounds(423, -2, 177, 30);
		contentPane.add(lblOnlone);

		lblOnline = new JLabel("Online users");
		lblOnline.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnline.setBounds(420, 169, 180, 35);
		contentPane.add(lblOnline);
		contactList.setEditable(false);
		contactList.setBounds(423, 36, 177, 127);

		contentPane.add(contactList);
		chatArea.setEditable(false);
		chatArea.setBounds(105, 37, 312, 364);

		contentPane.add(chatArea);
		onlineList.setEditable(false);
		onlineList.setBounds(423, 210, 177, 164);

		contentPane.add(onlineList);
		messageField.setBounds(105, 426, 274, 82);

		contentPane.add(messageField);

		sentImage.setBounds(10, 233, 89, 82);
		contentPane.add(sentImage);

		userProfilePic.setBounds(12, 410, 85, 82);
		contentPane.add(userProfilePic);

		enemyProfilePic1.setBounds(0, 39, 47, 49);
		contentPane.add(enemyProfilePic1);

		enemyProfilePic2.setBounds(52, 39, 47, 49);
		contentPane.add(enemyProfilePic2);

		enemyProfilePic4.setBounds(52, 96, 47, 49);
		contentPane.add(enemyProfilePic4);

		enemyProfilePic3.setBounds(0, 96, 47, 49);
		contentPane.add(enemyProfilePic3);

		enemyProfilePic1.setVisible(false);
		enemyProfilePic2.setVisible(false);
		enemyProfilePic3.setVisible(false);
		enemyProfilePic4.setVisible(false);
		receiversIcons[0] = enemyProfilePic1;
		receiversIcons[1] = enemyProfilePic2;
		receiversIcons[2] = enemyProfilePic3;
		receiversIcons[3] = enemyProfilePic4;
	}

	public void imageButtonPressed() {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			mess = new Message(text, new ImageIcon(selectedFile.getAbsolutePath()), client.getUser(),
					client.getReceivers());

		}

	}

	public void sendButtonPressed() {

		String text = messageField.getText();
		mess = new Message(text, null, client.getUser(), client.getReceivers());
		client.checkCommands(mess);
		if (mess.getImage() != null) {

			mess.setTextMsg(text);
		} else {

			if (text.equals(""))
				return;

			mess = new Message(text, null, client.getUser(), client.getReceivers());

		}

		messageField.setText("");

		// mess.setImage(null);
		// mess.setTextMsg(null);
	}

	public void updateChatArea(Message mess) {

		text += mess.getSender().getName() + ": " + mess.getTextMsg() + "\n";

		if (mess.getImage() != null)
			text += "" + mess.getImage() + "\n";
		sentImage.setIcon(mess.getImage());

		chatArea.setText(text);

	}

	public String getUserName() throws Exception {

		String name = JOptionPane.showInputDialog(contentPane, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);

		if (name == null || name.isEmpty()) {
			throw new Exception("You can't have an empty screen name lol");
		}
		userLabel.setText(name);
		return name;
	}

	public ImageIcon getProfilePic() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			image = new ImageIcon(selectedFile.getAbsolutePath());
		}

		userProfilePic.setIcon(image);

		return image;
	}

	public void updateOnlineUsers() {
		User[] userArray = new User[client.getOnlineUsers().length];
		for (int i = 0; i < client.getOnlineUsers().length; i++) {
			userArray[i] = client.getOnlineUsers()[i];
		}
		onlineList.setText("");

		for (int i = 0; i < userArray.length; i++) {
			if (userArray[i] != null) {

				onlineList.append(userArray[i].getName() + "\n");
			}
		}
	}

	public void updateContacts() {
		ArrayList<User> contacts = client.getFriends();
		contactList.setText("");
		for (int i = 0; i < contacts.size(); i++) {
			contactList.append(contacts.get(i).getName() + "\n");
		}
	}

	public void setClient(Client client, User user) {
		this.client = client;
	}

	public void newReceiverPic(ImageIcon icon) {
		for (int i = 0; i < receiversIcons.length; i++) {
			if (!receiversIcons[i].isVisible()) {
				receiversIcons[i].setIcon(icon);
				receiversIcons[i].setVisible(true);
				i = receiversIcons.length;
			}
		}
	}

	public void removeReceiverPic(ImageIcon icon) {
		for (int i = 0; i < receiversIcons.length; i++) {
			if (receiversIcons[i].getIcon().equals(icon)) {
				receiversIcons[i].setVisible(false);
				System.out.println("bortA");
				i = receiversIcons.length;
			}
		}
	}
}