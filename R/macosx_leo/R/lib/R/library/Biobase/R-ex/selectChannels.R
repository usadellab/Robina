### Name: selectChannels
### Title: Create a new NChannelSet instance by selecting specific channels
### Aliases: selectChannels
### Keywords: manip

### ** Examples

obj <- new("NChannelSet",
           R=matrix(runif(100), 20, 5),
           G=matrix(runif(100), 20, 5))

## G channel as NChannelSet
selectChannels(obj, "G")
  


