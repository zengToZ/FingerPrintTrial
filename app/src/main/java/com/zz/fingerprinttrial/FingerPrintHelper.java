package com.zz.fingerprinttrial;

import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.TextView;
import android.widget.Toast;

public class FingerPrintHelper extends FingerprintManager.AuthenticationCallback {
    private CancellationSignal cancellationSignal;
    private Context context;
    private TextView textView;

    public FingerPrintHelper(Context context){
        this.context = context;
        textView = (TextView)((Activity)context).findViewById(R.id.message_text);
    }

    public void startAuthentication(FingerprintManager fingerprintManager,
                                    FingerprintManager.CryptoObject cryptoObject){
        cancellationSignal = new CancellationSignal();

        if(PermissionCheck.hasPermissions(context, PermissionCheck.PERMISSIONS))
            fingerprintManager.authenticate(cryptoObject,cancellationSignal,0,this,null);
        else
            Toast.makeText(context,"Finger print permission not permitted",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationError(int errorMsgID, CharSequence errorMsg){
        Toast.makeText(context,"onAuthenticationError:\n" + errorMsg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpID, CharSequence help){
        Toast.makeText(context,"onAuthenticationHelp:\n" + help,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed(){
        textView.setText("failed");
        Toast.makeText(context,"onAuthenticationFailed" ,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult){
        textView.setText("Approved");
        Toast.makeText(context,"onAuthenticationSucceeded:\n" + authenticationResult.toString() ,Toast.LENGTH_LONG).show();
    }

}
