package GroupChat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connect extends Thread
{
	private ObjectOutputStream out;
	private Client client;
	private User user;
	private Message massage;
	private int portMin = 4473;
	private int portMax = 4483;
	private int port = portMin;
	
	public Connect(Client client, User user)
	{
		this.client = client;
		this.user = user;
		massage = new Message("hej meddelande från clienten", null, user, user);
	}
	
	public void run()
	{
		Socket socket = null;
		
		boolean attemptingToStart = true;
		while (attemptingToStart)
		{
			try
			{
				socket = new Socket("localhost", port);
				
				out = new ObjectOutputStream(socket.getOutputStream());
				
				System.out.println("Klient kopplad till server");
				attemptingToStart = false;
				
				client.startRecieve(socket);
				
				out.writeObject(user);
				out.writeObject(client);
				while (true)
				{
					out.writeObject(massage);
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
			}
			catch (UnknownHostException e)
			{
				System.out.println("Something failed, attempting port: " + port);
				if (port <= portMax)
				{
					port++;
				}
				else
				{
					port = portMin;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}