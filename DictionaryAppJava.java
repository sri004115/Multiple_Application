import java.util.Scanner;
import java.io.*;
import java.nio.file.*;

public class DictionaryAppJava {

    private static final String DB_FILE = "dictionary.db";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nDictionary Application");
            System.out.println("1. Add Word");
            System.out.println("2. List Words");
            System.out.println("3. Search Word");
            System.out.println("4. Delete Word");
            System.out.println("5. Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addWord(scanner);
                    break;
                case 2:
                    listWords();
                    break;
                case 3:
                    searchWord(scanner);
                    break;
                case 4:
                    deleteWord(scanner);
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

    private static void addWord(Scanner scanner) throws IOException {
        System.out.print("Enter word: ");
        String word = scanner.nextLine();

        System.out.print("Enter meaning: ");
        String meaning = scanner.nextLine();

        System.out.print("Enter example sentence: ");
        String sentence = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILE, true))) {
            writer.write(word + "," + meaning + "," + sentence + "\n");
        }

        System.out.println("Word added successfully.");
    }

    private static void listWords() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE))) {
            String line;
            System.out.println("\nWord List:");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                System.out.printf("Word: %s\nMeaning: %s\nExample Sentence: %s\n\n", parts[0], parts[1], parts[2]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No words have been added yet.");
        }
    }

    private static void searchWord(Scanner scanner) throws IOException {
        System.out.print("Enter word to search: ");
        String searchWord = scanner.nextLine().toLowerCase();

        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts[0].toLowerCase().contains(searchWord)) {
                    System.out.printf("Word: %s\nMeaning: %s\nExample Sentence: %s\n\n", parts[0], parts[1], parts[2]);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("Word not found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No words have been added yet.");
        }
    }

    private static void deleteWord(Scanner scanner) throws IOException {
        System.out.print("Enter word to delete: ");
        String deleteWord = scanner.nextLine().toLowerCase();

        File tempFile = new File("temp.db");
        boolean wordDeleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (!parts[0].toLowerCase().equals(deleteWord)) {
                    writer.write(line + "\n");
                } else {
                    wordDeleted = true;
                }
            }
        }

        if (wordDeleted) {
            Files.delete(Paths.get(DB_FILE));
            Files.move(tempFile.toPath(), Paths.get(DB_FILE));
            System.out.println("Word deleted successfully.");
        } else {
            tempFile.delete();
            System.out.println("Word not found.");
        }
    }
}
