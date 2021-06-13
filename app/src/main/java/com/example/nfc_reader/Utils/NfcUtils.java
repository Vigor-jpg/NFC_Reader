package com.example.nfc_reader.Utils;

public class NfcUtils {
    public static byte[] StringYoByteArray(String str){
        byte[] array=new byte[str.length()/2];
        for(int i=0;i<str.length();i=i+2){
            array[i/2]=(byte)Integer.parseInt(str.substring(i,i+2),16);
        }
        return array;
    }
    public static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    public static byte[] MsgChange(byte[] array){
        array[0]=(byte)(array[0]+10);
        System.out.println(ByteArrayToHexString(array));
        array[4]=(byte)(array[4]-10);
        System.out.println(ByteArrayToHexString(array));
        array[8]=(byte)(array[8]+10);
        System.out.println(ByteArrayToHexString(array));
        return array;
    }
}
