package com.one;

import com.one.collector.netty.NettyServer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        new NettyServer().start(9999);
    }
}