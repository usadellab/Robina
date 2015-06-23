### Name: qc.read.file
### Title: Read a file defining the QC parameters for a specified array and
###   set up the QC Environment
### Aliases: qc.read.file
### Keywords: misc

### ** Examples

  fn <- system.file("extdata","hgu133plus2cdf.qcdef",package="simpleaffy")
  qc.read.file(fn)
  qc.get.spikes()
  qc.get.probes()
  qc.get.ratios()



