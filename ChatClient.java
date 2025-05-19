package com.example.chatapplication;

import java.io.*;
import java.util.Scanner;
import java.net.*;

public class ChatClient {
	private static final String SERVER_IP="localhost";
	private static final int SERVER_PORT=12345;
	
	public static void startClient() {
		try(Socket socket=new Socket(SERVER_IP,SERVER_PORT);
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
			Scanner sc=new Scanner(System.in))
				{
					System.out.println("Server connected...");
					Thread readerThread=new Thread(()->{
						try {
							String message;
							while((message=in.readLine())!=null)
								System.out.println("Server "+message);
						}
						catch(IOException e) {
							System.out.println("Disconnected from server "+e);
						}
					});
					readerThread.start();
					
					while(true) {
						String msgToSend=sc.nextLine();
						out.println(msgToSend);
					}
				}
		catch(IOException e) {
			System.out.println("Unnable to connect to server "+e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		startClient();
	}
	
}
