package com.craigdearden.simulation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Simpletron {

    /**
     * These define the instruction codes for the processor to execute.
     */
    final int READ = 10;
    final int WRITE = 11;
    final int LOAD = 20;
    final int STORE = 21;
    final int ADD = 30;
    final int SUBTRACT = 31;
    final int DIVIDE = 32;
    final int MULTIPLY = 33;
    final int BRANCH = 40;
    final int BRANCHNEG = 41;
    final int BRANCHZERO = 42;
    final int HALT = 43;

    /**
     * Defines the acceptable instruction values when writing or loading a program 
     * for Simpletron to execute.
     */
    private static final HashSet<Integer> instructionValues = new HashSet<>(
            Arrays.asList(10, 11,
                    20, 21, 30, 31, 32, 32, 40, 41, 42, 43, -999, -888));

    /**
     * Simpletron's memory which holds instructions and values.
     */
    int[] memory = new int[100];

    /**
     * Starts Simpletron
     */
    public void powerOn() {
        startUp();
        loadProgram();
        executeProgram();
    }

    /**
     * Displays start up welcome and instructions.
     */
    public void startUp() {
        System.out.println("*** Welcome to Simpletron! ***\n" +
                "*** Please enter your program one instruction ***\n" +
                "*** (or data word) at a time. I will display ***\n" +
                "*** the location number and a question mark (?). ***\n" +
                "*** You then type the word for that location. ***\n" +
                "*** Type -99999 to stop entering your program. ***");
    }

    /**
     * Prompts user to create a program by writing instructions to memory or the
     * user may load an internal program by entering -88888. Enter -99999 to
     * exit.
     */
    private void loadProgram() {
        int location = 0;

        System.out.print(location +
                " ? ");
        int input = getUserInput(instructionValues);

        int[] existingProgram = {1009, 1010, 2009, 3110, 4107, 1109, 4300, 1110,
            4300};
        if (input == -88888) {
            for (int i = 0; i < existingProgram.length; i++) {
                memory[i] = existingProgram[i];
            }
        } else {
            while (input != -99999) {
                memory[location] = input;
                location++;
                System.out.print(location + " ? ");
                input = getUserInput(instructionValues);
            }
        }

        System.out.println("*** Program loading completed ***\n" +
                "*** Program execution begins ***");
    }

    /**
     * Reads instruction from memory and writes it to the instruction register.
     * Identifies the memory block of the operand. Interprets, and executes the
     * instruction on the operand. Moves to the next instruction in memory.
     */
    private void executeProgram() {
        int accumulator = 0;
        int instructionCounter = 0;
        int operationCode = 0;
        int operand = 0;
        int instructionRegister = 0;

        instructionRegister = memory[instructionCounter];
        operationCode = instructionRegister / 100;
        operand = instructionRegister % 100;

        while (operationCode != HALT && instructionCounter <=
                98) {
            switch (operationCode) {
                case READ:
                    memory[operand] = getUserInput();
                    break;
                case WRITE:
                    System.out.println(memory[operand]);
                    break;
                case LOAD:
                    accumulator = memory[operand];
                    break;
                case STORE:
                    memory[operand] = accumulator;
                    break;
                case ADD:
                    accumulator += memory[operand];
                    break;
                case SUBTRACT:
                    accumulator -= memory[operand];
                    break;
                case DIVIDE:
                    accumulator /= memory[operand];
                    break;
                case MULTIPLY:
                    accumulator *= memory[operand];
                    break;
                case BRANCH:
                    instructionCounter = operand;
                    break;
                case BRANCHNEG:
                    if (accumulator < 0) {
                        instructionCounter = operand;
                    }
                    break;
                case BRANCHZERO:
                    if (accumulator == 0) {
                        instructionCounter = operand;
                    }
                    break;
            }

            if (operationCode != BRANCH && operationCode != BRANCHNEG &&
                    operationCode != BRANCHZERO) {
                instructionCounter++;
            }

            instructionRegister = memory[instructionCounter];
            operationCode = instructionRegister / 100;
            operand = instructionRegister % 100;
        }
        print(accumulator, instructionCounter, instructionRegister,
                operationCode, operand);
    }
    
    
    /**
     * The registers and memory are printed to the terminal.
     * 
     * @param accumulator
     * @param instructionCounter
     * @param instructionRegister
     * @param operationCode
     * @param operand 
     */
    private void print(int accumulator, int instructionCounter,
            int instructionRegister, int operationCode, int operand) {
        System.out.printf("%nREGISTERS:%n");
        System.out.printf("%-20s %6s %n", "accumulator", accumulator);
        System.out.printf("%-20s %6s %n", "instructionCounter",
                instructionCounter);
        System.out.printf("%-20s %6s %n", "instructionRegister",
                instructionRegister);
        System.out.printf("%-20s %6s %n", "operationCode", operationCode);
        System.out.printf("%-20s %6s %n", "operand", operand);

        System.out.printf("%nMEMORY:%n  ");

        for (int i = 0; i < 10; i++) {
            System.out.printf("%6d", i);
        }

        for (int i = 0; i < 10; i++) {
            System.out.printf("%n%2d", i * 10);

            for (int j = 0; j < 10; j++) {
                System.out.printf("%6s", "+" + memory[i * 10 + j]);
            }
        }

        System.out.println();
    }

    /**
     * Get any integer value from -9999 to +9999. If input is not between these
     * values re-prompt user.
     *
     * @return user input
     */
    private int getUserInput() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter a value;");
        // Still need to handle InputMisMatchException.
        int input = sc.nextInt();
        while (input < -9999 || input > +9999) {
            System.out.println("Invalid value. (-9999 < value < +9999)" +
                    "%nPlease enter a value:");
            input = sc.nextInt();
        }
        return input;
    }

    /**
     * Get any integer value from that begins with a valid instruction code or
     * the escape sequence -99999. If input is not now of these values re-prompt
     * user.
     *
     * @return user input
     */
    private int getUserInput(HashSet<Integer> instructionValues) {
        Scanner sc = new Scanner(System.in);

        // Need to handle InputMisMatchException.
        int input = sc.nextInt();
        while (!instructionValues.contains(input / 100)) {
            System.out.println("Invalid value. Instruction must start with " +
                    instructionValues.toString() +
                    "%nPlease enter a value:");
            input = sc.nextInt();
        }
        return input;
    }
}
