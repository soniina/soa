package heroes.resource;

import heroes.dto.CarDto;
import heroes.dto.ErrorResponseDto;
import heroes.dto.HumanBeingDto;
import heroes.model.TeamMember;
import heroes.service.FirstServiceClient;
import heroes.service.TeamRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class HeroesResource {
    @Inject
    private TeamRepository teamRepository;

    @Inject
    private FirstServiceClient firstServiceClient;

    @DELETE
    @Path("/team/{team-id}/remove/{hero-id}")
    public Response removeHeroFromTeam(@PathParam("team-id") Long teamId,
                                       @PathParam("hero-id") Long heroId) {
        if (teamId == null || teamId < 1 || heroId == null || heroId < 1) {
            return Response.status(400)
                    .entity(new ErrorResponseDto("Идентификаторы должны быть положительными числами"))
                    .build();
        }

        if (!teamRepository.teamExists(teamId)) {
            return teamNotFound(teamId);
        }

        if (firstServiceClient.getHeroById(heroId) == null) {
            return heroNotFound(heroId);
        }

        if (!teamRepository.removeMember(teamId, heroId)) {
            return Response.status(404)
                    .entity(new ErrorResponseDto("Герой " + heroId + " не состоит в команде " + teamId))
                    .build();
        }

        return Response.noContent().build();
    }

    @POST
    @Path("/team/{team-id}/car/add")
    public Response addCarToTeamHeroesWithoutCar(@PathParam("team-id") Long teamId) {
        if (teamId == null || teamId < 1) {
            return Response.status(400)
                    .entity(new ErrorResponseDto("Идентификатор команды должен быть больше 0"))
                    .build();
        }

        if (!teamRepository.teamExists(teamId)) {
            return teamNotFound(teamId);
        }

        List<HumanBeingDto> updatedHeroes = new ArrayList<>();
        for (TeamMember member : teamRepository.getTeamMembers(teamId)) {
            HumanBeingDto hero = firstServiceClient.getHeroById(member.getHeroId());
            if (hero == null || !hasNoCar(hero.getCar())) continue;

            if (hero.getCar() == null) {
                hero.setCar(new CarDto("Красная Lada Kalina", false));
            } else {
                hero.getCar().setName("Красная Lada Kalina");
            }
            updatedHeroes.add(firstServiceClient.updateHero(hero.getId(), hero));
        }

        return Response.ok(updatedHeroes).build();
    }

    @GET
    @Path("/team/{team-id}")
    public Response getTeamMembers(@PathParam("team-id") Long teamId) {
        if (teamId == null || teamId < 1) {
            return Response.status(400)
                    .entity(new ErrorResponseDto("Идентификатор команды должен быть больше 0"))
                    .build();
        }
        return Response.ok(teamRepository.getTeamMembers(teamId)).build();
    }

    @POST
    @Path("/team/{team-id}/add/{hero-id}")
    public Response addHeroToTeam(@PathParam("team-id") Long teamId,
                                  @PathParam("hero-id") Long heroId) {
        if (teamId == null || teamId < 1 || heroId == null || heroId < 1) {
            return Response.status(400)
                    .entity(new ErrorResponseDto("Идентификаторы должны быть положительными числами"))
                    .build();
        }
        if (firstServiceClient.getHeroById(heroId) == null) {
            return heroNotFound(heroId);
        }

        teamRepository.addMember(teamId, heroId);
        return Response.ok(teamRepository.getTeamMembers(teamId)).build();
    }

    private boolean hasNoCar(CarDto car) {
        return car == null || car.getName() == null
                || car.getName().equalsIgnoreCase("Без машины")
                || car.getName().equalsIgnoreCase("Пешком");
    }

    private Response teamNotFound(Long teamId) {
        return Response.status(404)
                .entity(new ErrorResponseDto("Команда с идентификатором " + teamId + " не найдена"))
                .build();
    }

    private Response heroNotFound(Long heroId) {
        return Response.status(404)
                .entity(new ErrorResponseDto("HumanBeing с идентификатором " + heroId + " не найден"))
                .build();
    }
}
