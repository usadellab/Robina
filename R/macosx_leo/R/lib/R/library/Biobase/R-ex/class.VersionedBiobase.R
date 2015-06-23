### Name: VersionedBiobase
### Title: Class "VersionedBiobase"
### Aliases: VersionedBiobase-class VersionedBiobase
### Keywords: classes

### ** Examples


obj <- new("VersionedBiobase")
classVersion(obj)

obj <- new("VersionedBiobase", versions=list(A="1.0.0"))
classVersion(obj)

setClass("A", contains="VersionedBiobase")

classVersion("A")
a <- new("A")
classVersion(a)

obj <- new("VersionedBiobase", versions=c(MyVersion="1.0.0"))
classVersion(obj)

setClass("B",
         contains="VersionedBiobase",
         prototype=prototype(new("VersionedBiobase",versions=list(B="1.0.0"))))

classVersion("B")
b <- new("B")
classVersion(b)

removeClass("A")
removeClass("B")




