import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static PrintWriter out;
    private static BufferedReader in;
    private static BufferedReader userInput;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            // Read and print messages from the server (broadcast messages)
            Thread readThread = new Thread(new ReadMessages());
            readThread.start();

            // Prompt user for username and send it to the server
            System.out.println(in.readLine());  // Server prompt for username
            String username = userInput.readLine();
            out.println(username);

            // Send messages to the server
            String message;
            while (true) {
                message = userInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Error in client: " + e.getMessage());
        }
    }

    // Class to handle receiving messages from the server (other clients)
    static class ReadMessages implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.err.println("Error reading messages: " + e.getMessage());
            }
        }
    }
}
