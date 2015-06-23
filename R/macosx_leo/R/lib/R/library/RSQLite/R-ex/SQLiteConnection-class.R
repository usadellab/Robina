### Name: SQLiteConnection-class
### Title: Class SQLiteConnection
### Aliases: SQLiteConnection-class
### Keywords: database interface classes

### ** Examples

drv <- dbDriver("SQLite")
tfile <- tempfile()
con <- dbConnect(drv, dbname = tfile)
dbDisconnect(con)
dbUnloadDriver(drv)



