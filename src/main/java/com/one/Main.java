package com.one;

import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello and welcome!");

        IntStream.rangeClosed(1, 5)
                .forEach(i -> System.out.println("i = " + i));
    }
}