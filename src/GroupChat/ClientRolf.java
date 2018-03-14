package GroupChat;

import java.util.HashMap;

import GroupChat.Server.ClientHandler;

public class ClientRolf {
	private HashMap<User, ClientHandler> clients = new HashMap<User, ClientHandler>();

	public synchronized void put(User user, ClientHandler client) {
		clients.put(user, client);
		System.out.println(clients.size());
	}

	public synchronized ClientHandler get(User user) {
		return clients.get(user);
	}

}
