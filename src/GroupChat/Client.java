package GroupChat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Client implements Serializable {
	private int port = 80;
	private String IP;
	private static User user;
	private Message massage;
	private LinkedList<User> contactList = new LinkedList<User>();
	private Socket socket = null;
	private User[] onlineUsers;
	private ObjectOutputStream out;
	private ClientUI ui;
	private ArrayList<User> friends = new ArrayList<User>();

	public Client(ClientUI ui) {
		createUser(ui.setUserName(), null);
		this.ui=ui;
		Thread thread;
		thread = new Connect(this, user);
		thread.start();
		ui.setClient(this, user);
	}

	public class Connect extends Thread {
		private Client client;
		private User user;
		private Message massage;
		private Thread threadRes;

		public Connect(Client client, User user) {
			this.client = client;
			this.user = user;
			friends = readFriends("files/friends.txt");

		}

		public void run() {
			try {
				socket = new Socket("94.234.170.141", port);
				out = new ObjectOutputStream(socket.getOutputStream());

				System.out.println("Klient kopplad till server");

				threadRes = new RecieveMessage();
				threadRes.start();

				out.writeObject(user);
				sendMessage(massage);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnect() {
		try {
			socket.close();
			System.out.println("Disconnected from server");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class RecieveMessage extends Thread {

		ObjectInputStream in;

		public RecieveMessage() {
			try {
				in = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			Message mess;
			Object obj = null;
			while (!Thread.interrupted()) {
				try {
					obj = in.readObject();
					if (obj instanceof LinkedList) {
						LinkedList<User> onlineList = (LinkedList) obj;
						onlineUsers = new User[onlineList.size()];
						
						int listSize = onlineList.size();
						for (int i = 0; i < listSize; i++) {
							onlineUsers[i] = onlineList.removeFirst();
							System.out.println(onlineUsers[i].getName() + " är online");
							
						}
						ui.updateOnlineUsers();
						ui.updateContacts();
					}

					else if (obj instanceof Message) {
						mess = (Message) obj;
						removeFriend(user);
						ui.updateContacts();
						ui.updateChatArea(mess);
						obj = null;
					}
					Thread.sleep(200);
				} catch (InterruptedException | ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public User[] getOnlineUsers() {
		return onlineUsers;
	}

	public static ArrayList<User> readFriends(String filename) {
		ArrayList<User> list = new ArrayList<User>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String[] parts;
			User friend;
			String str = br.readLine();
			int i = 0;
			while (str != null) {
				parts = str.split(",");
				friend = new User(parts[0], new ImageIcon(parts[1]));
				list.add(friend);
				str = br.readLine();
				System.out.println(list.get(i).getName() + " är tillagd som vän med bilden: " + friend.getImage());
				i++;

			}
			br.close();
		} catch (IOException e) {
			System.out.println("readPersons: " + e);
		}
		return list;
	}

	public ArrayList<User> getFriends() {
		ArrayList<User> lawl = readFriends("files/friends.txt");
		return lawl;
		
	}
	
	public void addFriend(User newFriend) {
		friends.add(newFriend);
		writeFriendsToFile();
	}
	
	public void removeFriend(User oldFriend) {
		friends.remove(oldFriend);
		writeFriendsToFile();
	}
	
	public void writeFriendsToFile() {
		PrintWriter writer;
		
		
		try {
			writer = new PrintWriter("files/friends.txt", "UTF-8");
			for(int i = 0; i<friends.size(); i++){
				writer.println(friends.get(i).getName() + "," + friends.get(i).getImage());
				System.out.println(friends.get(i).getName());
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	public void sendMessage(Message message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createUser(String name, ImageIcon image) {

		user = new User(name, image);

		if (user == null)
			return;

		addFriend(user);
//		readFriends("files/friends.txt");

	}
	
	public User getUser() {
		return this.user;
	}

	public static void main(String[] args) {

		ClientUI ui = new ClientUI();
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.setVisible(true);
		Client client = new Client(ui);
//		Server server = new Server();


	}

}
