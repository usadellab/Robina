### Name: dbCommit-methods
### Title: DBMS Transaction Management
### Aliases: dbBeginTransaction dbBeginTransaction-methods dbCommit-methods
###   dbRollback-methods dbBeginTransaction,SQLiteConnection-method
###   dbCommit,SQLiteConnection-method dbRollback,SQLiteConnection-method
### Keywords: methods interface database

### ** Examples

drv <- dbDriver("SQLite")
tfile <- tempfile()
con <- dbConnect(drv, dbname = tfile)
data(USArrests)
dbWriteTable(con, "arrests", USArrests)
dbGetQuery(con, "select count(*) from arrests")[1, ]

dbBeginTransaction(con)
rs <- dbSendQuery(con, "DELETE from arrests WHERE Murder > 1")
do_commit <- if (dbGetInfo(rs)[["rowsAffected"]] > 40) FALSE else TRUE
dbClearResult(rs)
dbGetQuery(con, "select count(*) from arrests")[1, ]
if (!do_commit)
    dbRollback(con)
dbGetQuery(con, "select count(*) from arrests")[1, ]

dbBeginTransaction(con)
rs <- dbSendQuery(con, "DELETE from arrests WHERE Murder > 5")
dbClearResult(rs)
dbCommit(con)
dbGetQuery(con, "select count(*) from arrests")[1, ]

dbDisconnect(con)



