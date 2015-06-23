### Name: fetch
### Title: Fetch records from a previously executed query
### Aliases: fetch
### Keywords: interface database

### ** Examples
## Not run: 
##D # Run an SQL statement by creating first a resultSet object
##D drv <- dbDriver("ODBC")
##D con <- dbConnect(drv, ...)
##D res <- dbSendQuery(con, statement = paste(
##D                       "SELECT w.laser_id, w.wavelength, p.cut_off",
##D                       "FROM WL w, PURGE P", 
##D                       "WHERE w.laser_id = p.laser_id",
##D                       "ORDER BY w.laser_id"))
##D # we now fetch the first 100 records from the resultSet into a data.frame
##D data1 <- fetch(res, n = 100)   
##D dim(data1)
##D 
##D dbHasCompleted(res)
##D 
##D # let's get all remaining records
##D data2 <- fetch(res, n = -1)
## End(Not run)



