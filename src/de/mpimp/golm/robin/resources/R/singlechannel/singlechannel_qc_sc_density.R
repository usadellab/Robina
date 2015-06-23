# this is just to make the plotSigDensities function happy...
data2 <- data
data2$R <- data$E
data2$G <- NULL
data2$Gb <- NULL

data2.norm <- data.norm
data2.norm$R <- data.norm$E
data2.norm$G <- NULL
data2.norm$Gb <- NULL

outfile <- paste(tempRoot, "_sc_density", ".png", sep="")
colnames(data2$R) <- basename(colnames(data2$R))
longestName <- nchar(max(colnames(data2$R)))

png(file=outfile, width=18, height=6+longestName*0.075, units="in", res=120)
par(mfrow=c(1,3), cex=0.75, mar=c(longestName*0.6, 4, 4, 2))

# generate the density plots
plotSigDensities(   data2,
                    main="Unnormalized signal intensity distribution",
                    col = colors)

plotSigDensities(   data2.norm,
                    main="Normalized: subtract, scale",
                    col = colors)

# generate boxplots of the unnormalized expression values
boxplot(    log2(data2$R),
            las=2,
            col=colors,
            main=paste("Boxplot of", length(PARAM_INPUTFILES), "input files"))
dev.off()

