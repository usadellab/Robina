### Name: dbGetInfo
### Title: Database interface meta-data
### Aliases: dbGetInfo dbGetDBIVersion dbGetStatement dbGetRowCount
###   dbGetRowsAffected dbColumnInfo dbHasCompleted
### Keywords: interface database

### ** Examples
## Not run: 
##D drv <- dbDriver("SQLite")
##D con <- dbConnect(drv)
##D 
##D dbListTables(con)
##D 
##D rs <- dbSendQuery(con, query.sql)
##D dbGetStatement(rs)
##D dbHasCompleted(rs)
##D 
##D info <- dbGetInfo(rs)
##D names(dbGetInfo(drv))  
##D 
##D # DBIConnection info
##D names(dbGetInfo(con))
##D 
##D # DBIResult info
##D names(dbGetInfo(rs)) 
## End(Not run)



