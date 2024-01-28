import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your name: ");
            String clientName = scanner.nextLine();

            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Mengirimkan nama klien ke server
            out.println(clientName);

            new Thread(() -> {
                try {
                    Scanner in = new Scanner(socket.getInputStream());
                    while (true) {
                        if (in.hasNextLine()) {
                            String serverMessage = in.nextLine();
                            System.out.println(serverMessage);

                            // Memberikan pesan jika klien terputus dari server
                            if (serverMessage.equals("Server: Connection closed by the server")) {
                                System.out.println("You are disconnected from the server.");
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    while (true) {
                        String clientMessage = scanner.nextLine();

                        // Memberikan pilihan untuk mengakhiri koneksi
                        if (clientMessage.equalsIgnoreCase("exit")) {
                            System.out.println("Exiting... Goodbye!");
                            out.println(clientName + " left the chat");
                            break;
                        }

                        // Mengirim pesan ke server bersama dengan nama klien
                        out.println(clientName + ": " + clientMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        // Menutup koneksi saat klien memilih keluar
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
