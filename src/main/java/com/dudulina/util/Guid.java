package com.dudulina.util;

import java.util.Random;

public class Guid
{
    public static String generate()
    {
        byte[] bytes = new byte[20];
        new Random().nextBytes(bytes);
        return bin2hex(bytes);
    }

    public static String bin2hex(byte[] in)
    {
        StringBuilder sb = new StringBuilder(in.length * 2);
        for (byte b : in) {
            sb.append(
                forDigit((b & 0xF0) >> 4)
            ).append(
                forDigit(b & 0xF)
            );
        }
        return sb.toString();
    }

    public static char forDigit(int digit)
    {
        if (digit < 10) {
            return (char) ('0' + digit);
        }
        return (char) ('A' - 10 + digit);
    }
}
