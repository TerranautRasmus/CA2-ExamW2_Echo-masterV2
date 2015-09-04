package echoclient;

import echoserver.ClientHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

public class EchoClient extends Observable implements Runnable
{
  Socket socket;
  private int port;
  private InetAddress serverAddress;
  private Scanner input;
  private PrintWriter output;
  BufferedReader input2;
  
  public void connect(String address, int port) throws UnknownHostException, IOException
  {
    this.port = port;
    serverAddress = InetAddress.getByName(address);
    socket = new Socket(serverAddress, port);
    input2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    input = new Scanner(socket.getInputStream());
    output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
  }
  
  public void send(String msg)
  {
    output.println(msg);
  }
  
  public void stop() throws IOException {
    output.println(ProtocolStrings.STOP);
  }
  
  @Override
  public void run()
  {
    String msg = "";
    try {
        msg = input2.readLine();
    } catch (IOException ex) {
        Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    if(msg.equals(ProtocolStrings.STOP)){
      try {
        stop();
        socket.close();
      } catch (IOException ex) {
        Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    setChanged();
    notifyObservers(msg);
  }
}
