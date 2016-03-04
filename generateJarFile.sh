#!/bin/bash

cd src
cp ../out/production/RNN\ Binary\ Arithmetic/com/devankuleindiren/rnnarithmetic/* com/devankuleindiren/rnnarithmetic
jar -cfe RNN-Arithmetic.jar com.devankuleindiren.rnnarithmetic.Main com/devankuleindiren/rnnarithmetic/*
rm com/devankuleindiren/rnnarithmetic/*.class
mv RNN-Arithmetic.jar ../RNN-Arithmetic.jar
