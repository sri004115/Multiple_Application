import java.util.*;
import java.io.*;
import java.nio.file.*;

public class PasswordGeneratorJava {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Random Password Generator");

        System.out.print("Enter password length: ");
        int length = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Use uppercase letters? (Y/N): ");
        boolean useUppercase = scanner.nextLine().equalsIgnoreCase("Y");

        System.out.print("Use lowercase letters? (Y/N): ");
        boolean useLowercase = scanner.nextLine().equalsIgnoreCase("Y");

        System.out.print("Use digits? (Y/N): ");
        boolean useDigits = scanner.nextLine().equalsIgnoreCase("Y");

        System.out.print("Use symbols? (Y/N): ");
        boolean useSymbols = scanner.nextLine().equalsIgnoreCase("Y");

        if (length <= 0) {
            System.out.println("Password length must be greater than 0.");
            return;
        }

        String password = generatePassword(length, useUppercase, useLowercase, useDigits, useSymbols);

        if (password.isEmpty()) {
            System.out.println("Invalid character set selection. Cannot generate password.");
            return;
        }

        System.out.println("\nGenerated Password: " + password);

        System.out.print("Save the password to a file? (Y/N): ");
        boolean saveToFile = scanner.nextLine().equalsIgnoreCase("Y");

        if (saveToFile) {
            System.out.print("Enter the filename: ");
            String filename = scanner.nextLine();
            try {
                savePasswordToFile(password, filename);
                System.out.println("Password saved to " + filename);
            } catch (IOException e) {
                System.out.println("Error saving password to file: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static String generatePassword(int length, boolean useUppercase, boolean useLowercase, boolean useDigits, boolean useSymbols) {
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_+=<>?";

        StringBuilder characterSet = new StringBuilder();

        if (useUppercase) characterSet.append(uppercaseLetters);
        if (useLowercase) characterSet.append(lowercaseLetters);
        if (useDigits) characterSet.append(digits);
        if (useSymbols) characterSet.append(symbols);

        if (characterSet.length() == 0) return "";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterSet.length());
            password.append(characterSet.charAt(index));
        }

        return password.toString();
    }

    private static void savePasswordToFile(String password, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(password);
        }
    }
}
