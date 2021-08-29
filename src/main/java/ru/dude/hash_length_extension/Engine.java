package ru.dude.hash_length_extension;

import java.security.MessageDigest;

/**
 * Основной механихм атаки
 *
 * @author Vladimir X
 * Date: 29.08.2021
 */
public class Engine {


    static void attack(byte[] firstLineDigest, int firsLineWithSecretLength, String firstLine, String curseSuffix) throws Exception {
        makeCursedLine(firstLine, firsLineWithSecretLength, curseSuffix);
        makeCursedDigest(firstLineDigest,firsLineWithSecretLength,curseSuffix);
    }

    static void calcBySecret(String secret, String input) throws Exception {
        byte[] digestWithTailAndCurse = calcDigest(secret.getBytes(), input.getBytes());
        System.out.println("digest by secret: " + Util.toHex(digestWithTailAndCurse));
    }

    static void makeCursedLine(String firstLine, int firsLineWithSecretLength, String curseSuffix) {
        byte[] tail = makeTail(firsLineWithSecretLength);
        String tailStr = Util.toHex(tail);
        String cursedLine = firstLine + tailStr + curseSuffix;
        System.out.println("cursedLine: " + cursedLine);
    }

    static void makeCursedDigest(byte[] firstLineDigest, int firsLineWithSecretLength, String curseSuffix) throws Exception {
        byte[] digestByFldAndCurse = calcDigestByPrev(firstLineDigest, firsLineWithSecretLength, curseSuffix.getBytes());
        System.out.println("cursed digest: " + Util.toHex(digestByFldAndCurse));
    }

    /**
     * вычислить хвост, который необходимо дописать между известной частью сообщения и добавляемым суффиксом
     * @param dataSize количество символов в известной части сообщения
     */
    static byte[] makeTail(int dataSize){
        int tailSize = 64 - dataSize % 64;

        int smallB = 0;
        if (tailSize < 9) {
            // добить блок нулями и сделать новый блок
            smallB = tailSize;
            tailSize = 64;
        }

        byte[] res = new byte[smallB + tailSize];
        res[0] = (byte) 0x80;

        // в конце блока -размер блока данных в битах
        Util.toArrayBigEndian(dataSize * 8, res, res.length - 1);

        return res;
    }

    /**
     * Посчитать хэш
     * @param inputs блоки с данными, хэш которых надо посчитать
     */
    static byte[] calcDigest(byte[] ... inputs)throws Exception{
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        if (inputs != null) {
            for (byte[] input : inputs) {
                if (input!=null) {
                    digest.update(input);
                }
            }
        }
        return digest.digest();
    }

    /**
     * Посчитать хэш по значению предыдущего хэша
     *
     * Вычисляет новый хэш,
     *
     * @param previousDigest хэш исходного сообщения
     * @param firsLineLength длина исходного сообщения (секрет + сообщение)
     * @param additionInputs дополнительные блоки сообщения
     * @return
     * @throws Exception
     */
    static byte[] calcDigestByPrev(byte[] previousDigest, int firsLineLength, byte[] ... additionInputs) throws Exception {

        int blockCount = blockCount(firsLineLength);

        // инициализация
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        // получения долступа до массива state
        Object digestSpi = Util.getFieldPrivate(digest, "digestSpi");
        int[] state = (int[]) Util.getFieldPrivate(digestSpi,"state");

        // инициализация state
        Util.b2iBig(previousDigest, 0, state, 0, 5);
        // инициализация количества обработанных блоков
        Util.setFieldPrivate(digestSpi,"bytesProcessed",blockCount * 64); // Blocksize of the algorithm in bytes.

        // добавление сроки
        if (additionInputs != null) {
            for (byte[] input : additionInputs) {
                if (input != null) {
                    digest.update(input);
                }
            }
        }

        return digest.digest();
    }


    /**
     * посчитать количество блоков по 64 байта (512 бит)
     * @param dataSize
     * @return
     */
    static int blockCount(int dataSize) {
        // количество полных блоков, +  дополняемый в tail блок
        int fullBlocks = dataSize / 64  + 1 ;
        int tailSize = 64 - dataSize % 64;
        if (tailSize < 9) {
            // если tail слишком короткий то надо добавить дополнительный блок для записи длины
            fullBlocks += 1;
        }
        return fullBlocks;
    }

}
