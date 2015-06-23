### Name: dbDataType-methods
### Title: Determine the SQL Data Type of an S object
### Aliases: dbDataType-methods dbDataType,SQLiteObject-method
### Keywords: methods interface database

### ** Examples

data(quakes)
drv <- dbDriver("SQLite")
sapply(quakes, function(x) dbDataType(drv, x))

dbDataType(drv, 1)
dbDataType(drv, as.integer(1))
dbDataType(drv, "1")
dbDataType(drv, charToRaw("1"))




