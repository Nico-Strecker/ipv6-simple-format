import java.math.BigInteger;
import java.util.Locale;

/**
 * IPv6SimpleFormat (v6sf) implementation in Java
 * 
 * Version 0.0.1
 * 
 * This class provides methods to encode and decode IPv6 addresses
 * according to the IPv6 Simple Format specification.
 * 
 * Features:
 * - Base35 encoding (lowercase, without 'o')
 * - Checksum calculation and validation
 * - Optional block separation (4 chars per block)
 * - Prefix "v6sf:" handling
 * - Fully reversible mapping
 * 
 * Licensed under Apache License 2.0
 * 
 * @author Nico Strecker
 * @version 0.0.1
 */
public class IPv6SimpleFormat {

    private static final String PREFIX = "v6sf:";
    /* base35 */
    private static final String CHARSET = "0123456789abcdefghijklmnpqrstuvwxyz"; 
    private static final int BASE = CHARSET.length();
    /* for readability */
    private static final int BLOCK_SIZE = 4; 

    /**
     * Encodes a normalized IPv6 address (fully expanded) to v6sf format.
     * 
     * @param ipv6Expanded IPv6 address in fully expanded form (8 blocks of 4 hex digits)
     * @param addBlocks Whether to add colon separators every 4 chars
     * @return v6sf encoded string with checksum and prefix
     */
    public static String encode(String ipv6Expanded, boolean addBlocks) {
        if (ipv6Expanded == null) throw new IllegalArgumentException("IPv6 address cannot be null");
        
        /* Remove colons */
        String hex = ipv6Expanded.replace(":", "").toLowerCase(Locale.ROOT);
        if (hex.length() != 32) throw new IllegalArgumentException("IPv6 must be fully expanded (32 hex digits)");

        /* Convert to BigInteger */
        BigInteger value = new BigInteger(hex, 16);

        /* Encode to base35 string */
        StringBuilder encoded = new StringBuilder();
        BigInteger baseBig = BigInteger.valueOf(BASE);
        BigInteger current = value;
        while (current.compareTo(BigInteger.ZERO) > 0) {
            int remainder = current.mod(baseBig).intValue();
            encoded.insert(0, CHARSET.charAt(remainder));
            current = current.divide(baseBig);
        }

        /* Pad with '0' if necessary to have fixed length */
        int expectedLength = 25;
        while (encoded.length() < expectedLength) {
            encoded.insert(0, '0');
        }

        /* Calculate checksum */
        char checksum = calculateChecksum(encoded.toString());
        encoded.append(checksum);

        /* Add block separators if requested */
        if (addBlocks) {
            encoded = addBlockSeparators(encoded.toString());
        }

        return PREFIX + encoded.toString();
    }

    /**
     * Decodes a v6sf string into a fully expanded IPv6 address.
     * 
     * @param v6sf v6sf encoded string with prefix
     * @return Fully expanded IPv6 address
     */
    public static String decode(String v6sf) {
        if (v6sf == null || !v6sf.toLowerCase(Locale.ROOT).startsWith(PREFIX)) {
            throw new IllegalArgumentException("Input must start with prefix " + PREFIX);
        }

        /* Remove prefix and colons */
        String clean = v6sf.substring(PREFIX.length()).replace(":", "").toLowerCase(Locale.ROOT);
        if (clean.length() < 2) throw new IllegalArgumentException("Encoded value too short");

        /* Separate checksum */
        char checksumChar = clean.charAt(clean.length() - 1);
        String encoded = clean.substring(0, clean.length() - 1);

        /* Validate checksum */
        char expectedChecksum = calculateChecksum(encoded);
        if (checksumChar != expectedChecksum) {
            throw new IllegalArgumentException("Invalid checksum");
        }

        /* Decode base35 string to BigInteger */
        BigInteger value = BigInteger.ZERO;
        for (char c : encoded.toCharArray()) {
            int index = CHARSET.indexOf(c);
            if (index == -1) throw new IllegalArgumentException("Invalid character in encoded string: " + c);
            value = value.multiply(BigInteger.valueOf(BASE)).add(BigInteger.valueOf(index));
        }

        /* Convert BigInteger to hex string */
        String hex = value.toString(16);
        while (hex.length() < 32) {
            hex = "0" + hex;
        }

        /* Format into IPv6 blocks */
        StringBuilder ipv6 = new StringBuilder();
        for (int i = 0; i < 32; i += 4) {
            ipv6.append(hex, i, i + 4);
            if (i < 28) ipv6.append(":");
        }

        return ipv6.toString();
    }

    /* Calculates a simple checksum character for a given base35 string */
    private static char calculateChecksum(String encoded) {
        int sum = 0;
        for (char c : encoded.toCharArray()) {
            int index = CHARSET.indexOf(c);
            if (index == -1) throw new IllegalArgumentException("Invalid character for checksum: " + c);
            sum += index;
        }
        int checksumIndex = sum % BASE;
        return CHARSET.charAt(checksumIndex);
    }

    /* Adds block separators (colons) every BLOCK_SIZE characters */
    private static StringBuilder addBlockSeparators(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            sb.append(s.charAt(i));
            if ((i + 1) % BLOCK_SIZE == 0 && (i + 1) < len) {
                sb.append(":");
            }
        }
        return sb;
    }
}
