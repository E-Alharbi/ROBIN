[![Build Status](https://travis-ci.com/E-Alharbi/ProteinModelBuildingPipelinePredictor.svg?token=z92wc12inrqPgG6Faxv2&branch=master)](https://travis-ci.com/E-Alharbi/ProteinModelBuildingPipelinePredictor)

# Performance prediction of automated crystallographic model-building pipelines

A tool to predict the performance of three crystallographic model building pipelines (ARP/wARP, Buccaneer and  PHENIX AutoBuild) as well as their combinations. Structure completeness and R-work/R-free are the measures that the tool can predict.       



## Prerequisites

- CCP4 <br />
You need a CCP4 installed in your machine. You need to set up the CCP4 environment variables before using this tool. To set up the CCP4 environment variables, from the command line, run this command from the CCP4 installation directory 

source ccp4.setup-sh   


## How to use PMBPP? 

You need to download the jar file from here <a href="https://github.com/E-Alharbi/ProteinModelBuildingPipelinePredictor/releases"> PMBPP </a> 

-To start from experimental phasing <br />
java -jar PMBPP-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP

-For MR <br />
java -jar PMBPP-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP MR=T SequenceIdentity=0.85



## Authors

Emad Alharbi, Radu Calinescu and Kevin Cowtan



## Acknowledgments

