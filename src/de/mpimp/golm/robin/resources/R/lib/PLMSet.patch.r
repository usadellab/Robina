setMethod("image",signature(x="PLMset"),
function(x,which=0,type=c("weights","resids","pos.resids","neg.resids","sign.resids"),use.log=TRUE,add.legend=FALSE,standardize=FALSE,col=NULL,...){
if (is.null(col)){
col.weights <- terrain.colors(25)
col.resids <- pseudoPalette(low="blue",high="red",mid="white")
col.pos.resids <- pseudoPalette(low="white",high="red")
col.neg.resids <- pseudoPalette(low="blue",high="white")
} else {
col.weights <- col
col.resids <- col
col.pos.resids <-  col
col.neg.resids <-  col
}
type <- match.arg(type)
pm.index <- unique(unlist(indexProbes(x, "pm",row.names(coefs(x)))))
rows <- x@nrow
cols <- x@ncol
pm.x.locs <- pm.index%%rows
pm.x.locs[pm.x.locs == 0] <- rows
pm.y.locs <- pm.index%/%rows + 1
xycoor <- matrix(cbind(pm.x.locs,pm.y.locs),ncol=2)
xycoor2 <- matrix(cbind(pm.x.locs,pm.y.locs+1),ncol=2)
if (is.element(type,c("weights"))){
if (any(dim(x@weights[[1]]) ==0) & any(dim(x@weights[[2]]) ==0)){
stop("Sorry this PLMset does not appear to have weights
");
} 
if (which == 0){
which <- 1:max(dim(x@weights[[1]])[2], dim(x@weights[[2]])[2])
}
}
if (is.element(type,c("resids","pos.resids","neg.resids","sign.resids"))){
if (any(dim(x@residuals[[1]]) ==0) & any(dim(x@residuals[[2]]) ==0)){
stop("Sorry this PLMset does not appear to have residuals
");
}
if (which == 0){
which <- 1:max(dim(x@residuals[[1]])[2],dim(x@residuals[[2]])[2])
}
if (standardize & type == "resids"){
if (x@model.description$R.model$response.variable == 0){
resid.range <- c(-4,4)
} else if (x@model.description$R.model$response.variable == -1){
resid.range <- range(resid(x,standardize)[[2]])
} else if (x@model.description$R.model$response.variable == 1){
resid.range <- range(resid(x,standardize)[[1]])
}
} else {
if (x@model.description$R.model$response.variable == 0){
resid.range1 <- range(x@residuals[[1]])
resid.range2 <- range(x@residuals[[2]])
resid.range <- resid.range1
resid.range[1] <- min(resid.range1 , resid.range2)
resid.range[2] <- max(resid.range1 , resid.range2)
} else if (x@model.description$R.model$response.variable == -1){
resid.range <- range(x@residuals[[2]])
} else if (x@model.description$R.model$response.variable == 1){
resid.range <- range(x@residuals[[1]])
}
}
}
for (i in which){
if (type == "weights"){
weightmatrix <-matrix(nrow=rows,ncol=cols)
if (x@model.description$R.model$response.variable == 0){
weightmatrix[xycoor]<- x@weights[[1]][,i]
weightmatrix[xycoor2]<- x@weights[[2]][,i]
} else if (x@model.description$R.model$response.variable == -1){
weightmatrix[xycoor]<- x@weights[[2]][,i]
weightmatrix[xycoor2]<- x@weights[[2]][,i]
} else if (x@model.description$R.model$response.variable == 1){
weightmatrix[xycoor]<- x@weights[[1]][,i]
weightmatrix[xycoor2]<- x@weights[[1]][,i]
}
#this line flips the matrix around so it is correct
weightmatrix <-as.matrix(rev(as.data.frame(weightmatrix)))
if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
image(weightmatrix,col=col.weights,xaxt='n',yaxt='n',zlim=c(0,1))
# title(sampleNames(x)[i])
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(0,1,0.1), horizontal = FALSE, col = col.weights, main = "")
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
}
}
if (type == "resids"){
residsmatrix <- matrix(nrow=rows,ncol=cols)
if (standardize){
if (x@model.description$R.model$response.variable == 0){
residsmatrix[xycoor]<- resid(x,standardize)[[1]][,i]
residsmatrix[xycoor2]<- resid(x,standardize)[[2]][,i]
} else if  (x@model.description$R.model$response.variable == -1){
residsmatrix[xycoor]<- resid(x,standardize)[[2]][,i]
residsmatrix[xycoor2]<- resid(x,standardize)[[2]][,i]
} else if (x@model.description$R.model$response.variable == 1){
residsmatrix[xycoor]<- resid(x,standardize)[[1]][,i]
residsmatrix[xycoor2]<- resid(x,standardize)[[1]][,i]
}
} else {
if (x@model.description$R.model$response.variable == 0){
residsmatrix[xycoor]<- x@residuals[[1]][,i]
residsmatrix[xycoor2]<- x@residuals[[2]][,i]
} else if (x@model.description$R.model$response.variable == -1){
residsmatrix[xycoor]<- x@residuals[[2]][,i]
residsmatrix[xycoor2]<- x@residuals[[2]][,i]
} else if (x@model.description$R.model$response.variable == 1){
residsmatrix[xycoor]<- x@residuals[[1]][,i]
residsmatrix[xycoor2]<- x@residuals[[1]][,i]
}

}
#this line
#flips the matrix around so it is correct
residsmatrix<- as.matrix(rev(as.data.frame(residsmatrix)))
if (use.log){
if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
residsmatrix <- sign(residsmatrix)*log2(abs(residsmatrix)+1)
image(residsmatrix,col=col.resids,xaxt='n',
yaxt='n',main=sampleNames(x)[i],zlim=c(-max(log2(abs(resid.range)+1)),max(log2(abs(resid.range)+1))))
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(-max(log2(abs(resid.range)+1)),max(log2(abs(resid.range)+1)),0.1), horizontal = FALSE, col = col.resids, main = "",log.ticks=TRUE)
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
} 
} else {

if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
image(residsmatrix,col=col.resids,xaxt='n',
yaxt='n',main=sampleNames(x)[i],zlim=c(-max(abs(resid.range)),max(abs(resid.range))))
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(-max(abs(resid.range)),max(abs(resid.range)),0.1), horizontal = FALSE, col = col.resids, main = "")
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
} 
}
}
if (type == "pos.resids"){
residsmatrix <- matrix(nrow=rows,ncol=cols)

if (x@model.description$R.model$response.variable == 0){
residsmatrix[xycoor]<- pmax(x@residuals[[1]][,i],0)
residsmatrix[xycoor2]<- pmax(x@residuals[[2]][,i],0)
} else if (x@model.description$R.model$response.variable == -1){
residsmatrix[xycoor]<- pmax(x@residuals[[2]][,i],0)
residsmatrix[xycoor2]<- pmax(x@residuals[[2]][,i],0)
} else if (x@model.description$R.model$response.variable == 1){
residsmatrix[xycoor]<- pmax(x@residuals[[1]][,i],0)
residsmatrix[xycoor2]<- pmax(x@residuals[[1]][,i],0)
}
#this line flips the matrix around so it is correct
residsmatrix <- as.matrix(rev(as.data.frame(residsmatrix)))
if (use.log){
if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
residsmatrix <- sign(residsmatrix)*log2(abs(residsmatrix) +1)
image(residsmatrix,col=col.pos.resids,xaxt='n',
yaxt='n',main=sampleNames(x)[i],zlim=c(0,max(log2(pmax(resid.range,0)+1))))
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(0,max(log2(pmax(resid.range,0)+1)),0.1), horizontal = FALSE, col = col.pos.resids, main = "",log.ticks=TRUE)
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
} 
} else {
if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
image(residsmatrix,col=col.pos.resids,xaxt='n',
yaxt='n',main=sampleNames(x)[i],zlim=c(0,max(resid.range)))
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(0,max(resid.range),0.1), horizontal = FALSE, col = col.pos.resids, main = "")
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
} 
}
}
if (type == "neg.resids"){
residsmatrix <- matrix(nrow=rows,ncol=cols)
if (x@model.description$R.model$response.variable == 0){
residsmatrix[xycoor]<- pmin(x@residuals[[1]][,i],0)
residsmatrix[xycoor2]<- pmin(x@residuals[[2]][,i],0)
} else if (x@model.description$R.model$response.variable == -1){
residsmatrix[xycoor]<- pmin(x@residuals[[2]][,i],0)
residsmatrix[xycoor2]<- pmin(x@residuals[[2]][,i],0)
} else if (x@model.description$R.model$response.variable == 1){
residsmatrix[xycoor]<- pmin(x@residuals[[1]][,i],0)
residsmatrix[xycoor2]<- pmin(x@residuals[[1]][,i],0)
}
residsmatrix <- as.matrix(rev(as.data.frame(residsmatrix)))
if(use.log){
if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
residsmatrix <- sign(residsmatrix)*log2(abs(residsmatrix) +1)
image(residsmatrix,col=col.neg.resids,xaxt='n',
yaxt='n',main=sampleNames(x)[i],zlim=c(-log2(abs(min(resid.range))+1),0))
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(-max(log2(abs(pmin(resid.range,0))+1)),0,0.1), horizontal = FALSE, col = col.neg.resids, main = "",log.ticks=TRUE)
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
} 
} else {
if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
image(residsmatrix,col=col.neg.resids,xaxt='n',
yaxt='n',main=sampleNames(x)[i],zlim=c(-abs(min(resid.range)),0))
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(min(resid.range),0,0.1), horizontal = FALSE, col = col.neg.resids, main = "")
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
} 
}
}
if (type == "sign.resids"){
residsmatrix <- matrix(nrow=rows,ncol=cols)
if (x@model.description$R.model$response.variable == 0){
residsmatrix[xycoor]<- sign(x@residuals[[1]][,i])
residsmatrix[xycoor2]<- sign(x@residuals[[2]][,i])
} else if (x@model.description$R.model$response.variable == -1){
residsmatrix[xycoor]<- sign(x@residuals[[2]][,i])
residsmatrix[xycoor2]<- sign(x@residuals[[2]][,i])
} else if (x@model.description$R.model$response.variable == 1){
residsmatrix[xycoor]<- sign(x@residuals[[1]][,i])
residsmatrix[xycoor2]<- sign(x@residuals[[1]][,i])
}
if (add.legend){
layout(matrix(c(1, 2), 1, 2), width = c(9, 1))
par(mar = c(4, 4, 5, 3))
}
image(residsmatrix,col=col.resids,xaxt='n',
yaxt='n',main=sampleNames(x)[i],zlim=c(-1,1))
if (add.legend){
par(mar = c(4, 0, 5, 3))
pseudoColorBar(seq(-1,1,2), horizontal = FALSE, col = col.resids, main = "")
layout(1)
par(mar = c(5, 4, 4, 2) + 0.1)
} 
}
}
})