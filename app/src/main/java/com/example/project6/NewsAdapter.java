package com.example.project6;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String DATE_SPLITTER = "T";

    public NewsAdapter(Activity context, ArrayList<News> newsArrayList) {

        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for 3 TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, newsArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_activity, parent, false
            );
        }

        // Get the current position of the view being inflated
        News currentNews = getItem(position);

        TextView headline = convertView.findViewById(R.id.headline_text_view);
        headline.setText(currentNews.getHeadline());

        TextView section = convertView.findViewById(R.id.section_text_view);
        section.setText(getContext().getString(R.string.section) + currentNews.getSection());

        String split = currentNews.getDate();
        String[] parts = split.split(DATE_SPLITTER);


        TextView date = convertView.findViewById(R.id.date_text_view);
        date.setText(getContext().getString(R.string.published_on) + parts[0]);

        TextView author = convertView.findViewById(R.id.author_text_view);
        author.setText(getContext().getString(R.string.author_by) + currentNews.getAuthor());


        return convertView;
    }
}
