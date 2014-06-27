package com.sanpower.cloud.socket.hjt212.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanpower.cloud.socket.hjt212.model.DataPackage;
import com.sanpower.cloud.socket.hjt212.thread.HandlerRunable;

public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
	private ExecutorService threadpool;

	public NettyServerHandler(ExecutorService threadpool) {
		this.threadpool = threadpool;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof DataPackage){
			try{
				threadpool.execute(new HandlerRunable(ctx, ((DataPackage)msg).getData()));
			}catch(RejectedExecutionException e){
				LOGGER.error("server threadpool full,threadpool maxsize is:"
						+ ((ThreadPoolExecutor) threadpool).getMaximumPoolSize());
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//NON OP
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(!(cause.getCause() instanceof IOException)){
			// only log
			LOGGER.error("catch some exception not IOException",cause);
		}
		//ctx.close();
	}
}