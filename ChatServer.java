package com.example.chatapplication;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private static final int PORT =12345;
	private static Set<PrintWriter> clientWriters=Collections.synchronizedSet(new HashSet<>());

	public static void startServer() {
		System.out.println("Server started....Waiting for clients");
		try(ServerSocket serverSocket=new ServerSocket(PORT)){
			while(true) {
				Socket clientSocket=serverSocket.accept();
				System.out.println("New client connected: "+clientSocket.getInetAddress());
				new ClientHandler(clientSocket).start();
			}
		}
		catch(IOException e) {
			System.out.println("Server Error: "+e.getMessage());
		}
	}
	
	private static class ClientHandler extends Thread{
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;
	
		public ClientHandler(Socket socket) {
			this.socket=socket;
		}
		
		public void run() {
			try {
				in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out=new PrintWriter(socket.getOutputStream(),true);
				
				clientWriters.add(out);
				
				String message;
				while((message=in.readLine())!=null) {
					System.out.println("Message from client: "+ message);
					broadcast(message);
				}
			}
			catch(IOException e) {
				System.out.println("Client disconnected");
			}finally {
				try {
					socket.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
				clientWriters.remove(out);
			}
		}
		
		private void broadcast(String message) {
			synchronized(clientWriters) {
				for(PrintWriter writer: clientWriters) {
					writer.println(message);
				}
			}
		}
	}
	
	public static void main(String[] args) {
	    ChatServer.startServer();
	}
}
