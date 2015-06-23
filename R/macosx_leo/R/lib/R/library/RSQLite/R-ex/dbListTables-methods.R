### Name: dbListTables-methods
### Title: List items from an SQLite DBMS and from objects
### Aliases: dbListTables-methods dbListFields-methods
###   dbListConnections-methods dbListResults-methods
###   dbListTables,SQLiteConnection-method
###   dbListFields,SQLiteConnection,character-method
###   dbListConnections,SQLiteDriver-method
###   dbListResults,SQLiteConnection-method
### Keywords: methods interface database

### ** Examples
## Not run: 
##D drv <- dbDriver("SQLite")
##D # after working awhile...
##D for(con in dbListConnections(odbc)){
##D    dbGetStatement(dbListResults(con))
##D }
## End(Not run)



