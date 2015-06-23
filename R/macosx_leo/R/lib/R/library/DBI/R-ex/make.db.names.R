### Name: make.db.names
### Title: Make R/Splus identifiers into legal SQL identifiers
### Aliases: make.db.names make.db.names,DBIObject,character-method
###   SQLKeywords SQLKeywords,DBIObject-method SQLKeywords,missing-method
###   isSQLKeyword isSQLKeyword,DBIObject,character-method
### Keywords: interface database

### ** Examples
## Not run: 
##D # This example shows how we could export a bunch of data.frames
##D # into tables on a remote database.
##D 
##D con <- dbConnect("Oracle", user="iptraffic", pass = pwd)
##D 
##D export <- c("trantime.email", "trantime.print", "round.trip.time.email")
##D tabs <- make.db.names(export, unique = T, allow.keywords = T)
##D 
##D for(i in seq(along = export) )
##D    dbWriteTable(con, name = tabs[i],  get(export[i]))
##D 
##D # Oracle's extensions to SQL keywords
##D oracle.keywords <- c("CLUSTER", "COLUMN", "MINUS", "DBNAME")
##D isSQLKeyword(nam, c(.SQL92Keywords, oracle.keywords))
##D [1]  T  T  T  F
## End(Not run)



