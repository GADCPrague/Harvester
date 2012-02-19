package cz.harvester.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.ImmutableList;

import cz.harvester.api.ApiDataIO.ApiDataOutput;
import cz.harvester.api.ApiDataIO.ApiParcelWrp;

public class ApiBase {
	public static abstract class ApiObject {
		public final void save(ApiDataOutput d) {
			save(d, 0);
		}
		
		public abstract void save(ApiDataOutput d, int flags);
		
		protected static void writeToDataOutput(ApiDataOutput d, int flags, ImmutableList<? extends ApiObject> value) {
			d.write(value.size());
			for (ApiObject o : value) {
				o.save(d, flags);
			}
		}
	}
	
	public static abstract class ApiParcelable extends ApiObject implements Parcelable {
		public int describeContents() {
			return 0;
		}

		public final void writeToParcel(Parcel dest, int flags) {
			ApiParcelWrp p = new ApiParcelWrp(dest, true);
			save(p, flags);
		}
	}
}
