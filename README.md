RNN Arithmetic
================

##Description
This is a multilayer Recurrent Neural Network (RNN) that can learn how to perform basic arithmetic (binary addition so far).

In other words, you can feed it two binary strings, X and Y, and a target string, T. In the case of binary addition, T = X+Y :

```
X: 1 0 1 0 1 1 1 1 1 0 1 0 1 1
Y: 0 1 0 0 1 1 1 0 0 1 1 1 1 0 
T: 1 1 1 1 1 1 1 0 0 0 1 0 0 1
                             ^ 
                             |
                        <== RNN works from the LSB to the MSB
```

At the start, the RNN knows absolutely nothing about binary addition. However, after seeing enough inputs & targets, it can figure out how to perform binary addition for itself!

This RNN uses back propagation through time (BPTT) for training, and has the following structure:

![Structure](http://www.devankuleindiren.com/Images/RNN-Arithmetic.png "RNN Structure")
