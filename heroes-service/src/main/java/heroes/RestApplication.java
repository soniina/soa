package heroes;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
@DataSourceDefinition(
        name = "java:app/jdbc/Heroes",
        className = "org.postgresql.ds.PGSimpleDataSource",
        serverName = "pg",
        portNumber = 5432,
        databaseName = "studs",
        user = "s408391",
        password = "${ALIAS=heroes-db-password}"
)
public class RestApplication extends Application {
}
