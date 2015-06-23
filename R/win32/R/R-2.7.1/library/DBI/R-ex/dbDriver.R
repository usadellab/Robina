### Name: dbDriver
### Title: Database Interface (DBI) Classes and drivers
### Aliases: dbDriver dbDriver,character-method dbUnloadDriver
### Keywords: interface database

### ** Examples
## Not run: 
##D # create a MySQL instance for capacity of up to 25 simultaneous
##D # connections.
##D m <- dbDriver("MySQL", max.con = 25)
##D p <- dbDriver("PgSQL")
##D 
##D # open the connection using user, password, etc., as
##D con <- dbConnect(m, user="ip", password = "traffic", dbname="iptraffic")
##D rs <- dbSubmitQuery(con, 
##D          "select * from HTTP_ACCESS where IP_ADDRESS = '127.0.0.1'")
##D df <- fetch(rs, n = 50)
##D df2 <- fetch(rs, n = -1)
##D dbClearResult(rs)
##D 
##D pcon <- dbConnect(p, "user", "password", "dbname")
##D dbListTables(pcon)
## End(Not run)



