package cz.harvester.api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Parcel;

public class ApiDataIO {
	public static interface ApiDataOutput {
		static final int LAST_DATA_VERSION = 2;
		
		boolean write(boolean value);
		void write(int value);
		void write(long value);
		void write(double value);
		void write(byte[] value);
		void write(String value);
		void write(Bitmap value, int flags);
	}
	
	public static interface ApiDataInput {
		int getDataVersion();
		boolean readBoolean();
		int readInt();
		long readLong();
		double readDouble();
		byte[] readBytes();
		String readString();
		Bitmap readBitmap();
	}
	
	public static class ApiDataOutputStreamWrp implements ApiDataOutput {
		private final int dataVersion;
		private final DataOutputStream dataOutputStream;
		
		private final Map<String, Integer> stringMap = new HashMap<String, Integer>();
		private final Map<Bitmap, Integer> bmpMap = new HashMap<Bitmap, Integer>();
		
		public ApiDataOutputStreamWrp(DataOutputStream dataOutputStream) {
			this(dataOutputStream, LAST_DATA_VERSION);
		}
		
		public ApiDataOutputStreamWrp(DataOutputStream dataOutputStream, int customLastDataVersion) {
			this.dataVersion = customLastDataVersion;
			this.dataOutputStream = dataOutputStream;
			write(dataVersion);
		}
		
		public DataOutputStream getDataOutputStream() {
			return dataOutputStream;
		}
		
		public void close() {
			try {
				dataOutputStream.close();
				stringMap.clear();
				bmpMap.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public boolean write(boolean value) {
			try {
				dataOutputStream.writeBoolean(value);
				return value;
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
		
		public void write(int value) {
			try {
				dataOutputStream.writeInt(value);
			} catch (IOException e) {
				throw new RuntimeException();
			}			
		}

		public void write(long value) {
			try {
				dataOutputStream.writeLong(value);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public void write(double value) {
			try {
				dataOutputStream.writeDouble(value);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public void write(byte[] value) {
			try {
				dataOutputStream.writeInt(value.length);
				dataOutputStream.write(value);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public void write(String value) {
			try {
				Integer index = stringMap.get(value);
				if (index == null) {
					dataOutputStream.writeBoolean(true);
					dataOutputStream.writeUTF(value);
					stringMap.put(value, stringMap.size());
				}
				else {
					dataOutputStream.writeBoolean(false);
					dataOutputStream.writeInt(index);
				}
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public void write(Bitmap value, int flags) {
			try {
				write(value != null);
				if (value != null) {
					Integer index = bmpMap.get(value);
					if (index == null) {
						dataOutputStream.writeBoolean(true);
						if (value.compress(CompressFormat.PNG, 100, dataOutputStream) == false)
							throw new RuntimeException();
						bmpMap.put(value, bmpMap.size());
					}
					else {
						dataOutputStream.writeBoolean(false);
						dataOutputStream.writeInt(index);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}		
	}
	
	public static class ApiDataInputStreamWrp implements ApiDataInput {
		private final DataInputStream dataInputStream;
		private final int dataVersion;
		
		private final List<String> stringList = new ArrayList<String>();
		private final List<Bitmap> bmpList = new ArrayList<Bitmap>();
		
		public ApiDataInputStreamWrp(DataInputStream dataInputStream) {
			this.dataInputStream = dataInputStream;
			this.dataVersion = readInt();				
		}
		
		public int getDataVersion() {
			return dataVersion;
		}
		
		public void close() {
			try {
				dataInputStream.close();
				stringList.clear();
				bmpList.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public boolean readBoolean() {
			try {
				return dataInputStream.readBoolean();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
	
		public int readInt() {
			try {
				return dataInputStream.readInt();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public long readLong() {
			try {
				return dataInputStream.readLong();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public double readDouble() {
			try {
				return dataInputStream.readDouble();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public byte[] readBytes() {
			try {
				byte[] ret;
				ret = new byte[dataInputStream.readInt()];
				dataInputStream.readFully(ret);
				return ret;
			} catch (IOException e) {
				throw new RuntimeException();
			}			
		}

		public String readString() {
			try {
				if (dataVersion < 2 || dataInputStream.readBoolean()) {
					String ret = dataInputStream.readUTF();
					stringList.add(ret);
					return ret;
				}
				else {
					int index = dataInputStream.readInt();
					return stringList.get(index);
				}
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}

		public Bitmap readBitmap() {
			try {
				if (readBoolean()) {
					if (dataVersion < 2 || dataInputStream.readBoolean()) {
						Bitmap ret = BitmapFactory.decodeStream(dataInputStream);
						if (ret == null)
							throw new RuntimeException();
						bmpList.add(ret);
						return ret;
					}
					else {
						int index = dataInputStream.readInt();
						return bmpList.get(index);
					}
				}
				else
					return null;
			}
			catch (IOException e) {
				throw new RuntimeException();
			}
		}
	}
	
	public static class ApiParcelWrp implements ApiDataOutput, ApiDataInput {
		private final Parcel parcel;
		private int dataVersion;
		
		public ApiParcelWrp(Parcel parcel, boolean forWriting) {
			this.parcel = parcel;
			if (forWriting) {
				this.dataVersion = LAST_DATA_VERSION;
				write(dataVersion);
			}
			else {
				this.dataVersion = readInt();
			}
		}
				
		public Parcel getParcel() {
			return parcel;
		}
		
		public int getDataVersion() {
			return dataVersion;
		}
		
		public boolean readBoolean() {
			return parcel.readByte() != 0;
		}

		public int readInt() {
			return parcel.readInt();
		}

		public long readLong() {
			return parcel.readLong();
		}

		public double readDouble() {
			return parcel.readDouble();
		}

		public byte[] readBytes() {
			return parcel.createByteArray();
		}

		public String readString() {
			return parcel.readString();
		}
		
		public Bitmap readBitmap() {
			if (readBoolean())
				return Bitmap.CREATOR.createFromParcel(parcel);
			else
				return null;
		}

		public boolean write(boolean value) {
			parcel.writeByte(value ? (byte)1 : (byte)0);
			return value;
		}
		
		public void write(int value) {
			parcel.writeInt(value);
		}

		public void write(long value) {
			parcel.writeLong(value);
		}

		public void write(double value) {
			parcel.writeDouble(value);
		}

		public void write(byte[] value) {
			parcel.writeByteArray(value);
		}

		public void write(String value) {
			parcel.writeString(value);
		}
		
		public void write(Bitmap value, int flags) {
			write(value != null);
			if (value != null) {
				value.writeToParcel(parcel, flags);
			}
		}
	}
}
