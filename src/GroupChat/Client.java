package GroupChat;

import java.io.BufferedReader;
import java.io.File;
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
import javax.swing.JOptionPane;

public class Client implements Serializable {
	private int port = 5000;
	private User user;
	private Socket socket = null;
	private User[] onlineUsers;
	private ObjectOutputStream oos;
	private ClientUI ui;
	private ArrayList<User> friends = new ArrayList<User>();
	private ArrayList<User> currentReceivers = new ArrayList<User>();

	/*
	 * Standard constructor, attaches to a user interface
	 */

	public Client(ClientUI ui) {
		while (this.user == null) {
			try {
				createUser(ui.getUserName(), ui.getProfilePic());
			} catch (Exception e) {
				JOptionPane.showConfirmDialog(null, "You can't have an empty screen name!!!");
			}
		}
		this.ui = ui;
		Thread thread;
		thread = new Connect(this, user);
		thread.start();
	}

	/*
	 * Attaches the client software to the UI and adds the current user to the list
	 * of receivers
	 */
	public void startMainClient() {
		ui.setClient(this, user);
		currentReceivers.add(user);
	}

	/*
	 * Checks if the username is already taken
	 */
	public static void checkIfTaken(boolean notTaken) {
		if (notTaken = false) {
			JOptionPane.showConfirmDialog(null, "USer already taken lmao moron");
		}
	}

	/*
	 * Internal class for handling the connection, runs as a separate thread
	 */
	public class Connect extends Thread {
		private Client client;
		private User user;
		private Message message;
		private Thread receiverThread;

		public Connect(Client client, User user) {
			this.client = client;
			this.user = user;
			friends = readFriends("files/friends.txt");

		}

