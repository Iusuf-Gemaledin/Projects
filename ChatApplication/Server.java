package ChatApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
	
	private ArrayList<ConnectionHandler> connections;
	private ServerSocket server;
	private boolean done;
	private ExecutorService pool;
	
	public Server() {
		connections = new ArrayList<>();
		done = false;
	}
	
	@Override
	public void run () {
		try {
			server = new ServerSocket(9999);
			pool = Executors.newCachedThreadPool();
			while (!done) {
				Socket client = server.accept();
				ConnectionHandler handler = new ConnectionHandler(client);
				connections.add(handler);
				pool.execute(handler);
			}
		} catch (Exception e) {
			shutdown();
		}
	}
	
	public void broadcast (String message) {
		for (ConnectionHandler ch : connections) {
			if (ch != null) {
				ch.sendMessage(message);
			}
		}
	}
	
	public void shutdown() {
	    done = true;
	    pool.shutdown(); //35:09
	    try {
	        if (server != null && !server.isClosed()) {
	            server.close();
	        }
	        for (ConnectionHandler ch : connections) {
	            ch.shutdown();
	        }
	    } catch (IOException e) {
	        // ignore
	    }
	}

		
	class ConnectionHandler implements Runnable {
			
		private Socket client;
		private BufferedReader in;
		private PrintWriter out;
		private String nickname;
		private boolean goodNickname = true;
		
		public ConnectionHandler(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			
			try {
				out = new PrintWriter (client.getOutputStream(), true);
				in = new BufferedReader (new InputStreamReader(client.getInputStream()));
				//out.println("Hello World!");		= sending a message
				//in.readLine();					= receive a message
				
				out.print("Please input a nickname: ");
				nickname = in.readLine();
				
				if (nickname.isBlank() || nickname.isEmpty()) {
					goodNickname = false;
				}
				while (nickname == null || nickname.isBlank()) {
				    out.println("Please enter a valid nickname!");
				    nickname = in.readLine();
				}
				
				System.out.println(nickname + " connected!"); 
				broadcast(nickname + " joined the chat!");
				
				String message = null;
				while ((message = in.readLine()) != null) {
					if (message.startsWith("/nick ")) {
						String[] messageSplit = message.split(" ", 2);
						if (messageSplit.length == 2) {
							broadcast(nickname + " has changed their nickname to: " + messageSplit[1]);
							System.out.println(nickname + " has changed their nickname to: " + messageSplit[1]);
							nickname = messageSplit[1];
							out.println("Nickname changed succesfully to: " + nickname);
							
						} else {
							out.println("No nickname provided!");
						}
						
					} else if (message.startsWith("/quit")) {
						broadcast(nickname + " has left the chat!");
						shutdown();
						
					} else {
						broadcast(nickname + ": " + message);
					}
				}
				
			} catch (IOException e) {
				shutdown();
			}
		}
		
		public void sendMessage(String message) {
			out.println(message);
		}
		
		public void shutdown() {
			try {
				in.close();
				out.close();
				if(!client.isClosed()) {
					client.close();
				}
				
			} catch (IOException e) {
				//ignore; nothing can be done
			}
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
}
