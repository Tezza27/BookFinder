package com.example.android.bookfinder;

public class BookClass {
    private String mBookTitle;
    private String mBookAuthor;
    private String mImageUrl;
    private String mInfoUrl;

    public BookClass(String bookTitle, String bookAuthor, String imageUrl, String infoUrl) {
        this.mBookTitle = bookTitle;
        this.mBookAuthor = bookAuthor;
        this.mImageUrl = imageUrl;
        this.mInfoUrl = infoUrl;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getBookAuthor() {
        return mBookAuthor;
     }

     public String getImageUrl() {
        return mImageUrl;
     }

    public String getInfoUrl() {
        return mInfoUrl;
    }
}


