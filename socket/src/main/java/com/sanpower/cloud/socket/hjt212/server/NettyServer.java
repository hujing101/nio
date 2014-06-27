package com.sanpower.cloud.socket.hjt212.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sanpower.cloud.socket.hjt212.GlobalPropertyMgmt;
import com.sanpower.cloud.socket.hjt212.serialize.NettyHJ212Decoder;
import com.sanpower.cloud.socket.hjt212.thread.NamedThreadFactory;
import com.sanpower.cloud.socket.hjt212.utils.Constants;

public class NettyServer {
	private static final Log LOGGER = LogFactory.getLog(NettyServer.class);
	private ServerBootstrap bootstrap = null;
	private Properties properties = null;

	public NettyServer() {
		properties = GlobalPropertyMgmt.getInstance().getProperties();
		int serverBossThreadSize = 10;
		try {
			serverBossThreadSize = Integer.parseInt(properties
					.getProperty(Constants.SERVER_BOSS_THREAD_SIZE));
		} catch (NumberFormatException e) {

		}

		int serverWorkerThreadSize = 10;
		try {
			serverWorkerThreadSize = Integer.parseInt(properties
					.getProperty(Constants.SERVER_WORKER_THREAD_SIZE));
		} catch (NumberFormatException e) {

		}

		ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
		ThreadFactory serverWorkerTF = new NamedThreadFactory(
				"NETTYSERVER-WORKER-");
		EventLoopGroup bossGroup = new NioEventLoopGroup(serverBossThreadSize,
				serverBossTF);
		EventLoopGroup workerGroup = new NioEventLoopGroup(
				serverWorkerThreadSize, serverWorkerTF);
		bootstrap = new ServerBootstrap().group(bossGroup, workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.ALLOCATOR,
				new PooledByteBufAllocator(false));// heap buf 's better
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.SO_RCVBUF, 1048576);
		bootstrap.childOption(ChannelOption.SO_SNDBUF, 1048576);
	}

	public void start(String ip,int listenPort) throws InterruptedException {

		String maxPoolSizeStr = properties
				.getProperty(Constants.MAX_BUSSINESS_POOL_SIZE);
		int maxPoolSize = 100;
		try {
			maxPoolSize = Integer.parseInt(maxPoolSizeStr);
		} catch (NumberFormatException e) {

		}
		
		int blockingQueueSize = 50;
		try {
			String blockingQueueSizeStr = properties
					.getProperty(Constants.SERVER_BLOCKING_QUEUE_SIZE);
			blockingQueueSize = Integer.parseInt(blockingQueueSizeStr);
		} catch (NumberFormatException e) {

		}
		
		ThreadFactory tf = new NamedThreadFactory("BUSINESSTHREADPOOL");
		BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(
				blockingQueueSize);
		final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20,
				maxPoolSize, 300, TimeUnit.SECONDS,
				// new SynchronousQueue<Runnable>(), tf);
				blockingQueue, tf);
		threadPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r,
					ThreadPoolExecutor executor) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
				executor.execute(r);
			}
		});

		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				// data package Incomplete problem solving
				pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
						Delimiters.lineDelimiter()));
				// system Handler
				pipeline.addLast("decoder", new NettyHJ212Decoder());
				//pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());
				
				int eventExecutorSize = 32;
				try {
					String eventExecutorSizeStr = properties
							.getProperty(Constants.SERVER_EVENT_EXECUTOR_GROUP);
					eventExecutorSize = Integer.parseInt(eventExecutorSizeStr);
				} catch (NumberFormatException e) {

				}
				
				EventExecutorGroup e1 = new DefaultEventExecutorGroup(eventExecutorSize);
				// my Business Handler
				pipeline.addLast(e1, new NettyServerHandler(threadPool));
			}
		});

		ChannelFuture f = bootstrap.bind(ip, listenPort).sync();
		//ChannelFuture f = bootstrap.bind(listenPort).sync();
		f.channel().closeFuture().sync();
		LOGGER.info("Server started,listen at: " + listenPort);
	}
}