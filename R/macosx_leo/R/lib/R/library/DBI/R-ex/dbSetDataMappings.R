### Name: dbSetDataMappings
### Title: Set data mappings between an DBMS and R/Splus
### Aliases: dbSetDataMappings
### Keywords: interface database

### ** Examples
## Not run: 
##D makeImage <- function(x) {
##D   .C("make_Image", as.integer(x), length(x))
##D }
##D 
##D res <- dbSendQuery(con, statement)
##D flds <- dbColumnInfo(res)
##D flds[3, "Sclass"] <- makeImage
##D 
##D dbSetDataMappings(rs, flds)
##D 
##D im <- fetch(rs, n = -1)
## End(Not run)



