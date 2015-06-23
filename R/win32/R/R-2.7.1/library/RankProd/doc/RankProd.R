###################################################
### chunk number 1: 
###################################################
library(RankProd)


###################################################
### chunk number 2: 
###################################################
data(arab)


###################################################
### chunk number 3: 
###################################################
n <- 5
cl <- rep(1,5)
cl


###################################################
### chunk number 4: 
###################################################
n1 <- 5
n2 <- 4
cl <- rep(c(0,1),c(n1,n2))
cl


###################################################
### chunk number 5: 
###################################################
n1 <- 5
n2 <- 4
cl <- rep(c(0,1),c(n1,n2))
cl
origin <- rep(1, n1+n2)
origin


###################################################
### chunk number 6: 
###################################################
n <- 9
cl <- rep(1,n)
cl
origin <- rep(1, n)
origin


###################################################
### chunk number 7: 
###################################################
origin <- c(rep(1, 6), rep(2,4), rep(3,8))
origin


###################################################
### chunk number 8: 
###################################################
colnames(arab)
arab.cl
arab.origin


###################################################
### chunk number 9: 
###################################################
arab.sub <- arab[,which(arab.origin==1)]
arab.cl.sub <- arab.cl[which(arab.origin==1)]
arab.origin.sub <- arab.origin[which(arab.origin==1)]


###################################################
### chunk number 10: 
###################################################
RP.out <- RP(arab.sub,arab.cl.sub, num.perm=100, logged=TRUE,
na.rm=FALSE,plot=FALSE,  rand=123)


###################################################
### chunk number 11: 
###################################################
plotRP(RP.out, cutoff=0.05)


###################################################
### chunk number 12: 
###################################################
topGene(RP.out,cutoff=0.05,method="pfp",logged=TRUE,logbase=2,gene.names=arab.gnames)


###################################################
### chunk number 13: 
###################################################
##identify differentially expressed  genes
RP.adv.out <-  RPadvance(arab,arab.cl,arab.origin,num.perm=100,
logged=TRUE,gene.names=arab.gnames,rand=123)


###################################################
### chunk number 14: 
###################################################
plotRP(RP.adv.out, cutoff=0.05)


###################################################
### chunk number 15: 
###################################################
data(lymphoma)


###################################################
### chunk number 16: 
###################################################
refrs <- (1:8)*2-1
samps <- (1:8)*2
M <- lym.exp[,samps]-lym.exp[,refrs]
colnames(M)
cl <- c(rep(0,4),rep(1,4))
cl  #"CLL" is class 1, and "DLCL" is class 2
RP.out <- RP(M,cl, logged=TRUE, rand=123)


###################################################
### chunk number 17: 
###################################################
topGene(RP.out,cutoff=0.05,logged=TRUE,logbase=exp(1))


###################################################
### chunk number 18: 
###################################################
arab.cl2 <- arab.cl
arab.cl2[arab.cl==0 &arab.origin==2] <- 1
arab.cl2[arab.cl==1 &arab.origin==2] <- 0
arab.cl2


###################################################
### chunk number 19: 
###################################################
Rsum.adv.out <- RSadvance(arab,arab.cl2,arab.origin,num.perm=100,
logged=TRUE,gene.names=arab.gnames,rand=123)
topGene(Rsum.adv.out,cutoff=0.05,gene.names=arab.gnames)


###################################################
### chunk number 20: 
###################################################
topGene(Rsum.adv.out,num.gene=10,gene.names=arab.gnames)


###################################################
### chunk number 21: 
###################################################
plotRP(Rsum.adv.out,cutoff=0.05)


###################################################
### chunk number 22: 
###################################################
RP.adv.out <- RPadvance(arab,arab.cl2,arab.origin,num.perm=100,
logged=TRUE,gene.names=arab.gnames,rand=123)


###################################################
### chunk number 23: 
###################################################
topGene(RP.adv.out,cutoff=0.05,gene.names=arab.gnames)


