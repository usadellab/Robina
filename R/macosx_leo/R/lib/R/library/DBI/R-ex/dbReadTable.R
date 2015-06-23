### Name: dbReadTable
### Title: Convenience functions for Importing/Exporting DBMS tables
### Aliases: dbReadTable dbWriteTable dbExistsTable dbRemoveTable
### Keywords: interface database

### ** Examples
## Not run: 
##D conn <- dbConnect("MySQL", group = "vitalAnalysis")
##D con2 <- dbConnect("ODBC", "dsn", "user", "pwd")
##D if(dbExistsTable(con2, "fuel_frame")){
##D    fuel.frame <- dbReadTable(con2, "fuel_frame")
##D    dbRemoveTable(conn, "fuel_frame")
##D    dbWriteTable(conn, "fuel_frame", fuel.frame)
##D }
##D if(dbExistsTable(conn, "RESULTS")){
##D    dbWriteTable(conn, "RESULTS", results2000, append = T)
##D else
##D    dbWriteTable(conn, "RESULTS", results2000)
##D }
## End(Not run)



