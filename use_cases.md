---
layout: default
---

# Some use cases of BenzAI

We describe several use cases that we use to produce some figures of our article "BenzAI: A Program to Design Benzenoids with Defined Properties Using Constraint Programming" published in JCIM.


## A basic generation of the benzenoids having six hexagons and a mirror symmetry (Figure 1)

The first use case deals with the generation of the first four benzenoids having six hexagons and a mirror symmetry.

First, we go to the menu Input and choose the entry Generator.
![Menu](gallery/use_case_1_1.png)

We impose as the first criterion that the number of hexagons is equal to six. Then we define a second criterion related to the symmetry and a third one in order to limit the number of solutions to four.
![Criteria](gallery/use_case_1_2.png)

Finally, we run the generation and obtain the first four desired benzenoids (among eleven).
![Result](gallery/use_case_1_3.png)


## A basic generation of the benzenoids having seven hexagons and contains two patterns (Figure 2)

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


