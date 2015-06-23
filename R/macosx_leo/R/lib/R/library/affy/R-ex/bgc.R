### Name: bg.correct
### Title: Background Correction
### Aliases: bg.correct bg.correct.none bg.correct.rma bg.correct.mas
### Keywords: manip

### ** Examples

    data(affybatch.example)

    ##bgc will be the bg corrected version of affybatch.example 
    bgc <- bg.correct(affybatch.example,method="rma") 

    ##This plot shows the tranformation
    plot(pm(affybatch.example)[,1],pm(bgc)[,1],log="xy",
        main="PMs before and after background correction")




