### Name: dbGetInfo-methods
### Title: Database interface meta-data
### Aliases: dbGetInfo dbGetDBIVersion-methods dbGetStatement-methods
###   dbGetRowCount-methods dbGetRowsAffected-methods dbColumnInfo-methods
###   dbHasCompleted-methods dbGetInfo,SQLiteObject-method
###   dbGetInfo,SQLiteDriver-method dbGetInfo,SQLiteConnection-method
###   dbGetInfo,SQLiteResult-method dbGetStatement,SQLiteResult-method
###   dbGetRowCount,SQLiteResult-method
###   dbGetRowsAffected,SQLiteResult-method
###   dbColumnInfo,SQLiteResult-method dbHasCompleted,SQLiteResult-method
### Keywords: methods interface database

### ** Examples

data(USArrests)
drv <- dbDriver("SQLite")
con <- dbConnect(drv, dbname=":memory:")
dbWriteTable(con, "t1", USArrests)
dbWriteTable(con, "t2", USArrests)

dbListTables(con)

rs <- dbSendQuery(con, "select * from t1 where UrbanPop >= 80")
dbGetStatement(rs)
dbHasCompleted(rs)

info <- dbGetInfo(rs)
names(info)
info$fields

fetch(rs, n=2)
dbHasCompleted(rs)
info <- dbGetInfo(rs)
info$fields
dbClearResult(rs)

names(dbGetInfo(drv))  

# DBIConnection info
names(dbGetInfo(con))

dbDisconnect(con)



