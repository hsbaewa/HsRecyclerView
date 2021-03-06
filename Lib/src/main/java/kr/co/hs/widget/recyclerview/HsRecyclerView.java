package kr.co.hs.widget.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    //전체 아이템 갯수 변경 이벤트
    private OnItemCountChangedListener mOnItemCountChangedListener;
    //스크롤하면서 아이템 보여지는게 바뀌는 이벤트
    private OnItemVisibleStateChangedListener mOnItemVisibleStateChangedListener;
    private HsOnScrollListener mHsOnScrollListener;
    //처음 아이템이 보이는경우 이벤트
    private OnFirstItemVisibleListener mOnFirstItemVisibleListener;
    //마지막 아이템이 보이는경우 이벤트
    private OnLastItemVisibleListener mOnLastItemVisibleListener;

    private int mSingleChoiceIndex = -1;
    private ArrayList<Integer> mChoiceIndex = null;

    //현재 선택된 아이템 갯수
    private int mCurrentCheckedItemCount = 0;

    private HsDividerItemDecoration mHsDividerItemDecoration;

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

    public void setOnItemCountChangedListener(OnItemCountChangedListener onItemCountChangedListener) {
        mOnItemCountChangedListener = onItemCountChangedListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(adapter instanceof HsAdapter){
            ((HsAdapter) adapter).setRecyclerView(this);
        }
        super.setAdapter(adapter);
    }

    public void setDivider(Drawable drawable){
        //기존 divider 제거
        if(mHsDividerItemDecoration != null){
            removeItemDecoration(mHsDividerItemDecoration);
            mHsDividerItemDecoration = null;
        }
        mHsDividerItemDecoration = new HsDividerItemDecoration(drawable);
        addItemDecoration(mHsDividerItemDecoration);
    }

    protected void itemClick(ViewHolder viewHolder, View view, int position){
        if(this.mItemClickListener != null){
            this.mItemClickListener.onItemClick(this, viewHolder, view, position);
        }
    }
    protected boolean itemLongClick(ViewHolder viewHolder, View view, int position){
        if(this.mItemLongClickListener != null){
            return this.mItemLongClickListener.onItemLongClick(this, viewHolder, view, position);
        }
        return false;
    }
    protected void itemCountChange(int before, int after){
        if(this.mOnItemCountChangedListener != null){
            this.mOnItemCountChangedListener.onChangedItemCount(before, after);
        }
    }


    public interface OnItemCountChangedListener{
        void onChangedItemCount(int beforeCount, int currentCount);
    }

    public interface OnItemCheckedCountChangedListener{
        void onChangedCheckedItemCount(int beforeCount, int currentCount);
    }

    public static abstract class HsAdapter<Holder extends HsViewHolder> extends Adapter<Holder>{
        private HsRecyclerView mRecyclerView = null;
        private int beforeCount = -1;

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder = onCreateHsViewHolder(parent, viewType);
            return holder;
        }
        @Override
        public void onBindViewHolder(Holder holder, int position) {
            if(holder != null){
                holder.setClickEventListener(mRecyclerView);
            }
            boolean isChecked = false;
            if(mRecyclerView != null){
                isChecked = mRecyclerView.isChecked(position);
            }
            onBindHsViewHolder(holder, position, isChecked);
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
            if(beforeCount != currentCount){
                getRecyclerView().itemCountChange(beforeCount, currentCount);
            }
            beforeCount = currentCount;
            return currentCount;
        }

        public String getString(int res){
            Context context = getContext();
            if(context != null)
                return context.getString(res);
            else
                return null;
        }

        public String getString(int res, Object... object){
            Context context = getContext();
            if(context != null)
                return context.getString(res, object);
            else
                return null;
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


    public static abstract class HsListAdapter<Holder extends HsViewHolder, Item> extends HsAdapter<Holder>{
        private List<Item> mItems;

        public HsListAdapter() {
            mItems = new ArrayList<>();
        }

        @Override
        protected Item getItem(int position) {
            return mItems.get(position);
        }

        public boolean addItem(Item item){
            return mItems.add(item);
        }

        public boolean addAll(Collection<? extends Item> c){
            return mItems.addAll(c);
        }

        public boolean addAll(int index, Collection<? extends Item> c){
            return mItems.addAll(index, c);
        }

        public Item remove(int index){
            return mItems.remove(index);
        }

        public boolean remove(Item o){
            return mItems.remove(o);
        }

        public boolean removeAll(Collection<?> c){
            return mItems.removeAll(c);
        }

        public void clear(){
            mItems.clear();
        }

        @Override
        public int getHsItemCount() {
            return mItems.size();
        }

        public ArrayList<Item> getCheckedItems(){
            ArrayList<Item> result = new ArrayList<>();
            HsRecyclerView recyclerView = getRecyclerView();
            if(recyclerView != null){
                ArrayList<Integer> checkedPosition = recyclerView.getChoiceIndex();
                for(Integer idx : checkedPosition){
                    result.add(getItem(idx));
                }
            }
            return result;
        }
    }


    public static abstract class HsViewHolder extends ViewHolder implements View.OnClickListener, View.OnLongClickListener, Toolbar.OnMenuItemClickListener {
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

        protected void setToolbar(Toolbar toolbar){
            this.mToolbar = toolbar;
            this.mToolbar.setOnMenuItemClickListener(this);
        }

        protected void setToolbar(Toolbar toolbar, OnHolderMenuItemListener onHolderMenuItemListener){
            this.mOnHolderMenuItemListener = onHolderMenuItemListener;
            setToolbar(toolbar);
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



    public static class HsItemDecoration extends ItemDecoration{

    }

    public static class HsDividerItemDecoration extends HsItemDecoration{
        private Drawable mDrawable;

        public HsDividerItemDecoration(Drawable divider) {
            this.mDrawable = divider;
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }
        }
    }



    class HsOnScrollListener extends OnScrollListener{
        int mLastFirstPosition = -1;
        boolean isLastFirstVisibleState = false;
        int mLastEndPosition = -1;
        boolean isLastEndVisibleState = false;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(getLayoutManager() instanceof LinearLayoutManager){
                boolean isChanged = false;
                int firstPos = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                int lastPos = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

                if(mLastFirstPosition != firstPos){
                    mLastFirstPosition = firstPos;
                    isChanged = true;
                    if(mOnFirstItemVisibleListener != null){
                        boolean isFirstVisible;
                        if(mLastFirstPosition == 0)
                            isFirstVisible = true;
                        else
                            isFirstVisible = false;
                        if(isLastFirstVisibleState != isFirstVisible){
                            isLastFirstVisibleState = isFirstVisible;
                            mOnFirstItemVisibleListener.onFirstItemVisible(isLastFirstVisibleState);
                        }
                    }
                }
                if(mLastEndPosition != lastPos){
                    mLastEndPosition = lastPos;
                    isChanged = true;
                    if(getAdapter() != null){
                        int count = getAdapter().getItemCount();
                        if(mOnLastItemVisibleListener != null){
                            boolean isEndVisible;
                            if(mLastEndPosition == (count - 1))
                                isEndVisible = true;
                            else
                                isEndVisible = false;

                            if(isLastEndVisibleState != isEndVisible){
                                isLastEndVisibleState = isEndVisible;
                                mOnLastItemVisibleListener.onLastItemVisible(isLastEndVisibleState);
                            }
                        }
                    }
                }
                if(mOnItemVisibleStateChangedListener != null && isChanged){
                    mOnItemVisibleStateChangedListener.onVisibleStateChanged(mLastFirstPosition, mLastEndPosition);
                }
            }
        }
    }

    public void setOnItemVisibleStateChangedListener(OnItemVisibleStateChangedListener listener){
        if(mHsOnScrollListener == null && listener != null){
            mHsOnScrollListener = new HsOnScrollListener();
            addOnScrollListener(mHsOnScrollListener);
        }
        this.mOnItemVisibleStateChangedListener = listener;
    }

    public void setOnFirstItemVisibleListener(OnFirstItemVisibleListener listener){
        if(mHsOnScrollListener == null && listener != null){
            mHsOnScrollListener = new HsOnScrollListener();
            addOnScrollListener(mHsOnScrollListener);
        }
        this.mOnFirstItemVisibleListener = listener;
    }

    public void setOnLastItemVisibleListener(OnLastItemVisibleListener listener){
        if(mHsOnScrollListener == null && listener != null){
            mHsOnScrollListener = new HsOnScrollListener();
            addOnScrollListener(mHsOnScrollListener);
        }
        this.mOnLastItemVisibleListener = listener;
    }


    public interface OnItemClickListener{
        void onItemClick(HsRecyclerView adapterView, ViewHolder viewHolder, View itemView, int position);
    }
    public interface OnItemLongClickListener{
        boolean onItemLongClick(HsRecyclerView adapterView, ViewHolder viewHolder, View itemView, int position);
    }
    public interface OnItemVisibleStateChangedListener{
        void onVisibleStateChanged(int topPosition, int bottomPosition);
    }
    public interface OnFirstItemVisibleListener{
        void onFirstItemVisible(boolean visible);
    }
    public interface OnLastItemVisibleListener{
        void onLastItemVisible(boolean visible);
    }
}
