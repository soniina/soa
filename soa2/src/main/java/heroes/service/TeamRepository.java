package heroes.service;

import heroes.model.TeamMember;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TeamRepository {

    private final Map<Long, Map<Long, TeamMember>> teams = new ConcurrentHashMap<>();

    public boolean teamExists(Long teamId) {
        return teamId != null && teamId > 0;
    }

    public Map<Long, TeamMember> getTeamMembers(Long teamId) {
        return teams.computeIfAbsent(teamId, id -> new ConcurrentHashMap<>());
    }

    public boolean removeMember(Long teamId, Long heroId) {
        Map<Long, TeamMember> members = teams.get(teamId);
        if (members == null) return false;
        return members.remove(heroId) != null;
    }
}