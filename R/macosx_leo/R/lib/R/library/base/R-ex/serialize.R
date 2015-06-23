### Name: serialize
### Title: Simple Serialization Interface
### Aliases: .readRDS .saveRDS serialize unserialize
### Keywords: internal file

### ** Examples

x <- serialize(list(1,2,3), NULL)
unserialize(x)
## test earlier interface as a length-one character vector
y <- rawToChar(x)
unserialize(y)



