package org.andglkmod.glk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TextAppearanceSpan;
import android.util.Log;

import org.andglkmod.SharedPrefKeys;
import org.andglkmod.hunkypunk.EasyGlobalsA;


/** This class handles all the styles a window can have. 
 * */
public class Styles {
	/** Create an empty styles object */
	public Styles() {
	}
	
	/** Create a styles from an existing styles (useful when opening a window and fixing the styles) */
	public Styles(Styles styles) {
		for (int style=0; style < Glk.STYLE_NUMSTYLES; style++) {
			for (int hint=0; hint < Glk.STYLEHINT_NUMHINTS; hint++) {
				_styles[style][hint] = styles._styles[style][hint];
			}
		}
	}

	// _styles contains all the hints, indexed by style and hint
	private final Integer[][] _styles = new Integer[Glk.STYLE_NUMSTYLES][Glk.STYLEHINT_NUMHINTS];
	
	/** sets a style hint */
	public void set(int styl, int hint, int val) {
		if (styl >= 0 && styl < Glk.STYLE_NUMSTYLES && hint >= 0 && hint < Glk.STYLEHINT_NUMHINTS) {
			_styles[styl][hint] = val;
		} else {
			//Log.w("Glk/Styles/set", "unknown style or hint: " + styl + " " + hint);
		}
	}
	
	/** clears a style hint */
	public void clear(int styl, int hint) {
		if (styl >= 0 && styl < Glk.STYLE_NUMSTYLES && hint >= 0 && hint < Glk.STYLEHINT_NUMHINTS) {
			_styles[styl][hint] = null;
		} else {
			//Log.w("Glk/Styles/clear", "unknown style or hint: " + styl + " " + hint);
		}
	}

	/**
	 * Get a text paint for the given style, using the hints
	 * @param context
	 * @param mPaint
	 * @param styl
	 * @param reverse
	 * @return
	 */
	public TextPaint getPaint(Context context, TextPaint mPaint, int styl, boolean reverse) {
		// Create an copy of the paint
		TextPaint tpx = new TextPaint();
		tpx.set(mPaint);
		
		// Update it using the span we get from the style resource
		new StyleSpan(context, styl, reverse).updateDrawState(tpx);
		
		return tpx;
	}

	/**
	 * Used for the Day-Night Mode to toggle styles
	 * @param context Used to call the value in the SharedPreferences
	 * @param styl the style to be checked and respectively toggled
	 * @return     the new updated style
	 */
	private static int determineStyle(Context context, int styl) {
		boolean preferenceNightOn = context.getSharedPreferences(SharedPrefKeys.KEY_FILE_Night, Context.MODE_PRIVATE).getBoolean("NightOn", false);

		if (styl == Glk.STYLE_HEADER && preferenceNightOn)
			styl = Glk.STYLE_NIGHT_HEADER;

		if (styl == Glk.STYLE_SUBHEADER && preferenceNightOn)
			styl = Glk.STYLE_NIGHT_SUBHEADER;

		if (styl == Glk.STYLE_INPUT && preferenceNightOn)
			styl = Glk.STYLE_NIGHT;

		if (styl == Glk.STYLE_NIGHT && ! preferenceNightOn)
			styl = Glk.STYLE_INPUT;

		if (styl == Glk.STYLE_PREFORMATTED && preferenceNightOn)
			styl = Glk.STYLE_NIGHT_FORMAT;

		if (styl == Glk.STYLE_NIGHT_FORMAT && ! preferenceNightOn)
			styl = Glk.STYLE_PREFORMATTED;

		return styl;
	}

	public class StyleSpan extends TextAppearanceSpan {
		private final int _styl;
		private final boolean _reverse;

		public StyleSpan(Context context, int styl, boolean reverse) {
			super(context,Window.getTextAppearanceId(determineStyle(context, styl)));
			_styl = styl;
			_reverse = reverse;
		}
		
		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			updatePaint(_styl, _reverse, ds);
		}

		@Override
		public void updateMeasureState(TextPaint ds) {
			super.updateMeasureState(ds);
			//updatePaint(_styl, _reverse, ds);
		}

		public int getStyle() {
			return _styl;
		}
		
