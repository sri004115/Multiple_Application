import java.util.Scanner;
import java.io.*;
import java.nio.file.*;

public class BankAppJava {

    private static final String DB_FILE = "bank.db";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nBank Application");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. View Transaction History");
            System.out.println("4. Delete Account");
            System.out.println("5. Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createAccount(scanner);
                    break;
                case 2:
                    login(scanner);
                    break;
                case 3:
                    viewTransactionHistory(scanner);
                    break;
                case 4:
                    deleteAccount(scanner);
                    break;
                case 5:
                    System.out.println("Exiting application.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void createAccount(Scanner scanner) throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILE, true))) {
            writer.write(username + "," + password + ",0.0\n");
        }

        System.out.println("Account created successfully.");
    }

    private static void login(Scanner scanner) throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    found = true;
                    accountMenu(scanner, username, Double.parseDouble(parts[2]));
                    break;
                }
            }

            if (!found) {
                System.out.println("Invalid username or password.");
            }
        }
    }

    private static void accountMenu(Scanner scanner, String username, double balance) throws IOException {
        while (true) {
            System.out.println("\nAccount Menu");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Logout");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.printf("Balance: $%.2f\n", balance);
                    break;
                case 2:
                    System.out.print("Enter deposit amount: ");
                    double deposit = scanner.nextDouble();
                    balance += deposit;
                    logTransaction(username, "Deposit", deposit);
                    System.out.println("Deposit successful.");
                    break;
                case 3:
                    System.out.print("Enter withdrawal amount: ");
                    double withdraw = scanner.nextDouble();
                    if (withdraw > balance) {
                        System.out.println("Insufficient balance.");
                    } else {
                        balance -= withdraw;
                        logTransaction(username, "Withdraw", withdraw);
                        System.out.println("Withdrawal successful.");
                    }
                    break;
                case 4:
                    updateBalance(username, balance);
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void updateBalance(String username, double balance) throws IOException {
        File tempFile = new File("temp.db");
        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    writer.write(username + "," + parts[1] + "," + balance + "\n");
                } else {
                    writer.write(line + "\n");
                }
            }
        }

        Files.delete(Paths.get(DB_FILE));
        Files.move(tempFile.toPath(), Paths.get(DB_FILE));
    }

    private static void logTransaction(String username, String type, double amount) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(username + "_transactions.log", true))) {
            writer.write(type + ": $" + amount + "\n");
        }
    }

    private static void viewTransactionHistory(Scanner scanner) {
        System.out.print("Enter username to view transaction history: ");
        String username = scanner.nextLine();

        File file = new File(username + "_transactions.log");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                System.out.println("\nTransaction History:");
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading transaction history: " + e.getMessage());
            }
        } else {
            System.out.println("No transaction history found for user: " + username);
        }
    }

    private static void deleteAccount(Scanner scanner) throws IOException {
        System.out.print("Enter username to delete account: ");
        String username = scanner.nextLine();

        File tempFile = new File("temp.db");
        boolean accountFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    accountFound = true;
                } else {
                    writer.write(line + "\n");
                }
            }
        }

        if (accountFound) {
            Files.delete(Paths.get(DB_FILE));
            Files.move(tempFile.toPath(), Paths.get(DB_FILE));
            System.out.println("Account deleted successfully.");

            File transactionFile = new File(username + "_transactions.log");
            if (transactionFile.exists()) {
                transactionFile.delete();
            }
        } else {
            System.out.println("No account found with username: " + username);
        }
    }
}
