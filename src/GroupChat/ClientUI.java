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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ClientUI extends JFrame
{
	
	private User user;
	private ImageIcon image;
	
	private JPanel contentPane = new JPanel();
	private JTextArea messageArea = new JTextArea();
	private JTextArea textArea = new JTextArea();
	private JLabel lblOnline;
	
	private ArrayList<User> userList = new ArrayList<User>();
	// private ArrayList<User> onlineUserList = new ArrayList<User>();
	private JLabel lblOnlone;
	private Label userLabel;
	private JList<User> onlineList;
	private JList<User> contactList;
	private JButton btnImage;
	private JButton sendButton;
	private Client client;
	private Message massage;
	
	/**
	 * Create the frame.
	 */
	public ClientUI()
	{
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 575, 514);
		setContentPane(contentPane);
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		sendButton = new JButton("Send");
		sendButton.setBounds(368, 409, 81, 57);
		contentPane.add(sendButton);
		
		messageArea.setEditable(false);
		messageArea.setText("");
		messageArea.setBounds(0, 36, 356, 363);
		contentPane.add(messageArea);
		
		btnImage = new JButton("Image");
		btnImage.setBounds(461, 409, 84, 57);
		contentPane.add(btnImage);
		
		contactList = new JList<User>();
		contactList.setBounds(365, 36, 182, 130);
		contentPane.add(contactList);
		
		onlineList = new JList<User>();
		onlineList.setBounds(365, 210, 182, 190);
		contentPane.add(onlineList);
		
		userLabel = new Label();
		userLabel.setAlignment(Label.CENTER);
		userLabel.setBounds(10, -2, 346, 35);
		contentPane.add(userLabel);
		
		textArea.setEditable(true);
		textArea.setText("");
		textArea.setBounds(0, 409, 356, 57);
		contentPane.add(textArea);
		
		sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource().equals(sendButton))
				{
					sendButtonPressed();
				}
			}
		});
		
		lblOnlone = new JLabel("Contacts");
		lblOnlone.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnlone.setBounds(368, 0, 177, 30);
		contentPane.add(lblOnlone);
		
		lblOnline = new JLabel("Online users");
		lblOnline.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnline.setBounds(365, 169, 180, 35);
		contentPane.add(lblOnline);
	}
	
	public void sendButtonPressed()
	{
		String text = textArea.getText();
		Message mess = null;
		
		if (text.equals(""))
		{
			return;
		}
		else
		{
		mess = new Message(text, null, user.getUser(), contactList.getSelectedValue());
		
		client.sendMessage(mess);
		textArea.setText("");
		}
	}
	
	public String setUserName()
	{
		
		String name = JOptionPane.showInputDialog(contentPane, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
		
		userLabel.setText(name);
		
		return name;
	}
	
}
