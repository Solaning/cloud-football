package com.kinth.football.chat.util;

import java.util.UUID;

public class ShortUUID {
	private final static char[] chars = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
		'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z', '-', '_' };
	private final static byte[] revertChars = new byte[255];
	
	static {
		for (int i = 0; i < chars.length; ++i) {
			revertChars[chars[i]] = (byte) i;
		}
	}
	
	private final static int radix = 64;
	
	public static String toUUID(String shortUuid) {
		if (shortUuid.length() < 22)
			throw new IllegalArgumentException("Invalid Short UUID string: " + shortUuid);
		
		char[] uuidChars = new char[22]; 
		shortUuid.getChars(0, 22, uuidChars, 0);
		int mask = radix - 1;
		
		long most = 0;
		most |= (revertChars[uuidChars[0]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[1]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[2]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[3]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[4]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[5]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[6]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[7]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[8]] & mask); most <<= 6;
		most |= (revertChars[uuidChars[9]] & mask); most <<= 4;
		
		byte b = revertChars[uuidChars[10]];
		most |= ((b >>> 2) & 0x0F);
		
		long least = 0;
		least |= (b & 0x03); least <<= 6;
		least |= (revertChars[uuidChars[11]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[12]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[13]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[14]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[15]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[16]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[17]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[18]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[19]] & mask); least <<= 6;
		least |= (revertChars[uuidChars[20]] & mask); least <<= 2;
		least |= (revertChars[uuidChars[21]] & 0x03);
		
		return new UUID(most, least).toString();
	}
	
	public static String toShortUUID(String uuid) {
		return toShotUUID(UUID.fromString(uuid));
	}
	
	public static String randomShortUUID() {
		return toShotUUID(UUID.randomUUID());
	}
	
	private static String toShotUUID(UUID uuid) {
		StringBuilder sb = new StringBuilder();
		
		int mask = radix - 1;
		long most = uuid.getMostSignificantBits();
		sb.append(chars[(int) (most >>> 58) & mask]);
		sb.append(chars[(int) (most >>> 52) & mask]);
		sb.append(chars[(int) (most >>> 46) & mask]);
		sb.append(chars[(int) (most >>> 40) & mask]);
		sb.append(chars[(int) (most >>> 34) & mask]);
		sb.append(chars[(int) (most >>> 28) & mask]);
		sb.append(chars[(int) (most >>> 22) & mask]);
		sb.append(chars[(int) (most >>> 16) & mask]);
		sb.append(chars[(int) (most >>> 10) & mask]);
		sb.append(chars[(int) (most >>> 4)  & mask]);
		
		long least = uuid.getLeastSignificantBits();
		sb.append(chars[(int) (((most & 0x0F) << 2) | (least >>> 62)) & mask]);
		sb.append(chars[(int) (least >>> 56)  & mask]);
		sb.append(chars[(int) (least >>> 50)  & mask]);
		sb.append(chars[(int) (least >>> 44)  & mask]);
		sb.append(chars[(int) (least >>> 38)  & mask]);
		sb.append(chars[(int) (least >>> 32)  & mask]);
		sb.append(chars[(int) (least >>> 26)  & mask]);
		sb.append(chars[(int) (least >>> 20)  & mask]);
		sb.append(chars[(int) (least >>> 14)  & mask]);
		sb.append(chars[(int) (least >>> 8 )  & mask]);
		sb.append(chars[(int) (least >>> 2)  & mask]);
		sb.append(chars[(int) (least & 0x03)]);
		
		return sb.toString();
	}
}
