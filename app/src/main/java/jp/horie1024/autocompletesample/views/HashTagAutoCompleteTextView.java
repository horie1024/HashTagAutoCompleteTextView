package jp.horie1024.autocompletesample.views;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import jp.horie1024.autocompletesample.R;
import jp.horie1024.autocompletesample.adapters.HashTagSuggestAdapter;

/**
 * replaceTextの処理をOverrideしたカスタムView
 *
 * @author Horie1024
 */
public class HashTagAutoCompleteTextView extends AutoCompleteTextView {
    public HashTagAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public HashTagAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.autoCompleteTextViewStyle);
    }

    public HashTagAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void replaceText(CharSequence text) {

        clearComposingText();

        HashTagSuggestAdapter adapter = (HashTagSuggestAdapter) getAdapter();
        HashTagSuggestAdapter.HashTagFilter filter = (HashTagSuggestAdapter.HashTagFilter) adapter.getFilter();

        Editable span = getText();
        span.replace(filter.start, filter.end, text);
    }
}
