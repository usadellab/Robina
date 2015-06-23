### Name: SQLite
### Title: Instantiate the SQLite engine from the current R session.
### Aliases: SQLite SQLiteDriver
### Keywords: interface database

### ** Examples

   # create a SQLite instance and create one connection.
   m <- dbDriver("SQLite")
   
   # initialize a new database to a tempfile and copy some data.frame
   # from the base package into it
   tfile <- tempfile()
   con <- dbConnect(m, dbname = tfile)
   data(USArrests)
   dbWriteTable(con, "USArrests", USArrests)
   
   # query
   rs <- dbSendQuery(con, "select * from USArrests")
   d1 <- fetch(rs, n = 10)      # extract data in chunks of 10 rows
   dbHasCompleted(rs)
   d2 <- fetch(rs, n = -1)      # extract all remaining data
   dbHasCompleted(rs)
   dbClearResult(rs)
   dbListTables(con)

   # clean up
   dbDisconnect(con)
   file.info(tfile)
   file.remove(tfile)



