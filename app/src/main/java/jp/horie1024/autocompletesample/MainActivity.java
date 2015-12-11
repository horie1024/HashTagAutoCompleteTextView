package jp.horie1024.autocompletesample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.horie1024.autocompletesample.adapters.HashTagSuggestAdapter;
import jp.horie1024.autocompletesample.views.HashTagAutoCompleteTextView;

/**
 * HashTag Auto Complete Sample
 *
 * @author Horie1024
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HashTagAutoCompleteTextView textView = (HashTagAutoCompleteTextView) findViewById(R.id.input_form);

        HashTagSuggestAdapter adapter = new HashTagSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        adapter.setCursorPositionListener(new HashTagSuggestAdapter.CursorPositionListener() {
            @Override
            public int currentCursorPosition() {
                return textView.getSelectionStart();
            }
        });

        textView.setAdapter(adapter);
    }

    private static final String[] COUNTRIES = new String[]{
            "#Belgium", "#France", "#Italy", "#Germany", "#Spain"
    };
}
