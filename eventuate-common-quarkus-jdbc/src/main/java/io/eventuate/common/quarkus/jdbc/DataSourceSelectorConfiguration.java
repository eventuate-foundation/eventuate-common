package io.eventuate.common.quarkus.jdbc;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.sql.DataSource;

@ApplicationScoped
@Alternative
@Priority(1)
public class DataSourceSelectorConfiguration {

  @Produces
  public DataSource defaultDatasource(@ConfigProperty(name = "eventuateDatabase") String db,
                                      @Named("mysql") Instance<DataSource> mysql,
                                      @Named("postgres") Instance<DataSource> postgres,
                                      @Named("mssql") Instance<DataSource> mssql) {
    if ("mysql".equals(db)) return mysql.get();
    if ("postgres".equals(db)) return postgres.get();
    if ("mssql".equals(db)) return mssql.get();

    throw new RuntimeException("DB not found");
  }
}
