### Name: getGOTerm
### Title: Functions to Access GO data.
### Aliases: getGOTerm getGOParents getGOChildren getGOOntology
### Keywords: manip

### ** Examples

 library("GO.db")

 sG <- c("GO:0005515", "GO:0000123", "GO:0000124", "GO:0000125",
         "GO:0000126", "GO:0020033", "GO:0006830", 
         "GO:0015916")

 gT <- getGOTerm(sG)
 gP <- getGOParents(sG)
 gC <- getGOChildren(sG)
 gcat <- getGOOntology(sG)




