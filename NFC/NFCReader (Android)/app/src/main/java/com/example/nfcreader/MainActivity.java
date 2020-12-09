package com.example.nfcreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfcreader.Response.TambahUserResponse;
import com.example.nfcreader.RestApi.ApiClient;
import com.example.nfcreader.RestApi.RestApi;

import java.math.BigInteger;
import java.util.prefs.Preferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView textUID;
    private Button saveBtn;

    private final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textUID = findViewById(R.id.uid);
        saveBtn = findViewById(R.id.simpan_button);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDatabase();
            }
        });
    }

    private void saveDatabase() {
        if (!validateUid()){
            return;
        }
        String getNumber = textUID.getText().toString().split(":")[1];
        RestApi apiService = ApiClient.getClient().create(RestApi.class);
        Call<TambahUserResponse> tambahUserResponseCall = apiService.postTambahUser(getNumber);
        tambahUserResponseCall.enqueue(new Callback<TambahUserResponse>() {
            @Override
            public void onResponse(Call<TambahUserResponse> call, Response<TambahUserResponse> response) {
                TambahUserResponse tambahUserResponse = response.body();
                assert tambahUserResponse != null;
                if (tambahUserResponse.isStatus()){
                    Toast.makeText(getApplicationContext(),response.message(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),tambahUserResponse.getMessage(), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<TambahUserResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean validateUid() {
        String inputUid = textUID.getText().toString();

        if (inputUid.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Scan NFC terlebih dahulu", Toast.LENGTH_LONG).show();
            return false;
        } else {

            return true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

            textUID.setText("NFC UID : " +
                    ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
        }
    }

    private String ByteArrayToHexString(byte [] inarray) {
        return String.format("%0" + (inarray.length * 2) + "X", new BigInteger(1,inarray));
    }

}