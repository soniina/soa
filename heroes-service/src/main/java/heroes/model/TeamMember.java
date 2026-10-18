package heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "hero_team_members", schema = "s408391")
@IdClass(TeamMemberId.class)
public class TeamMember {
    @Id
    @Column(name = "team_id")
    private Long teamId;

    @Id
    @Column(name = "hero_id")
    private Long heroId;

    protected TeamMember() {
    }

    public TeamMember(Long teamId, Long heroId) {
        this.teamId = teamId;
        this.heroId = heroId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public Long getHeroId() {
        return heroId;
    }
}
