---
layout: default
---

# Some use cases of BenzAI

We describe, step by step, several use cases that we use to produce some figures of our article "BenzAI: A Program to Design Benzenoids with Defined Properties Using Constraint Programming" published in JCIM.


## A basic generation of the benzenoids having six hexagons and a mirror symmetry (Figure 1)

The first use case deals with the generation of the first four benzenoids having six hexagons and a mirror symmetry.

First, we go to the menu Input and choose the entry Generator.
![Menu](gallery/use_case_1_1.png)

We impose as the first criterion that the number of hexagons is equal to six. Then we define a second criterion related to the symmetry and a third one in order to limit the number of solutions to four.
![Criteria](gallery/use_case_1_2.png)

Finally, we run the generation and obtain the first four desired benzenoids (among eleven).
![Result](gallery/use_case_1_3.png)


## A generation of the benzenoids having seven hexagons and contains two patterns (Figure 2)

The second use case is related to generation of benzenoids with seven hexagons, made of all possible combinations of two given patterns. 

We first impose that the number of hexagons is equal to seven.
![Menu](gallery/use_case_2_1.png)

Then we add a criterion related to the first considered pattern.
![Menu](gallery/use_case_2_2.png)

We draw the first pattern and specify that it must be present in any solution.
For patterns, green hexagons indicate that the hexagons must be present in the solution, whereas red hexagons must not be in the solution and represent the vacuum.
![Menu](gallery/use_case_2_3.png)

We do the same for the second pattern.
![Menu](gallery/use_case_2_4.png)

We are now ready to run the generation
![Menu](gallery/use_case_2_5.png)

We run the generation and obtain 213 desired benzenoids from which five have been selected to form Figure 2.
![Menu](gallery/use_case_2_6.png)


## A generation based on the number of carbon atoms (Figure 4)

In this third use case, we are interested in generating all the benzenoid structures having 32 carbon atoms with a ξ value of 1.

We first impose that the number of carbon atoms is equal to 32.
![Menu](gallery/use_case_3_1.png)

We add the criterion about the irregularity.
![Menu](gallery/use_case_3_2.png)

We run the generation and obtain a single desired benzenoid.
![Menu](gallery/use_case_3_3.png)

Another possibility is to first generate all benzenoids with 32 carbon atoms.
![Menu](gallery/use_case_3_4.png)

We are now ready to run the generation and obtain 330 benzenoid structures.
![Menu](gallery/use_case_3_5.png)

Now, we sort the benzenoids by decreasing ξ value and the desired benzenoid is the first one.
![Menu](gallery/use_case_3_6.png)

Here is the result of the sorting:
![Menu](gallery/use_case_3_7.png)

The first solution is the best one since we only generate the desired structured (remember that the number of benzenoids may become huge).


## Vibrational spectra (Figure 5)

In this fourth use case, we are interested in the vibrational spectra for the benzenoids with four hexagons. First, we generate a collection containing all the benzenoids with four hexagons.
![Menu](gallery/use_case_4_1.png)

Here is the result:
![Menu](gallery/use_case_4_2.png)

We select all the benzenoids.
![Menu](gallery/use_case_4_3.png)

We go to the menu Computations and select the entry IR spectra.
![Menu](gallery/use_case_4_4.png)

Another possibility is to use the right click and select the entry IR spectra.
![Menu](gallery/use_case_4_5.png)

In both cases, we obtain the desired spectra by downloading the required data from our database.
Three spectra are obtained as these benzenoids can have either 16, 17, or 18 carbon atoms. 
The spectrum for the benzenoids having 16 carbon atoms:
![Menu](gallery/use_case_4_6.png)

The spectrum for the benzenoids having 17 carbon atoms:
![Menu](gallery/use_case_4_7.png)

The spectrum for the benzenoids having 18 carbon atoms:
![Menu](gallery/use_case_4_8.png)
