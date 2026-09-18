package heroes.dto;

public class FieldViolationDto {
    private String field;
    private String message;

    public FieldViolationDto() {}

    public FieldViolationDto(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}