package cz.harvester.api;

import cz.harvester.api.ApiBase.ApiObject;
import cz.harvester.api.ApiDataIO.ApiDataInput;
import cz.harvester.api.ApiDataIO.ApiDataOutput;

public class ApiCommon {
	public static class ApiLocPoint extends ApiObject {
		public static final ApiLocPoint INVALID = new ApiLocPoint(0, 0);
				
		private final double latitude;
		private final double longitude;
		
		public ApiLocPoint(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		public ApiLocPoint(ApiDataInput d) {
			this.latitude = d.readDouble();
			this.longitude = d.readDouble();
		}
		
		@Override
		public void save(ApiDataOutput d, int flags) {
			d.write(latitude);
			d.write(longitude);
		}	
		
		public double getLatitude() {
			return latitude;
		}
		
		public double getLongitude() {
			return longitude;
		}		
		
		@Override 
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof ApiLocPoint)) {
				return false;     
			}
			
			ApiLocPoint lhs = (ApiLocPoint)o;
			return latitude == lhs.latitude &&
				longitude == lhs.longitude;
		}
		
		@Override 
		public int hashCode() { 
			int result = 17; 
			long doubleFieldBits = Double.doubleToLongBits(latitude);
			result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));
			doubleFieldBits = Double.doubleToLongBits(longitude);
			result = 31 * result + (int) (doubleFieldBits ^ (doubleFieldBits >>> 32));
			return result;
		}
	}
}
