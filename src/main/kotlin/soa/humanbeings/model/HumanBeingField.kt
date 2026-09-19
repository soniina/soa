package soa.humanbeings.model

enum class HumanBeingField(val property: String) {
    ID("id"),
    NAME("name"),
    COORDINATES_X("coordinates.x"),
    COORDINATES_Y("coordinates.y"),
    CREATION_DATE("creationDate"),
    REAL_HERO("realHero"),
    HAS_TOOTHPICK("hasToothpick"),
    IMPACT_SPEED("impactSpeed"),
    WEAPON_TYPE("weaponType"),
    MOOD("mood"),
    CAR_NAME("car.name"),
    CAR_COOL("car.cool"),
}
