package jp.horie1024.autocompletesample.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.horie1024.autocompletesample.R;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * APIを通して補完候補を動的に用意するサンプル
 *
 * @author Horie1024
 */
public class HashTagSuggestWithAPIAdapter extends ArrayAdapter<HashTagSuggestWithAPIAdapter.HashTag> {

    private HashTagFilter filter;
    private List<HashTag> suggests = new ArrayList<>();

    private LayoutInflater inflater;
    private CursorPositionListener listener;

    public HashTagSuggestWithAPIAdapter(Context context, int resource, List<HashTag> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface CursorPositionListener {
        int currentCursorPosition();
    }

    public void setCursorPositionListener(CursorPositionListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.hashtag_suggest_cell, null);
        }

        try {
            HashTag hashTag = getItem(position);

            AppCompatTextView tagName = (AppCompatTextView) convertView.findViewById(R.id.hash_tag_name);
            AppCompatTextView tagCount = (AppCompatTextView) convertView.findViewById(R.id.hash_tag_count);

            tagName.setText(hashTag.tag);
            tagCount.setText(hashTag.count);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return suggests.size();
    }

    @Override
    public HashTag getItem(int position) {
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

        private SuggestService service;

        public int start;
        public int end;

        public HashTagFilter() {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("YOUR_BASE_URL")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(SuggestService.class);
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return String.format("#%s ", ((HashTag) resultValue).tag);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            if (constraint != null) {

                suggests.clear();

                int cursorPosition = listener.currentCursorPosition();

                Matcher m = pattern.matcher(constraint.toString());
                while (m.find()) {

                    if (m.start() < cursorPosition && cursorPosition <= m.end()) {

                        start = m.start();
                        end = m.end();

                        String keyword = constraint.subSequence(m.start() + 1, m.end()).toString();
                        Call<SuggestResponse> call = service.listHashTags(keyword);
                        try {
                            suggests = call.execute().body().results;
                        } catch (IOException e) {
                            e.printStackTrace();
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

    public interface SuggestService {
        @GET("YOUR_PATH")
        Call<SuggestResponse> listHashTags(@Query("keyword") String keyword);
    }

    public class SuggestResponse {

        private ArrayList<HashTag> results;

        public SuggestResponse() {
        }
    }

    public class HashTag {
        private final String tag;
        private final String count;

        public HashTag(String tag, String count) {
            this.tag = tag;
            this.count = count;
        }
    }
}
