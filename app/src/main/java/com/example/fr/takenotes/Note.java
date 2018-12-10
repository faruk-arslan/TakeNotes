package com.example.fr.takenotes;

public class Note {
    private String mTitle, mNote;

    public Note(String title,String note){
        mTitle=title;
        mNote=note;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }
}
