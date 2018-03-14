package GroupChat;

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.TextArea;
import javax.swing.JSpinner;

public class ClientUI extends JFrame
{
	
	private User user;
	private ImageIcon image;
	
	private JPanel contentPane = new JPanel();
	private JLabel lblOnline;
	
	private ArrayList<User> userList = new ArrayList<User>();
	// private ArrayList<User> onlineUserList = new ArrayList<User>();
	private JLabel lblOnlone;
	private Label userLabel;
	private JButton btnImage;
	private JButton sendButton;
	private Client client;
	private Message massage;
	private String text = "";
	private String friendsList = "";
	private final TextArea contactList = new TextArea();
	private final TextArea chatArea = new TextArea();
	private final TextArea onlineList = new TextArea();
	private final TextArea messageField = new TextArea();
	private final JLabel lblNewLabel = new JLabel("New label");
	
	/**
	 * Create the frame.
	 */
	public ClientUI()
	{
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 658, 565);
		setContentPane(contentPane);
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendButtonPressed();
			}
		});
		sendButton.setBounds(397, 426, 114, 82);
		contentPane.add(sendButton);
		
		btnImage = new JButton("Image");
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
		contactList.setBounds(423, 36, 177, 127);
		
		contentPane.add(contactList);
		chatArea.setBounds(105, 37, 312, 364);
		
		contentPane.add(chatArea);
		onlineList.setBounds(423, 210, 177, 164);
		
		contentPane.add(onlineList);
		messageField.setBounds(105, 426, 274, 82);
		
		contentPane.add(messageField);
		lblNewLabel.setBounds(10, 39, 89, 87);
		
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("New label");
		label.setBounds(10, 426, 89, 82);
		contentPane.add(label);
		
	}
	
	public void sendButtonPressed()
	{
		String text = messageField.getText();
		Message mess = null;
		if (text.equals(""))
			return;
		mess = new Message(text, null, client.getUser(), client.getUser());
		client.sendMessage(mess);
		messageField.setText("");
	}
	
	public void updateChatArea(Message message)
	{
		text += message.getSender().getName() + ": " + message.getTextMsg() + "\n";
		chatArea.setText(text);
	}
	
	public String setUserName()
	{
		
		String name = JOptionPane.showInputDialog(contentPane, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
		userLabel.setText(name);
		return name;
	}
	
	public void updateOnlineUsers()
	{
		User userArray[] = client.getOnlineUsers();
		
		for (int i = 0; i < userArray.length; i++)
		{
			onlineList.append(userArray[i].getName());
		}
	}
	
	public void addToContacts(User newContact)
	{
		client.addFriend(newContact);
		updateContacts();
	}
	
	public void removeFromContact(User oldContact)
	{
		client.removeFriend(oldContact);
	}
	
	public void updateContacts()
	{
		ArrayList<User> contacts = client.getFriends();
		contactList.setText("");
		
		for (int i = 0; i < contacts.size(); i++)
		{
			contactList.append(contacts.get(i).getName());
		}
		
	}
	
	public void setClient(Client client, User user)
	{
		this.client = client;
		this.user = user;
	}
}
