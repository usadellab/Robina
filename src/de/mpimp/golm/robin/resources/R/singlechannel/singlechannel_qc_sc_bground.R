
## create images of the background signal intensity

for (i in 1:ncol(data$Eb)) {
    outfile <- paste(tempRoot, "_sc_bground_",i , ".png", sep="")
    png(file=outfile, height=480, width=960)
    imageplot(	data$Eb[,i],
                layout=data$printer,
                main=basename(colnames(data$Eb)[i]),
                low="white",
                high="blue",
                ncolors=100 )
    dev.off()
}

