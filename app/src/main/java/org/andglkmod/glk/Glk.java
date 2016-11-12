/*
	Copyright © 2009 Rafał Rzepecki <divided.mind@gmail.com>

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

package org.andglkmod.glk;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.andglkmod.hunkypunk.EasyGlobalsA;
import org.andglkmod.hunkypunk.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;

/** <strong>DO NOT EVER INSTANTIATE OR START THIS CLASS MORE THAN ONCE IN A PROCESS' LIFETIME</strong> */
public class Glk extends Thread {
	public static class AlreadyRunning extends Exception {
		private static final long serialVersionUID = -8966218915411360727L;
	}
	
	public final static int STYLE_NORMAL = 0;
	public final static int STYLE_EMPHASIZED = 1;
	public final static int STYLE_PREFORMATTED = 2;
	public final static int STYLE_HEADER = 3;
	public final static int STYLE_SUBHEADER = 4;
	public final static int STYLE_ALERT = 5;
	public final static int STYLE_NOTE = 6;
	public final static int STYLE_BLOCKQUOTE = 7;
	public final static int STYLE_INPUT = 8;
	public final static int STYLE_USER1 = 9;
	public final static int STYLE_USER2 = 10;
	//Night Constants
	public final static int STYLE_NIGHT = 11;
	public final static int STYLE_NIGHT_HEADER = 12;
	public final static int STYLE_NIGHT_SUBHEADER = 13;
	public final static int STYLE_NIGHT_FORMAT = 14;
	//Night Constants
	public final static int STYLE_NUMSTYLES = 15;
	
	public final static int STYLEHINT_INDENTATION = 0;
	public final static int STYLEHINT_PARAINDENTATION = 1;
	public final static int STYLEHINT_JUSTIFICATION = 2;
	public final static int STYLEHINT_SIZE = 3;
	public final static int STYLEHINT_WEIGHT = 4;
	public final static int STYLEHINT_OBLIQUE = 5;
	public final static int STYLEHINT_PROPORTIONAL = 6;
	public final static int STYLEHINT_TEXTCOLOR = 7;
	public final static int STYLEHINT_BACKCOLOR = 8;
	public final static int STYLEHINT_REVERSECOLOR = 9;
	public final static int STYLEHINT_NUMHINTS = 10;

	public final static int STYLEHINT_JUST_LEFTFLUSH = 0;
	public final static int STYLEHINT_JUST_LEFTRIGHT = 1;
	public final static int STYLEHINT_JUST_CENTERED = 2;
	public final static int STYLEHINT_JUST_RIGHTFLUSH = 3;

	private static Glk _instance;
	
	public final static int GESTALT_VERSION = 0;
	public final static int GESTALT_CHARINPUT = 1;
	public final static int GESTALT_LINEINPUT = 2;
	public final static int GESTALT_CHAROUTPUT = 3;
	public final static int GESTALT_CHAROUTPUT_CANNOTPRINT = 0;
	public final static int GESTALT_CHAROUTPUT_APPROXPRINT = 1;
	public final static int GESTALT_CHAROUTPUT_EXACTPRINT = 2;
	public final static int GESTALT_MOUSEINPUT = 4;
	public final static int GESTALT_TIMER = 5;
	public final static int GESTALT_GRAPHICS = 6;
	public final static int GESTALT_DRAWIMAGE = 7;
	public final static int GESTALT_SOUND = 8;
	public final static int GESTALT_SOUNDVOLUME = 9;
	public final static int GESTALT_SOUNDNOTIFY = 10;
	public final static int GESTALT_HYPERLINKS = 11;
	public final static int GESTALT_HYPERLINKINPUT = 12;
	public final static int GESTALT_SOUNDMUSIC = 13;
	public final static int GESTALT_GRAPHICSTRANSPARENCY = 14;
	public final static int GESTALT_UNICODE = 15;

	private Stream mCurrentStream;
	private FrameLayout mFrame;
	private Handler mUiHandler = new Handler();
	private BlockingQueue<Event> _eventQueue = new LinkedBlockingQueue<Event>();
	protected boolean _done;
	private Context mContext;
	private File mSaveDir;
	private File mTranscriptDir;

