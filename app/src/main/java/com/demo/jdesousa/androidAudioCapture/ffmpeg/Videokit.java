package com.demo.jdesousa.androidAudioCapture.ffmpeg;

public final class Videokit
{
	static
	{
		System.loadLibrary ("videokit");
	}

	private static Videokit	instance	= null;

	public static Videokit getInstance ()
	{
		if (instance == null)
			instance = new Videokit ();

		return instance;
	}

	private Videokit ()
	{
	}

	/**
	 * Call ffmpeg with specified arguments.
	 * 
	 * Note that the first should be the executable name, "ffmpeg".
	 * 
	 * @param args
	 *            ffmpeg arguments
     * @return
     *            true if ffmpeg exited successfully, false otherwise
	 */
	public native boolean run (String[] args);
}
