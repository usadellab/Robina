### Name: classVersion
### Title: Retrieve information about versioned classes
### Aliases: classVersion classVersion<- classVersion,ANY-method
###   classVersion,character-method
### Keywords: manip

### ** Examples

obj <- new("VersionedBiobase")

classVersion(obj)
classVersion(obj)["Biobase"]
classVersion(1:10) # no version
classVersion("ExpressionSet") # consult ExpressionSet prototype

classVersion(obj)["MyVersion"] <- "1.0.0"
classVersion(obj)



