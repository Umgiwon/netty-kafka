package com.one.collector;

import com.one.collector.handler.TcpDataHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 기반 TCP 서버를 실행하는 클래스
 */
@Slf4j
public class NettyServer {

    private final KafkaSender kafkaSender = new KafkaSender();

    /**
     * Netty 서버 실행
     *
     * @param port 포트
     * @throws InterruptedException 중단 예외
     */
    public void start(int port) throws InterruptedException {

        // BossGroup: 클라이언트 연결 수락 담당 (1개 스레드)
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        // WorkerGroup: 실제 데이터 처리 담당 (N개 스레드)
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            // 서버 설정 초기화
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // NIO 기반 소켓 서버
                    .childHandler(new ChannelInitializer<>() {

                        @Override
                        protected void initChannel(Channel channel) {
                            // 메시지 수신 시 사용할 핸들러 추가
                            channel.pipeline().addLast(new TcpDataHandler(kafkaSender));
                        }
                    });

            // 서버 바인딩 및 실행
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("Netty 서버 시작 (포트 : {})", port);

            // 서버가 종료될 때까지 대기
            future.channel().closeFuture().sync();
        } finally {
            // 자원 정리
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            kafkaSender.close();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 9999번 포트로 Netty 서버 실행
        new NettyServer().start(9999);
    }
}
