package seedu.address.storage;

import static seedu.address.logic.commands.AddAssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.UniqueAssignmentList;
import seedu.address.model.student.Address;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.subject.Subject;


/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";

    private final String name;
    private final String phone;
    private final String address;
    private final String email;
    private final List<JsonAdaptedSubject> subjects = new ArrayList<>();
    private final List<JsonAdaptedAssignment> assignments;

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
                              @JsonProperty("address") String address, @JsonProperty("email") String email,
                              @JsonProperty("subjects") List<JsonAdaptedSubject> subjects,
                              @JsonProperty("assignments") List<JsonAdaptedAssignment> assignments) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        if (subjects != null) {
            this.subjects.addAll(subjects);
        }
        this.assignments = assignments;
    }

    /**
     * Converts a given {@code Student} into this class for Jackson use.
     */
    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        subjects.addAll(source.getSubjects().stream()
                .map(JsonAdaptedSubject::new)
                .toList());
        assignments = source.getAssignments().asUnmodifiableObservableList().stream()
                .map(JsonAdaptedAssignment::new)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Converts this Jackson-friendly adapted student object into the model's {@code Student} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted student.
     */
    public Student toModelType() throws IllegalValueException {
        // model type for subjects
        final Set<Subject> studentSubjects = new HashSet<>();

        for (JsonAdaptedSubject subject : subjects) {
            studentSubjects.add(subject.toModelType());
        }

        // model type for assignments
        final UniqueAssignmentList studentAssignments = new UniqueAssignmentList();
        if (assignments != null) {
            for (JsonAdaptedAssignment assignment : assignments) {
                Assignment assignmentToAdd = assignment.toModelType();
                if (studentAssignments.contains(assignmentToAdd)) {
                    throw new IllegalValueException(MESSAGE_DUPLICATE_ASSIGNMENT);
                }
                studentAssignments.add(assignment.toModelType());
            }
        }

        // model type for name
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        // model type for phone
        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        // model type for address
        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        // model type for email
        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);


        final Set<Subject> modelSubjects = new HashSet<>(studentSubjects);

        return new Student(modelName, modelPhone, modelEmail, modelAddress, modelSubjects,
                studentAssignments);
    }

}
