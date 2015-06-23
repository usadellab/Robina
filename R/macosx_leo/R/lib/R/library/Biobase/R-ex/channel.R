### Name: channel
### Title: Create a new ExpressionSet instance by selecting a specific
###   channel
### Aliases: channel
### Keywords: manip

### ** Examples

obj <- new("NChannelSet",
           R=matrix(runif(100), 20, 5),
           G=matrix(runif(100), 20, 5))
## G channel as ExpressionSet
channel(obj, "G")
  


