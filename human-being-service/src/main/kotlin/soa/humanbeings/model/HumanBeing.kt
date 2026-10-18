package soa.humanbeings.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "human_beings")
class HumanBeing(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var name: String = "",

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "x", column = Column(name = "coordinates_x")),
        AttributeOverride(name = "y", column = Column(name = "coordinates_y")),
    )
    var coordinates: Coordinates = Coordinates(),

    @Column(updatable = false)
    var creationDate: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS),

    var realHero: Boolean = false,

    var hasToothpick: Boolean = false,

    var impactSpeed: Float = 0f,

    @Enumerated(EnumType.STRING)
    var weaponType: WeaponType? = null,

    @Enumerated(EnumType.STRING)
    var mood: Mood = Mood.LONGING,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "name", column = Column(name = "car_name")),
        AttributeOverride(name = "cool", column = Column(name = "car_cool")),
    )
    var car: Car = Car(),
)
