### Name: DBIObject-class
### Title: Class DBIObject
### Aliases: DBIObject-class
### Keywords: classes interface database

### ** Examples
## Not run: 
##D drv <- dbDriver("MySQL")
##D con <- dbConnect(drv, group = "rs-dbi")
##D res <- dbSendQuery(con, "select * from vitalSuite")
##D is(drv, "DBIObject")   ## True
##D is(con, "DBIObject")   ## True
##D is(res, "DBIObject")
## End(Not run)



