package jp.horie1024.autocompletesample.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 固定の補完候補を用いたサンプル
 *
 * @author Horie1024
 */
public class HashTagSuggestAdapter extends ArrayAdapter<String> {

    private HashTagFilter filter;
    private List<String> objects;
    private List<String> suggests = new ArrayList<>();

    private CursorPositionListener listener;

    public HashTagSuggestAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.objects = Arrays.asList(objects);
    }

    public interface CursorPositionListener {
        int currentCursorPosition();
    }

    public void setCursorPositionListener(CursorPositionListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return suggests.size();
    }

    @Override
    public String getItem(int position) {
        return suggests.get(position);
    }

    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new HashTagFilter();
        }

        return filter;
    }

    public class HashTagFilter extends Filter {

        private final Pattern pattern = Pattern.compile("[#＃]([Ａ-Ｚａ-ｚA-Za-z一-\u9FC60-9０-９ぁ-ヶｦ-ﾟー])+");

        public int start;
        public int end;

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return "#" + resultValue.toString() + " ";
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            suggests.clear();

            int cursorPosition = listener.currentCursorPosition();

            Matcher m = pattern.matcher(constraint.toString());
            while (m.find()) {

                if (m.start() < cursorPosition && cursorPosition <= m.end()) {

                    start = m.start();
                    end = m.end();

                    String tag = constraint.subSequence(m.start(), m.end()).toString();

                    for (int i = 0; i < objects.size(); i++) {

                        String country = objects.get(i);
                        if (country.toLowerCase().startsWith(tag)) {
                            suggests.add(country);
                        }
                    }
                }
            }

            filterResults.values = suggests;
            filterResults.count = suggests.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
