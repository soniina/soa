package heroes.service;

import heroes.model.TeamMember;
import heroes.model.TeamMemberId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class TeamRepository {
    @PersistenceContext(unitName = "heroes")
    private EntityManager entityManager;

    public boolean teamExists(Long teamId) {
        return entityManager.createQuery(
                        "SELECT COUNT(tm) FROM TeamMember tm WHERE tm.teamId = :teamId", Long.class)
                .setParameter("teamId", teamId)
                .getSingleResult() > 0;
    }

    public List<TeamMember> getTeamMembers(Long teamId) {
        return entityManager.createQuery(
                        "SELECT tm FROM TeamMember tm WHERE tm.teamId = :teamId ORDER BY tm.heroId",
                        TeamMember.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }

    public boolean containsMember(Long teamId, Long heroId) {
        return entityManager.find(TeamMember.class, new TeamMemberId(teamId, heroId)) != null;
    }

    @Transactional
    public void addMember(Long teamId, Long heroId) {
        if (!containsMember(teamId, heroId)) {
            entityManager.persist(new TeamMember(teamId, heroId));
        }
    }

    @Transactional
    public boolean removeMember(Long teamId, Long heroId) {
        TeamMember member = entityManager.find(TeamMember.class, new TeamMemberId(teamId, heroId));
        if (member == null) return false;
        entityManager.remove(member);
        return true;
    }
}
