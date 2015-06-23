### Name: controlStatus
### Title: Set Status of each Spot from List of Spot Types
### Aliases: controlStatus
### Keywords: IO

### ** Examples

genes <- data.frame(ID=c("Control","Control","Control","Control","AA1","AA2","AA3","AA4"),
Name=c("Ratio 1","Ratio 2","House keeping 1","House keeping 2","Gene 1","Gene 2","Gene 3","Gene 4"))
types <- data.frame(SpotType=c("Gene","Ratio","Housekeeping"),ID=c("*","Control","Control"),Name=c("*","Ratio*","House keeping*"),col=c("black","red","blue"))
status <- controlStatus(types,genes)



