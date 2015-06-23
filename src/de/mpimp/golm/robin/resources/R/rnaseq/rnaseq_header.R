##
# generic template for loading the raw count data
##

PROJECT_DIR <- "__OUTPUT_DIR__"
PROJECT_NAME <- basename(PROJECT_DIR)

setwd(PROJECT_DIR)

source("source/lib/info.R")

raw <- read.table(file=paste("detailed_results/",PROJECT_NAME, "_raw_countstable.txt", sep=""), header=T, row.names=1, sep="\t")
raw <- raw[, order(colnames(raw))]

# make sure there are no missing values (NAs) in any column
raw[is.na(raw)] <- 0


groups <- as.factor( 
    __PARAM_GROUPS__
)


