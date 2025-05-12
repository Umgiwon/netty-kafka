package com.one;

import com.one.collector.netty.NettyServer;

import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        new NettyServer().start(9999);
    }
}