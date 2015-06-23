### Name: dbListTables
### Title: List items from a remote DBMS and from objects that implement
###   the database interface DBI.
### Aliases: dbListTables dbListFields dbListConnections dbListResults
### Keywords: interface database

### ** Examples
## Not run: 
##D odbc <- dbDriver("ODBC")
##D # after working awhile...
##D for(con in dbListConnections(odbc)){
##D    dbGetStatement(dbListResults(con))
##D }
## End(Not run)



