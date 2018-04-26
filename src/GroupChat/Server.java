package GroupChat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	public Server() {
		serverThread = new Connect();
		serverThread.start();
	}
	
	/**
	 * ger servern tillgång till ServerUI
	 * @param ui == ServerUI
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
					System.out.println("jämför " + (userList.get(i).getName() + " och " + userContact.getName()));
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
						System.out.println(user.getImagePath());
						
						
						// kollar om användaren finns i systemet
						for (int i = 0; i < currentUsers.size(); i++) {
							if (user.getName().equals(currentUsers.get(i).getName())) {
								System.out.println("logged1");
								System.out.println(user.getName());
								System.out.println(currentUsers.get(i).getName());
								userNameTaken = true;
								Client.checkIfTaken(userNameTaken);
								acceptMessage = new Message(null, null, null, null);

								// kollar om bilden stämmer med namnet
								if (user.getImagePath().equals(currentUsers.get(i).getImagePath())) {
									acceptMessage = new Message("accepted", null, null, null);
									loggedIn = true;
									System.out.println("logged");
								}
							}
						}
						oos.writeObject(acceptMessage);
						if (!userNameTaken) {
							currentUsers.add(user);
							System.out.println(user.getName() + " Ã¤r tillagd i systemet");
							writeUsersToFile();
							userList.add(user);
							clientList.put(user, this);
							ui.setUserList();
						}
						if (loggedIn) {
							Message[] offlineMessage = getOfflineMessage(user);
							for (int i = 0; i < offlineMessage.length; i++) {
								sendMessage(offlineMessage[i]);
							}
						}
						sendOnlineUsersToClients();

					} else if (obj instanceof Message) {
						Message newMessage = (Message) obj;
						Date date = new Date();
						allMessages.add(dateFormat.format(date) + " " + newMessage.getSender().getName() + ": " + newMessage.getTextMsg());
						
						for (int i = 0; i < newMessage.getReceivers().size(); i++) {
							if(isUserOnline(newMessage.getReceivers().get(i))){
								getReceiverHandler(newMessage.getReceivers().get(i)).sendMessage(newMessage);
							}
							else {
								writeOfflineMessageToFile(newMessage.getSender().getName(),
								newMessage.getReceivers().get(i).getName(), newMessage.getTextMsg());
							}
						}

//						int numberOfReceivers = newMessage.getReceivers().size();
//						for (int i = 0; i < numberOfReceivers; i++) {
//							if (isUserOnline(newMessage.getReceivers().get(i))) {
//								for (int o = 0; o < clients.size(); o++) {
//									clients.get(o).sendOnlineUsers();
//								}
//							} else {
//								writeOfflineMessageToFile(newMessage.getSender().getName(),
//										newMessage.getReceivers().get(i).getName(), newMessage.getTextMsg());
//							}
//						}
					}
					currentUsers.remove(user); // Hopefully this won't cause invisible users, since it's ran when interrupted
					obj = null;
				}
				

			} catch (IOException e) {
				currentUsers.remove(user);
				System.out.println("Could not read/write object");
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
	
	public ArrayList<User> getCurrentUsers(){
		return currentUsers;
	}
	
	public void sendOnlineUsersToClients() {
		for(int i = 0; i<userList.size(); i++) {
			clientList.get(userList.get(i)).sendOnlineUsers();
		}
	}
	
	/**
	 * Metoden hämtar och returnerar alla meddelanden från startpunkten till slutpunkten som skickas in
	 * @param start == startpunkt för meddelandelistan
	 * @param end == slutpunkt för meddelandelistan
	 * @return == ArrayList<String> med alla meddelande
	 */
	public ArrayList<String> getMessages(String start, String end){
		ArrayList<String> messagesList = new ArrayList<String>();
		String startPointString = start.replace("/", "");
		String endPointString = end.replace("/", "");
		String arrayPointString;
		try {
			int startPoint = Integer.parseInt(startPointString);
			int endPoint = Integer.parseInt(endPointString);
			int arrayPoint;
			for(int i = 0; i<allMessages.size(); i++) {
				arrayPointString = allMessages.get(i).substring(0, 10).replace("/", "");
				arrayPoint = Integer.parseInt(arrayPointString);
				if(arrayPoint>=startPoint && arrayPoint<=endPoint) {
					messagesList.add(allMessages.get(i));
				}
			}
			if(messagesList.isEmpty()) {
				messagesList.add("Inga meddelande mellan angivna datum");
			}
		}catch (NumberFormatException e) {
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
			oldUsers = (ArrayList<User>)fileInput.readObject();
		} catch (IOException | ClassNotFoundException e1) {}
		
		for(int i = 0; i < currentUsers.size(); i++)
		{
			if(!allUsers.contains(currentUsers.get(i))) {
				allUsers.add(currentUsers.get(i));
			}
		}
		
		for(int i = 0; i < oldUsers.size(); i++)
		{
			if(!allUsers.contains(oldUsers.get(i))) {
				allUsers.add(oldUsers.get(i));
			}
		}
		
		try {
			ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(new File("files/users.txt")));
			fileOutput.flush();
			fileOutput.writeObject(allUsers);
			fileOutput.flush();
			fileOutput.close();
		} catch (IOException e1) {}
	}

	/**
	 * Kollar om användaren som metoden får in i parametern fått några meddelande
	 * som offline. Om användaren fått meddelande lägger den in meddelandet i en
	 * array och returnerar sedan arrayen när den hämtat alla meddelanden.
	 * 
	 * @param user
	 *            == användare som metoden kollar ifall den har fått meddelande
	 * @return En Message-array med alla meddelande som man user fått när den varit
	 *         offline
	 */

	/**
	 * Hämtar osända meddelanden och returnerar dessa till dess rättmätiga
	 * mottagare. Läser antal meddelanden som skall skickas via en annan metod som i
	 * sig hämtar meddelanden från en textfil, och matchar rätt sändare och
	 * mottagare med sparade Message-objekt.
	 * 
	 * @param user
	 *            Användare som de osända meddelanden ska levereras till.
	 * 
	 * @return
	 */

	public Message[] getOfflineMessage(User user) {
		String[] parts = readOfflineMessages().split("newStuff");
		String offlineMess = "";
		String senderName = "";
		int amountOfMessages = 0;
		for (int i = 0; i < parts.length; i++) {
			String userName = "receiver" + user.getName();
			if (parts[i].equals(userName)) {
				amountOfMessages++;
			}
		}
		Message[] offlineMessage = new Message[amountOfMessages];
		int messageIndex = 0;

		for (int i = 0; i < parts.length; i++) {
			String userName = "receiver" + user.getName();
			if (parts[i].equals(userName)) {
				offlineMess = parts[i + 1];
				senderName = parts[i - 1];
				User sender = new User(senderName, null);
				ArrayList<User> users = new ArrayList<User>();
				users.add(user);
				offlineMessage[messageIndex] = new Message(offlineMess, null, sender, users);
				messageIndex++;
			}
		}
		return offlineMessage;
	}

	/**
	 * Läser en textfil av oskickade meddelanden med en BufferedReader och skriver
	 * över dem i en sträng som returneras.
	 * 
	 * @return Sträng med oskickade meddelanden
	 * 
	 */
	public static String readOfflineMessages() {
		// ArrayList<User> list = new ArrayList<User>();
		String allMess = "";
		try {
			String filename = "files/offlineMessage.txt";
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String str = br.readLine();

			while (str != null) {
				allMess += str;
				str = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			System.out.println("readPersons: " + e);
		}
		return allMess;
	}

	/**
	 * Skriver meddelande till en fil som senare ska läsas av readOfflineMessages().
	 * Den sepparerar de olika delarn (sender, receiver, message) genom att skriva
	 * "newStuff" emellan dom.
	 * 
	 * @param sender
	 *            == User som skickade meddelandet
	 * @param receiver
	 *            == User som meddelandet ska till
	 * @param message
	 *            == meddelandet som en sträng
	 */
	public void writeOfflineMessageToFile(String sender, String receiver, String message) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("files/offlineMessage.txt", "UTF-8");
			writer.println(sender + "newStuff" + "receiver" + receiver + "newStuff" + message);
			writer.close();
		} catch (IOException e) {} 
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

