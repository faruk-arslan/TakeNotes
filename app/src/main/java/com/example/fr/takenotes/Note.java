package com.example.fr.takenotes;

public class Note {
    private String mTitle, mNote, mNoteId;
    private long mTime;
    private long mEditTime;

    public Note(String title, String note, String noteId, long time, long editTime){
        mTitle=title;
        mNote=note;
        mNoteId=noteId;
        mTime=time;
        mEditTime=editTime;

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

    public String getmNoteId() { return mNoteId; }

    public void setmNoteId(String mNoteId) { this.mNoteId = mNoteId; }

    public long getmTime() { return mTime; }

    public void setmTime(long mTime) { this.mTime = mTime; }

    public long getmEditTime() { return mEditTime; }

    public void setmEditTime(long mEditTime) { this.mEditTime = mEditTime; }

}
