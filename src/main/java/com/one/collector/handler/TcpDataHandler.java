package com.one.collector.handler;

import com.one.collector.kafka.KafkaSender;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpDataHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final KafkaSender kafkaSender;

    // KafkaSender 주입
    public TcpDataHandler(KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // ByteBuf -> String 변환
        String data = msg.toString(CharsetUtil.UTF_8);
        log.info("수신된 메시지: {}", data);

        // Kafka로 메시지 전송
        kafkaSender.send(data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception : ", cause);
        log.error("Exception : {}", cause.getMessage());

        cause.printStackTrace();

        ctx.close(); // 연결 종료
    }
}
