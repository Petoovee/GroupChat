package GroupChat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JFrame;

public class Server {
	private Thread serverThread;
	private LinkedList<ClientHandler> clients = new LinkedList<ClientHandler>();
	private ClientRolf clientList = new ClientRolf();
	private LinkedList<User> userList = new LinkedList<User>();
	private ArrayList<User> currentUsers = new ArrayList<User>();
	private ArrayList<User> allUsers = new ArrayList<User>();
	private ArrayList<User> oldUsers = new ArrayList<User>();
	private ArrayList<String> allMessages = new ArrayList<String>();
	private int port = 5000;
	private ServerUI ui;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private HashMap<String, Message> offlineMessages = new HashMap<String, Message>();

	public Server() {
		serverThread = new Connect();
		serverThread.start();
	}

	/**
	 * ger servern tillgång till ServerUI
	 * 
	 * @param ui
	 *            == ServerUI
	 */
	public void setUI(ServerUI ui) {
		this.ui = ui;
	}

	/**
	 * 
	 * @author Anton, Maida, Petar, Sara, Malin, Antoine
	 * 
	 *         Skapar anslutningar till och från klienter och lägger in varje
	 *         anslutning till klienter i clients-listan
	 *
	 */
	public class Connect extends Thread {
		public void run() {

			ServerSocket serverSocket = null;
			Socket socket = null;

			try {
				serverSocket = new ServerSocket(port);
				System.out.println("Server started");
			} catch (IOException e2) {
				System.out.println("Could not listen to port");
				System.exit(1);
			}

			while (!Thread.interrupted()) {
				try {

					socket = serverSocket.accept();
					ClientHandler ch = new ClientHandler(socket);
					new Thread(ch).start();

				} catch (IOException e1) {
					System.out.println("Accept failed on port");
				}
			}
		}
	}

	/**
	 * 
	 * @author Anton, Petar, Maida, Malin, Antoine, Sara
	 * 
	 *         Klass med en tråd som sköter kommunikationen till och från klienten
	 */
	public class ClientHandler implements Runnable {
		private Socket socket;
		private User user;
		private ObjectOutputStream oos;

		public ClientHandler(Socket socket) {
			this.socket = socket;
			clients.add(this);
		}

		public User getUser() {
			return user;
		}

		/**
		 * Skickar Message-objekt till klienten via en output-stream.
		 * 
		 * @param mess
		 *            = Message-objektet som ska skickas vidare.
		 * 
		 */
		public void sendMessage(Message mess) {
			try {
				oos.writeObject(mess);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Skickas vidare en kopia av en lista av användare som är online till klienten.
		 */
		public void sendOnlineUsers() {
			try {
				oos.writeObject(userList.clone());
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Kontrollerar ifall användaren den får in är online.
		 * 
		 * @param userContact
		 *            == User-objekt som metoden ska kontrollera
		 * @return == boolean som visar true om användaren är online, annars false
		 */

		public boolean isUserOnline(User userContact) {
			boolean userOnline = false;
			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).getName().equals(userContact.getName())) {
					userOnline = true;
				}
			}
			return userOnline;
		}

		/**
		 * Servern lyssnar efter objekt från en inputstream med en öppen tråd,
		 * kontinuerligt.
		 * 
		 * Om objekt är User kollar servern ifall användarnamnet redan är taget. Om namn
		 * inte är taget läggs användaren till i användarlista, och till en fil med
		 * användarlista.
		 * 
		 * Är användarnamnet redan taget, och den valda profilbilden inte matchar Userns
		 * användarnamn, så måste man "logga in" igen med nytt namn och bild.
		 * 
		 * Är användarnamnet redan taget kollar servern om den valda profilbilden
		 * matchar Usern med samma användarnamn, och "loggar in" ifall de matchar. Usern
		 * får då osända meddelanden som skickats när hen varit offline.
		 * 
		 * Om objekt är Message kontrollerar servern vem sändaren är och vem mottagaren
		 * är, och skickar det rätt utefter den informationen.
		 *
		 *
		 */
		public void run() {
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

				// Servern lyssnar efter objekt fr�n en inputstream med en �ppen tr�d ALL DE
				// TIME.
				while (!Thread.interrupted()) {
					Object obj = ois.readObject();
					boolean userNameTaken = false;
					boolean loggedIn = false;

					// Om objekt
					if (obj instanceof User) {
						user = (User) obj;
						Message acceptMessage = new Message("accepted", null, null, null);
						Message declineMessage = new Message(null, null, null, null);
						// kollar om användaren finns i systemet
						for (int i = 0; i < currentUsers.size(); i++) {
							if (user.getName().equals(currentUsers.get(i).getName())) {
								userNameTaken = true;

								// kollar om bilden stämmer med namnet
								if (user.getImagePath().equals(currentUsers.get(i).getImagePath())) {
									loggedIn = true;
								}
							}
						}

						if (!userNameTaken || loggedIn) {
							currentUsers.add(user);
							userList.add(user);
							writeUsersToFile();
							clientList.put(user, this);
							oos.writeObject(acceptMessage);
							oos.flush();
							oos.reset();
							ui.setUserList();
							if (offlineMessages.containsKey(user.getName())) {
								sendMessage(offlineMessages.get(user.getName()));
							}
						} else {
							oos.writeObject(declineMessage);
							oos.flush();
							oos.reset();
						}
						sendOnlineUsersToClients();

					} else if (obj instanceof Message) {
						Message newMessage = (Message) obj;
						Date date = new Date();
						newMessage.setDate(dateFormat.format(date));

						allMessages.add(dateFormat.format(date) + " " + newMessage.getSender().getName() + ": "
								+ newMessage.getTextMsg());
						for (int i = 0; i < newMessage.getReceivers().size(); i++) {
							if (isUserOnline(newMessage.getReceivers().get(i))) {
								getReceiverHandler(newMessage.getReceivers().get(i)).sendMessage(newMessage);
							} else {
								offlineMessages.put(newMessage.getReceivers().get(i).getName(), newMessage);
							}
						}

						int numberOfReceivers = newMessage.getReceivers().size();
						for (int i = 0; i < numberOfReceivers; i++) {
							if (isUserOnline(newMessage.getReceivers().get(i))) {
								for (int o = 0; o < clients.size(); o++) {
									System.out.println("");
								}
							} else {
								offlineMessages.put(newMessage.getReceivers().get(i).getName(), newMessage);
							}
						}
					}
					currentUsers.remove(user);
					obj = null;
				}

			} catch (IOException e) {
				currentUsers.remove(user);
				removeUserFromUserList(user);
				clientList.remover(user);
				System.out.println("Could not read/write object");
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				currentUsers.remove(user);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returnerar en clienthandler från hjälpklassens hashmap som matchar med
	 * User-argumentet.
	 * 
	 * @param userCheck
	 *            användaren vars clienthandler skall returneras
	 * @return
	 */
	public ClientHandler getReceiverHandler(User userCheck) {
		for (int i = 0; i < userList.size(); i++) {
			if ((userList.get(i).toString()).equals(userCheck.toString())) {
				return clientList.get(userList.get(i));
			}
		}
		return null;
	}

	public void removeUserFromUserList(User user) {
		for (int i = 0; i < userList.size(); i++) {
			System.out.println(user.getName());
			System.out.println(userList.get(i).getName());

			if (user.getName().equals(userList.get(i).getName())) {
				userList.remove(i);
			}
		}
	}

	public ArrayList<User> getCurrentUsers() {
		return currentUsers;
	}

	public void sendOnlineUsersToClients() {
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i) != null) {
				clientList.get(userList.get(i)).sendOnlineUsers();
			}
		}
	}

