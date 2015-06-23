write.short.sessionInfo <- function(file=file) {
	info <- toLatex(sessionInfo())        
	info <- gsub("\\item", "", info, fixed=T)
	info <- gsub("\\begin{itemize}\\raggedright", "R session information", info, fixed=T)
	info <- gsub("\\end{itemize}", "", info, fixed=T)
	info <- gsub("\\verb|", "", info, fixed=T)
	info <- gsub("~", " ", info, fixed=T)
        info <- gsub("^\\s+", "\t", info, perl=T) 
        info <- sub("R session information", "", info, fixed=T)
	write(info, file)
}