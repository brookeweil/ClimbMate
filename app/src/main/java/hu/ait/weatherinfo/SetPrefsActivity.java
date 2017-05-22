package hu.ait.weatherinfo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.UUID;

import io.realm.Realm;

public class SetPrefsActivity extends AppCompatActivity {

    private static final int MIN_TEMP = 32;
    private static final int MAX_TEMP = 100;

    Button   btnSave;
    Button   btnCancel;
    EditText etTemp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_prefs);

        setupButtonsAndListeners();
        Intent intent = getIntent();
        String curPrefTemp = (intent.getStringExtra(PlacesListActivity.CUR_TEMP));
        etTemp.setHint(curPrefTemp);

    }

    private void setupButtonsAndListeners() {
        btnSave   = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etTemp    = (EditText) findViewById(R.id.etTemp);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTemp();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void cancel() {
        Intent intentResult = new Intent();
        setResult(PlacesListActivity.RESULT_CANCEL, intentResult);
        finish();
    }

    private void saveTemp() {

        int temp = Integer.parseInt(etTemp.getText().toString());
        if (temp > 100 || temp < 32) {
            etTemp.setError(getString(R.string.temp_error));
            return;
        }

        Intent intentResult = new Intent();
        intentResult.putExtra(PlacesListActivity.NEW_TEMP, temp);
        setResult(PlacesListActivity.TEMP_OK, intentResult);

        finish();
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

}