	private int _autoSaveLineEvent = 0;
	private File _autoSave = null;
	private String _autoSavePath = "";
	private String[] _arguments = {};
	private boolean _needToSave = false;
	private boolean _exiting = false;

	native private void startTerp(String terpPath, String saveFilePath, int argc, String[] argv);


	@Override
	public void run() {
		// Native C code can crash the App in nasty ways, best to check for obvious crash causes before we get into the native code.
		// Heavy defensive checks
		// ToDo: display a message on the View when return happens?
		if (_arguments[0] == null) {
			Log.e("Glk/Java", "Glk.java startTerp attempted with essential _arguments[0] null (interpreter .so binary file path?)");
			return;
		}

		if (_autoSavePath == null) {
			Log.e("Glk/Java", "Glk.java startTerp attempted with essential _autoSavePath null");
			return;
		}

		if (_arguments.length > 1)
		{
			if (_arguments[1] == null) {
				Log.e("Glk/Java", "Glk.java startTerp attempted with essential _arguments[1] null (data work dir?)");
				return;
			}
		}
/*
Right now, this is crashing hard the app on Blu Studio Energy 2 Android 5.0:

10-31 10:27:04.521 14742-14770/? I/Glk/Java: calling native code startTerp /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so  2 [/data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so, /storage/sdcard0/storyInteractiveFiction/storyGames0/advent.blb]
10-31 10:27:04.521 14742-14770/? D/HunkyPunk: loader.dlopen /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.522 14742-14770/? E/HunkyPunk: loader.dlopen failed for /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.522 14742-14770/? D/HunkyPunk: loader.dlopen /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.522 14742-14742/? V/ActivityThread: Performing resume of ActivityRecord{2aaecfb7 token=android.os.BinderProxy@e426324 {org.andglkmod.hunkypunk.dev/org.andglkmod.hunkypunk.InterpreterActivity}}
10-31 10:27:04.522 14742-14770/? E/HunkyPunk: loader.dlopen failed for /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.522 14742-14770/? D/HunkyPunk: loader.dlopen /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.523 14742-14770/? E/HunkyPunk: loader.dlopen failed for /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.523 14742-14770/? D/HunkyPunk: loader.dlopen /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.523 14742-14770/? E/HunkyPunk: loader.dlopen failed for /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.523 14742-14770/? D/HunkyPunk: loader.dlopen /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.523 14742-14770/? E/HunkyPunk: loader.dlopen failed for /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.523 14742-14770/? D/HunkyPunk: loader.dlopen /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.524 14742-14770/? E/HunkyPunk: loader.dlopen failed for /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.524 14742-14770/? D/HunkyPunk: loader.dlopen /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
10-31 10:27:04.524 14742-14770/? E/HunkyPunk: loader.dlopen failed for /data/data/org.andglkmod.hunkypunk.dev/files/../lib/libgit.so
 */
		try {
			Log.i("Glk/Java", "calling native code startTerp " + _arguments[0] + " " + _autoSavePath + " " + _arguments.length + " " + Arrays.toString(_arguments));
			startTerp(_arguments[0], _autoSavePath, _arguments.length, _arguments);
		}
		catch (Exception e)
		{
			// Note: fatal app crash with NoSuchMethodError that does not allow catch?
			//     No static method startTerp(Lorg/andglkmod/glk/Glk;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/String;)V in class Lorg/andglkmod/glk/Glk$override; or its super classes (declaration of 'org.andglkmod.glk.Glk$override' appears in /data/data/org.andglkmod.hunkypunk.dev/files/instant-run/dex-temp/reload0x0000.dex)
			// This can be reproduced 1) by fresh sart of app. 2) failed launch of story due to midding emulator .so, 3) try to launch again.
			//   So there seems to be a lack of cleanup on failed start that makes 2nd start fatally crash entire app.
			Log.e("Glk/Java", "startTerp got Exception", e);
		}
		try {
			notifyQuit();
			_instance = null;
			Window.setRoot(null);
		}
		catch (Exception e)
		{
			Log.e("Glk/Java", "startTerp after got Exception", e);
		}
	}

	// loader has successfully loaded and linked to glk interpreter and is about to start
	// ToDo: implement C code that calls this method
	public void notifyLinked() {
		// just a concept, not sure if its useful
	}
	
