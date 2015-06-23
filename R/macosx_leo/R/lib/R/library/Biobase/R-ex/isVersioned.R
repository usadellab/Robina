### Name: isVersioned
### Title: Determine whether object or class contains versioning
###   information
### Aliases: isVersioned isVersioned,ANY-method
###   isVersioned,character-method
### Keywords: manip

### ** Examples


obj <- new("VersionedBiobase")
isVersioned(obj)

isVersioned(1:10) # FALSE

setClass("A", contains="VersionedBiobase",
         prototype=prototype(new("VersionedBiobase", versions=c(A="1.0.0"))))
a <- new("A")
isVersioned(a)

removeClass("A")



