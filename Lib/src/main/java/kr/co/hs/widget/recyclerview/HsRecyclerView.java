package kr.co.hs.widget.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * Created by Bae on 2016-11-21.
 */
public class HsRecyclerView extends RecyclerView {
    public static final int CHOICE_MODE_MULTIPLE = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;
    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;

    private int mChoiceMode;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    private int mSingleChoiceIndex = -1;
    private ArrayList<Integer> mChoiceIndex = null;


    public HsRecyclerView(Context context) {
        super(context);
    }

    public HsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HsRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setChoiceMode(int mode){
        switch (mode){
            case CHOICE_MODE_MULTIPLE:
            case CHOICE_MODE_MULTIPLE_MODAL:
            {
                if(mChoiceIndex != null){
                    mChoiceIndex.clear();
                    mChoiceIndex = null;
                }
                mSingleChoiceIndex = -1;
                mChoiceIndex = new ArrayList<>();
                break;
            }
            case CHOICE_MODE_NONE:{
                if(mChoiceIndex != null){
                    mChoiceIndex.clear();
                    mChoiceIndex = null;
                }
                mSingleChoiceIndex = -1;
                break;
            }
            case CHOICE_MODE_SINGLE:{
                if(mChoiceIndex != null){
                    mChoiceIndex.clear();
                    mChoiceIndex = null;
                }
                mSingleChoiceIndex = -1;
                break;
            }
        }
        this.mChoiceMode = mode;
    }
    public int getChoiceMode(){
        return this.mChoiceMode;
    }

    public boolean isChecked(int position){
        switch (getChoiceMode()){
            case CHOICE_MODE_NONE:{
                return false;
            }
            case CHOICE_MODE_MULTIPLE:
            case CHOICE_MODE_MULTIPLE_MODAL:
            {
                if(mChoiceIndex != null){
                    return mChoiceIndex.contains(position);
                }
            }
            case CHOICE_MODE_SINGLE:{
                if(position == mSingleChoiceIndex)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }
    public void setChecked(int position, boolean check){
        switch (getChoiceMode()){
            case CHOICE_MODE_NONE:{

                break;
            }
            case CHOICE_MODE_SINGLE:{
                mSingleChoiceIndex = position;
                break;
            }
            case CHOICE_MODE_MULTIPLE:
            case CHOICE_MODE_MULTIPLE_MODAL:
            {
                if(mChoiceIndex != null){
                    if(check){
                        if(!isChecked(position))
                            mChoiceIndex.add(position);
                    }else{
                        if(isChecked(position))
                            mChoiceIndex.remove((Integer)position);
                    }
                }
                break;
            }
        }
    }

    public ArrayList<Integer> getChoiceIndex(){
        return mChoiceIndex;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        if(adapter instanceof HsAdapter){
            ((HsAdapter) adapter).setRecyclerView(this);
        }
        super.setAdapter(adapter);
    }


    private void itemClick(View view, int position){
        if(this.mItemClickListener != null){
            this.mItemClickListener.onItemClick(this, view, position);
        }
    }
    private boolean itemLongClick(View view, int position){
        if(this.mItemLongClickListener != null){
            return this.mItemLongClickListener.onItemLongClick(this, view, position);
        }
        return false;
    }



    public static abstract class HsAdapter<Holder extends HsViewHolder> extends RecyclerView.Adapter{
        private HsRecyclerView mRecyclerView = null;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            HsViewHolder holder = onCreateHsViewHolder(parent, viewType);
            return holder;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Holder hsViewHolder = (Holder) holder;
            if(hsViewHolder != null){
                hsViewHolder.setClickEventListener(mRecyclerView);
            }
            boolean isChecked = false;
            if(mRecyclerView != null){
                isChecked = mRecyclerView.isChecked(position);
            }
            onBindHsViewHolder(hsViewHolder, position, isChecked);
        }
        protected void setRecyclerView(HsRecyclerView view){
            this.mRecyclerView = view;
        }

        public abstract Holder onCreateHsViewHolder(ViewGroup parent, int viewType);
        public abstract void onBindHsViewHolder(Holder holder, int position, boolean isChecked);
    }


    public static abstract class HsCursorAdapter<Holder extends HsViewHolder> extends HsAdapter{
        private Cursor mCursor = null;

        @Override
        public void onBindHsViewHolder(HsViewHolder holder, int position, boolean isChecked) {
            if(mCursor == null)
                return;

            mCursor.moveToPosition(position);
            Holder viewHolder = (Holder) holder;
            onBindHsCursorViewHolder(viewHolder, position, isChecked, mCursor);
        }

        public void setCursor(Cursor cursor){
            this.mCursor = cursor;
        }
        public Cursor getCursor(){
            return this.mCursor;
        }
        public void closeCursor(){
            if(mCursor != null && !mCursor.isClosed()){
                mCursor.close();
            }
        }
        public void swapCursor(Cursor newCursor){
            Cursor tempCursor = mCursor;
            mCursor = newCursor;
            if(tempCursor != null && !tempCursor.isClosed()){
                tempCursor.close();
            }
        }
        @Override
        public int getItemCount() {
            if(mCursor == null || mCursor.isClosed())
                return 0;
            return mCursor.getCount();
        }

        @Override
        public int getItemViewType(int position) {
//            return super.getItemViewType(position);
            if(mCursor == null || mCursor.isClosed())
                return super.getItemViewType(position);

            mCursor.moveToPosition(position);
            return getItemViewType(mCursor);
        }

        public int getItemViewType(@NonNull Cursor cursor){
            return super.getItemViewType(cursor.getPosition());
        }

        public abstract void onBindHsCursorViewHolder(Holder holder, int position, boolean isChecked, Cursor cursor);
    }


    public static abstract class HsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private HsRecyclerView mHsRecyclerView;

        public HsViewHolder(View itemView) {
            super(itemView);
        }

        public View findViewById(int id){
            return getItemView().findViewById(id);
        }

        public View getItemView(){
            return itemView;
        }

        protected HsRecyclerView getHsRecyclerView(){
            return mHsRecyclerView;
        }

        protected void setClickEventListener(HsRecyclerView recyclerView){
            this.mHsRecyclerView = recyclerView;
            getItemView().setOnClickListener(this);
            getItemView().setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if(mHsRecyclerView != null){
                mHsRecyclerView.itemClick(getItemView(), pos);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int pos = getAdapterPosition();
            if(mHsRecyclerView != null)
                return mHsRecyclerView.itemLongClick(getItemView(), pos);
            return false;
        }
    }


    public interface OnItemClickListener{
        void onItemClick(HsRecyclerView adapterView, View itemView, int position);
    }
    public interface OnItemLongClickListener{
        boolean onItemLongClick(HsRecyclerView adapterView, View itemView, int position);
    }
}
