### Name: Versioned
### Title: Class "Versioned"
### Aliases: Versioned-class Versioned initialize,Versioned-method
###   isVersioned,Versioned-method classVersion,Versioned-method
###   classVersion<-,Versioned,Versions-method
###   isCurrent,Versioned,missing-method
###   isCurrent,Versioned,character-method show,Versioned-method
### Keywords: classes

### ** Examples


obj <- new("Versioned", versions=list(A="1.0.0"))
obj
classVersion(obj)

setClass("A", contains="Versioned")

classVersion("A")
a <- new("A")
a # 'show' nothing by default
classVersion(a)

setClass("B",
         contains="Versioned",
         prototype=prototype(new("Versioned",versions=list(B="1.0.0"))))

classVersion("B")
b <- new("B")
classVersion(b)

classVersion(b)["B"] <- "1.0.1"
classVersion(b)
classVersion("B")

classVersion("B") < classVersion(b)
classVersion(b) == "1.0.1"

setClass("C",
         representation(x="numeric"),
         contains=("VersionedBiobase"),
         prototype=prototype(new("VersionedBiobase", versions=c(C="1.0.1"))))

setMethod("show", signature(object="C"),
          function(object) print(object@x))

c <- new("C", x=1:10)

c

classVersion(c)




