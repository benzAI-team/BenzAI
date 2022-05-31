---
layout: default
---

# BenzAI software

BenzAI is an open-source software for chemists that addresses several questions about benzenoids using artificial intelligence techniques.
It allows notably for:
* generating benzenoid structures on the basis of several criteria (number of hexagons/carbon atoms/hydrogen atoms, structural properties, presence or absence of one or more patterns, etc.),
* analyzing their  electronic structure,
* providing infrared spectra stored in an external database.  
[More details](details)

These problems are modeled and solved thanks to constraint programming and, in particular, by using [Choco Solver](https://www.cosling.com/fr/choco-solver).

# Download links 

* [Source code](https://github.com/benzAI-team/BenzAI)
* [Release](https://github.com/benzAI-team/BenzAI/releases)
* An [archive](benzenoids.zip) containing some well-known benzenoids in our [graph file format](graph_format).

# Bibliography
* Y. Carissan, D. Hagebaum-Reignier, N. Prcovic, C. Terrioux and A. Varet.  
  [Using Constraint Programming to Generate Benzenoid Structures in Theoretical Chemistry](https://hal.archives-ouvertes.fr/hal-02931934/).   
  In _Proceedings of the 26th International Conference on Principles and Practice of Constraint Programming (CP)_, pages 690-706, 2020.
* Y. Carissan, C. Dim, D. Hagebaum-Reignier, N. Prcovic, C. Terrioux and A. Varet.  
  [Computing the Local Aromaticity of Benzenoids Thanks to Constraint Programming](https://hal-amu.archives-ouvertes.fr/hal-02931928).  
  In _Proceedings of the 26th International Conference on Principles and Practice of Constraint Programming (CP)_, pages 673-689, 2020.
* Y. Carissan, D. Hagebaum-Reignier, N. Prcovic, C. Terrioux and A. Varet.  
  [Exhaustive Generation of Benzenoid Structures Sharing Common Patterns](https://hal-amu.archives-ouvertes.fr/hal-03402690).  
  In _Proceedings of the 27th International Conference on Principles and Practice of Constraint Programming (CP)_, pages 19:1-19:18, 2021.
* Y. Carissan, D. Hagebaum-Reignier, N. Prcovic, C. Terrioux and A. Varet.  
  [How Constraint Programming Can Help Chemists to Generate Benzenoid Structures and Assess the local Aromaticity of Benzenoids](https://link.springer.com/article/10.1007/s10601-022-09328-x).   
  In _Constraints_, 2022.
* A. Varet, N. Prcovic, C. Terrioux, D. Hagebaum-Reignier and Y. Carissan.  
  [BenzAI: A Program to Design Benzenoids with Defined Properties Using Constraint Programming](https://pubs.acs.org/doi/10.1021/acs.jcim.2c00353).   
  In _Journal of Chemical Information and Modeling_, 2022.
  
[BibTeX file](biblio.bib)


# BenzAI team
* Adrien VARET
* [Yannick CARISSAN](https://ism2.univ-amu.fr/fr/annuaire/ctom/carissanyannick)
* [Denis HAGEBAUM-REIGNER](https://ism2.univ-amu.fr/fr/annuaire/ctom/hagebaum-reignierdenis)
* Nicolas PRCOVIC
* [Cyril TERRIOUX](https://pageperso.lis-lab.fr/cyril.terrioux/en/index.html)

# Issues and suggestions
If you encounter any problem, if you want to report a bug, or if you want to propose a property or a calculation that you would like to see implemented, please contact us using the form available at [https://github.com/benzAI-team/BenzAI/issues](https://github.com/benzAI-team/BenzAI/issues).

# Contact
* firstname.name@univ-amu.fr
