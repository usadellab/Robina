### Name: isCurrent
### Title: Use version information to test whether class is current
### Aliases: isCurrent isCurrent,ANY,ANY-method
### Keywords: manip

### ** Examples



obj <- new("VersionedBiobase")
isCurrent(obj)

isCurrent(1:10) # NA

setClass("A", contains="VersionedBiobase",
         prototype=prototype(new("VersionedBiobase", versions=c(A="1.0.0"))))

a <- new("A")
classVersion(a)

isCurrent(a, "VersionedBiobase") # is the 'VersionedBiobase' portion current?
classVersion(a)["A"] <- "1.0.1"
classVersion(a)
isCurrent(a, "VersionedBiobase")
isCurrent(a) # more recent, so does not match 'current' defined by prototype

removeClass("A")



