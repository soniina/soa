package heroes.dto;

public class CarDto {
    private String name;
    private boolean cool;

    public CarDto() {}

    public CarDto(String name, boolean cool) {
        this.name = name;
        this.cool = cool;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isCool() { return cool; }
    public void setCool(boolean cool) { this.cool = cool; }
}