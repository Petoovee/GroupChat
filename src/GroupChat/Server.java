package GroupChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
	private Thread serverThread;
	private LinkedList<ClientHandler> clients = new LinkedList<ClientHandler>();
	private ClientRolf clientList = new ClientRolf();

	private int port = 4473;

	public Server() {
		serverThread = new Connect();
		serverThread.start();
	}

	/**
	 * 
	 * @author Anton
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
				System.out.println("Server startad");
			} catch (IOException e2) {
				System.out.println("Could not listen to port");
				System.exit(1);
			}

			while (!Thread.interrupted()) {
				try {

					socket = serverSocket.accept();

					clients.add(new ClientHandler(socket));
					clients.getLast().start();
				} catch (IOException e1) {
					System.out.println("Accept failed on port");
				}
			}
		}
	}

	/**
	 * 
	 * @author Anton
	 * 
	 *         Klass med en tråd som sköter kommunikationen till och från
	 *         klienten
	 *
	 */
	public class ClientHandler extends Thread {
		private Socket socket;
		private User user;
		private Client client;
		private ObjectOutputStream outToClient;
		private Message newMessage = null;
		private Message oldMessage = null;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		public User getUser() {
			return user;
		}
		
		public void sendMessage(Message mess) {
			try {
				outToClient.writeObject(mess);
				outToClient.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				outToClient = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inp = new ObjectInputStream(socket.getInputStream());
				while (!Thread.interrupted()) {
			

					Object obj = inp.readObject();

					if (obj instanceof User) {

						user = (User) obj;
						
						System.out.println(user.getName() + " has connected");
						Thread.sleep(10);
					}
					 else if (obj instanceof Client) {
					 client = (Client) obj;
					 
					 clientList.put(user, this);
					
					 }
					else if (obj instanceof Message){
						newMessage = (Message) obj;
						
						if(newMessage!=oldMessage) {
							clientList.get(newMessage.getRecievers()).sendMessage(newMessage);;
							oldMessage=newMessage;
						}
					}
					Thread.sleep(50);
				}
			} catch (IOException e) {
				System.out.println("Could not read/write object");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

}
