### Name: dbCommit
### Title: DBMS Transaction Management
### Aliases: dbCommit dbRollback
### Keywords: interface database

### ** Examples
## Not run: 
##D ora <- dbDriver("Oracle")
##D con <- dbConnect(ora)
##D rs <- dbSendQuery(con, 
##D       "delete * from PURGE as p where p.wavelength<0.03")
##D if(dbGetInfo(rs, what = "rowsAffected") > 250){
##D   warning("dubious deletion -- rolling back transaction")
##D   dbRollback(con)
##D }
## End(Not run)



