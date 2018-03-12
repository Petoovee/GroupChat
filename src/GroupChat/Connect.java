package GroupChat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connect extends Thread {
	private int port = 4462;
	private ObjectOutputStream out;
	private Client client;
	private User user;
	private Message massage;
	
	public Connect(Client client, User user) {
		this.client=client;
		this.user=user;
		massage = new Message("hej meddelande från clienten", null, user, user);
	}
	public void run() {
		Socket socket = null;
		

		try {
			socket = new Socket("localhost", port);

			out = new ObjectOutputStream(socket.getOutputStream());

			System.out.println("Klient kopplad till server");

			client.startRecieve(socket);

			out.writeObject(user);
			out.writeObject(client);
			while (true) {
				out.writeObject(massage);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
