package com.devankuleindiren.rnnarithmetic;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Devan Kuleindiren on 28/08/15.
 */
public class Main {

    public static void main (String[] args) {

        String command = "";
        Scanner scanner = new Scanner(System.in);
        while (!command.equals("q")) {

            System.out.print("RNN: ");

            command = scanner.nextLine();

            if (command.equals("help")) {
                System.out.println("The following commands are available:                   ");
                System.out.println("                                                        ");
                System.out.println("    test <input 1> <input 2>                            ");
                System.out.println("        This gets the RNN to perform binary addition on ");
                System.out.println("        input 1 and input 2 using its current weights.  ");
                System.out.println("                                                        ");
                System.out.println("    train                                               ");
                System.out.println("        This initiates the training of the RNN on binary");
                System.out.println("        addition.                                       ");
                System.out.println("                                                        ");
                System.out.println("    help                                                ");
                System.out.println("        This displays help.                             ");
                System.out.println("                                                        ");
                System.out.println("    q                                                   ");
                System.out.println("        This quits the application.                     ");
                System.out.println("                                                        ");
            } else if (command.equals("train")) {
                Matrix inputBatch[][] = InputGenerator.generateInput(100);
                RNN rnn = RNN.getInstance(2, 3, 1);

                try {
                    rnn.train(inputBatch, 0.1, 1000);
                } catch (MatrixDimensionMismatchException e) {
                    System.out.println("Failed to train the RNN:");
                    System.out.println();
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            } else if (command.length() > 3 && command.substring(0, 4).equals("test")) {
                String[] arguments = command.split(" ");

                if (arguments.length > 2) {
                    // CHECK STRINGS ARE BINARY STRINGS
                    String regex = "[01]*";
                    boolean isValid = true;
                    if (!Pattern.matches(regex, arguments[1])) {
                        System.out.println(arguments[1] + " is not a valid binary number.");
                        isValid = false;
                    }
                    if (!Pattern.matches(regex, arguments[2])) {
                        System.out.println(arguments[2] + " is not a valid binary number.");
                        isValid = false;
                    }

                    if (isValid) {
                        // PAD STRINGS TO EQUAL LENGTH
                        if (arguments[1].length() < arguments[2].length()) {
                            for (int len = arguments[1].length(); len < arguments[2].length(); len++) {
                                arguments[1] = "0" + arguments[1];
                            }
                        } else if (arguments[2].length() < arguments[1].length()) {
                            for (int len = arguments[2].length(); len < arguments[1].length(); len++) {
                                arguments[2] = "0" + arguments[2];
                            }
                        }
                        // GET RNN TO PERFORM ADDITION
                        try {
                            Testing.test(arguments[1], arguments[2]);
                        } catch (MatrixDimensionMismatchException e) {
                            System.out.println("Test failed:");
                            System.out.println();
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Insufficient arguments. Enter 'help' for more info.");
                }
            } else if (!command.equals("q")) {
                System.out.println("'" + command + "' is not a valid command. Enter 'help' for a list of available commands.");
            }
        }
    }
}
















