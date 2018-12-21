package com.example.fr.takenotes;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private static final String TAG=NotesAdapter.class.getSimpleName();
    private ArrayList<Note> notesArrayList;
    private Context context;
//    private static ClickListener clickListener;


    public NotesAdapter(ArrayList<Note> data,Context context){
        this.notesArrayList=data;
        this.context=context;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_list_item,viewGroup,false);
        NotesViewHolder viewHolder=new NotesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NotesViewHolder notesViewHolder, final int i) {
        notesViewHolder.titleTextView.setText(notesArrayList.get(i).getmTitle());
        notesViewHolder.noteTextView.setText(notesArrayList.get(i).getmNote());

        notesViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){

                }else {
                    Intent editNoteIntent=new Intent(context,EditNoteActivity.class);
                    editNoteIntent.putExtra("title_extra",notesArrayList.get(i).getmTitle());
                    editNoteIntent.putExtra("note_extra",notesArrayList.get(i).getmNote());
                    editNoteIntent.putExtra("time_extra",notesArrayList.get(i).getmTime());
                    editNoteIntent.putExtra("edit_time_extra",notesArrayList.get(i).getmTime());
                    editNoteIntent.putExtra("note_id_extra",notesArrayList.get(i).getmNoteId());
                    context.startActivity(editNoteIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

        TextView titleTextView, noteTextView;
        RelativeLayout parentLayout;
        private ItemClickListener itemClickListener;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView=(TextView) itemView.findViewById(R.id.tv_title);
            noteTextView=(TextView) itemView.findViewById(R.id.tv_note);
            parentLayout=itemView.findViewById(R.id.main_layout);

            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);

//            tvTitle =(TextView)itemView.findViewById(R.id.item_title);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

//        @Override
//        public boolean onLongClick(View view) {
//            itemClickListener.onClick(view,getAdapterPosition(),true);
//            return true;
//        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle(context.getString(R.string.context_menu_select_action));
            contextMenu.add(this.getAdapterPosition(),
                    R.id.share_single_note, 0, context.getString(R.string.share));//groupId, itemId, order, title
            contextMenu.add(this.getAdapterPosition(),
                    R.id.delete_single_note, 0, context.getString(R.string.delete));
        }

    }
}
