# Robina
This is Robina - Arcas featuring Isoform (as in allopolyploid plant genomes) and in the future single cell analysis. Uses Kallisto
The Arcas branch compiles in JAVA 8 again still lots of clean ups to do.  
Arcas drops in Kallisto for bowtie to allow isoform analysis important for polyploids and plants in general.  
In addition pseudpmapping is faster etc. Unlike our other workflows kallisto is chosen over salmon as kalisto is available for windows.  
Simpler, easier done. At the moment single reads uses vague estimates of length as does salmon.   
Genomic data will likely no longer be supported. (Thoughts?)
In the case of not freely accesibly genomes only offering scafolds and gffs e.g. C.arabica download the files and use e.g. gffread to convert fasta and gff3 to create transcript models like so:  
gffread -w coffee_transcripts.fa -g /path/to/Ca_scaffolds.fasta Ca_scaffolds.gff3

