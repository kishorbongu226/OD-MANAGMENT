package org.studyeasy.SpringRestdemo.util.constants;

public enum StudentError {

    PROFILE_NOT_FOUND("Student profile not found"),
    ENROLLMENT_NOT_FOUND("Enrollment not found"),
    FEEDBACK_NOT_FOUND("Feedback not found"),
    APPROVAL_REQUEST_NOT_FOUND("Approval request not found"),
    CERTIFICATE_NOT_FOUND("Certificate not found"),
    ADD_ERROR("Error while adding record"),
    UPDATE_ERROR("Error while updating record"),
    DELETE_ERROR("Error while deleting record");

    private final String message;

    StudentError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
