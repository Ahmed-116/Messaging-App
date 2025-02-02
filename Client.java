import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private List<Contact> contacts = new ArrayList<>();
    Sms sms[]=new Sms[100];
    private int messageCounter=0;



    public static void main(String[] args) {
        //new Client("192.168.100.12", 12345);
        new Client("127.0.0.1", 12345);

    }


    public Client(String serverAddress, int port) {
        try {

            socket = new Socket(serverAddress, port);
            advanceContacts();
            new Thread(this).start(); // Start client thread
        } catch (IOException e) {
            System.out.println("Could not connect to server at " + serverAddress + ":" + port);
            e.printStackTrace();
        }
    }

    private void advanceContacts() {
        contacts.add(new Contact("1", "Wajahat", "0317080150"));
        contacts.add(new Contact("2", "Ahmad", "03039812367"));
    }

    private void MainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Contacts");
        System.out.println("2. Send Message");
        System.out.println("3. View Messages");
        System.out.println("4. Exit");
        System.out.print("Enter choice: ");
    }

    private void ContactsMenu() {
        System.out.println("\n--- Contacts ---");
        System.out.println("1. Add Contact");
        System.out.println("2. Delete Contact");
        System.out.println("3. View Contacts");
        System.out.println("4. Go Back");
        System.out.print("Enter choice: ");
    }



    private void SendMessage() {
        System.out.println("\n--- Send Message ---");
        System.out.println("1. Select Contact");
        System.out.println("2. Start New Chat");
        System.out.println("3. Go Back");
        System.out.print("Enter choice: ");
    }

    private void MenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1 -> manageContacts(scanner);
            case 2 -> sendMessageMenu(scanner);
            case 3 -> viewMessages(scanner);
            case 4 -> System.exit(0);
            default -> System.out.println("Invalid choice. Try again.");
        }
    }

    private void manageContacts(Scanner scanner) {
        while (true) {
            ContactsMenu();
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> addContact(scanner);
                case 2 -> deleteContact(scanner);
                case 3 -> viewAllContacts();
                case 4 -> { return; }


                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }



    private void sendMessageMenu(Scanner scanner) {
        while (true) {
            SendMessage();
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> selectContactToSendMessage(scanner);
                case 2 -> startNewChat(scanner);
                case 3 -> { return; } // Go to Main Menu
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addContact(Scanner scanner) {
        System.out.print("Enter Contact ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Contact Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Contact Number: ");
        String number = scanner.nextLine();
        contacts.add(new Contact(id, name, number));
        System.out.println("Contact added successfully!");
    }

    private void deleteContact(Scanner scanner) {
        System.out.print("Enter Contact ID to delete: ");
        String id = scanner.nextLine();
        Contact contact = findContactById(id);
        if (contact != null) {
            contacts.remove(contact);
            System.out.println("Contact deleted successfully!");
        } else {
            System.out.println("Contact not found.");
        }
    }

    private Contact findContactById(String id) {
        for (Contact contact : contacts) {
            if (contact.getId().equals(id)) {
                return contact;
            }
        }
        return null;
    }




    private void viewAllContacts() {
        System.out.println("Contacts:");
        if (contacts.isEmpty()) {
            System.out.println("No contacts to display.");
        } else {
            for (Contact contact : contacts) {
                System.out.println(contact);
            }
        }
    }



    private void selectContactToSendMessage(Scanner scanner) {
        viewAllContacts();
        System.out.print("Enter Contact ID to send message: ");
        String id = scanner.nextLine();
        Contact contact = findContactById(id);
        if (contact != null) {
            enterMessagingMode(scanner, contact.getName());
        } else {
            System.out.println("Contact not found.");
        }
    }

    private void startNewChat(Scanner scanner) {
        System.out.print("Enter New Contact Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter New Contact Number: ");
        String number = scanner.nextLine();
        String id = String.valueOf(contacts.size() + 1);
        Contact contact = new Contact(id, name, number);
        contacts.add(contact);
        System.out.println("New contact added successfully. Starting chat...");
        enterMessagingMode(scanner, contact.getName());
    }

    private void enterMessagingMode(Scanner scanner, String contactName) {
        System.out.println("Enter messages to " + contactName + " (enter '0' to exit messaging mode):");
        while (true) {
            String message = scanner.nextLine();
            if (message.equals("0")) {
                System.out.println("Exiting messaging mode...");
                break;
            }
            sendMessage(message);
            if(messageCounter< sms.length) {
                Sms sms0 = new Sms(message);

                sms[messageCounter] = sms0;
                messageCounter++;
            }
            else {
                System.out.println("Messages full");
            }
        }

    }
    public void viewMessages(Scanner scanner) {
        viewAllContacts();
        System.out.println("Enter the name of the contact to view all conversation:");
        String contactName = scanner.nextLine();
        Contact contact = null;

        for (Contact c : contacts) {
            if (c.getName().equals(contactName)) {
                contact = c;
                break;
            }
        }
        if (contact != null) {
            System.out.println("Your chat with "+contactName+":");
            for (int i=0;i<=sms.length&&sms[i]!=null;i++) {
                System.out.println(sms[i].toString());
            }
        }
        else {
            System.out.println("Contact not found.");
        }

    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);


            Scanner scanner = new Scanner(System.in);


            new Thread(() -> {
                try {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println((contacts.getFirst().getName()) +": " + inputLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Main loop for menu options
            while (true) {
                MainMenu();
                String choiceInput = scanner.nextLine();
                int choice = Integer.parseInt(choiceInput);
                MenuChoice(choice, scanner);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }


}
