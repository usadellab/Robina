library(Biostrings)
library(limma)


gffRead <- function(gffFile, nrows = -1) {
     cat("Reading ", gffFile, ": ", sep="")
     gff = read.table(gffFile, sep="\t", as.is=TRUE, quote="",
     header=FALSE, comment.char="#", nrows = nrows,
     colClasses=c("character", "character", "character", "integer",  
"integer",
     "character", "character", "character", "character"))
     colnames(gff) = c("seqname", "source", "feature", "start", "end",
             "score", "strand", "frame", "attributes")
     cat("found", nrow(gff), "rows with classes:",
         paste(sapply(gff, class), collapse=", "), "\n")
     stopifnot(!any(is.na(gff$start)), !any(is.na(gff$end)))
     return(gff)
}


extractAttributeByKey <- function(gff.line=NULL, key=NULL, att.index=9) {
	attvec <- strsplit(gff.line[att.index], ";")
	attmat <- strsplit2(attvec[[1]], split="=")
	attmat[which(attmat[,1] == key), 2]	
}


getGCcontentForLocus <- function(locus=NULL, seq.data=NULL, exons=NULL) {	
	locus.exons <- exons[grep(locus, exons$attributes), ]
	chromosome <- locus.exons[1,]$seqname # they'll all be on the same chromosome, right?

	min.start <- min(c(locus.exons$end, locus.exons$start))
	max.end	  <- max(c(locus.exons$end, locus.exons$start))

	len <- max.end - min.start
	merge.map <- rep.int(0,len)

	for (i in 1:nrow(locus.exons) ) {
		exon <- locus.exons[i, ]
		start <- exon$start - min.start
		end <- exon$end - min.start
		merge.map[start:end] <- 1	
	}

	merged.exons.length <- sum(merge.map)
	run.lengths <- rle(merge.map)

	starts <- c(1)
	## get the exon start indices in merge.map
	if (length(run.lengths$values) >= 2) {
		for (i in 1:length( which(run.lengths$values == 0 ) ) ) {
			starts <- c(starts, sum(run.lengths$length[1:which(run.lengths$values == 0 )[i]]))
		}
	}
	widths <- run.lengths$lengths[which(run.lengths$values == 1)]
	starts <- starts + min.start # get the actual positions in the chromosome

	## now get the sequence together
	merged.exons.sequence <- DNAStringSet()
	
	chr.seq <- seq.data[which(names(seq.data) == chromosome) ]

	for (i in 1:length(starts)) {
		merged.exons.sequence <- append(merged.exons.sequence, 
			subseq(chr.seq,	start=starts[i], width=widths[i]))	
	}

	GC.cont <- sum(letterFrequency(merged.exons.sequence, "GC")) / sum(width(merged.exons.sequence))
    c(sum(width(merged.exons.sequence)), GC.cont)
}

generateGCLengthTable <- function(fasta.file=NULL, gff3.file=NULL) {
	## read the gff3 file
	gff <- gffRead( gff3.file )
	
	## extract the locus IDs
	genes <- gff[which(gff$feat == "gene"), ]
	
	locus.ids <- apply(genes, 1, extractAttributeByKey, key="ID")	
	locus.ids <- unique(locus.ids)
	
	#read the seq data
	 seq.data <- read.DNAStringSet( fasta.file )

	 ## reduce names to first word (...)
	 names(seq.data) <- sub("^(\\w+)\\s+.*", "\\1", names(seq.data))
	 
	 exons <- gff[which(gff$feat == "exon"), ]
	 
	 GC.table <- matrix(
	 	unlist(lapply(locus.ids, getGCcontentForLocus, seq.data=seq.data, exons=exons))
	 		, ncol=2, byrow=T, dimnames=list(c(locus.ids), c("perc_GC", "length")))
	 
	 write.table(GC.table, file="input/gene.lengths", sep="\t", row.names=T, col.names=F, quote=F)
}