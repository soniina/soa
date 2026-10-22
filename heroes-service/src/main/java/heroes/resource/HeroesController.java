package heroes.resource;

import heroes.dto.CarDto;
import heroes.dto.ErrorResponseDto;
import heroes.dto.HumanBeingDto;
import heroes.model.TeamMember;
import heroes.service.HumanBeingApiClient;
import heroes.service.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
})
@RestController
@RequestMapping("/heroes")
public class HeroesController {

    private final TeamRepository teamRepository;
    private final HumanBeingApiClient humanBeingApiClient;

    public HeroesController(TeamRepository teamRepository, HumanBeingApiClient humanBeingApiClient) {
        this.teamRepository = teamRepository;
        this.humanBeingApiClient = humanBeingApiClient;
    }

    //DELETE /heroes/team/{team-id}/remove/{hero-id}
    @DeleteMapping("/team/{team-id}/remove/{hero-id}")
    public ResponseEntity<?> removeHeroFromTeam(@PathVariable("team-id") Long teamId,
                                                @PathVariable("hero-id") Long heroId) {
        if (teamId == null || teamId < 1 || heroId == null || heroId < 1) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Идентификаторы должны быть больше 0"));
        }

        if (!teamRepository.teamExists(teamId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto("Команда с идентификатором " + teamId + " не найдена"));
        }

        if (!teamRepository.containsMember(teamId, heroId)) {
            HumanBeingDto hero = humanBeingApiClient.getHeroById(heroId);
            if (hero == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDto("HumanBeing с идентификатором " + heroId + " не найден"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto("Герой " + heroId + " не состоит в команде " + teamId));
        }

        teamRepository.removeMember(teamId, heroId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // POST /heroes/team/{team-id}/car/add
    @PostMapping("/team/{team-id}/car/add")
    public ResponseEntity<?> addCarToTeamHeroesWithoutCar(@PathVariable("team-id") Long teamId) {
        if (teamId == null || teamId < 1) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Идентификатор команды должен быть больше 0"));
        }

        if (!teamRepository.teamExists(teamId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto("Команда с идентификатором " + teamId + " не найдена"));
        }

        List<TeamMember> members = teamRepository.getTeamMembers(teamId);
        List<HumanBeingDto> updatedHeroes = new ArrayList<>();

        for (TeamMember member : members) {
            HumanBeingDto hero = humanBeingApiClient.getHeroById(member.getHeroId());
            if (hero != null) {
                boolean hasCar = hero.getCar() != null
                        && hero.getCar().getName() != null
                        && !hero.getCar().getName().trim().isEmpty()
                        && !hero.getCar().getName().equalsIgnoreCase("Без машины");

                if (!hasCar) {
                    if (hero.getCar() == null) {
                        hero.setCar(new CarDto("Красная Lada Kalina", false));
                    } else {
                        hero.getCar().setName("Красная Lada Kalina");
                    }

                    HumanBeingDto updated = humanBeingApiClient.updateHero(hero.getId(), hero);
                    if (updated != null) {
                        updatedHeroes.add(updated);
                    }
                }
            }
        }

        return ResponseEntity.ok(updatedHeroes);
    }

    // Вспомогательный: GET /heroes/team/{team-id} (для фронтенда)
    @GetMapping("/team/{team-id}")
    public ResponseEntity<?> getTeamMembers(@PathVariable("team-id") Long teamId) {
        return ResponseEntity.ok(teamRepository.getTeamMembers(teamId));
    }

    // Вспомогательный: POST /heroes/team/{team-id}/add/{hero-id} (для фронтенда)
    @PostMapping("/team/{team-id}/add/{hero-id}")
    public ResponseEntity<?> addHeroToTeam(@PathVariable("team-id") Long teamId,
                                           @PathVariable("hero-id") Long heroId) {
        HumanBeingDto hero = humanBeingApiClient.getHeroById(heroId);
        if (hero == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto("HumanBeing с идентификатором " + heroId + " не найден"));
        }

        teamRepository.addMember(teamId, heroId);
        return ResponseEntity.ok(teamRepository.getTeamMembers(teamId));
    }
}