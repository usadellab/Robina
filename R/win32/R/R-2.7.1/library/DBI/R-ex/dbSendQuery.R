### Name: dbSendQuery
### Title: Execute a statement on a given database connection
### Aliases: dbSendQuery dbGetQuery dbClearResult dbGetException
### Keywords: interface database

### ** Examples
## Not run: 
##D drv <- dbDriver("MySQL")
##D con <- dbConnect(drv)
##D res <- dbSendQuery(con, "SELECT * from liv25")
##D data <- fetch(res, n = -1)
## End(Not run)



