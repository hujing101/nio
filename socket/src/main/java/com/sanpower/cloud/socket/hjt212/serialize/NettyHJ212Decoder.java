package com.sanpower.cloud.socket.hjt212.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sanpower.cloud.socket.hjt212.model.DataPackage;
import com.sanpower.cloud.socket.hjt212.model.DataSegment;
import com.sanpower.cloud.socket.hjt212.server.NettyServer;
import com.sanpower.cloud.socket.hjt212.utils.StringUtils;

public class NettyHJ212Decoder extends MessageToMessageDecoder<ByteBuf> {
	private static final Log logger = LogFactory.getLog(NettyServer.class);

	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (!in.isReadable()) {
			return;
		}
		if (in.readableBytes() < 12) {
			return;
		}
		ByteBuf byteBuf = in;
		if (byteBuf.readableBytes() > 0) {
			DataPackage pkg = new DataPackage();
			ByteBuf packageHeader = byteBuf.readBytes(2);
			pkg.setPackageHeader(new String(packageHeader.array()));
			if (!"##".equals(pkg.getPackageHeader())) {
				int readableBytes = byteBuf.readableBytes();
				String msg = new String(byteBuf.readBytes(readableBytes)
						.array());
				logger.error("decode packets fail!Data not begin with \"##\" data={"
						+ pkg.getPackageHeader() + msg + "}");
				return;
			}
			ByteBuf dataLengthBuf = byteBuf.readBytes(4);
			int dataLength = Integer.parseInt(
					new String(dataLengthBuf.array()), 10);
			pkg.setDataLength(dataLength);
			ByteBuf dataSegment = byteBuf.readBytes(dataLength);

			pkg.setData(createDataSegment(dataSegment));

			ByteBuf crc = byteBuf.readBytes(4);
			pkg.setCRC(new String(crc.array()));
			out.add(pkg);
		}
	}

	private DataSegment createDataSegment(ByteBuf byteBuf) {
		String dataSegmentStr = new String(byteBuf.array());
		DataSegment dataSegment = null;
		if (!StringUtils.isEmpty(dataSegmentStr)) {
			// ST=32;CN=2011;PW=123456;MN=37868253301032;CP=&&DataTime=20140617144524;011-Rtd=293,011-Flag=N;B01-Rtd=0,B01-Flag=N;001-Rtd=7.25,001-Flag=N&&
			dataSegment = new DataSegment();
			String cpStr = dataSegmentStr.substring(
					dataSegmentStr.indexOf('&') + 2,
					dataSegmentStr.length() - 2);
			dataSegmentStr = dataSegmentStr.substring(0,
					dataSegmentStr.indexOf('&') - 3);

			String[] cpArrays = cpStr.split("\\;");
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < cpArrays.length; i++) {
				String[] rows = cpArrays[i].split("\\,");
				for (String keyValueStr : rows) {
					String[] keyValue = keyValueStr.split("\\=");
					if(keyValue.length>1){
						map.put(keyValue[0], StringUtils.getValue(keyValue[1]));
					}else{
						map.put(keyValue[0], "");
					}
					
				}
			}
			dataSegment.setCP(map);

			Map<String, String> datas = new HashMap<String, String>();
			String[] temp = dataSegmentStr.split("\\;");
			for (int i = 0; i < temp.length; i++) {
				String[] terms = temp[i].split("\\=");
				datas.put(terms[0], terms[1]);
			}

			// String[] fields = StringUtils.getFiledName(DataSegment.class);
			dataSegment.setCN(datas.get("CN"));
			if (datas.containsKey("Flag")) {
				dataSegment.setFlag(datas.get("Flag"));
			}
			dataSegment.setMN(datas.get("MN"));
			dataSegment.setPNO(datas.get("PNO"));
			dataSegment.setPNUM(datas.get("PNUM"));
			dataSegment.setPW(datas.get("PW"));
			dataSegment.setQN(datas.get("QN"));
			dataSegment.setST(datas.get("ST"));
		}
		return dataSegment;
	}

}
