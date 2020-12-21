package util;

import java.util.Random;

import math.Maths;

public class PerlinNoise {

	private Random random = new Random();
	private int seed;

	public PerlinNoise() {
		this.seed = random.nextInt(1000000000);
	}

	public float getPerlinNoise(float x, float z) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}

	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) (1f - Math.cos(theta)) * 0.5f;
		return Maths.lerp(a, b, f);
	}

	public float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)
				+ getNoise(x + 1, z - 1)) / 16f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}

	public float getNoise(int x, int z) {
		// big numbers for different seed
		random.setSeed(x * 983876 + z * 567243 + seed);
		return random.nextFloat() * 2f - 1f;
	}

}
