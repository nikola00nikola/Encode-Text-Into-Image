package converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Converter {

	private byte[] bytes;
	private int maxLen;
	
	public int setFile(byte[] bytes) {
		this.bytes=bytes;
		int compression = toInt(30);
		if (compression != 0) {
			return -1;
		}
		int bpp = toShort(28);
		if(bpp != 24) {
			return -2;
		}
		int start=toInt(10);
		this.maxLen=(bytes.length-start-32)/16;
		return (bytes.length-start-32)/16;
	}
	
	public void writeMessage(char[] m, String path) {
		try {
			int compression = toInt(30);
			System.out.println("cmp = "+compression);
			int start=toInt(10);
			System.out.println("Max duzina: "+(bytes.length-start-32)/16);
			System.out.println("Duzina" + m.length);
			long mask=(long)1<<31;
			for(int i=0; i<32; i++) {
				if ((m.length & mask) != 0)
					bytes[start+i]|=1;
				else
					bytes[start+i]&=-2;
				mask=mask>>1;
			}
			System.out.println();
			start+=32;
			for(int i=0; i<m.length;i++) {
				mask=1<<15;
				for(int j=0; j<16; j++) {
					if ((m[i] & mask)!=0)
						bytes[start+i*16+j]|=1;
					else
						bytes[start+i*16+j]&=-2;
					mask>>=1;
				}
			}
			File file=new File(path);
			OutputStream os= new FileOutputStream(file);
			os.write(bytes);
			os.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String readMessage() {
		int start=toInt(10);
		int len=0;
		long mask=(long)1<<31;
		for(int i=0; i<32; i++) {
			if((bytes[start+i] & 1) != 0)
				len|= mask;
			mask>>=1;
		}
		if(len<0)
			len=-len;
		if(len > maxLen)
			len = maxLen;
		start+=32;
		char[] s = new char[len];
		for(int i=0; i<len;i++) {
			mask=1<<15;
			char c=0;
			for(int j=0; j<16; j++) {
				if((bytes[start+i*16+j] & 1) != 0)
					c|= mask;
				mask>>=1;
			}
			s[i]=c;
		}
		
		
		
		return new String(s);
	}
	
	private int toInt(int start) {
		return ((bytes[start+3] & 0xFF) << 24) | ((bytes[start+2] & 0xFF) << 16) | ((bytes[start+1] & 0xFF) << 8 ) | ((bytes[start] & 0xFF) << 0 );
	}
	
	private short toShort(int start) {
		return (short)((bytes[start+1] & 0xFF << 8) | (bytes[start] & 0xFF) << 0);
	}
	
	public void clearFile() {
		bytes=null;
		maxLen=0;
	}
}
