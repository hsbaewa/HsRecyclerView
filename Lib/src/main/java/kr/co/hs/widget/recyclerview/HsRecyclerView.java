package kr.co.hs.widget.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.co.hs.app.HsActivity;
import kr.co.hs.app.OnActivityLifeCycleListener;


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
    //선택된 아이템 갯수 변경 이벤트
    private OnItemCheckedCountChangedListener mOnItemCheckedCountChangedListener;

    private int mSingleChoiceIndex = -1;
    private ArrayList<Integer> mChoiceIndex = null;

    //현재 선택된 아이템 갯수
    private int mCurrentCheckedItemCount = 0;


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
                mChoiceIndex = new ArrayList<>();

                //이벤트 발생 시키자
                int tempCurrentCheckCount = mChoiceIndex.size();
                if(mOnItemCheckedCountChangedListener != null){
                    if(mCurrentCheckedItemCount != tempCurrentCheckCount)
                        mOnItemCheckedCountChangedListener.onChangedCheckedItemCount(mCurrentCheckedItemCount, tempCurrentCheckCount);
                }
                mCurrentCheckedItemCount = tempCurrentCheckCount;

                mSingleChoiceIndex = -1;

                break;
            }
            case CHOICE_MODE_NONE:{
                if(mChoiceIndex != null){
                    mChoiceIndex.clear();
                    mChoiceIndex = null;
                }
                //이벤트 발생 시키자
                int tempCurrentCheckCount = 0;
                if(mOnItemCheckedCountChangedListener != null){
                    if(mCurrentCheckedItemCount != tempCurrentCheckCount)
                        mOnItemCheckedCountChangedListener.onChangedCheckedItemCount(mCurrentCheckedItemCount, tempCurrentCheckCount);
                }
                mCurrentCheckedItemCount = tempCurrentCheckCount;

                mSingleChoiceIndex = -1;
                break;
            }
            case CHOICE_MODE_SINGLE:{
                if(mChoiceIndex != null){
                    mChoiceIndex.clear();
                    mChoiceIndex = null;
                }
                //이벤트 발생 시키자
                int tempCurrentCheckCount = 0;
                if(mOnItemCheckedCountChangedListener != null){
                    if(mCurrentCheckedItemCount != tempCurrentCheckCount)
                        mOnItemCheckedCountChangedListener.onChangedCheckedItemCount(mCurrentCheckedItemCount, tempCurrentCheckCount);
                }
                mCurrentCheckedItemCount = tempCurrentCheckCount;

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
    private void setChecked(int position, boolean check, boolean isEvent){
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
                    if(isEvent){
                        //갯수 체크하여 이벤트 발생
                        int tempCurrentCheckCount = mChoiceIndex.size();
                        if(mOnItemCheckedCountChangedListener != null){
                            if(mCurrentCheckedItemCount != tempCurrentCheckCount)
                                mOnItemCheckedCountChangedListener.onChangedCheckedItemCount(mCurrentCheckedItemCount, tempCurrentCheckCount);
                        }
                        mCurrentCheckedItemCount = tempCurrentCheckCount;
                    }
                }
                break;
            }
        }
    }
    public void setChecked(int position, boolean check){
        setChecked(position, check, true);
    }
    public boolean isCheckAll(){
        switch (getChoiceMode()){
            case CHOICE_MODE_NONE:{
                return false;
            }
            case CHOICE_MODE_SINGLE:{
                return false;
            }
            case CHOICE_MODE_MULTIPLE:
            case CHOICE_MODE_MULTIPLE_MODAL:
            {
                return mChoiceIndex.size() == getAdapter().getItemCount();
            }
            default:return false;
        }
    }
    public void setCheckAll(boolean check){
        switch (getChoiceMode()){
            case CHOICE_MODE_NONE:{
                return;
            }
            case CHOICE_MODE_SINGLE:{
                return;
            }
            case CHOICE_MODE_MULTIPLE:
            case CHOICE_MODE_MULTIPLE_MODAL:
            {
                HsAdapter adapter = (HsAdapter) getAdapter();
                if(adapter != null){
                    for(int i=0;i<adapter.getItemCount();i++){
                        setChecked(i, check, false);
                    }
                }

                //갯수 체크하여 이벤트 발생
                int tempCurrentCheckCount = mChoiceIndex.size();
                if(mOnItemCheckedCountChangedListener != null){
                    if(mCurrentCheckedItemCount != tempCurrentCheckCount)
                        mOnItemCheckedCountChangedListener.onChangedCheckedItemCount(mCurrentCheckedItemCount, tempCurrentCheckCount);
                }
                mCurrentCheckedItemCount = tempCurrentCheckCount;
            }
            default:return;
        }
    }

    public ArrayList<Integer> getChoiceIndex(){
        return mChoiceIndex;
    }

    public int getCheckedCount(){
        return mCurrentCheckedItemCount;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    public void setOnItemCheckedCountChangedListener(OnItemCheckedCountChangedListener onItemCheckedCountChangedListener) {
        mOnItemCheckedCountChangedListener = onItemCheckedCountChangedListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(adapter instanceof HsAdapter){
            ((HsAdapter) adapter).setRecyclerView(this);
        }
        super.setAdapter(adapter);
    }


    private void itemClick(ViewHolder viewHolder, View view, int position){
        if(this.mItemClickListener != null){
            this.mItemClickListener.onItemClick(this, viewHolder, view, position);
        }
    }
    private boolean itemLongClick(ViewHolder viewHolder, View view, int position){
        if(this.mItemLongClickListener != null){
            return this.mItemLongClickListener.onItemLongClick(this, viewHolder, view, position);
        }
        return false;
    }


    public interface OnItemCountChangedListener{
        void onChangedItemCount(int beforeCount, int currentCount);
    }

    public interface OnItemCheckedCountChangedListener{
        void onChangedCheckedItemCount(int beforeCount, int currentCount);
    }


    public static abstract class HsAdapter<Holder extends HsViewHolder> extends RecyclerView.Adapter{
        private HsRecyclerView mRecyclerView = null;
        private OnItemCountChangedListener mOnItemCountChangedListener;
        private int beforeCount = -1;

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
        protected HsRecyclerView getRecyclerView(){
            return this.mRecyclerView;
        }
        protected boolean isMultiChoiceMode(){
            int nRecyclerViewChoiceMode = this.mRecyclerView.getChoiceMode();
            return (nRecyclerViewChoiceMode == CHOICE_MODE_MULTIPLE || nRecyclerViewChoiceMode == CHOICE_MODE_MULTIPLE_MODAL);
        }

        protected Object getItem(int position){
            return null;
        }

        protected Context getContext(){
            if(getRecyclerView() != null)
                return getRecyclerView().getContext();
            else
                return null;
        }

        @Override
        public int getItemCount() {
            int currentCount = getHsItemCount();
            if(this.mOnItemCountChangedListener != null && beforeCount != currentCount)
                this.mOnItemCountChangedListener.onChangedItemCount(beforeCount, currentCount);
            beforeCount = currentCount;
            return currentCount;
        }

        public void setOnItemCountChangedListener(OnItemCountChangedListener onItemCountChangedListener) {
            this.mOnItemCountChangedListener = onItemCountChangedListener;
        }

        public abstract Holder onCreateHsViewHolder(ViewGroup parent, int viewType);
        public abstract void onBindHsViewHolder(Holder holder, int position, boolean isChecked);
        public abstract int getHsItemCount();
    }


    public interface HsRecyclerCursorAdapterListener<Adapter extends HsRecyclerCursorAdapter>{
        Cursor onLoadCursor(HsRecyclerView hsRecyclerView, Adapter adapter);
        void onReleaseCursor(HsRecyclerView hsRecyclerView, Adapter adapter);
    }

    public static abstract class HsRecyclerCursorAdapter<Holder extends HsViewHolder> extends HsAdapter<Holder> implements OnActivityLifeCycleListener{
        private Cursor mCursor;
        private HsRecyclerCursorAdapterListener mHsRecyclerCursorAdapterListener;

        public HsRecyclerCursorAdapter(HsRecyclerCursorAdapterListener hsRecyclerCursorAdapterListener) {
            mHsRecyclerCursorAdapterListener = hsRecyclerCursorAdapterListener;
        }

        @Override
        public void onBindHsViewHolder(Holder holder, int position, boolean isChecked) {
            if(getCursor() != null && !getCursor().isClosed()){
                getCursor().moveToPosition(position);
            }
            onBindHsViewHolder(holder, position, isChecked, getCursor());
        }

        public Cursor getCursor(){
            return getCursor(-1);
        }

        public Cursor getCursor(int position){
            if(mCursor == null || mCursor.isClosed()){
                if(mHsRecyclerCursorAdapterListener != null)
                    mCursor = mHsRecyclerCursorAdapterListener.onLoadCursor(getRecyclerView(), this);
            }

            if(mCursor != null && position >= 0)
                mCursor.moveToPosition(position);

            return mCursor;
        }

        public Cursor swapCursor(Cursor cursor){
            Cursor tempCursor = mCursor;
            mCursor = cursor;
            if(mCursor == null && mHsRecyclerCursorAdapterListener!=null)
                mCursor = mHsRecyclerCursorAdapterListener.onLoadCursor(getRecyclerView(), this);

            if(tempCursor != null && !tempCursor.isClosed())
                tempCursor.close();

            return mCursor;
        }

        public Cursor swapCursor(){
            return swapCursor(null);
        }

        public void closeCursor(){
            if(getCursor() != null && !getCursor().isClosed()){
                getCursor().close();
                mCursor = null;
            }
            if(mHsRecyclerCursorAdapterListener != null)
                mHsRecyclerCursorAdapterListener.onReleaseCursor(getRecyclerView(), this);
        }

        @Override
        public int getHsItemCount() {
            if(getCursor() == null || getCursor().isClosed())
                return 0;
            else
                return getCursor().getCount();
        }

        @Override
        protected void setRecyclerView(HsRecyclerView view) {
            super.setRecyclerView(view);
            if(view != null){
                Context context = view.getContext();
                if(context instanceof HsActivity){
                    HsActivity activity = (HsActivity) context;
                    activity.setOnActivityLifeCycleListener(this);
                }
            }
        }

        @Override
        protected Object getItem(int position) {
            return getCursor(position);
        }

        @Override
        public void onActivityCreateStatus() {

        }

        @Override
        public void onActivityStartStatus() {

        }

        @Override
        public void onActivityResumeStatus() {
            swapCursor();
        }

        @Override
        public void onActivityPauseStatus() {
            closeCursor();
        }

        @Override
        public void onActivityStopStatus() {

        }

        @Override
        public void onActivityDestryStatus() {

        }

        public abstract void onBindHsViewHolder(Holder holder, int position, boolean isChecked, Cursor cursor);
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
        public int getHsItemCount() {
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


    public static abstract class HsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, Toolbar.OnMenuItemClickListener {
        private HsRecyclerView mHsRecyclerView;
        private Object mItemObject;

        private Toolbar mToolbar;
        private OnHolderMenuItemListener mOnHolderMenuItemListener;

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
                mHsRecyclerView.itemClick(this, getItemView(), pos);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int pos = getAdapterPosition();
            if(mHsRecyclerView != null)
                return mHsRecyclerView.itemLongClick(this, getItemView(), pos);
            return false;
        }

        protected Context getContext(){
            if(getHsRecyclerView() != null)
                return getHsRecyclerView().getContext();
            else
                return null;
        }

        protected HsAdapter getHsAdapter(){
            if(getHsRecyclerView() != null)
                return (HsAdapter) getHsRecyclerView().getAdapter();
            else
                return null;
        }

        protected Object getItem(){
            if(getHsAdapter() != null)
                return getHsAdapter().getItem(getAdapterPosition());
            else
                return null;
        }

        protected void setToolbar(Toolbar toolbar, OnHolderMenuItemListener onHolderMenuItemListener){
            this.mOnHolderMenuItemListener = onHolderMenuItemListener;
            this.mToolbar = toolbar;
            this.mToolbar.setOnMenuItemClickListener(this);
        }

        public Toolbar getToolbar() {
            return mToolbar;
        }

        public String getString(int res){
            if(getContext() != null)
                return getContext().getString(res);
            else
                return null;
        }

        public String getString(int res, Object... object){
            if(getContext() != null)
                return getContext().getString(res, object);
            else
                return null;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int idx = getAdapterPosition();
            if(this.mOnHolderMenuItemListener != null)
                return this.mOnHolderMenuItemListener.onOptionsItemSelected(item, idx);
            else
                return false;
        }
    }


    public interface OnHolderMenuItemListener{
        boolean onOptionsItemSelected(MenuItem item, int idx);
    }


    public interface OnItemClickListener{
        void onItemClick(HsRecyclerView adapterView, ViewHolder viewHolder, View itemView, int position);
    }
    public interface OnItemLongClickListener{
        boolean onItemLongClick(HsRecyclerView adapterView, ViewHolder viewHolder, View itemView, int position);
    }
}
