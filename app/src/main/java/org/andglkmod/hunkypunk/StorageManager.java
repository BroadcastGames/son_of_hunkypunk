/*
	Copyright © 2009-2010 Rafał Rzepecki <divided.mind@gmail.com>

	This file is part of Hunky Punk.

    Hunky Punk is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Hunky Punk is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Hunky Punk.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.andglkmod.hunkypunk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.andglkmod.babel.Babel;
import org.andglkmod.glk.Utils;
import org.andglkmod.hunkypunk.HunkyPunk.Games;
import org.andglkmod.hunkypunk.events.BackgroundScanEvent;
import org.greenrobot.eventbus.EventBus;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

public class StorageManager {
	public static final int MESSAGE_CODE_DONE = 0;
	public static final int INSTALLED = 1;
	public static final int INSTALL_FAILED = 2;

	private static final String TAG = "hunkypunk.MediaScanner";
	private static final String[] PROJECTION = { Games._ID, Games.PATH };
	private static final String[] PROJECTION2 = {Games._ID, Games.PATH, Games.IFID, Games.TITLE};

	private static final String[] PROJECTION3 = {Games.IFID, Games.TITLE, Games.PATH};

	private static final int _ID = 0;
	private static final int PATH = 1;

	private final ContentResolver mContentResolver;
	private Handler mHandler;
	private DatabaseHelper mOpenHelper;
	private Context context;
	
	private StorageManager(Context context) {
		this.context = context;
		mContentResolver = context.getContentResolver();
		mOpenHelper = new DatabaseHelper(context);
	}
	
	private static StorageManager sInstance;
	
	public static StorageManager getInstance(Context context) {
		if (sInstance == null) sInstance = new StorageManager(context);
		
		assert(sInstance.mContentResolver == context.getContentResolver());
		return sInstance;
	}

	public void setHandler(Handler h) {
		mHandler = h;
	}

	public String gameInstalledFilePath(File f) {
		String ifid = null;
		String path = null;

		try {
			ifid = Babel.examine(f);
		}catch(Exception e){}
		
		if (ifid == null)
			return path;
		
		Uri uri = Uri.withAppendedPath(Games.CONTENT_URI, ifid);
		Cursor query = mContentResolver.query(uri, PROJECTION, null, null, null);		
		if (query != null || query.getCount() == 1)
			if (query.moveToNext())
				path = query.getString(PATH);			
		return path;
	}

	public void checkExisting() {
		Cursor c = mContentResolver.query(Games.CONTENT_URI, PROJECTION, Games.PATH + " IS NOT NULL", null, null);

		if (c == null)
		{
			Log.e(TAG, "cursor is null, database failure? URI: " + Games.CONTENT_URI);
			return;
		}

		while (c.moveToNext())
			if (!new File(c.getString(PATH)).exists()) {
				ContentValues cv = new ContentValues();
				cv.putNull(Games.PATH);
				mContentResolver.update(ContentUris.withAppendedId(Games.CONTENT_URI, c.getLong(_ID)), cv, null, null);
			}
		
		c.close();
	}

	public static AtomicBoolean scanRunning = new AtomicBoolean(false);

	public void startScanForGameFiles() {
		if (scanRunning.compareAndSet(false, true)) {
			Thread scanFilesThread = new Thread() {
				@Override
				public void run() {
					try {
						internalScanKnownDirectoryTrees();

						Message.obtain(mHandler, MESSAGE_CODE_DONE).sendToTarget();

						scanRunning.set(false);
						Log.i(TAG, "scanKnownDirectoryTrees scanRunning set false, finished.");
						EventBus.getDefault().post(new BackgroundScanEvent(BackgroundScanEvent.ECODE_GAME_FOLDER_SCAN_COMPLETED));
					} catch (Exception e0) {
						Log.e(TAG, "Exception in Scan thread run", e0);
					}
				}
			};
			scanFilesThread.setName("scanFilesThread");
			scanFilesThread.start();
		} else {
			Log.w(TAG, "scanKnownDirectoryTrees found scanRunning, skip.");
		}
	}

	/*
	Do not call directly, make sure on thread and that scanRunning AtomicBoolean is true.
	 */
	private void internalScanKnownDirectoryTrees() {
		if (EasyGlobalsA.storageManagerAssetStuffing0)
		{
			addKnownFileFromAndroidAssets("PublicDomainGames0/All_Things_Devours.z5", "ZCODE-3-050325-6D3F", "All Things Devours", "All_Things_Devours.z5");
		}

		scanDirectoryTreeRecursive(Paths.ifDirectory());

		// ToDo: proper path building in the SDK 24 style, ToDo: preference list of paths

		for (int i = 0; i < EasyGlobalsA.additionalStoryDirectories.length; i++) {
			String singlePath = EasyGlobalsA.additionalStoryDirectories[i];
			File singlePathFile = new File(singlePath);
			if (singlePathFile.exists())
			{
				scanDirectoryTreeRecursive(singlePathFile);
			}
		}
	}

	private void scanDirectoryTreeRecursive(File dir) {
		if (!dir.exists() || !dir.isDirectory())
			return;
		
		final File[] files = dir.listFiles();
		if (files == null)
			return;

		if (EasyGlobalsA.storageManagerScanDirectoryTreeLogA) {
			Log.d(TAG, "glk_window_get_sizescanDirectoryTree " + dir.toString() + " " + Thread.currentThread());
		}
		for (File f : files) {
			if (!f.isDirectory())
				try {

					String g = f.getName().toLowerCase();
					if (
						/* zcode: frotz, nitfol */
							g.matches(".*\\.z[1-9]$")
									|| g.matches(".*\\.dat$")
									|| g.matches(".*\\.zcode$")
									|| g.matches(".*\\.zblorb$")
									|| g.matches(".*\\.zlb$")

						/* tads */
									|| g.matches(".*\\.gam$")
									|| g.matches(".*\\.t2$")
									|| g.matches(".*\\.t3$")

						/* glulx */
									|| g.matches(".*\\.blorb$")
									|| g.matches(".*\\.gblorb$")
									|| g.matches(".*\\.blb$")
									|| g.matches(".*\\.glb$")
									|| g.matches(".*\\.ulx$")
							)
						checkFile(f);
				} catch (IOException e) {
					Log.w(TAG, "IO exception while checking " + f, e);
				}
			else {
				// Recursively call into the subdirectory
				scanDirectoryTreeRecursive(f);
			}
		}
	}

	public void updateGame(String ifid, String title) {
		Uri uri = Uri.withAppendedPath(Games.CONTENT_URI, ifid);
		Cursor query = mContentResolver.query(uri, PROJECTION, null, null, null);
		
		ContentValues cv = new ContentValues();
		cv.put(Games.TITLE, title);
		
		if (query != null && query.getCount() == 1) 
			mContentResolver.update(uri, cv, null, null);
		
		query.close();
	}

	public void deleteGame(String ifid) {
		String path = null;
		Uri uri = HunkyPunk.Games.uriOfIfid(ifid);
		Log.d("StorageManager",uri.toString());
		Cursor query = mContentResolver.query(uri, PROJECTION, null, null, null);
		if (query != null || query.getCount() == 1)
			if (query.moveToNext())
				path = query.getString(PATH);

		if (path != null){
			File fp = new File(path);
			if (fp.exists()) fp.delete();
		}
		query.close();

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.execSQL("delete from games where ifid = '"+ifid+"'");
	}

	private String checkFile(File f) throws IOException {
		String ifid = Babel.examine(f);
		if (ifid == null) {
			Log.w(TAG, "checkFile skipping " + f + " as Babel can't find ifid");
			return null;
		}

		return checkFile(f, ifid);
	}

	private String checkFile(File f, String ifid) throws IOException {
		if (ifid == null) ifid = Babel.examine(f);
		if (ifid == null) return null;
		
		Uri uri = Uri.withAppendedPath(Games.CONTENT_URI, ifid);
		Cursor query = mContentResolver.query(uri, PROJECTION, null, null, null);
		
		ContentValues cv = new ContentValues();
		cv.put(Games.PATH, f.getAbsolutePath());
		
		if (query == null || query.getCount() != 1) {
			cv.put(Games.IFID, ifid);
			final String fname = f.getName();
			cv.put(Games.TITLE, fname.substring(0, fname.lastIndexOf('.')));
			mContentResolver.insert(Games.CONTENT_URI, cv);
		} else
			mContentResolver.update(uri, cv, null, null);
		
		query.close();
		return ifid;
	}

	private File createFileFromInputStream(InputStream inputStream, File targetFile) {

		try{
			OutputStream outputStream = new FileOutputStream(targetFile);
			byte buffer[] = new byte[1024];
			int length = 0;

			while((length=inputStream.read(buffer)) > 0) {
				outputStream.write(buffer,0,length);
			}

			outputStream.close();
			inputStream.close();

			return targetFile;
		}catch (IOException e) {
			Log.e(TAG, "IOException in createFileFromInputStream", e);
		}

		return null;
	}

	/*
	Modeled after checkFile.
	Android Assets can not be used to create Java File object without consuming storage space
	Note: Android assets can not be a Java File, and given we are calling Native C code - it's not
	really possible to pass a Java stream down for the game virtual machines (VM) to execute.
	So we will consume local storage and copy. To ensure the best compatibility of storage paths,
	use a local path.
	 */
	private String addKnownFileFromAndroidAssets(String assetRelativeFilePath, String ifid, String gameTitle, String targetFilename) {
		File copiedAssetFileDir = new File(context.getFilesDir() + "/assetCopy0");
		copiedAssetFileDir.mkdirs();
		File copiedAssetFile = new File(copiedAssetFileDir + File.separator + targetFilename);

		AssetManager am = context.getAssets();
		try {
			InputStream inputStream = am.open(assetRelativeFilePath);
			createFileFromInputStream(inputStream, copiedAssetFile);

			if (copiedAssetFile.length() > 10L) {

				return checkFile(copiedAssetFile);
			}
			else
			{
				Log.e(TAG, "asset copy failed " + assetRelativeFilePath);
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// ToDo: method currently unused? Ideas on why this was intended? Validate the file is working - and mark DB as corrupted/problem?
	public void startCheckingFile(final File file) {
		new Thread() {
			@Override
			public void run() {
				try {
					String ifid;
					if ((ifid = checkFile(file)) != null) {
						Message.obtain(mHandler, INSTALLED, ifid).sendToTarget();
						return;
					}
				} catch (IOException e) {
				}
				
				Message.obtain(mHandler, INSTALL_FAILED).sendToTarget();
			}
		}.run();
	}


	public static String unknownContent = "IFID_";

	public void startInstall(final Uri game, final String scheme) {
		Thread startInstallThread = new Thread() {
			@Override
			public void run() {

				File fgame = null;
				File ftemp = null;
				String ifid = null;

				try {
					if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
						ftemp = File.createTempFile(unknownContent, null, Paths.tempDirectory());
						InputStream in = mContentResolver.openInputStream(game);
						OutputStream out = new FileOutputStream(ftemp);
						Utils.copyStream(in, out);
						in.close(); out.close();

						ifid = Babel.examine(ftemp);

						//TODO: obtain terp from Babel
						String ext = "zcode";
						if (ifid.indexOf("TADS")==0) ext="gam";

						fgame = new File(Paths.tempDirectory().getAbsolutePath() 
												 + "/" + unknownContent + ifid + "." + ext);
						ftemp.renameTo(fgame);
					}
					else {
						fgame = new File(game.getPath());
					}

					String src = fgame.getAbsolutePath();
					String dst = Paths.ifDirectory().getAbsolutePath() + "/" + fgame.getName();
					String installedPath = gameInstalledFilePath(fgame);

					if (installedPath == null || !(new File(installedPath).exists())) {
						if (!dst.equals(src)) {
							InputStream in = new FileInputStream(src);
							OutputStream out = new FileOutputStream(dst);

							Utils.copyStream(in,out);
							in.close(); out.close();
						}
					}
					else {
						dst = installedPath;
					}

					if ((ifid = checkFile(new File(dst), ifid)) != null) {
						Message.obtain(mHandler, INSTALLED, ifid).sendToTarget();
						return;
					}
				} catch (Exception e){
					Log.e("HunkyPunk/StorageManagr", "Exception", e);
				} finally {
					if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
						if (ftemp != null && ftemp.exists()) ftemp.delete();
						if (fgame != null && fgame.exists()) fgame.delete();
					}
				}

				Message.obtain(mHandler, INSTALL_FAILED).sendToTarget();
			}
		};
		startInstallThread.setName("startInstallThread");
		startInstallThread.start();
	}

	//added for Swipe
	//creates an array with pathes of all games
	public File[] getFiles(File dir) {
		return (dir.listFiles());
	}

	public String[] getIfIdArray(File dir) {
		String path = null;
		File[] x = dir.listFiles();
		String[] gameArray = new String[getFiles(dir).length - 1];
		String[] ifIdArray = new String[gameArray.length];
		for (int i = 0; i < (gameArray.length); i++) {
			try {
				path = Babel.examine(x[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Uri uri = Uri.withAppendedPath(Games.CONTENT_URI, path);

			Cursor query = mContentResolver.query(uri, PROJECTION2, null, null, null);

			query.moveToFirst();
			if (query != null) {

				gameArray[i] = query.getString(3);

			}
			query.close();


		}
		Arrays.sort(gameArray);
		for (int i = 0; i < (ifIdArray.length); i++) {
			String gameTitle = gameArray[i];

			for (int j = 0; j < (ifIdArray.length); j++) {
				try {
					path = Babel.examine(x[j]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Uri uri = Uri.withAppendedPath(Games.CONTENT_URI, path);
				Cursor query = mContentResolver.query(uri, PROJECTION3, null, null, null);
				query.moveToFirst();
				if (query != null) {
					if (query.getString(1).equals(gameTitle))
						ifIdArray[i] = query.getString(0);
				}
				query.close();
			}


		}
		return ifIdArray;
	}
}