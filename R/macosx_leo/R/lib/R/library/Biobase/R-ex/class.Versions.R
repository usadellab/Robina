### Name: Versions
### Title: Class "Versions"
### Aliases: Versions-class Versions initialize,Versions-method
###   [,Versions-method [<-,Versions-method [[<-,Versions-method
###   $<-,Versions-method Compare,Versions,Versions-method
###   Compare,Versions,character-method Compare,character,Versions-method
###   updateObject,Versions-method show,Versions-method
###   coerce,Versions,character-method
### Keywords: classes

### ** Examples


obj <- new("Versions", A="1.0.0")
obj

obj["A"] <- "1.0.1"
obj
obj["B"] <- "2.0"
obj

obj1 <- obj
obj1["B"] <- "2.0.1"

obj1 == obj
obj1["B"] > "2.0.0"
obj["B"] == "2.0" # TRUE!




