### Name: matchprobes
### Title: A function to match a query sequence to the sequences of a set
###   of probes.
### Aliases: matchprobes
### Keywords: manip

### ** Examples

  if(require("hgu95av2probe")){
    data("hgu95av2probe")
    seq <- hgu95av2probe$sequence[1:20]
    target <- paste(seq, collapse="")
    matchprobes(target, seq, probepos=TRUE)
  }



