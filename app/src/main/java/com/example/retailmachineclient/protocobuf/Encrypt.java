package com.example.retailmachineclient.protocobuf;


import com.example.retailmachineclient.util.Logger;

import java.util.Random;

/**
 * 作用：加解密
 * 注：但凡在涉及字节运算的时候，需要先把无符号的字节转换成有符号的整型
 * eg:Byte.toUnsignedInt(pCode[pnCode])
 * byte的范围在-128~127
 *
 *android api>26
 */
public class Encrypt {

    private static byte[] Txt_Encrypt;

    public static byte[] getTxt_Encrypt() {
        return Txt_Encrypt;
    }


    public static void setTxt_Encrypt(byte[] txt_Encrypt) {
        Txt_Encrypt = txt_Encrypt;
    }

    public static Integer byteToUnsignedInt(byte data) {
        return data & 0xff;
    }



    public static int nPrimes = 168;
        public static int[] Primes = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
                31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
                73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
                127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
                179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
                233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
                283, 293, 307, 311, 313, 317, 331, 337, 347, 349,
                353, 359, 367, 373, 379, 383, 389, 397, 401, 409,
                419, 421, 431, 433, 439, 443, 449, 457, 461, 463,
                467, 479, 487, 491, 499, 503, 509, 521, 523, 541,
                547, 557, 563, 569, 571, 577, 587, 593, 599, 601,
                607, 613, 617, 619, 631, 641, 643, 647, 653, 659,
                661, 673, 677, 683, 691, 701, 709, 719, 727, 733,
                739, 743, 751, 757, 761, 769, 773, 787, 797, 809,
                811, 821, 823, 827, 829, 839, 853, 857, 859, 863,
                877, 881, 883, 887, 907, 911, 919, 929, 937, 941,
                947, 953, 967, 971, 977, 983, 991, 997 };


        /*
        Encrypt bytes in [pSrc, pSrc + lenSrc) to [pTar, pTar + lenTar).
        Return false when (lenTar < lenSrc - 4) or !pSrc or !pTar or lenSrc <= 0 or lenTar <= 0.
        Otherwise fill the target buffer with encrypted bytes and return true.
        */

        public static boolean Txt_Encrypt(byte[] pSrc, int lenSrc,
                                       byte[] pTar, int lenTar)
        {
            if (pSrc == null || lenSrc <= 0 || pTar == null) { return false; }
            if (lenTar < lenSrc + 4 || lenSrc <= 0)
            {
                // one char index, two chars check
                return false;
            }
            byte[] pTxt = (byte[])pSrc;
            byte[] pCode = (byte[])pTar;

            int ms = 0;
            int aa = 0;
            int bb = 0;
            int cc = 0;
            //SYSTEMTIME st;
            //::GetSystemTime(&st);
            //ms = st.wMilliseconds;
           // ms = DateTime.Now.Millisecond % 1000;
           // ms=new Random().nextInt(10);
            ms=10;
            aa = 0;
            bb = nPrimes - 1;

            int pnCode = 0;
            int pnTxt = 0;
            while (bb > aa + 1)
            {
                if (ms == Primes[aa])
                {
                    cc = aa;
                    bb = aa;
                    break;
                }
                else if (ms == Primes[bb])
                {
                    cc = bb;
                    aa = bb;
                    break;
                }
                cc = (aa + bb) >> 1;
                if (ms == Primes[cc])
                {
                    aa = bb = cc;
                    break;
                }
                else if (ms > Primes[cc])
                {
                    aa = cc;
                }
                else
                {
                    bb = cc;
                }
            }
            if (aa != bb)
            {
                if (ms - Primes[aa] > Primes[bb] - ms)
                {
                    cc = bb;
                }
                else
                {
                    cc = aa;
                }
            }
            bb = 0;
            for (aa = 0; aa < lenSrc; ++aa)
            {
                pCode[pnCode] = (byte)((pTxt[pnTxt] + Primes[cc]) & 0xFF);
                //bb += (getUnsigned(pCode[pnCode])) * (getUnsigned(pCode[pnCode]));

                bb+= (byteToUnsignedInt(pCode[pnCode]) * byteToUnsignedInt(pCode[pnCode]));

                pCode[pnCode] = (byte)(((byteToUnsignedInt(pCode[pnCode])) + ((Primes[(cc + aa) % nPrimes] *
                        Primes[(cc + aa) % nPrimes]) & 0xFF)) & 0xFF);
                bb &= 0xFFFF;
                ++pnCode;
                ++pnTxt;
                cc = (cc + 1) % nPrimes;
            }

            pCode[pnCode] = (byte)(ms & 0xff);
            pCode[pnCode + 1] = (byte)((ms >> 8) & 0xff);
            pnCode += 2;

            pCode[pnCode] = (byte)(bb & 0xff);
            pCode[pnCode + 1] = (byte)((bb >> 8) & 0xff);
            pnCode += 2;
            for (aa += 4; aa < lenTar; ++aa) {
                bb += (byteToUnsignedInt(pCode[pnCode - 4]) * byteToUnsignedInt(pCode[pnCode - 4]));
                bb &= 0xFFFF;

                pCode[pnCode] = (byte)((bb >> 8) ^ (bb & 0xFF) ^ (Primes[(cc + aa) % nPrimes] & 0xFF));
                ++pnCode;
            }
            setTxt_Encrypt(pCode);
            return true;
        }

        /*
        Encrypt two segments of bytes from [pSrc1, pSrc1 + len1) and [pSrc2, pSrc2 + len2)
        to [pTar, pTar + lenTar).
        Return false when  !pSrc1 or !pSrc2 orr !pTar or len1 <= 0 or len2 <= 0
        or (lenTar < len1 + len2 + 4).
        Otherwise fill the target buffer with encrypted bytes and return true.
        */
        public static boolean Txt_Encrypt2(byte[] pSrc1, int len1,
                                        byte[] pSrc2, int len2,
                                        byte[] pTar, int lenTar)
        {
            if (pSrc1 == null || len1 <= 0 || pSrc2 == null || len2 <= 0) { return false; }
            if (pTar == null || lenTar < len1 + len2 + 4)
            {
                // one char index, two chars check
                return false;
            }
            int lenTxt = len1 + len2;
            byte[] pTxt1 = (byte[])pSrc1;
            byte[] pTxt2 = (byte[])pSrc2;
            byte[] pCode = (byte[])pTar;

            int ms = 0;
            int aa = 0;
            int bb = 0;
            int cc = 0;
            //SYSTEMTIME st;
            //::GetSystemTime(&st);
            //ms = st.wMilliseconds;
            //ms = DateTime.Now.Millisecond % 1000;
            ms=new Random().nextInt(10);
            aa = 0;
            bb = nPrimes - 1;


            int pnCode = 0;
            int pnTxt = 0;
            while (bb > aa + 1)
            {
                if (ms == Primes[aa])
                {
                    cc = aa;
                    bb = aa;
                    break;
                }
                else if (ms == Primes[bb])
                {
                    cc = bb;
                    aa = bb;
                    break;
                }
                cc = (aa + bb) >> 1;
                if (ms == Primes[cc])
                {
                    aa = bb = cc;
                    break;
                }
                else if (ms > Primes[cc])
                {
                    aa = cc;
                }
                else
                {
                    bb = cc;
                }
            }
            if (aa != bb)
            {
                if (ms - Primes[aa] > Primes[bb] - ms)
                {
                    cc = bb;
                }
                else
                {
                    cc = aa;
                }
            }
            byte[] pTxt = pTxt1;
            bb = 0;
            for (aa = 0; aa < lenTxt; ++aa)
            {
                pCode[pnCode] = (byte)(((int)(pTxt[pnTxt]) + Primes[cc]) & 0xFF);
                bb += (pCode[pnCode]) * (pCode[pnCode]);
                pCode[pnCode] = (byte)((pCode[pnCode] + ((Primes[(cc + aa) % nPrimes] *
                        Primes[(cc + aa) % nPrimes]) & 0xFF)) & 0xFF);
                bb &= 0xFFFF;
                ++pnCode;
                ++pnTxt;
                cc = (cc + 1) % nPrimes;
                if (len1 - 1 == aa)
                {
                    pTxt = pTxt2;
                }
            }

            pCode[pnCode] = (byte)(ms & 0xff);
            pCode[pnCode + 1] = (byte)((ms >> 8) & 0xff);
            pnCode += 2;


            pCode[pnCode] = (byte)(bb & 0xff);
            pCode[pnCode + 1] = (byte)((bb >> 8) & 0xff);
            pnCode += 2;
            for (aa += 4; aa < lenTar; ++aa) {
                bb += (pCode[-4] * pCode[-4]);
                bb &= 0xFFFF;

                pCode[pnCode] = (byte)((bb >> 8) ^ (bb & 0xFF) ^ (Primes[(cc + aa) % nPrimes] & 0xFF));
                ++pnCode;
            }
            return true;
        }
        /*
        Decrypt bytes in [pSrc, pSrc + lenSrc) to [pTar, pTar + lenTar)
        Return false when !pSrc or !pTar or (lenTar <= lenSrc - 4) or DECRYPTION FAILED
        Otherwise fill the target buffer with decrypted bytes and return true
        */
        public static boolean Txt_Decrypt(byte[] pSrc, int lenSrc,
                                       byte[] pTar, int lenTar)
        {
            int x = 0, aa = 0, bb = 0, cc = 0, c_ = 0;
            byte[] p1;
            char val = (char)0;
            if (pSrc == null || lenSrc <= 0 || pTar == null || lenSrc < lenTar + 4)
            {
                // one unsigned char index, two unsigned chars check
                Logger.e("解码错误");
                return false;
            }
            byte[] pCode = (byte[])pSrc;
            byte[] pTxt = (byte[])pTar;



            int pnCode = 0;
            int pnTxt = 0;

            x = ((byteToUnsignedInt(pCode[pnCode + lenTar + 1] )<< 8) + byteToUnsignedInt(pCode[pnCode + lenTar]));

            aa = 0;
            bb = nPrimes - 1;
            while (bb > aa + 1)
            {
                if (x == Primes[aa])
                {
                    cc = aa;
                    bb = aa;
                    break;
                }
                else if (x == Primes[bb])
                {
                    cc = bb;
                    aa = bb;
                    break;
                }
                cc = (aa + bb) >> 1;
                if (x == Primes[cc])
                {
                    aa = bb = cc;
                    break;
                }
                else if (x > Primes[cc])
                {
                    aa = cc;
                }
                else
                {
                    bb = cc;
                }
            }
            if (aa != bb)
            {
                if (x - Primes[aa] > Primes[bb] - x)
                {
                    cc = bb;
                }
                else
                {
                    cc = aa;
                }
            }

            c_ = cc;
            bb = 0;
            for (aa = 0; aa < lenTar; ++aa)
            {
                val = (char)((pCode[pnCode] - ((Primes[(cc + aa) % nPrimes] * Primes[(cc + aa) % nPrimes]) & 0xFF)) & 0xff);     //  ??是否需要转换成无符号
                bb += val * val;
                bb &= 0xFFFF;
                cc = (cc + 1) % nPrimes;
                ++pnCode;
            }

           /* if (((Byte.toUnsignedInt(pCode[pnCode + 2 + 1])<< 8) + (Byte.toUnsignedInt(pCode[pnCode + 2]))) != bb) {
                Logger.e("----42525:"+((int)(pCode[pnCode + 2 + 1] << 8) + (int)(pCode[pnCode + 2])));
                Logger.e("解码错误");
                return false;
            }*/

            pnCode += 4;
            for (aa += 4; aa < lenSrc; ++aa) {
                bb += (byteToUnsignedInt(pCode[pnCode-4]) * byteToUnsignedInt(pCode[pnCode-4]));
                bb &= 0xFFFF;
                if (pCode[pnCode] != (byte)((bb >> 8) ^ (bb & 0xFF) ^
                        (Primes[(cc + aa) % nPrimes] & 0xFF))) {
                    Logger.e("解码错误");
                    return false;
                }
                ++pnCode;
            }

            cc = c_;
            pnCode = 0;
            for (aa = 0; aa < lenTar; ++aa) {
                val = (char)((pCode[pnCode] - ((Primes[(cc + aa) % nPrimes] *
                        Primes[(cc + aa) % nPrimes]) & 0xFF))&0xff);

                pTxt[pnTxt] = (byte)(val - (Primes[cc] & 0xFF));
                cc = (cc + 1) % nPrimes;
                ++pnCode;
                ++pnTxt;
            }
            return true;
        }


}
