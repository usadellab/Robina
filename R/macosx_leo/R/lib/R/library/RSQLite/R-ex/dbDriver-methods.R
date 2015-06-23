### Name: dbDriver-methods
### Title: SQLite implementation of the Database Interface (DBI) classes
###   and drivers
### Aliases: dbDriver-methods dbUnloadDriver-methods
###   dbDriver,character-method dbUnloadDriver,SQLiteDriver-method
### Keywords: methods interface database

### ** Examples
## Not run: 
##D # create an SQLite instance for capacity of up to 25 simultaneous
##D # connections.
##D m <- dbDriver("SQLite", max.con = 25)
##D 
##D con <- dbConnect(m, dbname="sqlite.db")
##D rs <- dbSubmitQuery(con, 
##D          "select * from HTTP_ACCESS where IP_ADDRESS = '127.0.0.1'")
##D df <- fetch(rs, n = 50)
##D df2 <- fetch(rs, n = -1)
##D dbClearResult(rs)
##D 
##D pcon <- dbConnect(p, "user", "password", "dbname")
##D dbListTables(pcon)
## End(Not run)



