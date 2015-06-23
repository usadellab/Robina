## create a RNA degradation plot
png(file = paste("__PARAM_OUTPUTFILE__", "rna", ".png", sep=""), __PARAM_JPEG_PARAMS__)
plotDeg(data, farbe=colors)
dev.off()

