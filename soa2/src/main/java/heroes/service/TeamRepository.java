package heroes.service;

import jakarta.enterprise.context.ApplicationScoped;
import heroes.model.TeamMember;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TeamRepository {
    private final Map<Long, Map<Long, TeamMember>> teams = new ConcurrentHashMap<>();


    public TeamRepository() {
        // предзаполнение тестовой команды ID = 7
//        Map<Long, TeamMember> team7 = new ConcurrentHashMap<>();
//        team7.put(101L, new TeamMember(101L, true));
//        team7.put(102L, new TeamMember(102L, false));
//        teams.put(7L, team7);
    }

    public boolean teamExists(Long teamId) {
        return teamId != null && teamId > 0;

//        return teams.containsKey(teamId);
    }
    public Map<Long, TeamMember> getTeamMembers(Long teamId) {

//        return teams.get(teamId);
        return teams.computeIfAbsent(teamId, k -> new ConcurrentHashMap<>());
    }

    public boolean removeMember(Long teamId, Long heroId) {
        Map<Long, TeamMember> members = teams.get(teamId);
        if (members == null) return false;
        return members.remove(heroId) != null;
    }
}