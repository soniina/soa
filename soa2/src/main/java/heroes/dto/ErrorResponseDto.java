package heroes.dto;


import java.util.List;

public class ErrorResponseDto {
    private String message;
    private List<FieldViolationDto> violations;

    public ErrorResponseDto() {}

    public ErrorResponseDto(String message) {
        this.message = message;
    }

    public ErrorResponseDto(String message, List<FieldViolationDto> violations) {
        this.message = message;
        this.violations = violations;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<FieldViolationDto> getViolations() { return violations; }
    public void setViolations(List<FieldViolationDto> violations) { this.violations = violations; }
}
