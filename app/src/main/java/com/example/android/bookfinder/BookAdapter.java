package com.example.android.bookfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class BookAdapter extends ArrayAdapter<BookClass> {

    public BookAdapter(Context context, ArrayList<BookClass> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        BookClass currentBook = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID miwok_text_view.
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.Title_Label);

        // Get the Miwok translation from the currentWord object and set this text on
        // the Miwok TextView.
        titleTextView.setText(currentBook.getBookTitle());

        // Find the TextView in the list_item.xml layout with the ID default_text_view.
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.Author_Label);

        // Get the default translation from the currentWord object and set this text on
        // the default TextView.
        authorTextView.setText(currentBook.getBookAuthor());

        // Find the ImageView in the list_item.xml layout with the ID default_text_view.
        ImageView coverImageView = (ImageView) listItemView.findViewById(R.id.cover_image);

        Picasso.with(getContext()).setLoggingEnabled(true);

        Picasso.with(getContext())
                .load(currentBook.getImageUrl())
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(coverImageView);


        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}

