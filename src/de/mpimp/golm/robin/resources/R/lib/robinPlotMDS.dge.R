robinPlotMDS.dge <- function (x, top = 500, labels = colnames(x), cex = 1,
    dim.plot = c(1, 2), ndim = max(dim.plot), groups=NULL, ...)
{
    library(edgeR)
    if (is.matrix(x))
        x <- DGEList(counts = x)
    if (!is(x, "DGEList"))
        stop("x must be a DGEList or a matrix")
    ok <- is.finite(x$counts)
    if (!all(ok))
        x <- x[apply(ok, 1, all), ]
    if (is.null(labels))
        labels <- 1:dim(x)[2]
    nprobes <- nrow(x)
    nsamples <- ncol(x)
    if (is.null(labels))
        labels <- 1:nsamples
    if (ndim < 2)
        stop("dim.plot must be at least two")
    if (nsamples < ndim)
        stop("Too few samples")
    x$samples$group <- factor(rep.int(1, nsamples))
    cn <- colnames(x)
    dd <- matrix(0, nrow = nsamples, ncol = nsamples, dimnames = list(cn,
        cn))
    twd <- estimateTagwiseDisp(estimateCommonDisp(x), grid.length = 500)
    o <- order(twd$tagwise.dispersion, decreasing = TRUE)[1:min(nprobes,
        top)]
    subdata <- x$counts[o, ]
    gm <- function(x) exp(mean(log(x)))
    myFun <- function(delta, y, ...) colSums(condLogLikDerDelta(y,
        delta, ...))
    for (i in 2:(nsamples)) {
        for (j in 1:(i - 1)) {
            mm <- subdata[, c(i, j)]
            rs5 <- rowSums(mm) > 5
            norm <- t(t(mm)/colSums(mm)) * gm(colSums(mm))
            delta <- optimize(myFun, interval = c(1e-04, 0.99),
                tol = 1e-06, maximum = TRUE, y = norm[rs5, ],
                der = 0, doSum = FALSE)
            dd[i, j] = sqrt(delta$maximum/(1 - delta$maximum))
        }
    }
    a1 <- cmdscale(as.dist(dd), k = ndim)

    layout(matrix(1:2, nc = 2), c(3,1))

    plot(a1[, dim.plot[1]], a1[, dim.plot[2]], xlab = paste("Dimension",
        dim.plot[1]), ylab = paste("Dimension", dim.plot[2]), pch=as.numeric(groups), col=as.numeric(groups), cex=1.25, ...)

    for (i in 1:length(unique(groups))) {
        print(unique(groups)[i])
        groupidx <- which(groups==unique(groups)[i])
        print(groupidx)

        center.x <- mean(a1[groupidx, 1])
        center.y <- mean(a1[groupidx, 2])
        x.radius <- max(a1[groupidx, 1]) - min(a1[groupidx, 1])
        y.radius <- max(a1[groupidx, 2]) - min(a1[groupidx, 2])

        ellipse(center=c(center.x, center.y), radius=c(x.radius, y.radius), rotate=0, add=T, col=unique(groups)[i], lty="dotted")

    }

    par(mai = c(0,0,1.01,0))
    plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
    legend("topleft", legend=levels(groups), col=unique(as.numeric(groups)), pch=unique(as.numeric(groups)), bty="n", cex=0.75)
    return(invisible(dd))
} 