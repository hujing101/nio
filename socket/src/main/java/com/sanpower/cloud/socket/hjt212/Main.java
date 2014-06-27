package com.sanpower.cloud.socket.hjt212;

import com.sanpower.cloud.socket.hjt212.server.NettyServer;

public class Main {
	public static void main(String[] args) {
		// java -jar ${ip} ${port}
		String ip = "127.0.0.1";
		int port = 7878;
		if (args != null && args.length > 0) {
			ip = args[0];
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
			}
		}

		try {
			new NettyServer().start(ip, port);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
