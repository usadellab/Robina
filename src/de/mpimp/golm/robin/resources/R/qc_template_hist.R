## create a density plot of the raw probeset intensities
png(file = paste("__PARAM_OUTPUTFILE__", "hist", ".png", sep=""), __PARAM_JPEG_PARAMS__)
plotHist(data, farbe=colors)
dev.off()

