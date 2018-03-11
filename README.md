# DFA Minimization - Direct DFA construction

Enter a regexp, then get your minimized DFA & direct DFA creation.

## Getting Started

Fork the project or download it. Then just add the project
to your prefered IDE or compile it from Console.

## Video Demonstration & Results Documentation

IMPORTANT: for the documentation please see:

```
\doc\LAB_4_RESULTS_BRO15105.pdf
```

You can see a demonstration at:

```
https://youtu.be/55QhKtiMM0k
```

Note: video is in spanish.

### Prerequisites

Java SE8. SDK 8.

### Installing

Load the project to an IDE or copy the contents to a folder. 
Then compile the project from console or with IDE.

Note: Automated support for Jetbrains' IntelliJ (just load the project).

Main class is:

```
src\Run\Runnable.java
```

## Running the program

Enter a regular expression in console. Valid operators are:

```
'|', '*', '+', '?', '^', '.'
```

And for the symbols you may use any other Ascii or Unicode symbol.
Note: you MUST use the epsilon symbol to represent an empty
word:

```
ε
```

Example of a valid regular expression may be:

```
ab*ab*
```

Note that you can use abbreviations and yuxtaposed concatenation, ie. no
need for placing '.' in your expression.

For a list of regular expressions examples you can see:

```
root_directory\REGEXPS.txt
```

## Deployment

After running the program, a text file with the contents of the minimized
DFA will be exported as:

```
root_directory\MIN_DFA.txt
```

The direct generation will be exported to:

```
root_directory\DIRECT_DFA.txt
```
## Authors

* **Gabriel Brolo** 

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* Big shoutout to Guillaume Ménard who's code helped me finish the infix to postfix converter:
https://gist.github.com/gmenard/6161825
* To Alfred Aho, for making such explicit algorithms in his book 'Compilators'.
* To Barry Brown, for explaining the algorithm here: https://www.youtube.com/watch?v=taClnxU-nao
* However, the previous video NEEDED to be supplemented with: 
http://web.cecs.pdx.edu/~harry/compilers/slides/LexicalPart3.pdf
* Minimization algorithm: http://www.cs.odu.edu/~toida/nerzic/390teched/regular/fa/min-fa.html
