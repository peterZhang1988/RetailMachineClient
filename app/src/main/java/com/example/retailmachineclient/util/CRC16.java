package com.example.retailmachineclient.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* * 校验码：CRC16占用两个字节，包含了一个 16 位的二进制值。CRC 值由传输设备计算出来，
 * 然后附加到数据帧上，接收设备在接收数据时重新计算 CRC 值，然后与接收到的 CRC 域中的值
 * 进行比较，如果这两个值不相等，就发生了错误。
 *
 *
 * 生成一个 CRC16 的流程为：
 * (1) 预置一个 16 位寄存器为 0xFFFF（全为1），称之为 CRC 寄存器。
 * (2) 把数据帧中的第一个字节的 8 位与 CRC 寄存器中的低字节进行异或运算，结果存回 CRC 寄存器。
 * (3) 将 CRC 寄存器向右移一位，最高位填以 0，最低位移出并检测。
 * (4) 如果最低位为 0：重复第三步（下一次移位）；如果最低位为 1，将 CRC 寄存器与一个预设的固定值 0xA001 进行异或运算。
 * (5) 重复第三步和第四步直到 8 次移位。这样处理完了一个完整的八位。
 * (6) 重复第 2 步到第 5 步来处理下一个八位，直到所有的字节处理结束。
 * (7) 最终 CRC 寄存器的值就是 CRC16 的值。
 */
public class CRC16 {

    /**
     * 一个字节包含位的数量 8
     */
    private static final int BITS_OF_BYTE = 8;

    /**
     * 多项式 8005
     */
    private static final int POLYNOMIAL = 0x8005;

    /**
     * 初始值  CRC 寄存器
     */
    private static final int INITIAL_VALUE = 0xFFFF;

    /**
     * CRC16 编码
     *
     * @param bytes 编码内容
     * @return 编码结果
     */
    public static byte[] crc16(byte[] bytes) {
        //Logger.e("-----编码内容:"+Utils.byteBufferToHexString(bytes));
        System.out.println(Utils.byteBufferToHexString(bytes));
        int res = INITIAL_VALUE;
        for (int data : bytes) {
            res = res ^ data;
            for (int i = 0; i < BITS_OF_BYTE; i++) {
                res = (res & 0x0001) == 1 ? (res >> 1) ^ POLYNOMIAL : res >> 1;
            }
        }
        int data=revert(res);
        byte[] buf=new byte[2];
        buf[0]=(byte)(data >>8&0xff);
        buf[1]=(byte)(data&0xff);
        System.out.println(Utils.byteBufferToHexString(buf));
        return buf;
    }

    public static byte[] crc16New(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        int data=revert(CRC);
        byte[] buf=new byte[2];
        buf[0]=(byte)(data >>8&0xff);
        buf[1]=(byte)(data&0xff);
        System.out.println(Utils.byteBufferToHexString(buf));
        return buf;
    }
    public static void GetCRC_MODBUS(byte[] bytes) {
//        byte[] bytes = toBytes(str);
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        System.out.println("翻转前="+CRC);
        int data=revert(CRC);
        System.out.println("翻转后="+data);
        System.out.println("再翻转="+revert(data));
        byte[] buf=new byte[2];
        buf[0]=(byte)(data >>8&0xff);
        buf[1]=(byte)(data&0xff);
        System.out.println(Utils.byteBufferToHexString(buf));
//        System.out.println(crc);
    }

    /**
     * 功能： 十六进制字符串转字节数组
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    private static byte[] convertHexStringToBytes(String hexString){
        //判空
        if(hexString == null || hexString.length() == 0) {
            return null;
        }

        //合法性校验
        if(!hexString.matches("[a-fA-F0-9]*") || hexString.length() % 2 != 0) {
            return null;
        }

        //计算
        int mid = hexString.length() / 2;
        byte[]bytes = new byte[mid];
        for (int i = 0; i < mid; i++) {
            System.out.println("---------:"+hexString.substring(i * 2, i * 2 + 2));
            bytes[i] = (byte)Integer.valueOf(hexString.substring(i * 2, i * 2 + 2), 16).intValue();
        }

        return bytes;
    }


    /**
     * 翻转16位的高八位和低八位字节
     *
     * @param src 翻转数字
     * @return 翻转结果
     */
    private static int revert(int src) {
        int lowByte = (src & 0xFF00) >> 8;
        int highByte = (src & 0x00FF) << 8;
        return lowByte | highByte;

    }




    private static String convertToHexString(int src) {
        return Integer.toHexString(src);
    }

    public static void mainpp(String[] args) {

//        0003011E0005C50000042600000000000000 6D36
        //00 03 01 1E 00 05 C5 00 00 04 26 00000000000000
        byte[] data = new byte[]{0x00,0x03,0x01,0x1E,0x00,0x05, (byte) 0xC5,0x00,0x00,0x04,0x26,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

        //0005 0000000000000000000000000000000062B5
//        byte[] data = new byte[]{0x00,0x05,0x00,0x00,0x00,0x00, (byte) 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        GetCRC_MODBUS(data);
//        for (int i=0;i<result.length;i++){
//            System.out.println(result[i]);
//        }
//        byte[] rsp=new byte[20];
//        Utils.putBytes(rsp,0,data,data.length);
//        Utils.putBytes(rsp,18,result,2);
//        System.out.println(Utils.byteBufferToHexString(rsp));
//        int b = 0x0201;
//
//        // 将16位的高8位转换为低8位
//        int lowByte = (b & 0xFF00) >> 8;
//        System.out.println(lowByte);
//
//        // 将16位的低8位转换为高8位
//        int highByte = (b & 0x00FF) << 8;
//        System.out.println(highByte);
//
//        // 按位或运算，将两个数相加
//        int c = lowByte | highByte;
//        System.out.println(c);
//
//        boolean is=(byte)0x49==(byte) 73;
//        System.out.println(is);
    }

//    public static void mainOld(String[] args) {
//
////        0003011E0005C50000042600000000000000 6D36
//        //00 03 01 1E 00 05 C5 00 00 04 26 00000000000000
//        byte[] data = new byte[]{0x00,0x03,0x01,0x1E,0x00,0x05, (byte) 0xC5,0x00,0x00,0x04,0x26,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
//        byte[] result=CRC.crc16New(data);
//        for (int i=0;i<result.length;i++){
//            System.out.println(result[i]);
//        }
//        byte[] rsp=new byte[20];
//        Utils.putBytes(rsp,0,data,data.length);
//        Utils.putBytes(rsp,18,result,2);
//        System.out.println(Utils.byteBufferToHexString(rsp));
//        int b = 0x0201;
//
//        // 将16位的高8位转换为低8位
//        int lowByte = (b & 0xFF00) >> 8;
//        System.out.println(lowByte);
//
//        // 将16位的低8位转换为高8位
//        int highByte = (b & 0x00FF) << 8;
//        System.out.println(highByte);
//
//        // 按位或运算，将两个数相加
//        int c = lowByte | highByte;
//        System.out.println(c);
//
//        boolean is=(byte)0x49==(byte) 73;
//        System.out.println(is);
//    }

        public static void main(String[] args) {

            InetAddress add= null;//获得本机的InetAddress对象
            try {
                add = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            System.out.println(add.getHostAddress());//返回本机IP地址
            System.out.println(add.getHostName());//输出计算机名




    }
}


