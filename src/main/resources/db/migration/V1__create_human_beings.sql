CREATE TABLE human_beings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT COLLATE "C" NOT NULL CHECK (length(name) > 0),
    coordinates_x DOUBLE PRECISION NOT NULL CHECK (coordinates_x <= 740 AND coordinates_x > '-Infinity'::float8),
    coordinates_y REAL NOT NULL CHECK (coordinates_y <= 913 AND coordinates_y > '-Infinity'::float4),
    creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT LOCALTIMESTAMP,
    real_hero BOOLEAN NOT NULL,
    has_toothpick BOOLEAN NOT NULL,
    impact_speed REAL NOT NULL CHECK (impact_speed > -193 AND impact_speed < 'Infinity'::float4),
    weapon_type TEXT CHECK (weapon_type IN ('AXE', 'PISTOL', 'KNIFE', 'BAT')),
    mood TEXT NOT NULL CHECK (mood IN ('LONGING', 'GLOOM', 'APATHY', 'FRENZY')),
    car_name TEXT COLLATE "C" NOT NULL CHECK (length(car_name) > 0),
    car_cool BOOLEAN NOT NULL
);