		public int getReverse() {
			return _reverse ? 1 : 0;
		}
	}
	
	// Update paint of style according to style hints
	private void updatePaint(int styl, boolean reverse, TextPaint drawState) {
		// These are the hints we are going to use
		Integer[] hints = _styles[styl];
		// Set typeface first, because it's used by WEIGHT and OBLIQUE cases
		if (hints[Glk.STYLEHINT_PROPORTIONAL] != null) {
			/*if (Integer.valueOf(0).equals(hints[Glk.STYLEHINT_PROPORTIONAL])) {
				ds.setTypeface(Typeface.MONOSPACE);
			} else {
				ds.setTypeface(Typeface.SERIF);
			}*/
			if (TextBufferWindow.mTypeface != null) {
				if (EasyGlobalsA.storyOutput_style_Typeface_LogA) {
					// ToDo: running code shows this code path is being hit frequently! Optimize!
					Log.v("Java/Styles", "Preferences say to set Typeface to Preferences? STYLEHINT_PROPORTIONAL? " + hints[Glk.STYLEHINT_PROPORTIONAL]);
				}
				// SOHP 0.9 release always seemed to pick the Preferences typeface and seemed to ignore Monospace.
				if (EasyGlobalsA.storyOutput_style_Typeface_HonorMonospaceOverPrefsA) {
					if (Integer.valueOf(0).equals(hints[Glk.STYLEHINT_PROPORTIONAL])) {
						drawState.setTypeface(Typeface.MONOSPACE);
					} else {
						drawState.setTypeface(TextBufferWindow.mTypeface); // sets Typeface chosen from Preferences
					}
				} else {
					// SOHP 0.9 behavior.
					drawState.setTypeface(TextBufferWindow.mTypeface); // sets Typeface chosen from Preferences
				}
			}else {
				if (EasyGlobalsA.storyOutput_style_Typeface_LogA) {
					Log.v("Java/Styles", "set Typeface to STYLEHINT_PROPORTIONAL? " + hints[Glk.STYLEHINT_PROPORTIONAL]);
				}
				if (Integer.valueOf(0).equals(hints[Glk.STYLEHINT_PROPORTIONAL])) {
					drawState.setTypeface(Typeface.MONOSPACE);
				} else {
					drawState.setTypeface(Typeface.SERIF);
				}
			}
		}

		// We must modify typeface if we have weight or oblique hints
		Integer weight = hints[Glk.STYLEHINT_WEIGHT];
		Integer oblique = hints[Glk.STYLEHINT_OBLIQUE];
		if (weight != null || oblique != null) {
			// Code taken from TextAppearanceSpan
			Typeface tf = drawState.getTypeface();
			int style = 0;

			if (tf != null) {
				style = tf.getStyle();
			}

			if (Integer.valueOf(0).equals(weight)) {
				style &= ~Typeface.BOLD;
			} else if (Integer.valueOf(1).equals(weight)) {
				style |= Typeface.BOLD;
			}
			if (Integer.valueOf(0).equals(oblique)) {
				style &= ~Typeface.ITALIC;
			} else if (Integer.valueOf(1).equals(oblique)) {
				style |= Typeface.ITALIC;
			}

			if (tf != null) {
				tf = Typeface.create(tf, style);
			} else {
				tf = Typeface.defaultFromStyle(style);
			}

			int fake = style & ~tf.getStyle();

			if ((fake & Typeface.BOLD) != 0) {
				drawState.setFakeBoldText(true);
			}

			if ((fake & Typeface.ITALIC) != 0) {
				drawState.setTextSkewX(-0.25f);
			}

			drawState.setTypeface(tf);
			// End of code taken from TextAppearanceSpan
		}

		// We increase or decrease size by 10% for each step of the size hint (+1 means increase by 10%)
		if (hints[Glk.STYLEHINT_SIZE] != null) {
			double size = drawState.getTextSize() * (10.0+hints[Glk.STYLEHINT_SIZE])/10.0;
			drawState.setTextSize((float) size);
		}

		if (hints[Glk.STYLEHINT_TEXTCOLOR] != null) {
			drawState.setColor(hints[Glk.STYLEHINT_TEXTCOLOR]);
		}

		if (hints[Glk.STYLEHINT_BACKCOLOR] != null) {
			drawState.bgColor = hints[Glk.STYLEHINT_BACKCOLOR];
		}
		
		if (reverse || Integer.valueOf(1).equals(hints[Glk.STYLEHINT_REVERSECOLOR])) {
			// swap background and foreground colors
			int color = drawState.getColor();
			if (TextBufferWindow.DefaultTextColor == Color.WHITE) {//it's night
				drawState.setColor(TextBufferWindow.DefaultTextColor);
				drawState.bgColor = Color.BLACK;
				//reverse = true;
			} else {
				drawState.setColor(drawState.bgColor);
				drawState.bgColor = color;
			}
		}
	}

	public StyleSpan getSpan(Context context, int styl, boolean reverse) {
		return new StyleSpan(context, styl, reverse);
	}
}