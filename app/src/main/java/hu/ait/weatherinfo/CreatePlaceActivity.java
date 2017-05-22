package hu.ait.weatherinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.realm.Realm;

public class CreatePlaceActivity extends AppCompatActivity {

    public static final int INTENT_MAP = 987;
    public static final int RESULT_CANCEL = 666;
    public static final int RESULT_OK = 555;

    public static final String NEWLAT = "NEWLAT";
    public static final String NEWLON = "NEWLON";

    Button   btnSave;
    Button   btnCancel;
    EditText etPlaceName;
    EditText etLat;
    EditText etLon;
    Button btnAddFromMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_place);

        setupButtons();
        setupListeners();
        Intent intent = getIntent();

    }

    private void setupButtons() {
        btnSave       = (Button) findViewById(R.id.btnSave);
        btnCancel     = (Button) findViewById(R.id.btnCancel);
        btnAddFromMap = (Button) findViewById(R.id.btnMapAdd);

        etPlaceName = (EditText) findViewById(R.id.etPlaceName);
        etLat       = (EditText) findViewById(R.id.etLat);
        etLon       = (EditText) findViewById(R.id.etLon);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        btnAddFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStart = new Intent(CreatePlaceActivity.this, AddFromMapActivity.class);
                startActivityForResult(intentStart, INTENT_MAP);
            }
        });
        etPlaceName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    saveItem();
                    return true;
                }
                else if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    cancel();
                    return true;
                }
                return false;
            }
        });
    }

    private void cancel() {
        Intent intentResult = new Intent();
        setResult(PlacesListActivity.RESULT_CANCEL, intentResult);
        finish();
    }

    private void saveItem() {

        if (checkForInputError()) return;

        String lat = etLat.getText().toString();
        String lon = etLon.getText().toString();

        Intent intentResult = new Intent();
        intentResult.putExtra(PlacesListActivity.NAME_ITEM, etPlaceName.getText().toString());
        intentResult.putExtra(PlacesListActivity.LAT_ITEM, lat);
        intentResult.putExtra(PlacesListActivity.LON_ITEM, lon);
        setResult(PlacesListActivity.RESULT_OK, intentResult);

        finish();
    }

    private boolean checkForInputError() {
        if(etPlaceName.getText().length() == 0) {
            etPlaceName.setError(getString(R.string.error_place_name));
            return true;
        }

        else if(etLat.getText().length() == 0 ) {
            etLat.setError(getString(R.string.error_coord_txt));
            return true;
        }

        else if(etLon.getText().length() == 0 ) {
            etLon.setError(getString(R.string.error_coord_txt));
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_CANCEL:
                break;
            case RESULT_OK:
                etLat.setText(data.getStringExtra(NEWLAT));
                etLon.setText(data.getStringExtra(NEWLON));
                break;
        }
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }
}
