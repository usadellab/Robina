### Name: dbConnect
### Title: Create a connection to a DBMS
### Aliases: dbDisconnect dbConnect
### Keywords: interface database

### ** Examples
## Not run: 
##D # create an RODBC instance and create one connection.
##D m <- dbDriver("RODBC")
##D 
##D # open the connection using user, passsword, etc., as
##D # specified in the file \file{\$HOME/.my.cnf}
##D con <- dbConnect(m, dsn="data.source", uid="user", pwd="password"))    
##D 
##D # Run an SQL statement by creating first a resultSet object
##D rs <- dbSendQuery(con, statement = paste(
##D                       "SELECT w.laser_id, w.wavelength, p.cut_off",
##D                       "FROM WL w, PURGE P", 
##D                       "WHERE w.laser_id = p.laser_id", 
##D                       "SORT BY w.laser_id")
##D # we now fetch records from the resultSet into a data.frame
##D data <- fetch(rs, n = -1)   # extract all rows
##D dim(data)
## End(Not run)



