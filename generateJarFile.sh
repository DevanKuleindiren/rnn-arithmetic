#!/bin/bash

cd src
cp ../out/production/RNN\ Binary\ Arithmetic/com/devankuleindiren/rnnarithmetic/* com/devankuleindiren/rnnarithmetic
jar -cfe RNN-Arithmetic-Test.jar com.devankuleindiren.rnnarithmetic.Main com/devankuleindiren/rnnarithmetic/*
mv RNN-Arithmetic-Test.jar ../RNN-Arithmetic.jar