	private void notifyQuit() {
		mUiHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypedArray ta = mContext.obtainStyledAttributes(null,
							new int[]{android.R.attr.textAppearance},
							R.attr.textGridWindowStyle,
							0);
					int res = ta.getResourceId(0, -1);
					ta = mContext.obtainStyledAttributes(res, new int[]{android.R.attr.textSize, android.R.attr.textColor});
					int fontSize = (int) (ta.getDimensionPixelSize(0, -1));

					final View overlay = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(R.layout.floating_notification, null);
					TextView tw = (TextView) (overlay.findViewById(R.id.message));
					tw.setTextSize(fontSize);
					tw.setText(R.string.game_quit);

					overlay.measure(View.MeasureSpec.makeMeasureSpec(mFrame.getWidth(), MeasureSpec.AT_MOST),
							View.MeasureSpec.makeMeasureSpec(mFrame.getHeight(), MeasureSpec.AT_MOST));
					// On abnormal C code end, app crashing Exception here: "IllegalArgumentException: width and height must be > 0"
					// Running on Emulator, Android SDK 24, Nexus 9 device
					Log.d("Glk/Java", "bitmap width " + overlay.getMeasuredWidth() + " height " + overlay.getMeasuredHeight());
					if (overlay.getMeasuredHeight() > 0) {
						Bitmap bitmap = Bitmap.createBitmap(overlay.getMeasuredWidth(), overlay.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
						overlay.layout(0, 0, overlay.getMeasuredWidth(), overlay.getMeasuredHeight());
						overlay.draw(new Canvas(bitmap));
						mFrame.setForeground(new BitmapDrawable(bitmap));
						mFrame.setForegroundGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
					}
					else
					{
						// ToDo: do we need this complicated overylay anyway, why not just insert a standard View?
						// The overlay makes sense on normal end of game, but abnormal end (crash) or failed start, it does not.
						Log.e("Glk/Java", "notifyQuit got zero height, user will get blank screen.");
					}
				}
				catch (Exception e)
				{
					// This results in a blank screen
					Log.e("Glk/Java", "Exception in notifyQuit", e);
				}
			}
		});
	}

	public Glk(Context context) {
		assert (_instance == null);
		_instance = this;
		mFrame = new FrameLayout(context) {
			@Override
			protected void onLayout(boolean changed, int left, int top,
					int right, int bottom) {
				super.onLayout(changed, left, top, right, bottom);
				if (changed)
					postEvent(new ArrangeEvent());
			}
		};
		mContext = context;

		Activity activity = (Activity) context;
		if (activity != null)
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	public void setWindow(Window window) {
		if (window == null)
		{
			Log.e("Java/Glk", "setWindow on null window");
		}
		else {
			mCurrentStream = window.getStream();
		}
	}
	
	@SuppressWarnings("unused")
	private Event select()
	{
		if (EasyGlobalsA.glk_c_to_java_select_LogA){
			Log.v("Glk.java", "select()");
		}
		flush();
		Event ev;
		while (true) {
			try {
				ev = _eventQueue.take();
				if (ev instanceof SystemEvent) {
					((SystemEvent) ev).run();
					continue;
				}
				return ev;
			} catch (InterruptedException e) {
			}
		}
	}
	
	public ViewGroup getView() {
		return mFrame;
	}

	public Handler getUiHandler() {
		return mUiHandler;
	}

	public void postEvent(Event e) {
		// certain events shoudl invoke an autosave of game play, although CharInputEvent in fast-paced game may be a bad idea.
		_needToSave = _needToSave || (e instanceof CharInputEvent || e instanceof LineInputEvent);
		_eventQueue.add(e);
	}

	public void postExitEvent() {
		setExiting(true);
		_eventQueue.add(new ExitEvent(Window.getRoot()));
	}

	public void postTimerEvent() {
		_eventQueue.add(new TimerEvent(Window.getRoot()));
	}

	public boolean postAutoSaveEvent(String fileName) {
		if (_needToSave) {
			Log.i("Glk.java", "Saved game.");
			//Toast.makeText(mContext, "Saved game.", Toast.LENGTH_SHORT).show();
			_eventQueue.add(new AutoSaveEvent(Window.getRoot(), fileName, 1)); //_autoSaveLineEvent));
			_needToSave = false;
			return true;
		}
		return false;
	}

	/*
	Todo: Explain the logic of this method and when it should and should-not be used
	 */
	public synchronized void waitForUi(final Runnable runnable) {
		if (_exiting) return;

		if (Thread.currentThread().equals(mUiHandler.getLooper().getThread())) {
			runnable.run();
			return;
		}
		
		_done = false;
		mUiHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized(Glk.this) {
					runnable.run();
					_done = true;
					Glk.this.notify();
				}
			}
		});

		while (!_done) try {
			wait();
		} catch (InterruptedException e) {
			// try again
		}
	}

	public Context getContext() {
		return mContext;
	}

	public static Glk getInstance() {
		return _instance;
	}

	/** Get directory to place game data in.
	 * 
	 * @param usage Usage of files placed in the directory. 
	 * One of {@link FileRef#FILEUSAGE_DATA}, {@link FileRef#FILEUSAGE_INPUTRECORD}, 
	 * {@link FileRef#FILEUSAGE_SAVEDGAME}, {@link FileRef#FILEUSAGE_TRANSCRIPT}.
	 * @return Directory to place the files in. <code>null</code> if this type of files cannot be stored.
	 */
	public File getFilesDir(int usage) {
		switch (usage) {
		case FileRef.FILEUSAGE_SAVEDGAME:
			return getSaveDir();
		case FileRef.FILEUSAGE_TRANSCRIPT:
			return getTranscriptDir();
		default:
			Log.e("Glk", "I don't know where to place files with usage = " + Integer.toString(usage));
			return getSaveDir();
			//return null;
		}
	}
	
	/** Query Glk capabilities.
	 * 
	 * @param sel Selector -- which capability you are requesting information about.
	 * @param val Parameter for that selector. Optional, pass 0 if not needed.
	 * @return An array which first element is the main return value and the rest is any additional information pertinent.
	 */
	static final int[] sZero = { 0 };
	static final int[] sOne = { 1 };

	public int[] gestalt(int sel, int val) {

		if (EasyGlobalsA.glk_c_to_java_gestalt_LogA) {
			Log.d("Glk","gestalt " + Integer.toString(sel));
		}

		switch (sel) {
		case GESTALT_VERSION:
			return new int[] { 0x700 };
		case GESTALT_CHAROUTPUT:
			if (EasyGlobalsA.glk_gestalt_answerUnicdeTrue) {
				// ToDo: basic range check for values like 0 and such that can never print?
				return new int[]{GESTALT_CHAROUTPUT_EXACTPRINT, 1};
			}
			else {
				// version 0.9 logic
				// we only do latin-1 ATM
				if (isPrintable((char) val))
					return new int[]{GESTALT_CHAROUTPUT_EXACTPRINT, 1};
				else
					return new int[]{GESTALT_CHAROUTPUT_CANNOTPRINT, 0};
			}
		case GESTALT_LINEINPUT:
			if (isPrintable((char) val) && val != 10)
				return sOne;
			else
				return sZero;
		case GESTALT_CHARINPUT:
			// TODO: handle special characters; this needs getChar support too.
			return CharInputEvent.accepts(val) ? sOne : sZero;
		case GESTALT_UNICODE:
			Log.i("Glk.java", "GESTALT_UNICODE, was just asked if Unicode is supported!");
			return sOne;
		case GESTALT_TIMER:
			return sOne;
		default:
			Log.w("Glk", "unhandled gestalt selector: " + Integer.toString(sel) + " (value " + val + ")");
		// all below are TODO
		case GESTALT_MOUSEINPUT:
		case GESTALT_GRAPHICS:
		case GESTALT_DRAWIMAGE:
		case GESTALT_GRAPHICSTRANSPARENCY:
		case GESTALT_SOUND:
		case GESTALT_SOUNDMUSIC:
		case GESTALT_SOUNDVOLUME:
		case GESTALT_SOUNDNOTIFY:
		case GESTALT_HYPERLINKS:
		case GESTALT_HYPERLINKINPUT:
			return sZero;
		}
	}

	private static boolean isPrintable(char val) {
		if ((val >= 0 && val < 10) || (val > 10 && val < 32) || (val > 126 && val < 160) || val > 255)
			return false;
		else
			return true;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		mFrame.requestLayout();
	}

	public void setCurrentStream(Stream stream) {
		mCurrentStream = stream;
	}

	public Stream getCurrentStream() {
		return mCurrentStream;
	}
	
	public void flush() {
		if (EasyGlobalsA.glk_c_to_java_input_events_LogA) {
			Log.d("Glk.java", "Glk.java flush()");
		}
		final Window root = Window.getRoot();
		if (root != null)
			root.flush();
	}

	// ToDo: AtomicBoolean to allow concurrency?
	public static boolean timerTickingNow = false;
	public boolean timerHasBeenCanceled = false;
	public long timerRepeatInterval = 0L;
	public Thread timerThread;

	/*
	A single timer. If you wanted to implement multiple timers, put this into a class and create an instance.
	 */
	@SuppressWarnings("unused")
	public void requestTimer(final int millisecs) {
		if (EasyGlobalsA.glk_c_to_java_timer_LogA){
			Log.d("Glk.java", "Glk.java requestTimer() " + millisecs);
		}

		if (timerTickingNow) {
			Log.w("Glk.java", "Glk.java requestTimer() " + millisecs + " found overlapping timers");
		}

		timerHasBeenCanceled = false;
		timerTickingNow = false;
		timerRepeatInterval = (long) millisecs;

		timerThread = new Thread() {
			@Override
			public void run() {
				int timerPostCount = 0;
				while (! timerHasBeenCanceled) {
					try {
						timerTickingNow = true;
						Thread.sleep(timerRepeatInterval);
						timerTickingNow = false;
						// check again in case cancel came in during sleep. Timers should otherwise repeat.
						if (!timerHasBeenCanceled) {
							if (EasyGlobalsA.glk_c_to_java_timer_LogA){
								Log.d("Glk.java", "Glk.java requestTimer() " + millisecs + " posts " + timerPostCount + " " + Thread.currentThread());
							}
							// This is messy, because C code may be running async? Don't we really want synchronous behavior?
							postTimerEvent();
							timerPostCount++;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		timerThread.setName("Glk_timerThread");
		timerThread.start();
	}

	@SuppressWarnings("unused")
	public void cancelTimer() {
		if (EasyGlobalsA.glk_c_to_java_timer_LogA){
			Log.d("Glk.java", "Glk.java timerCancel()");
		}
		timerHasBeenCanceled = true;
	}

	public void onSelect(Runnable runnable) {
		postEvent(new SystemEvent(runnable));
	}

	public void setTranscriptDir(File transcriptDir) {
		mTranscriptDir = transcriptDir;
	}

	public File getTranscriptDir() {
		return mTranscriptDir;
	}

	public void setNeedToSave(boolean need) {		
		_needToSave = need;
	}	
	
	public void setAutoSave(File autoSave, int autoSaveLineEvent) {		
		_autoSave = autoSave;
		if (_autoSave.exists()) _autoSavePath = _autoSave.getAbsolutePath();
		_autoSaveLineEvent = autoSaveLineEvent;
	}	

	public File getAutoSave() {
		return _autoSave;
	}
	
	public void setSaveDir(File saveDir) {
		mSaveDir = saveDir;
	}

	public File getSaveDir() {
		return mSaveDir;
	}

	public void setExiting(boolean flag) {
		_exiting = flag;
	}

	public boolean getExiting() {
		return _exiting;
	}

	public void setArguments(String[] arguments) {
		_arguments = arguments;
	}

	public String[] getArguments() {
		return _arguments;
	}

	/** returns a path appropriate for Android Andglk
	 * 
	 */
	public String sanitizePath (String path) {
		if (_autoSave == null) return path;

		String sanePath = new String(path);
		if (!sanePath.startsWith("/")) {
			int ix = sanePath.lastIndexOf('/');
			if (ix > -1) sanePath = sanePath.substring(ix+1);
			File saneFile = new File(_autoSave.getParentFile(), sanePath);
			sanePath = saneFile.getAbsolutePath();
		}
		return sanePath;
	}
}
