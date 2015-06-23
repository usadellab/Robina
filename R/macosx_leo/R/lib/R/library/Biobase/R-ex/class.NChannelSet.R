### Name: NChannelSet-class
### Title: Class to contain data from multiple channel array technologies
### Aliases: NChannelSet-class NChannelSet class.NChannelSet
###   channelNames,NChannelSet-method channel,NChannelSet,character-method
###   sampleNames,NChannelSet-method sampleNames<-,NChannelSet,list-method
###   selectChannels,NChannelSet,character-method
###   initialize,NChannelSet-method
### Keywords: classes

### ** Examples

## An empty NChannelSet
obj <- new("NChannelSet")

## An NChannelSet with two channels (R, G) and no phenotypic data
obj <- new("NChannelSet",
           R=matrix(0,10,5), G=matrix(0,10,5))
## An NChannelSet with two channels and channel-specific phenoData
R <- matrix(0, 10, 3, dimnames=list(NULL, LETTERS[1:3]))
G <- matrix(1, 10, 3, dimnames=list(NULL, LETTERS[1:3]))
assayData <- assayDataNew(R=R, G=G)
data <- data.frame(ChannelRData=numeric(ncol(R)),
                   ChannelGData=numeric(ncol(R)),
                   ChannelRAndG=numeric(ncol(R)))
varMetadata <- data.frame(labelDescription=c(
                            "R-specific phenoData",
                            "G-specific phenoData",
                            "Both channel phenoData"),
                          channel=factor(c("R", "G", "_ALL_")))
phenoData <- new("AnnotatedDataFrame",
                 data=data, varMetadata=varMetadata)
obj <- new("NChannelSet",
           assayData=assayData, phenoData=phenoData)
obj

## G channel as NChannelSet
selectChannels(obj, "G")

## G channel as ExpressionSet
channel(obj, "G")

## Samples "A" and "C"
obj[,c("A", "C")]



