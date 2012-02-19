package cz.harvester.data;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import cz.harvester.api.ApiDataIO.ApiDataInputStreamWrp;
import cz.harvester.api.ApiDataIO.ApiDataOutputStreamWrp;
import cz.harvester.data.Cards.CardUnique;

import android.content.Context;
import android.util.Log;


public class Album {
	private static final String TAG = "harvester.Album";
	private static final int DATA_VERSION = 2;
		
	private static final Cache cache = new Cache();
	
	public static synchronized ImmutableList<CardUnique> readAllCards(Context context) {
		File file = context.getFilesDir();
		File[] list = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".card");
			}
		});
		
		ImmutableList.Builder<CardUnique> ret = new ImmutableList.Builder<CardUnique>();
		for (File f : list) {
			int uniqueId = Integer.valueOf(f.getName().replace(".card", ""));
			CardUnique c = tryReadCard(context, uniqueId);
			if (c != null) {
				ret.add(c);
			}
		}
		
		return ret.build();
	}
	
	public static synchronized CardUnique readSingleCard(Context context, int uniqueId) {
		CardUnique ret = tryReadCard(context, uniqueId);
		if (ret == null)
			throw new RuntimeException("readSingleCard");
		else
			return ret;
	}
	
	public static synchronized void writeSingleCard(Context context, CardUnique card) {
		cache.add(card);
		
		String fileName = card.getUniqueId() + ".card";
		File file = context.getFileStreamPath(fileName);
		if (!file.exists()) {
			ApiDataOutputStreamWrp stream = null;
			try {
				stream = new ApiDataOutputStreamWrp(new DataOutputStream(
					context.openFileOutput(fileName, Context.MODE_PRIVATE)), DATA_VERSION);
				card.save(stream);
			} 
			catch (Exception e) {
				throw new RuntimeException(TAG + "|add");
			}
			finally {
				if (stream != null) {
					stream.close();
				}
			}
		}
	}
	
	public static synchronized void removeAllCards(Context context) {
		File file = context.getFilesDir();
		File[] list = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".card");
			}
		});
		
		for (File f : list) {
			f.delete();
		}
	}
	
	public static synchronized void removeCard(Context context, int uniqueId) {
		String fileName = uniqueId + ".card";
		File file = context.getFileStreamPath(fileName);
		if (file.exists()) {
			file.delete();
		}
	}
	
	private static CardUnique tryReadCard(Context context, int uniqueId) {
		CardUnique ret = cache.get(uniqueId);
		if (ret == null) {
			ApiDataInputStreamWrp stream = null;
			try {
				String fileName = uniqueId + ".card";
				stream = new ApiDataInputStreamWrp(new DataInputStream(
					new BufferedInputStream(context.openFileInput(fileName), 10000)));
				if (stream.getDataVersion() > DATA_VERSION) {
					stream.close();
					stream = null;
					context.deleteFile(fileName);
				}
				else
				{
					ret = new CardUnique(stream);
					cache.add(ret);
				}
			}
			catch (FileNotFoundException e) { }
			catch (Exception e) { 
				Log.e(TAG, "get", e);
			}
			finally {
				if (stream != null) {
					stream.close();
				}
			}
		}
		return ret;
	}
	
	
	private static class Cache {
		private final Map<Integer, WeakReference<CardUnique>> cache = new HashMap<Integer, WeakReference<CardUnique>>();
		private int counter = 0;
		
		public synchronized void add(CardUnique value) {
			if (!cache.containsKey(value.getUniqueId())) {
				cache.put(value.getUniqueId(), new WeakReference<CardUnique>(value));
				if (counter++ >= 10) {
					removeEmptyReferences();
					counter = 0;
				}
			}
		}
		
		public synchronized CardUnique get(int uniqueId) {
			WeakReference<CardUnique> ret = cache.get(uniqueId);
			if (ret != null)
				return ret.get();
			else
				return null;
		}
		
		private void removeEmptyReferences() {
			Iterator<Map.Entry<Integer, WeakReference<CardUnique>>> entries = cache.entrySet().iterator();
			while (entries.hasNext()) {
			    Map.Entry<Integer, WeakReference<CardUnique>> entry = entries.next();
			    if (entry.getValue().get() == null)
			    	entries.remove();
			}
		}
	}	
}
