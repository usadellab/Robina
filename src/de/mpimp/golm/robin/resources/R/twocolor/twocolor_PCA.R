################################
# plot PCA on twocolor array
# MAList data
################################

## get rid of incomplete measurements
noNA <- na.omit(MA$M)
pca <- prcomp(t(noNA))

quartz()
plot(	pca$x[1,], pca$x[2,],
		ylab="PC 2", 
		xlab="PC 1",
		main="Pricipal components plot",
		col=c(1:ncol(noNA)),
		pch=c(1:ncol(noNA)))

lnames <- basename(colnames(MA$M))

legend(	0, 0, lnames, bty="n",
		col=c(1:ncol(noNA)),
		pch=c(1:ncol(noNA)))
