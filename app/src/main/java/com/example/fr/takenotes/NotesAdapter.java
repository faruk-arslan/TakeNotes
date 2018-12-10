package com.example.fr.takenotes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private static final String TAG=NotesAdapter.class.getSimpleName();
    private ArrayList<Note> notesArrayList;

    public NotesAdapter(ArrayList<Note> data){
        this.notesArrayList=data;
        update(data);
    }

    public void update(ArrayList<Note> data) {
        notesArrayList.clear();
        notesArrayList.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_list_item,viewGroup,false);
        NotesViewHolder viewHolder=new NotesViewHolder(view);
        return viewHolder;

//        Context context = viewGroup.getContext();
//        int noteListItem = R.layout.note_list_item;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        boolean shouldAttachToParentImmediately = false;
//
//        View view = inflater.inflate(noteListItem, viewGroup, shouldAttachToParentImmediately);
//        NotesViewHolder viewHolder = new NotesViewHolder(view);
//
//        viewHolder.viewHolderIndex.setText("ViewHolder index: "+viewHolderCount);
//
//        viewHolderCount++;
//        Log.v(MainActivity.class.getSimpleName(),"viewHolderCount: "+viewHolderCount);
//
//        return viewHolder;
    }

    /**
     * i stands for position
     * @param notesViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, int i) {
        notesViewHolder.titleTextView.setText(notesArrayList.get(i).getmTitle());
        notesViewHolder.noteTextView.setText(notesArrayList.get(i).getmNote());
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView, noteTextView;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView=(TextView) itemView.findViewById(R.id.tv_title);
            noteTextView=(TextView) itemView.findViewById(R.id.tv_note);
        }
//        public void bind(Note note){
//            titleTextView.setText(note.getmTitle());
//            noteTextView.setText(note.getmNote());
//        }
    }
}
