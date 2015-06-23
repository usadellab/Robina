### Name: make.db.names-methods
### Title: Make R/S-Plus identifiers into legal SQL identifiers
### Aliases: SQLKeywords-methods isSQLKeyword-methods
###   make.db.names,SQLiteObject,character-method
###   SQLKeywords,SQLiteObject-method SQLKeywords,missing-method
###   isSQLKeyword,SQLiteObject,character-method
### Keywords: methods interface database

### ** Examples
## Not run: 
##D # This example shows how we could export a bunch of data.frames
##D # into tables on a remote database.
##D 
##D con <- dbConnect("SQLite", dbname = "sqlite.db")
##D 
##D export <- c("trantime.email", "trantime.print", "round.trip.time.email")
##D tabs <- make.db.names(export, unique = T, allow.keywords = T)
##D 
##D for(i in seq(along = export) )
##D    dbWriteTable(con, name = tabs[i],  get(export[i]))
## End(Not run)



