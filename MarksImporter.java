import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom Unchecked Exception for duplicate student IDs.
 */
class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}

public class MarksImporter {

    // Logger for recording technical details securely
    private static final Logger LOGGER = Logger.getLogger(MarksImporter.class.getName());

    static void importMarks(String filename) throws IOException {
        Set<String> seen = new HashSet<>();

        // Try-with-resources: Ensures the BufferedReader is automatically closed 
        // even if an exception occurs during reading.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Skip empty or purely whitespace lines safely
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");

                try {
                    // TODO 1: Check there are exactly 2 fields
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid format at line " + lineNumber + ": Expected 'ID, Mark'");
                    }

                    String studentId = parts[0].trim();
                    String markStr = parts[1].trim();

                    // TODO 3: Detect repeated student ID using seen set
                    if (seen.contains(studentId)) {
                        throw new DuplicateStudentException("Duplicate student ID detected: " + studentId);
                    }

                    // TODO 2: Parse mark and validate range 0..100
                    int mark = Integer.parseInt(markStr); // Can throw NumberFormatException
                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException("Mark out of range (0-100): " + mark);
                    }

                    // If all validations pass, accept and record the student ID
                    seen.add(studentId);

                    // TODO 4: Display accepted record
                    System.out.println("[ACCEPTED] Student ID: " + studentId + " | Mark: " + mark);

                } catch (NumberFormatException e) {
                    System.out.println("[REJECTED] Line " + lineNumber + ": Invalid numeric value format for mark.");
                    LOGGER.log(Level.WARNING, "Technical Parse Error at line " + lineNumber + ": " + e.getMessage(), e);
                } catch (IllegalArgumentException | DuplicateStudentException e) {
                    System.out.println("[REJECTED] Line " + lineNumber + ": " + e.getMessage());
                    LOGGER.log(Level.WARNING, "Validation Error at line " + lineNumber + ": " + e.getMessage(), e);
                }
                // Decision: We catch record-level exceptions inside the loop so that 
                // one bad record gets skipped safely without stopping the entire file import process.
            }
        }
    }

    public static void main(String[] args) {
        String filename = "marks.txt";

        try {
            System.out.println("--- Starting Import Process ---");
            importMarks(filename);
        } catch (FileNotFoundException e) {
            System.out.println("Friendly Error: Marks file not found; check the file name (" + filename + ").");
            LOGGER.log(Level.SEVERE, "File Not Found Exception: " + e.getMessage(), e);
        } catch (IOException e) {
            System.out.println("Friendly Error: An error occurred while reading the file.");
            LOGGER.log(Level.SEVERE, "IO Exception: " + e.getMessage(), e);
        } finally {
            System.out.println("Import attempt finished.");
        }
    }
}