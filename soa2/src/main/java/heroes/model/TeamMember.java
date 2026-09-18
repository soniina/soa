package heroes.model;

public class TeamMember {
    private Long heroId;
    private boolean hasCar;

    public TeamMember(Long heroId, boolean hasCar) {
        this.heroId = heroId;
        this.hasCar = hasCar;
    }

    public Long getHeroId() { return heroId; }
    public boolean isHasCar() { return hasCar; }
    public void setHasCar(boolean hasCar) { this.hasCar = hasCar; }
}