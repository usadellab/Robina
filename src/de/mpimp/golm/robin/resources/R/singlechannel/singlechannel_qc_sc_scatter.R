########################################
# Scatter plots of all combinations of chips
########################################

samplenames <- gsub("(\\.txt|\\.cut|\\.tab)",
                    "",
                    basename(colnames(data.norm$E)),
                    perl=T,
                    ignore.case=T)


for (i in 1:ncol(data.norm$E)) {
    for (j in 1:ncol(data.norm$E)) {
        if (j>i) {
            png(file=paste(tempRoot, "_sc_scatter_", i, j, ".png", sep=""),
                     width=480,
                     height=480)

            plot(   data.norm$E[,i],
                    data.norm$E[,j],
                    xlab = samplenames[i],
                    ylab = samplenames[j] )

            abline(0,1, col="blue")
            abline(1,1, col="red")
            abline(-1,1, col="red")
            dev.off()
        }
    }
}