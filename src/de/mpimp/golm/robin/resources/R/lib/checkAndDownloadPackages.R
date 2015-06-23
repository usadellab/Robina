######################################
# this script checks whether all required
# R packages are available on the system
# this should be the case for all distributions
# of Robin that come with an embedded R 
# engine (Win, MacOSX). For other 
# platforms it will try to download the
# packages.
#######################################

packages <- installed.packages()
required <- c(  "affy", 
                "affyPLM", 
                "RankProd", 
                "limma", 
                "gcrma", 
                "statmod", 
                "marray", 
                "plier",
                "makecdfenv",
                "edgeR",
                "DESeq",
                "EDASeq"    
)

## check which version is installed
ver <- version$version.string
#if (length(grep("2\\.15\\.0",ver)) != 1 ) {
#    print("__WARNINGS__", quote=F)
#	print(paste("Your installed R version is", ver), quote=F)
#	print("RobiNA was designed to work with v2.15.0, though.", quote=F)
#	print("Probably there will be problems...", quote=F)
#    print("__WARNINGS_END__", quote=F) 
#}

missing <- required[!(required %in% packages[,1])]

if ( length(missing) != 0 ) {
	
	print("__WARNINGS__", quote=F)
	print("Trying to download missing packages...", quote=F)
	print(missing, quote=F)
	print("This will require a working internet connection", quote=F)
	print("that is not blocked by a firewall or a restrictive", quote=F)
	print("proxy server. If the download fails, please check", quote=F)
	print("your internet connection/settings and retry.", quote=F)
    print("__WARNINGS_END__", quote=F) 
	
	
	source("http://bioconductor.org/biocLite.R")
	biocLite(missing)
} else {
	print("Congratulations, your installation contains all needed packages.", quote=F)
}