		public void run() {
			try {
				socket = new Socket("localhost", port);
				oos = new ObjectOutputStream(socket.getOutputStream());

				System.out.println("Klient kopplad till server");

				receiverThread = new MessageReceiver();
				receiverThread.start();

				oos.writeObject(user);
				sendMessage(message);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Disconnects the client from the server by closing the socket
	 */
	public void disconnect() {
		try {
			socket.close();
			System.out.println("Disconnected from server");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Internal class for receiving messages, runs as a separate thread
	 */
	public class MessageReceiver extends Thread {

		ObjectInputStream in;

		public MessageReceiver() {
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

					if (obj instanceof Message) {
						mess = (Message) obj;
						if (mess.getSender() == null) {
							if (mess.getTextMsg() == null) {
								JOptionPane.showMessageDialog(null, "Ajabaja, fel lösenord!");
								System.exit(1);
							} else {
								System.out.println("jag blev accepterad!");
								startMainClient();
							}
						} else {
							ui.updateChatArea(mess);
						}
					}

					else if (obj instanceof LinkedList) {
						LinkedList<User> onlineList = (LinkedList) obj;
						onlineUsers = new User[onlineList.size()];

						int contactIndex = 0;
						while (!onlineList.isEmpty()) {

							onlineUsers[contactIndex] = onlineList.removeFirst();
							contactIndex++;
						}

						ui.updateOnlineUsers();
						ui.updateContacts();
					}

				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Returns an array of the users that are currently online
	 */
	public User[] getOnlineUsers() {
		return onlineUsers;
	}

	/*
	 * Reads the given file and returns the results as an ArrayList
	 */
	public static ArrayList<User> readFriends(String filename) {
		ArrayList<User> list = new ArrayList<User>();

		File file = new File(filename);

		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Abandon ship");
				System.exit(0);
			}
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String[] parts;
			User friend;
			String str = br.readLine();
			int i = 0;
			while (str != null) {
				parts = str.split(",");
				friend = new User(parts[0], null);
				list.add(friend);
				str = br.readLine();
				i++;
			}
			br.close();
		} catch (IOException e) {
			System.out.println("readPersons: " + e);
		}
		// return list;
		return (ArrayList<User>) list.clone();
	}

	/*
	 * Reads the friends file and returns the results as an ArrayList
	 */
	public ArrayList<User> getFriends() {
		ArrayList<User> returnVal = readFriends("files/friends.txt");
		return returnVal;

	}

	/*
	 * Adds the given user as a friend and saves the changes to the friends file
	 */
	public void addFriend(User newFriend) {

		friends.add(newFriend);
		System.out.println(newFriend.getName() + " �r tillagd i kontakter");
		writeFriendsToFile();
	}

	/*
	 * Removes the given Receiver from the receivers list
	 */
	public void clearReceiver(User newReceiver) {
		for (int i = 0; i < currentReceivers.size(); i++) {
			if (currentReceivers.get(i).getName().equals(newReceiver.getName())) {
				currentReceivers.remove(i);
			}
		}
	}

	/*
	 * Removes the given friend from the friends list and saves the changes to the
	 * friends file
	 */
	public void removeFriend(User oldFriend) {
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).getName() == oldFriend.getName()) {
				friends.remove(i);
			}
		}
		writeFriendsToFile();
	}

	/*
	 * Saves the current list of friends as a file
	 */
	public void writeFriendsToFile() {
		PrintWriter writer;

		try {
			writer = new PrintWriter("files/friends.txt", "UTF-8");
			for (int i = 0; i < friends.size(); i++) {
				writer.println(friends.get(i).getName() + "," + friends.get(i).getImage());
			}

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Sends a message to the current list of receivers
	 */
	public void sendMessage(Message message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Creates a new user using a name and an image
	 */
	public void createUser(String name, ImageIcon image) {

		user = new User(name, image);

		if (user == null)
			System.out.println("user �r null");
		return;

	}

	/*
	 * Returns the current user
	 */
	public User getUser() {
		return this.user;
	}

	/*
	 * Returns an ArrayList of the current receivers
	 */
	public ArrayList<User> getReceivers() {
		return currentReceivers;
	}

	public static void main(String[] args) {
//		Server server = new Server();
		ClientUI ui = new ClientUI();
//		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.setVisible(true);
		Client client = new Client(ui);

		ClientUI ui2 = new ClientUI();
//		ui2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui2.setVisible(true);
		Client client2 = new Client(ui2);
	}

	/*
	 * Checks the given message for possible commands and executes those
	 */
	public void checkCommands(Message message) {
		String check = message.getTextMsg();

		if (check.contains("/remove ")) {
			check = check.replace("/remove ", "");
			for (int i = 0; i < friends.size(); i++) {
				if (friends.get(i).getName().equals(check)) {
					removeFriend(friends.get(i));
					ui.updateContacts();
					ui.updateOnlineUsers();
					i++;
					return;
				}

			}

		}

		else if (check.contains("/add ")) {
			check = check.replace("/add ", "");
			for (int i = 0; i < onlineUsers.length; i++) {
				if (onlineUsers[i] != null && onlineUsers[i].getName().equals(check)) {
					if (!readFriends("files/friends.txt").contains(onlineUsers[i])) {
						addFriend(onlineUsers[i]);
						ui.updateContacts();
						return;
					}
				}
			}

		}

		// check if user typed /send <User>
		else if (check.contains("/send ")) {
			check = check.replace("/send ", "");

			for (int i = 0; i < friends.size(); i++) {

				User userToCheck = friends.get(i);

				if (userToCheck.getName().equals(check)) {

					if (isReceiver(userToCheck)) {

						System.out.println(onlineUsers[i] + " är redan i listan");

						return;
					}

					currentReceivers.add(userToCheck);
					ui.newReceiverPic(userToCheck.getImage());
					System.out.println(userToCheck.getName() + " är tillagd i mottagarlistan");
					
					return;

				}
			}

		}

		else if (check.contains("/clear ")) {
			check = check.replace("/clear ", "");
			for (int i = 0; i < onlineUsers.length; i++) {
				if (onlineUsers[i].getName().equals(check)) {
					clearReceiver(onlineUsers[i]);
					ui.removeReceiverPic(onlineUsers[i].getImage());
					return;
				}
			}
		}
		message.setReceivers((ArrayList<User>) currentReceivers.clone());
		sendMessage(message);
	}

	/**
	 * Returns true or false depending on whether the passed User is in the
	 * currentReceiver-list, enabling the user to receive messages.
	 * 
	 * @param user
	 *            Object to be checked for membership in list
	 * @return  
	 *            True if user is in the list, false if not.
	 */
	public boolean isReceiver(User user) {
		boolean isReceiver = false;

		if (currentReceivers != null) {
			for (int i = 0; i < currentReceivers.size(); i++) {

				if (currentReceivers.get(i).getName().equals(user.getName())) {
					isReceiver = true;
				}
			}
		}

		return isReceiver;
	}
}
