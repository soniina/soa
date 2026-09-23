package heroes.resource;


import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import heroes.dto.CarDto;
import heroes.dto.ErrorResponseDto;
import heroes.dto.HumanBeingDto;
import heroes.model.TeamMember;
import heroes.service.FirstServiceClient;
import heroes.service.TeamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class HeroesResource {

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private FirstServiceClient firstServiceClient;

    // DELETE /heroes/team/{team-id}/remove/{hero-id}
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
            return Response.status(404)
                    .entity(new ErrorResponseDto("Команда с идентификатором " + teamId + " не найдена"))
                    .build();
        }

        Map<Long, TeamMember> members = teamRepository.getTeamMembers(teamId);

        // Если героя НЕТ в команде — проверяем по спецификации причину:
        if (!members.containsKey(heroId)) {
            HumanBeingDto hero = firstServiceClient.getHeroById(heroId);
            if (hero == null) {
                return Response.status(404)
                        .entity(new ErrorResponseDto("HumanBeing с идентификатором " + heroId + " не найден"))
                        .build();
            }
            return Response.status(404)
                    .entity(new ErrorResponseDto("Герой " + heroId + " не состоит в команде " + teamId))
                    .build();
        }

        // Если герой ЕСТЬ в команде — успешно удаляем его!
        teamRepository.removeMember(teamId, heroId);
        return Response.status(Response.Status.NO_CONTENT).build(); // 204
    }

    // POST /heroes/team/{team-id}/car/add
    @POST
    @Path("/team/{team-id}/car/add")
    public Response addCarToTeamHeroesWithoutCar(@PathParam("team-id") Long teamId) {
        if (teamId == null || teamId < 1) {
            return Response.status(400)
                    .entity(new ErrorResponseDto("Идентификатор команды должен быть больше 0"))
                    .build();
        }

        if (!teamRepository.teamExists(teamId)) {
            return Response.status(404)
                    .entity(new ErrorResponseDto("Команда с идентификатором " + teamId + " не найдена"))
                    .build();
        }

        Map<Long, TeamMember> members = teamRepository.getTeamMembers(teamId);
        List<HumanBeingDto> updatedHeroes = new ArrayList<>();

        for (TeamMember member : members.values()) {
            // Проверяем, нет ли машины
            if (!member.isHasCar()) {
                HumanBeingDto hero = firstServiceClient.getHeroById(member.getHeroId());
                if (hero != null) {
                    if (hero.getCar() == null) {
                        hero.setCar(new CarDto("Красная Lada Kalina", false));
                    } else {
                        // замена названия на Красная Lada Kalina, cool не трогаем
                        hero.getCar().setName("Красная Lada Kalina");
                    }

                    // отправка PUT в первый сервис
                    HumanBeingDto updated = firstServiceClient.updateHero(hero.getId(), hero);
                    updatedHeroes.add(updated);

                    //теперь во внутренних данных машина есть
                    member.setHasCar(true);
                }
            }
        }

        return Response.ok(updatedHeroes).build();
    }
    // Получить участников команды
    @GET
    @Path("/team/{team-id}")
    public Response getTeamMembers(@PathParam("team-id") Long teamId) {
        if (!teamRepository.teamExists(teamId)) {
            return Response.status(404)
                    .entity(new ErrorResponseDto("Команда с идентификатором " + teamId + " не найдена"))
                    .build();
        }
        return Response.ok(new ArrayList<>(teamRepository.getTeamMembers(teamId).values())).build();
    }

    // Добавить героя в команду
    @POST
    @Path("/team/{team-id}/add/{hero-id}")
    public Response addHeroToTeam(@PathParam("team-id") Long teamId,
                                  @PathParam("hero-id") Long heroId) {
        HumanBeingDto hero = firstServiceClient.getHeroById(heroId);
        if (hero == null) {
            return Response.status(404)
                    .entity(new ErrorResponseDto("HumanBeing с идентификатором " + heroId + " не найден"))
                    .build();
        }
        boolean hasRealCar = hero.getCar() != null
                && hero.getCar().getName() != null
                && !hero.getCar().getName().trim().isEmpty()
                && !hero.getCar().getName().equalsIgnoreCase("Без машины")
                && !hero.getCar().getName().equalsIgnoreCase("Пешком");

        teamRepository.getTeamMembers(teamId).put(heroId, new TeamMember(heroId, hasRealCar));
        return Response.ok(new ArrayList<>(teamRepository.getTeamMembers(teamId).values())).build();
    }
}