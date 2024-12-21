import java.sql.*;
import java.util.*;

public class ToDoApp {

    private Connection connection;

    public ToDoApp(String dbName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            createTable();
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS todos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "task TEXT NOT NULL," +
                "completed BOOLEAN NOT NULL DEFAULT 0)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public void addTask(String task) {
        if (task == null || task.trim().isEmpty()) {
            System.out.println("Task cannot be empty.");
            return;
        }
        if (connection == null) {
            System.out.println("Database connection is not available.");
            return;
        }
        String sql = "INSERT INTO todos (task, completed) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, task);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding task: " + e.getMessage());
        }
    }

    public void listTasks() {
        if (connection == null) {
            System.out.println("Database connection is not available.");
            return;
        }
        String sql = "SELECT * FROM todos";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean hasTasks = false;
            while (rs.next()) {
                hasTasks = true;
                int id = rs.getInt("id");
                String task = rs.getString("task");
                boolean completed = rs.getBoolean("completed");
                String status = completed ? "Completed" : "Not Completed";
                System.out.printf("ID: %d, Task: %s, Status: %s\n", id, task, status);
            }
            if (!hasTasks) {
                System.out.println("No tasks found.");
            }
        } catch (SQLException e) {
            System.err.println("Error listing tasks: " + e.getMessage());
        }
    }

    public void markTaskCompleted(int taskId) {
        if (connection == null) {
            System.out.println("Database connection is not available.");
            return;
        }
        String sql = "UPDATE todos SET completed = 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Task ID not found.");
            } else {
                System.out.println("Task marked as completed.");
            }
        } catch (SQLException e) {
            System.err.println("Error marking task as completed: " + e.getMessage());
        }
    }

    public void deleteTask(int taskId) {
        if (connection == null) {
            System.out.println("Database connection is not available.");
            return;
        }
        String sql = "DELETE FROM todos WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Task ID not found.");
            } else {
                System.out.println("Task deleted.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting task: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing the database connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ToDoApp todoApp = new ToDoApp("todo.db");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nTo-Do List Application");
            System.out.println("1. Add Task");
            System.out.println("2. List Tasks");
            System.out.println("3. Mark Task Completed");
            System.out.println("4. Delete Task");
            System.out.println("5. Exit");

            System.out.print("Select an operation: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter the task: ");
                        String task = scanner.nextLine();
                        todoApp.addTask(task);
                        System.out.println("Task added.");
                        break;
                    case "2":
                        todoApp.listTasks();
                        break;
                    case "3":
                        System.out.print("Enter the task ID to mark as completed: ");
                        int completeId = Integer.parseInt(scanner.nextLine());
                        todoApp.markTaskCompleted(completeId);
                        break;
                    case "4":
                        System.out.print("Enter the task ID to delete: ");
                        int deleteId = Integer.parseInt(scanner.nextLine());
                        todoApp.deleteTask(deleteId);
                        break;
                    case "5":
                        todoApp.close();
                        System.out.println("Exiting application.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
