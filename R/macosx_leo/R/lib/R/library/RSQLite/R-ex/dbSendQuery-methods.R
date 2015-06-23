### Name: dbSendQuery-methods
### Title: Execute a SQL statement on a database connection
### Aliases: dbSendQuery-methods dbGetQuery-methods dbSendPreparedQuery
###   dbGetPreparedQuery dbSendPreparedQuery-methods
###   dbGetPreparedQuery-methods dbGetException-methods
###   dbSendQuery,SQLiteConnection,character-method
###   dbGetQuery,SQLiteConnection,character-method
###   dbSendPreparedQuery,SQLiteConnection,character,data.frame-method
###   dbGetPreparedQuery,SQLiteConnection,character,data.frame-method
###   dbClearResult,SQLiteResult-method
###   dbGetException,SQLiteConnection-method
### Keywords: methods interface database

### ** Examples

con <- dbConnect(SQLite(), ":memory:")
data(USArrests)
dbWriteTable(con, "arrests", USArrests)

res <- dbSendQuery(con, "SELECT * from arrests")
data <- fetch(res, n = 2)
data
dbClearResult(res)

dbGetQuery(con, "SELECT * from arrests limit 3")

tryCatch(dbGetQuery(con, "SELECT * FROM tableDoesNotExist"),
         error=function(e) { print("caught") })
dbGetException(con)




