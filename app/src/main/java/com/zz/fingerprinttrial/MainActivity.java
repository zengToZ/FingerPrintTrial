package com.zz.fingerprinttrial;

import android.Manifest;
import android.accessibilityservice.FingerprintGestureController;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private static final String KEY = "key";
    private static int count = 0;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    final private static int REQUEST_ALL = 0XA1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if(!keyguardManager.isKeyguardSecure()){
            Toast.makeText(this,"Lock screen not enabled in setting",Toast.LENGTH_LONG).show();
        }

        /*---check all permissions--*/

        if(!PermissionCheck.hasPermissions(this, PermissionCheck.PERMISSIONS)){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                Toast.makeText(this,"Finger print permission not permitted",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, PermissionCheck.PERMISSIONS, REQUEST_ALL);
            }
        }

        if(!fingerprintManager.hasEnrolledFingerprints()){
            Toast.makeText(this,"Finger print not recorded in setting",Toast.LENGTH_LONG).show();
        }

        toAuthenticate(this);

        final ImageView imageView = findViewById(R.id.finger_img);
        final TextView textView = findViewById(R.id.message_text);
        imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "tap finger";
                for(int i=0;i<count;i++){
                    s = s + "! ";
                }
                textView.setText(s);
                toAuthenticate(MainActivity.this);
                count++;
            }
        });
        final ImageView signIn = findViewById(R.id.signin);
        signIn.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("dummy","to do");
                startActivity(intent);
            }
        });
    }

    public void toAuthenticate(Context context){
        generateKEY();
        if(cipherIni()){
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerPrintHelper fingerPrintHelper = new FingerPrintHelper(context);
            fingerPrintHelper.startAuthentication(fingerprintManager,cryptoObject);
        }
    }

    public boolean cipherIni(){
        try{
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+
                    KeyProperties.BLOCK_MODE_CBC+"/"
                    +KeyProperties.ENCRYPTION_PADDING_PKCS7);
        }catch(NoSuchAlgorithmException|NoSuchPaddingException e){
            throw new RuntimeException("fail to get cipher", e);
        }
        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return true;
        }catch(KeyPermanentlyInvalidatedException e){
            return false;
        }catch (CertificateException|IOException|NoSuchAlgorithmException
                |KeyStoreException|UnrecoverableKeyException|InvalidKeyException e){
            throw new RuntimeException("fail to get cipher", e);
        }
    }

    protected void generateKEY(){
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        }catch (KeyStoreException e){
            e.printStackTrace();
        }

        try{
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        }catch (NoSuchAlgorithmException| NoSuchProviderException e){
            throw new RuntimeException("fail to get keyGenerator instance", e);
        }

        try{
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
            keyGenerator.generateKey();
        }catch (IOException|NoSuchAlgorithmException|
                CertificateException|InvalidAlgorithmParameterException e){
            throw new RuntimeException("fail to generate key", e);
        }



    }

}
