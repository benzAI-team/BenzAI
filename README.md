# BenzAI

BenzAI is an open-source software for chemists that addresses several questions about benzenoids using artificial intelligence techniques.
It allows notably for:
* generating benzenoid structures on the basis of several criteria (number of hexagons/carbon atoms/hydrogen atoms, structural properties, presence or absence of one or more patterns, etc.),
* analyzing their electronic structure,
* providing infrared spectra stored in an external database.  
[More details](https://benzai-team.github.io/BenzAI/details)

These problems are modeled and solved thanks to constraint programming and, in particular, by using [Choco Solver](https://www.cosling.com/fr/choco-solver).

BenzAI is licensed under the terms of the [GPL v3](https://github.com/benzAI-team/BenzAI/blob/main/LICENSE).


# Download and installation

As your convenience, you can download an executable jar (from our [release](https://github.com/benzAI-team/BenzAI/releases) repository) or the [source code](https://github.com/benzAI-team/BenzAI).

BenzAI has the following requirements:
* Java 11
* Maven 3.6 or later (if you want to build BenzAI from sources)

## Building instructions
You can produce an executable jar thanks to Maven (available from [https://maven.apache.org](https://maven.apache.org) or from your favorite package manager). 

First, download the sources of BenzAI or clone the repository, for example, by executing the following command:  

    git clone https://github.com/benzAI-team/BenzAI.git
    
Then, from the source directory, execute the following command:  

    mvn clean package
    
The generated jar is located in the target directory.	

## Running BenzAI
BenzAI can be run by executing the following command (assuming that the filename of the jar is BenzAI.jar):  

    java -jar BenzAI.jar

# Third-party

BenzAI relies on several third-party open-source libraries, each being licensed under its own license (see the [https://github.com/benzAI-team/BenzAI/blob/main/3rd-party-licenses.md](https://github.com/benzAI-team/BenzAI/blob/main/3rd-party-licenses.md) files for more details).

* [Choco Solver](https://github.com/chocoteam/choco-solver)
* [gson](https://github.com/google/gson/)
* [JAMA: A Java Matrix Package](https://math.nist.gov/javanumerics/jama/)
* [JavaFX](https://openjfx.io/)
* [mysql-connector-java](https://github.com/mysql/mysql-connector-j)

Icons come from [https://www.flaticon.com/authors/surang](https://www.flaticon.com/authors/surang).
