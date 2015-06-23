### Name: DBIResult-class
### Title: Class DBIResult
### Aliases: DBIResult-class
### Keywords: classes interface database

### ** Examples
## Not run: 
##D  drv <- dbDriver("Oracle")
##D  con <- dbConnect(drv, "user/password@dbname")
##D  res <- dbSendQuery(con, "select * from LASERS where prdata > '2002-05-01'")
##D  summary(res)
##D  while(dbHasCompleted(res)){
##D     chunk <- fetch(res, n = 1000)
##D     process(chunk)
##D  }
## End(Not run)