	/**
	 * Metoden hämtar och returnerar alla meddelanden från startpunkten till
	 * slutpunkten som skickas in
	 * 
	 * @param start
	 *            == startpunkt för meddelandelistan
	 * @param end
	 *            == slutpunkt för meddelandelistan
	 * @return == ArrayList<String> med alla meddelande
	 */
	public ArrayList<String> getMessages(String start, String end) {
		ArrayList<String> messagesList = new ArrayList<String>();
		String startPointString = start.replace("/", "");
		String endPointString = end.replace("/", "");
		String arrayPointString;
		try {
			int startPoint = Integer.parseInt(startPointString);
			int endPoint = Integer.parseInt(endPointString);
			int arrayPoint;
			for (int i = 0; i < allMessages.size(); i++) {
				arrayPointString = allMessages.get(i).substring(0, 10).replace("/", "");
				arrayPoint = Integer.parseInt(arrayPointString);
				if (arrayPoint >= startPoint && arrayPoint <= endPoint) {
					messagesList.add(allMessages.get(i));
				}
			}
			if (messagesList.isEmpty()) {
				messagesList.add("Inga meddelande mellan angivna datum");
			}
		} catch (NumberFormatException e) {
			messagesList.add("Endast siffror och snedstreck är tillåtna");
		}
		return messagesList;
	}

	/**
	 * Skriver ned alla användare från användarlistan till en textfil och sparar dem
	 * med namn och bild.
	 */
	public void writeUsersToFile() {
		try {
			ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(new File("files/users.txt")));
			oldUsers = (ArrayList<User>) fileInput.readObject();
		} catch (IOException | ClassNotFoundException e1) {
		}

		for (int i = 0; i < currentUsers.size(); i++) {
			if (!allUsers.contains(currentUsers.get(i))) {
				allUsers.add(currentUsers.get(i));
			}
		}

		for (int i = 0; i < oldUsers.size(); i++) {
			if (!allUsers.contains(oldUsers.get(i))) {
				allUsers.add(oldUsers.get(i));
			}
		}

		try {
			ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(new File("files/users.txt")));
			fileOutput.flush();
			fileOutput.writeObject(allUsers);
			fileOutput.flush();
			fileOutput.close();
		} catch (IOException e1) {
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		JFrame frame = new JFrame("Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new ServerUI(server));
		frame.setSize(600, 500);
		frame.setVisible(true);
	}
}