### Name: makeContrasts
### Title: Construct Matrix of Custom Contrasts
### Aliases: makeContrasts
### Keywords: regression

### ** Examples

makeContrasts(B-A,C-B,C-A,levels=c("A","B","C"))
makeContrasts(contrasts="A-(B+C)/2",levels=c("A","B","C"))
x <- c("B-A","C-B","C-A")
makeContrasts(contrasts=x,levels=c("A","B","C"))



