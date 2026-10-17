package heroes.model;

import java.io.Serializable;
import java.util.Objects;

public class TeamMemberId implements Serializable {
    private Long teamId;
    private Long heroId;

    public TeamMemberId() {
    }

    public TeamMemberId(Long teamId, Long heroId) {
        this.teamId = teamId;
        this.heroId = heroId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TeamMemberId id)) return false;
        return Objects.equals(teamId, id.teamId) && Objects.equals(heroId, id.heroId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, heroId);
    }
}
