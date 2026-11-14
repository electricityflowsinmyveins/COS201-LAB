import java.io.*;
import java.util.*;

// Custom exception class for invalid course inputs
class InvalidCourseException extends Exception {
    public InvalidCourseException(String message) {
        super(message);
    }
}

// Course class representing a course entity
class Course {
    private String courseCode;
    private String courseTitle;
    private int units;

    public Course(String courseCode, String courseTitle, int units) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.units = units;
    }

    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    // Formatted display
    @Override
    public String toString() {
        return "[" + courseCode.toUpperCase() + "] " + courseTitle + " (" + units + " Units)";
    }

    // String format for file saving
    public String toFileString() {
        return courseCode + "," + courseTitle + "," + units;
    }
}

public class StudentCourseManager {

    // Internal storage for courses
    private static ArrayList<Course> courseList = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = "courses.txt";

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   WELCOME TO STUDENT COURSE MANAGEMENT SYSTEM   ");
        System.out.println("=================================================");

        // Start recursive menu execution
        displayMenuRecursive();
    }

    // RECURSION POINT 1: Recursive Main Menu Loop
    public static void displayMenuRecursive() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add Course");
        System.out.println("2. View All Courses");
        System.out.println("3. Search Course by Code");
        System.out.println("4. Compute Total Units");
        System.out.println("5. Save to File");
        System.out.println("6. Load from File");
        System.out.println("7. Exit Program");
        System.out.print("Enter choice (1-7): ");

        int choice = -1;
        try {
            String inputStr = scanner.nextLine().trim();
            choice = Integer.parseInt(inputStr);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input! Please enter a number between 1 and 7.");
            displayMenuRecursive(); // Recursive retry on bad input
            return;
        }

        // Processing menu selection
        switch (choice) {
            case 1:
                addCourse();
                break;
            case 2:
                viewAllCourses();
                break;
            case 3:
                searchCourse();
                break;
            case 4:
                computeTotalUnits();
                break;
            case 5:
                saveToFile();
                break;
            case 6:
                loadFromFile();
                break;
            case 7:
                System.out.println("Exiting system... Thank you!");
                return; // Base case: terminates recursion
            default:
                System.out.println("Invalid choice! Select a number from 1 to 7.");
        }

        // Recursive call to keep menu running until user exits
        displayMenuRecursive();
    }

    // Feature 1: Add Course with Exception Handling & Validation
    private static void addCourse() {
        try {
            System.out.print("Enter Course Code (e.g., COS201): ");
            String code = scanner.nextLine().trim();

            System.out.print("Enter Course Title: ");
            String title = scanner.nextLine().trim();

            System.out.print("Enter Units: ");
            String unitStr = scanner.nextLine().trim();
            int units = Integer.parseInt(unitStr);

            // Validation checks
            if (code.isEmpty() || title.isEmpty()) {
                throw new InvalidCourseException("Course code and title cannot be blank!");
            }
            if (units <= 0) {
                throw new InvalidCourseException("Credit units must be greater than 0!");
            }

            // Normalizing string input (casing)
            code = code.toUpperCase();

            // Create and store course object
            Course newCourse = new Course(code, title, units);
            courseList.add(newCourse);
            System.out.println("SUCCESS: Course added successfully!");

        } catch (NumberFormatException nfe) {
            System.out.println("Input Error: Units must be a valid numeric integer!");
        } catch (InvalidCourseException ice) {
            System.out.println("Validation Error: " + ice.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error occurred while adding course: " + e.getMessage());
        } finally {
            System.out.println("[Add Course operation completed]");
        }
    }

    // Feature 2: Display All Stored Courses using Loop
    private static void viewAllCourses() {
        if (courseList.isEmpty()) {
            System.out.println("No courses recorded yet. Please add courses first.");
            return;
        }

        System.out.println("\n--- LIST OF REGISTERED COURSES ---");
        for (int i = 0; i < courseList.size(); i++) {
            Course c = courseList.get(i);
            System.out.println((i + 1) + ". " + c.toString());
        }
    }

    // Feature 3: Search Course (Calls Recursive Search Function)
    private static void searchCourse() {
        if (courseList.isEmpty()) {
            System.out.println("Course list is empty. Nothing to search.");
            return;
        }

        System.out.print("Enter Course Code to search: ");
        String searchCode = scanner.nextLine().trim().toUpperCase();

        // Call RECURSION POINT 2: Recursive Search
        Course result = searchRecursive(searchCode, 0);

        if (result != null) {
            System.out.println("MATCH FOUND: " + result.toString());
        } else {
            System.out.println("No course found matching code: " + searchCode);
        }
    }

    // RECURSION POINT 2: Recursive linear search function
    private static Course searchRecursive(String code, int index) {
        // Base case 1: Reached end of list without match
        if (index >= courseList.size()) {
            return null;
        }
        // Base case 2: Match found at current index
        if (courseList.get(index).getCourseCode().equalsIgnoreCase(code)) {
            return courseList.get(index);
        }
        // Recursive step: search next index
        return searchRecursive(code, index + 1);
    }

    // Feature 4: Compute Total Units using Iteration
    private static void computeTotalUnits() {
        if (courseList.isEmpty()) {
            System.out.println("Total Credit Units: 0 (No courses registered)");
            return;
        }

        int totalUnits = 0;
        for (Course course : courseList) {
            totalUnits += course.getUnits();
        }

        System.out.println("\nTOTAL CREDIT UNITS: " + totalUnits + " Units (" + courseList.size() + " course(s) total)");
    }

    // Feature 5: Save Course List to File
    private static void saveToFile() {
        if (courseList.isEmpty()) {
            System.out.println("Notice: Storing an empty list to file.");
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_NAME));
            for (Course course : courseList) {
                writer.write(course.toFileString());
                writer.newLine();
            }
            System.out.println("SUCCESS: " + courseList.size() + " course(s) saved to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("File Error: Failed to save courses. " + e.getMessage());
        } finally {
            // Proper resource handling in finally block
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                System.out.println("Error closing file writer: " + ex.getMessage());
            }
        }
    }

    // Feature 6: Load Course List from File
    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("File Error: Cannot load. File '" + FILE_NAME + "' does not exist yet.");
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FILE_NAME));
            courseList.clear(); // Clear existing list before loading

            String line;
            int loadedCount = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String code = parts[0].trim().toUpperCase();
                    String title = parts[1].trim();
                    int units = Integer.parseInt(parts[2].trim());

                    courseList.add(new Course(code, title, units));
                    loadedCount++;
                }
            }
            System.out.println("SUCCESS: Successfully loaded " + loadedCount + " course(s) from " + FILE_NAME);

        } catch (FileNotFoundException fnfe) {
            System.out.println("File Error: File not found - " + fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("IO Error: Failed reading file - " + ioe.getMessage());
        } catch (NumberFormatException nfe) {
            System.out.println("Data Error: File contains invalid unit data format.");
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                System.out.println("Error closing reader: " + e.getMessage());
            }
        }
    }
}