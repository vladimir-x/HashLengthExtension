package ru.dude.hash_length_extension;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

/**
 * Вспомогательные фукнции
 *
 * @author Vladimir X
 * Date: 29.08.2021
 */
public class Util {

    /**
     * получить значение приватного поля
     */
    static Object getFieldPrivate(Object o, String fieldName) throws Exception{
        Class c = o.getClass();
        while (!c.getName().equals(Object.class.getName())) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getName().equals(fieldName)){
                    f.setAccessible(true);
                    return f.get(o);
                }
            }
            c = c.getSuperclass();
        }
        return null;
    }

    /**
     * установить значение приватного поля
     */
    static void setFieldPrivate(Object o, String fieldName, Object value) throws Exception {
        Class c = o.getClass();
        while (!c.getName().equals(Object.class.getName())) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getName().equals(fieldName)){
                    f.setAccessible(true);
                    f.set(o,value);
                    return;
                }
            }
            c = c.getSuperclass();
        }
    }

    /**
     * представление массива int в hex
     */
    static String toHex(byte[] input){
        StringBuilder sb = new StringBuilder();
        for (byte b : input) {
            sb.append(String.format("\\x%02X", b));
        }
        return sb.toString();
    }

    static byte[] fromHex(String input){
        ByteBuffer bf = ByteBuffer.allocate(input.length()/4);

        for (int i = 0; i < input.length() - 4; ++i) {
            if (input.charAt(i) == '\\' && input.charAt(i + 1) == 'x') {
                byte x = (byte) Integer.parseInt(input.substring(i + 2, i + 4),16);
                bf.put(x);
                System.out.println(x);
                i+=3;
            }
        }

        return bf.array();
    }

    /**
     * Запись массива byte в массив int с представлением в BigEndian
     */
    static void b2iBig(byte[] in, int inOfs, int[] out, int outOfs, int len) {
        len += outOfs;
        while (outOfs < len) {
            out[outOfs] = fromArrayBigEndian(in, inOfs);
            inOfs += 4;
            outOfs++;
        }
    }

    /**
     * Вычитывает int из массива в BIG_ENDIAN
     * @param payload массив с данными
     * @param offset откуда читать
     * @return int
     */
    static int fromArrayBigEndian(byte[] payload, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(payload, offset, 4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getInt();
    }

    /**
     * запись числа в виде BigEndian
     *
     * например: 56 -> 0x00 0x00 0x00 0x00 0x00 0x00 0x08 0x30
     *
     * @param x число
     * @param out массив для записи
     * @param firstPos первая позиция, с которой производить запись
     */
    static void toArrayBigEndian(long x, byte[] out, int firstPos) {
        while (x > 0) {
            out[firstPos] = (byte) (x % 256);
            x /= 256;
            firstPos--;
        }
    }
}
