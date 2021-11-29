[![Build Status](https://app.travis-ci.com/E-Alharbi/ROBIN.svg?token=z92wc12inrqPgG6Faxv2&branch=master)](https://app.travis-ci.com/E-Alharbi/ROBIN)

# Performance prediction of automated crystallographic model-building pipelines

ROBIN: c**R**ystallographic m**O**del **B**uilding p**I**peli**N**es predictor is a tool to predict the performance of four crystallographic model-building pipelines (ARP/wARP, Buccaneer, PHENIX AutoBuild and SHELXE) as well as their combinations. Structure completeness and R-work/R-free are the measures that the tool can predict.    



## Prerequisites

- CCP4 <br />
You need the CCP4 installed in your machine. You need to set up the CCP4 environment variables before using this tool. To set up the CCP4 environment variables, from the command line, run this command from the CCP4 installation directory. 
```
source ccp4.setup-sh  
```

## How to use Robin predictor? 

You need to download the jar file from here <a href="https://github.com/E-Alharbi/ROBIN/releases"> Robin </a> or you can use Robin from the web application <a href="http://www.robin-predictor.org"> http://www.robin-predictor.org </a>

- To start from experimental phasing <br />
```
java -jar Robin-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP
```
| Keyword | Explanation |
| ------------- | ------------- |
| mtz | reflection data in MTZ format |
| Phases | the phases (Hendrickson-Lattman coefficients) as in the mtz file  |
| Colinfo | column labels for the observed amplitudes  |

To get an accurate prediction, use the phases after DM when you predict the performance of ARP/wARP, Buccaneer, Phenix AutoBuild(P) and SHELXE(P). 

```
java -jar Robin-Runnable-(version).jar PredictDatasets Datasets=PathToDatasetsFolder Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP ParrotPhases=parrot.ABCD.A,parrot.ABCD.B,parrot.ABCD.C,parrot.ABCD.D
```

The above command will use Parrot phases for the pipelines that should be predicted using these phases. 


- For MR <br />
```
java -jar Robin-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP MR=T SequenceIdentity=0.85
```

| Keyword | Explanation |
| ------------- | ------------- |
| MR | Indicating that this is a molecular replacement case. Set to T    |
| SequenceIdentity | sequence identity for MR case  |

The output of the above command is a table that contains the following: 

| Pipeline variant  | R-free | R-free prediction group | R-work | R-work prediction group| Completeness |Completeness prediction group|
| ------------- | ------------- | ------------- | ------------- |------------- |------------- |------------- |

*Completeness: is the percentage of residues in the deposited model whose C alpha atoms have the same residue type as, and coordinates within 1.0 A ̊ of, the corresponding residue in the built model.  
*R-free, R-work completeness and  prediction group: it is an uncertainty estimation. A lower number means that more accurate prediction.


## Predicting multiple datasets  
You can predict the performance of the pipeline for multiple data sets using one command line. This helps when you have multiple initial phases set, and you want to find out which of these initial phases is the best to build a protein model. For example, in case of MR and you have multiple search models.  

- To start from experimental phasing <br />

```
java -jar Robin-Runnable-(version).jar PredictDatasets Datasets=PathToDatasetsFolder Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP
```

| Keyword | Explanation |
| ------------- | ------------- |
| Datasets | path to the datasets folder. The folder should contain the datasets in mtz format |



- For MR
```
java -jar Robin-Runnable-(version).jar PredictDatasets Datasets=PathToDatasetsFolder Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP MR=T
```
*Sequence identity should be saved into a JSON file. The JSON filename has to be the same as the mtz file. The JSON file should contain the sequence identity in such as this structure: 
 ```
 {
 "gesamt_seqid": 0.22
}
 ```

The output will be saved in a CSV file contains the following for all the pipelines :
 | ID | R-free |R-work|Completeness|Prediction|PDB|Pipeline
| ------------- | ------------- |------------- |------------- |------------- |------------- |------------- |


In addition to these, the CSV will contain the prediction interval for R-free, R-work and Completeness.   

A CSV file will be created for each pipeline. 

You can also run the above commands for one pipeline. For example, only predict the performance of ARP/wARP: 
 ```
java -jar Robin-Runnable-(version).jar PredictDatasets Datasets=PathToDatasetsFolder Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP FilteredModels=ARPwARP FilterModels=T
```

To measure the execution time, we tested Robin (on MacBook Pro 2.5 GHz Intel Core i7) to predict the performance of ARP/wARP, Buccaneer, PHENIX AutoBuild and SHELXE using 1351 data sets. The execution time was around 18 mins.    
 
## How to speed up PMBPP? 

- We compressed the predictive models due to its large size. Uncompressing might take around 3 mins, and it happens each time you run the PMBPP. If you want to uncompress the predictive models permanently, use the following command:      
```
java -jar Robin-Runnable-(version).jar UncompressMLModel
```  

The above command will uncompress the predictive models and save them, meaning that the Robin will not need to uncompress in each run. Please do not use the above command more than one time. If something went wrong, remove the folder that Robin created and then rerun the above command.   

- An alternative solution is to predict the performance of a specific pipeline. The following command predict only the performance of ARP/wARP 

1- To start from experimental phasing <br />
```
java -jar Robin-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP FilteredModels=ARPwARP FilterModels=T 
```
2- For MR <br />
```
java -jar Robin-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP MR=T SequenceIdentity=0.85 FilteredModels=ARPwARP FilterModels=T
```       

## Auto-generated scripts

Robin predictor generates a script for each pipeline and pipeline combination to use in running them. The script is customized depending on the data provided by the user. 

To generate the script, add this keyword: 
```
GenerateScript=T
```

## Names of the pipelines to use with FilteredModels keyword 
1. ARPwARP|Phenix AutoBuild(P)
2. Buccaneer|Phenix AutoBuild(P)
3. Phenix AutoBuild
4. SHELXE
5. SHELXE(P)
6. SHELXE|Phenix AutoBuild(P)
7. Phenix AutoBuild(P)
8. Buccaneer
9. SHELXE|ARPwARP
10. SHELXE(P)|Phenix AutoBuild(P)
11. Phenix AutoBuild|ARPwARP
12. ARPwARP
13. SHELXE(P)|ARPwARP
14. Phenix AutoBuild|Buccaneer
15. Buccaneer|Phenix AutoBuild
16. SHELXE(P)|Buccaneer
17. Buccaneer|ARPwARP
18. ARPwARP|Phenix AutoBuild
19. SHELXE|Buccaneer
20. Phenix AutoBuild(P)|Buccaneer
21. SHELXE|Phenix AutoBuild
22. SHELXE(P)|Phenix AutoBuild
23. ARPwARP|Buccaneer
24. Phenix AutoBuild(P)|ARPwARP

*(P) meaning, this pipeline should be run after Parrot 

## Authors

Emad Alharbi, Paul Bond, Kevin Cowtan and Radu Calinescu

## Citing

```
Alharbi, E., Bond, P., Calinescu, R., & Cowtan, K. (2021). Predicting the performance of Automated Crystallographic Model-building pipelines. Acta Crystallographica Section D Structural Biology, 77(12). https://doi.org/10.1107/s2059798321010500
```