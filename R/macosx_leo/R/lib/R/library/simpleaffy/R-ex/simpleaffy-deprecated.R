### Name: simpleaffy-deprecated
### Title: Does simpleaffy have a QC definition file for the specified
###   array?
### Aliases: simpleaffy-deprecated getTao getAlpha1 getAlpha2 getActin3
###   getActinM getActin5 getGapdh3 getGapdhM getGapdh5 getAllQCProbes
###   getBioB getBioC getBioD getCreX getAllSpikeProbes haveQCParams
### Keywords: misc

### ** Examples

  #old
  getBioB("hgu133plus2cdf")
  getActin3("hgu133plus2cdf")
  getActinM("hgu133plus2cdf")
  getActin5("hgu133plus2cdf")
  #new
  setQCEnvironment("hgu133plus2cdf")
  qc.get.spikes()["bioB"]
  r <- qc.get.probes()
  r["actin3"]
  r["actinM"]
  r["actin5"]



