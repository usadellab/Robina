### Name: fetch-methods
### Title: Fetch records from a previously executed query
### Aliases: fetch-methods fetch,SQLiteResult,numeric-method
###   fetch,SQLiteResult-method
### Keywords: methods interface database

### ** Examples

drv <- dbDriver("SQLite")
tfile <- tempfile()
con <- dbConnect(drv, dbname = tfile)
data(USJudgeRatings)
dbWriteTable(con, "jratings", USJudgeRatings)

res <- dbSendQuery(con, statement = paste(
                      "SELECT row_names, ORAL, DILG, FAMI",
                      "FROM jratings"))

# we now fetch the first 10 records from the resultSet into a data.frame
data1 <- fetch(res, n = 10)   
dim(data1)

dbHasCompleted(res)

# let's get all remaining records
data2 <- fetch(res, n = -1)

dbClearResult(res)
dbDisconnect(con)



