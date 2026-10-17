package heroes.dto;

public class CoordinatesDto {
    private double x;
    private Float y;

    public CoordinatesDto() {}

    public CoordinatesDto(double x, Float y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public Float getY() { return y; }
    public void setY(Float y) { this.y = y; }
}
