package engine.sound;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

public class SoundManager {

	private static SoundManager manager;

	public static SoundManager get() {
		if (manager == null) {
			manager = new SoundManager();
			manager.setListener(new SoundListener());
		}
		return manager;
	}

	private long device;

	private long context;

	private SoundListener listener;

	private final Map<String, SoundBuffer> soundBufferMap;
	private SoundSource[] soundSources = new SoundSource[64];

	public SoundManager() {
		soundBufferMap = new HashMap<>();
		this.device = alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		this.context = alcCreateContext(device, (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		initSoundSources();
	}
	
	public void playSoundEffect(String name) {
		SoundSource s = getFreeSoundSource();
		s.setBuffer(getBufferIdForSound(name));
		//set volume for sound effects, pos, velocity etc
		s.play();
	}

	public final int getBufferIdForSound(String name) {
		SoundBuffer sb = soundBufferMap.get(name);
		if (sb == null) {
			System.err.println("Sound: " + name + " not found. Trying to load it");
			loadSound(name);
		}
		return soundBufferMap.get(name).getBufferId();
	}

	public void loadSound(String fileName) {
		SoundBuffer sb = new SoundBuffer("sounds/" + fileName + ".ogg");
		addSoundBuffer(fileName, sb);
	}
	

	public void addSoundBuffer(String name, SoundBuffer soundBuffer) {
		soundBufferMap.put(name, soundBuffer);
	}

	public SoundListener getListener() {
		return this.listener;
	}

	public void setListener(SoundListener listener) {
		this.listener = listener;
	}

	public void setAttenuationModel(int model) {
		alDistanceModel(model);
	}

	public void cleanUp() {
		Arrays.stream(soundSources).forEach(ss->ss.cleanup());
		for (SoundBuffer soundBuffer : soundBufferMap.values()) {
			soundBuffer.cleanup();
		}
		soundBufferMap.clear();
		if (context != NULL) {
			alcDestroyContext(context);
		}
		if (device != NULL) {
			alcCloseDevice(device);
		}
	}
	
	private SoundSource getFreeSoundSource() {
		for(int i=0;i<soundSources.length;i++) {
			if(!soundSources[i].isPlaying()) {
				return soundSources[i];
			}
		}
		System.err.println("No free soundsources available");
		return soundSources[0];
	}
	
	private void initSoundSources() {
		for(int i=0;i<soundSources.length;i++) {
			soundSources[i]=new SoundSource();
		}
	}
}
