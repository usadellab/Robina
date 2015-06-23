### Name: dbReadTable-methods
### Title: Convenience functions for Importing/Exporting DBMS tables
### Aliases: dbReadTable-methods dbWriteTable-methods dbExistsTable-methods
###   dbRemoveTable-methods dbReadTable,SQLiteConnection,character-method
###   dbWriteTable,SQLiteConnection,character,data.frame-method
###   dbWriteTable,SQLiteConnection,character,character-method
###   dbExistsTable,SQLiteConnection,character-method
###   dbRemoveTable,SQLiteConnection,character-method
### Keywords: methods interface database

### ** Examples
## Not run: 
##D conn <- dbConnect("SQLite", dbname = "sqlite.db")
##D if(dbExistsTable(con, "fuel_frame")){
##D    dbRemoveTable(conn, "fuel_frame")
##D    dbWriteTable(conn, "fuel_frame", fuel.frame)
##D }
##D if(dbExistsTable(conn, "RESULTS")){
##D    dbWriteTable(conn, "RESULTS", results2000, append = T)
##D else
##D    dbWriteTable(conn, "RESULTS", results2000)
##D }
## End(Not run)



