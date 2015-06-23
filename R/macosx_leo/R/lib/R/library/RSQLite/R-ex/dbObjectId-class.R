### Name: dbObjectId-class
### Title: Class dbObjectId
### Aliases: dbObjectId-class
### Keywords: classes

### ** Examples

  sqlite <- dbDriver("SQLite")
  con <- dbConnect(sqlite, ":memory:")
  is(sqlite, "dbObjectId")   ## True
  is(con, "dbObjectId")  ## True
  isIdCurrent(con)       ## True
  dbDisconnect(con)
  isIdCurrent(con)       ## False



