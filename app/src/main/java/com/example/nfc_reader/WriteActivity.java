package com.example.nfc_reader;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_reader.Utils.NfcUtils;

import java.io.IOException;
public class WriteActivity extends AppCompatActivity {
    private static String TAG="IC_Activity";
    private NfcAdapter Ic_NfcAdapter;
    private PendingIntent Ic_PendingIntent;
    private IntentFilter[] Filters;
    private String[][] TechList;
    int block_new=-1;
    private byte[] password={(byte)0x16,(byte)0x20,(byte)0x07,(byte)0x11,(byte)0x07,(byte)0x86};
    private TextView textView;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume启动");
        if(Ic_NfcAdapter!=null){
            Ic_NfcAdapter.enableForegroundDispatch(this,Ic_PendingIntent,Filters,TechList);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause启动");
        if(Ic_NfcAdapter!=null){
            Ic_NfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"onStop启动");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart启动");
        if(Ic_NfcAdapter!=null){
            Log.e(TAG,"onStart中Adapter存在");
            if(Ic_NfcAdapter.isEnabled()){
                Log.e(TAG,"your nfc is enable");
            }else{
                Log.e(TAG,"your nfc is not enable");
                Toast.makeText(WriteActivity.this,"请检测手机NFC是否开启",Toast.LENGTH_LONG).show();
            }
        }else{
            Log.e(TAG,"onStart中Adapter不存在");
            Toast.makeText(WriteActivity.this,"请检测手机是否有NFC功能",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy启动");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);
        Log.e(TAG,"onCreate启动");
        textView=findViewById(R.id.text2);
        editText1=findViewById(R.id.text_w_num);
        editText2=findViewById(R.id.text_w_block);
        editText3=findViewById(R.id.text_w_pass);
        Ic_NfcAdapter=NfcAdapter.getDefaultAdapter(this);
        Ic_PendingIntent=PendingIntent.getActivity(this,0,new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        IntentFilter ndef=new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try{
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        Filters=new IntentFilter[]{
                ndef,
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        };
        TechList=new String[][]{
                new String[]{NfcF.class.getName()},
                new String[]{MifareClassic.class.getName()},
                new String[]{NfcA.class.getName()},
                new String[]{NfcB.class.getName()},
                new String[]{NfcV.class.getName()}
        };
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG,"nfc功能正常......");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.e(TAG,"ACTION_NDEF_DISCOVERED功能正常");
        }else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
            Log.e(TAG,"ACTION_TECH_DISCOVERED功能正常");
            Tag detectedTAG=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mifareClassic=MifareClassic.get(detectedTAG);
            try{
                mifareClassic.connect();
                int num=Integer.parseInt(editText1.getText().toString().trim());
                String pass=editText3.getText().toString().trim();
                byte write[]=NfcUtils.StringYoByteArray(editText2.getText().toString().trim());
                boolean isopen;
                if(pass.equals("")){
                    isopen=mifareClassic.authenticateSectorWithKeyB(num,MifareClassic.KEY_DEFAULT);
                }else {
                    isopen=mifareClassic.authenticateSectorWithKeyB(num,NfcUtils.StringYoByteArray(pass));
                }
                textView.setText("");
                if(isopen){
                    int bCount = mifareClassic.getBlockCountInSector(num);
                    int bIndex = mifareClassic.sectorToBlock(num);
                    mifareClassic.writeBlock(bIndex+num,write);
                    textView.append("数据已写入，该扇区数据如下：\n");
                    for (int j = 0; j < bCount; j++) {
                        textView.append("存储器的位置 ===" + bIndex + "当前块 === " + (bIndex + j));
                        Log.e("onNewIntent:", "存储器的位置 ===" + bIndex + "当前块 === " + (bIndex + j));
                        byte[] data = mifareClassic.readBlock(bIndex + j);
                        textView.append(NfcUtils.ByteArrayToHexString(data)+"\n");
                        Log.e(TAG, NfcUtils.ByteArrayToHexString(data));
                    }
                }else {
                    textView.append("密码错误！！");
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
            Log.e(TAG,"ACTION_TAG_DISCOVERED功能正常");
        }
    }
}
