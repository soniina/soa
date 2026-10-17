package heroes.dto;

public class HumanBeingDto {
    private Long id;
    private String name;
    private CoordinatesDto coordinates;
    private String creationDate;
    private boolean realHero;
    private Boolean hasToothpick;
    private float impactSpeed;
    private WeaponType weaponType;
    private Mood mood;
    private CarDto car;

    public HumanBeingDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CoordinatesDto getCoordinates() { return coordinates; }
    public void setCoordinates(CoordinatesDto coordinates) { this.coordinates = coordinates; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public boolean isRealHero() { return realHero; }
    public void setRealHero(boolean realHero) { this.realHero = realHero; }

    public Boolean getHasToothpick() { return hasToothpick; }
    public void setHasToothpick(Boolean hasToothpick) { this.hasToothpick = hasToothpick; }

    public float getImpactSpeed() { return impactSpeed; }
    public void setImpactSpeed(float impactSpeed) { this.impactSpeed = impactSpeed; }

    public WeaponType getWeaponType() { return weaponType; }
    public void setWeaponType(WeaponType weaponType) { this.weaponType = weaponType; }

    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }

    public CarDto getCar() { return car; }
    public void setCar(CarDto car) { this.car = car; }
}