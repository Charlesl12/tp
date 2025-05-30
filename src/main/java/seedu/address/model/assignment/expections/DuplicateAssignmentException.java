package seedu.address.model.assignment.expections;

/**
 * Signals that the operation will result in duplicate Assignments
 * (Assignments are considered duplicates if they have the same
 * identity).
 */
public class DuplicateAssignmentException extends RuntimeException {
    public DuplicateAssignmentException() {
        super("This assignment already exists in the student");
    }
}
