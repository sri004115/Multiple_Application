import java.util.*;
import java.io.*;
import java.nio.file.*;

public class NotepadJava {

    private static final String NOTES_FILE = "notes.txt";
    private static List<Note> notes = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        loadNotesFromFile();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSimple Notepad Application Menu:");
            System.out.println("1. Create a Note");
            System.out.println("2. View Notes");
            System.out.println("3. Edit a Note");
            System.out.println("4. Delete a Note");
            System.out.println("5. Save Notes to File");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    createNote(scanner);
                    break;
                case 2:
                    viewNotes();
                    break;
                case 3:
                    editNote(scanner);
                    break;
                case 4:
                    deleteNote(scanner);
                    break;
                case 5:
                    saveNotesToFile();
                    break;
                case 6:
                    System.out.println("Exiting Notepad Application.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createNote(Scanner scanner) {
        System.out.print("Enter a unique ID for the note: ");
        String id = scanner.nextLine();
        System.out.print("Enter the note title: ");
        String title = scanner.nextLine();
        System.out.print("Enter the note content: ");
        String content = scanner.nextLine();
        notes.add(new Note(id, title, content));
        System.out.println("Note has been created.");
    }

    private static void viewNotes() {
        if (notes.isEmpty()) {
            System.out.println("No notes found.");
        } else {
            System.out.println("Available Notes:");
            for (Note note : notes) {
                System.out.printf("ID: %s, Title: %s\n", note.getId(), note.getTitle());
            }
        }
    }

    private static void editNote(Scanner scanner) {
        System.out.print("Enter the ID of the note you want to edit: ");
        String id = scanner.nextLine();
        for (Note note : notes) {
            if (note.getId().equals(id)) {
                System.out.print("Enter a new title: ");
                String newTitle = scanner.nextLine();
                System.out.print("Enter new content: ");
                String newContent = scanner.nextLine();
                note.setTitle(newTitle);
                note.setContent(newContent);
                System.out.println("Note has been updated.");
                return;
            }
        }
        System.out.println("Note not found.");
    }

    private static void deleteNote(Scanner scanner) {
        System.out.print("Enter the ID of the note to delete: ");
        String id = scanner.nextLine();
        Iterator<Note> iterator = notes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(id)) {
                iterator.remove();
                System.out.println("Note has been deleted.");
                return;
            }
        }
        System.out.println("Note not found.");
    }

    private static void saveNotesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTES_FILE))) {
            for (Note note : notes) {
                writer.write(note.toString());
                writer.newLine();
            }
            System.out.println("Notes have been saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving notes: " + e.getMessage());
        }
    }

    private static void loadNotesFromFile() {
        Path filePath = Paths.get(NOTES_FILE);
        if (Files.exists(filePath)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", 3);
                    if (parts.length == 3) {
                        notes.add(new Note(parts[0], parts[1], parts[2]));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading notes: " + e.getMessage());
            }
        }
    }

    static class Note {
        private String id;
        private String title;
        private String content;

        public Note(String id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return id + "|" + title + "|" + content;
        }
    }
}
