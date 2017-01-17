package kr.co.hs.widget.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bae on 2016-12-29.
 */
public class HsExpandableRecyclerView extends HsRecyclerView {
    private OnExpandableItemClickListener mExpandableItemClickListener;
    private OnExpandableItemLongClickListener mExpandableItemLongClickListener;

    public HsExpandableRecyclerView(Context context) {
        super(context);
    }

    public HsExpandableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HsExpandableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setOnExpandableItemClickListener(OnExpandableItemClickListener listener){
        this.mExpandableItemClickListener = listener;
    }
    public void setOnExpandableItemLongClickListener(OnExpandableItemLongClickListener listener){
        this.mExpandableItemLongClickListener = listener;
    }


    private void expandableItemClick(ViewHolder viewHolder, View view, int parentPosition, int childPosition){
        if(this.mExpandableItemClickListener != null){
            this.mExpandableItemClickListener.onItemClick(this, viewHolder, view, parentPosition, childPosition);
        }
    }
    private boolean expandableItemLongClick(ViewHolder viewHolder, View view, int parentPosition, int childPosition){
        if(this.mExpandableItemLongClickListener != null){
            return this.mExpandableItemLongClickListener.onItemLongClick(this, viewHolder, view, parentPosition, childPosition);
        }
        return false;
    }



    public interface OnExpandableItemClickListener{
        void onItemClick(HsExpandableRecyclerView adapterView, ViewHolder viewHolder,  View itemView, int parentPosition, int childPosition);
    }
    public interface OnExpandableItemLongClickListener{
        boolean onItemLongClick(HsExpandableRecyclerView adapterView, ViewHolder viewHolder,  View itemView, int parentPosition, int childPosition);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if(adapter instanceof HsExpandableAdapter){
            HsExpandableAdapter expandableAdapter = (HsExpandableAdapter) adapter;
            expandableAdapter.setRecyclerView(this);
        }
    }

    public interface Parent<C> {
        List<C> getChildList();
        boolean isInitiallyExpanded();
    }

    public interface ExpandCollapseListener {
        @UiThread
        void onParentExpanded(int parentPosition);
        @UiThread
        void onParentCollapsed(int parentPosition);
    }

    interface ParentViewHolderExpandCollapseListener {
        @UiThread
        void onParentExpanded(int flatParentPosition);
        @UiThread
        void onParentCollapsed(int flatParentPosition);
    }

    public static class ParentViewHolder<P extends Parent<C>, C> extends HsViewHolder {
        @Nullable
        private ParentViewHolderExpandCollapseListener mParentViewHolderExpandCollapseListener;
        private boolean mExpanded;
        P mParent;
        private HsExpandableRecyclerView mHsRecyclerView;
        HsExpandableAdapter mExpandableAdapter;
        private boolean isWatchEvent = false;

        @Override
        protected HsExpandableRecyclerView getHsRecyclerView() {
            return mHsRecyclerView;
        }

        void setHsRecyclerView(HsExpandableRecyclerView recyclerView){
            this.mHsRecyclerView = recyclerView;
        }

        @UiThread
        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpanded = false;
        }

        @UiThread
        public P getParent() {
            return mParent;
        }
        @UiThread
        public int getParentAdapterPosition() {
            int flatPosition = getAdapterPosition();
            if (flatPosition == RecyclerView.NO_POSITION) {
                return flatPosition;
            }
            return mExpandableAdapter.getNearestParentPosition(flatPosition);
        }

        @UiThread
        public void setMainItemClickToExpand() {
            isWatchEvent = true;
        }
        @UiThread
        public boolean isExpanded() {
            return mExpanded;
        }

        @UiThread
        public void setExpanded(boolean expanded) {
            mExpanded = expanded;
        }
        @UiThread
        public void onExpansionToggled(boolean expanded) {

        }
        @UiThread
        void setParentViewHolderExpandCollapseListener(ParentViewHolderExpandCollapseListener parentViewHolderExpandCollapseListener) {
            mParentViewHolderExpandCollapseListener = parentViewHolderExpandCollapseListener;
        }

        @Override
        public void onClick(View view) {
            getHsRecyclerView().expandableItemClick(this, getItemView(), getParentAdapterPosition(), -1);
            if(isWatchEvent){
                if(mExpanded){
                    collapseView();
                }else{
                    expandView();
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return getHsRecyclerView().expandableItemLongClick(this, getItemView(), getParentAdapterPosition(), -1);
        }

        @UiThread
        public boolean shouldItemViewClickToggleExpansion() {
            return true;
        }
        @UiThread
        protected void expandView() {
            setExpanded(true);
            onExpansionToggled(false);

            if (mParentViewHolderExpandCollapseListener != null) {
                mParentViewHolderExpandCollapseListener.onParentExpanded(getAdapterPosition());
            }
        }
        @UiThread
        protected void collapseView() {
            setExpanded(false);
            onExpansionToggled(true);

            if (mParentViewHolderExpandCollapseListener != null) {
                mParentViewHolderExpandCollapseListener.onParentCollapsed(getAdapterPosition());
            }
        }
    }



    public static class ChildViewHolder<C> extends HsViewHolder {
        C mChild;
        HsExpandableAdapter mExpandableAdapter;
        private HsExpandableRecyclerView mHsRecyclerView;

        @Override
        protected HsExpandableRecyclerView getHsRecyclerView() {
            return mHsRecyclerView;
        }

        void setHsRecyclerView(HsExpandableRecyclerView recyclerView){
            this.mHsRecyclerView = recyclerView;
        }

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @UiThread
        public C getChild() {
            return mChild;
        }

        @UiThread
        public int getParentAdapterPosition() {
            int flatPosition = getAdapterPosition();
            if (mExpandableAdapter == null || flatPosition == RecyclerView.NO_POSITION) {
                return RecyclerView.NO_POSITION;
            }

            return mExpandableAdapter.getNearestParentPosition(flatPosition);
        }

        @UiThread
        public int getChildAdapterPosition() {
            int flatPosition = getAdapterPosition();
            if (mExpandableAdapter == null || flatPosition == RecyclerView.NO_POSITION) {
                return RecyclerView.NO_POSITION;
            }

            return mExpandableAdapter.getChildPosition(flatPosition);
        }

        @Override
        public boolean onLongClick(View view) {
            return getHsRecyclerView().expandableItemLongClick(this, getItemView(), getParentAdapterPosition(), getChildAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            getHsRecyclerView().expandableItemClick(this, getItemView(), getParentAdapterPosition(), getChildAdapterPosition());
        }
    }



    /**
     * Wrapper used to link metadata with a list item.
     *
     * @param <P> Parent list item
     * @param <C> Child list item
     */
    public static class ExpandableWrapper<P extends Parent<C>, C> {

        private P mParent;
        private C mChild;
        private boolean mWrappedParent;
        private boolean mExpanded;

        private List<ExpandableWrapper<P, C>> mWrappedChildList;

        /**
         * Constructor to wrap a parent object of type {@link P}.
         *
         * @param parent The parent object to wrap
         */
        public ExpandableWrapper(@NonNull P parent) {
            mParent = parent;
            mWrappedParent = true;
            mExpanded = false;

            mWrappedChildList = generateChildItemList(parent);
        }

        /**
         * Constructor to wrap a child object of type {@link C}
         *
         * @param child The child object to wrap
         */
        public ExpandableWrapper(@NonNull C child) {
            mChild = child;
            mWrappedParent = false;
            mExpanded = false;
        }

        public P getParent() {
            return mParent;
        }

        public void setParent(@NonNull P parent) {
            mParent = parent;
            mWrappedChildList = generateChildItemList(parent);
        }

        public C getChild() {
            return mChild;
        }

        public boolean isExpanded() {
            return mExpanded;
        }

        public void setExpanded(boolean expanded) {
            mExpanded = expanded;
        }

        public boolean isParent() {
            return mWrappedParent;
        }

        /**
         * @return The initial expanded state of a parent
         * @throws IllegalStateException If a parent isn't being wrapped
         */
        public boolean isParentInitiallyExpanded() {
            if (!mWrappedParent) {
                throw new IllegalStateException("Parent not wrapped");
            }

            return mParent.isInitiallyExpanded();
        }

        /**
         * @return The list of children of a parent
         * @throws IllegalStateException If a parent isn't being wrapped
         */
        public List<ExpandableWrapper<P, C>> getWrappedChildList() {
            if (!mWrappedParent) {
                throw new IllegalStateException("Parent not wrapped");
            }

            return mWrappedChildList;
        }

        private List<ExpandableWrapper<P, C>> generateChildItemList(P parentListItem) {
            List<ExpandableWrapper<P, C>> childItemList = new ArrayList<>();

            for (C child : parentListItem.getChildList()) {
                childItemList.add(new ExpandableWrapper<P, C>(child));
            }

            return childItemList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final ExpandableWrapper<?, ?> that = (ExpandableWrapper<?, ?>) o;

            if (mParent != null ? !mParent.equals(that.mParent) : that.mParent != null)
                return false;
            return mChild != null ? mChild.equals(that.mChild) : that.mChild == null;

        }

        @Override
        public int hashCode() {
            int result = mParent != null ? mParent.hashCode() : 0;
            result = 31 * result + (mChild != null ? mChild.hashCode() : 0);
            return result;
        }
    }


    public static abstract class HsExpandableAdapter<P extends Parent<C>, C, PVH extends ParentViewHolder, CVH extends ChildViewHolder>
            extends HsAdapter{
        private static final String EXPANDED_STATE_MAP = "ExpandableRecyclerAdapter.ExpandedStateMap";
        /**
         * Default ViewType for parent rows
         */
        public static final int TYPE_PARENT = 0;
        /**
         * Default ViewType for children rows
         */
        public static final int TYPE_CHILD = 1;
        /**
         * Start of user-defined view types
         */
        public static final int TYPE_FIRST_USER = 2;
        private static final int INVALID_FLAT_POSITION = -1;



        @NonNull
        protected List<ExpandableWrapper<P, C>> mFlatItemList;

        @NonNull
        private List<P> mParentList;

        @Nullable
        private ExpandCollapseListener mExpandCollapseListener;

        @NonNull
        private List<RecyclerView> mAttachedRecyclerViewPool;

        private Map<P, Boolean> mExpansionStateMap;



        public HsExpandableAdapter(@NonNull List<P> parentList) {
            super();
            mParentList = parentList;
            mFlatItemList = generateFlattenedParentChildList(parentList);
            mAttachedRecyclerViewPool = new ArrayList<>();
            mExpansionStateMap = new HashMap<>(mParentList.size());
        }

        @Override
        public HsViewHolder onCreateHsViewHolder(ViewGroup parent, int viewType) {
            if (isParentViewType(viewType)) {
                PVH pvh = onCreateParentHsViewHolder(parent, viewType);
                pvh.setParentViewHolderExpandCollapseListener(mParentViewHolderExpandCollapseListener);
                pvh.mExpandableAdapter = this;
                return pvh;
            } else {
                CVH cvh = onCreateChildHsViewHolder(parent, viewType);
                cvh.mExpandableAdapter = this;
                return cvh;
            }
        }

        @Override
        public void onBindHsViewHolder(HsViewHolder holder, int flatPosition, boolean isChecked) {
            if (flatPosition > mFlatItemList.size()) {
                throw new IllegalStateException("Trying to bind item out of bounds, size " + mFlatItemList.size()
                        + " flatPosition " + flatPosition + ". Was the data changed without a call to notify...()?");
            }

            ExpandableWrapper<P, C> listItem = mFlatItemList.get(flatPosition);
            if (listItem.isParent()) {
                PVH parentViewHolder = (PVH) holder;
                parentViewHolder.setHsRecyclerView((HsExpandableRecyclerView) getRecyclerView());

                if (parentViewHolder.shouldItemViewClickToggleExpansion()) {
                    parentViewHolder.setMainItemClickToExpand();
                }

                parentViewHolder.setExpanded(listItem.isExpanded());
                parentViewHolder.mParent = listItem.getParent();
                onBindParentHsViewHolder(parentViewHolder, getNearestParentPosition(flatPosition), isChecked, listItem.getParent());
            } else {
                CVH childViewHolder = (CVH) holder;
                childViewHolder.setHsRecyclerView((HsExpandableRecyclerView) getRecyclerView());
                childViewHolder.mChild = listItem.getChild();
                onBindChildHsViewHolder(childViewHolder, getNearestParentPosition(flatPosition), getChildPosition(flatPosition), isChecked, listItem.getChild());
            }
        }

        @NonNull
        @UiThread
        public abstract PVH onCreateParentHsViewHolder(@NonNull ViewGroup parentViewGroup, int viewType);

        @NonNull
        @UiThread
        public abstract CVH onCreateChildHsViewHolder(@NonNull ViewGroup childViewGroup, int viewType);

        @UiThread
        public abstract void onBindParentHsViewHolder(@NonNull PVH parentViewHolder, int parentPosition, boolean isChecked, @NonNull P parent);

        @UiThread
        public abstract void onBindChildHsViewHolder(@NonNull CVH childViewHolder, int parentPosition, int childPosition, boolean isChecked, @NonNull C child);

        @Override
        @UiThread
        public int getHsItemCount() {
            return mFlatItemList.size();
        }

        @Override
        @UiThread
        public int getItemViewType(int flatPosition) {
            ExpandableWrapper<P, C> listItem = mFlatItemList.get(flatPosition);
            if (listItem.isParent()) {
                return getParentViewType(getNearestParentPosition(flatPosition));
            } else {
                return getChildViewType(getNearestParentPosition(flatPosition), getChildPosition(flatPosition));
            }
        }

        public int getParentViewType(int parentPosition) {
            return TYPE_PARENT;
        }


        public int getChildViewType(int parentPosition, int childPosition) {
            return TYPE_CHILD;
        }

        public boolean isParentViewType(int viewType) {
            return viewType == TYPE_PARENT;
        }

        @NonNull
        @UiThread
        public List<P> getParentList() {
            return mParentList;
        }

        public List<C> getChildList(int parentPosition){
            return mParentList.get(parentPosition).getChildList();
        }

        public int getParentListSize(){
            return getParentList().size();
        }

        public int getChildListSize(int parentPosition){
            return getChildList(parentPosition).size();
        }

        public P getParentItem(int parentPosition){
            return getParentList().get(parentPosition);
        }

        public C getChildItem(int parentPosition, int childPosition){
            return getParentItem(parentPosition).getChildList().get(childPosition);
        }

        public void removeChildItem(int parentPosition, int childPosition){
            getChildList(parentPosition).remove(childPosition);
            notifyChildRemoved(parentPosition,childPosition);
        }

        @UiThread
        public void setParentList(@NonNull List<P> parentList, boolean preserveExpansionState) {
            mParentList = parentList;
            notifyParentDataSetChanged(preserveExpansionState);
        }

        @Override
        @UiThread
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mAttachedRecyclerViewPool.add(recyclerView);
        }


        /**
         * Implementation of Adapter.onDetachedFromRecyclerView(RecyclerView)
         * <p>
         * Called when this ExpandableRecyclerAdapter is detached from a RecyclerView.
         *
         * @param recyclerView The {@code RecyclerView} this {@code ExpandableRecyclerAdapter}
         *                     is being detached from
         */
        @Override
        @UiThread
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mAttachedRecyclerViewPool.remove(recyclerView);
        }

        @UiThread
        public void setExpandCollapseListener(@Nullable ExpandCollapseListener expandCollapseListener) {
            mExpandCollapseListener = expandCollapseListener;
        }

        /**
         * Called when a ParentViewHolder has triggered an expansion for it's parent
         *
         * @param flatParentPosition the position of the parent that is calling to be expanded
         */
        @UiThread
        protected void parentExpandedFromViewHolder(int flatParentPosition) {
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            updateExpandedParent(parentWrapper, flatParentPosition, true);
        }

        /**
         * Called when a ParentViewHolder has triggered a collapse for it's parent
         *
         * @param flatParentPosition the position of the parent that is calling to be collapsed
         */
        @UiThread
        protected void parentCollapsedFromViewHolder(int flatParentPosition) {
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            updateCollapsedParent(parentWrapper, flatParentPosition, true);
        }

        private ParentViewHolderExpandCollapseListener mParentViewHolderExpandCollapseListener = new ParentViewHolderExpandCollapseListener() {

            /**
             * <p>
             * Called when a {@link P} is triggered to expand.
             *
             * @param flatParentPosition The index of the item in the list being expanded, relative to the flattened list
             */
            @Override
            @UiThread
            public void onParentExpanded(int flatParentPosition) {
                parentExpandedFromViewHolder(flatParentPosition);
            }

            /**
             * <p>
             * Called when a {@link P} is triggered to collapse.
             *
             * @param flatParentPosition The index of the item in the list being collapsed, relative to the flattened list
             */
            @Override
            @UiThread
            public void onParentCollapsed(int flatParentPosition) {
                parentCollapsedFromViewHolder(flatParentPosition);
            }
        };

        // region Programmatic Expansion/Collapsing

        /**
         * Expands the parent associated with a specified {@link P} in the list of parents.
         *
         * @param parent The {@code P} of the parent to expand
         */
        @UiThread
        public void expandParent(@NonNull P parent) {
            ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);
            int flatParentPosition = mFlatItemList.indexOf(parentWrapper);
            if (flatParentPosition == INVALID_FLAT_POSITION) {
                return;
            }

            expandViews(mFlatItemList.get(flatParentPosition), flatParentPosition);
        }

        /**
         * Expands the parent with the specified index in the list of parents.
         *
         * @param parentPosition The position of the parent to expand
         */
        @UiThread
        public void expandParent(int parentPosition) {
            expandParent(mParentList.get(parentPosition));
        }

        /**
         * Expands all parents in a range of indices in the list of parents.
         *
         * @param startParentPosition The index at which to to start expanding parents
         * @param parentCount The number of parents to expand
         */
        @UiThread
        public void expandParentRange(int startParentPosition, int parentCount) {
            int endParentPosition = startParentPosition + parentCount;
            for (int i = startParentPosition; i < endParentPosition; i++) {
                expandParent(i);
            }
        }

        /**
         * Expands all parents in the list.
         */
        @UiThread
        public void expandAllParents() {
            for (P parent : mParentList) {
                expandParent(parent);
            }
        }

        /**
         * Collapses the parent associated with a specified {@link P} in the list of parents.
         *
         * @param parent The {@code P} of the parent to collapse
         */
        @UiThread
        public void collapseParent(@NonNull P parent) {
            ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);
            int flatParentPosition = mFlatItemList.indexOf(parentWrapper);
            if (flatParentPosition == INVALID_FLAT_POSITION) {
                return;
            }

            collapseViews(mFlatItemList.get(flatParentPosition), flatParentPosition);
        }

        /**
         * Collapses the parent with the specified index in the list of parents.
         *
         * @param parentPosition The index of the parent to collapse
         */
        @UiThread
        public void collapseParent(int parentPosition) {
            collapseParent(mParentList.get(parentPosition));
        }

        /**
         * Collapses all parents in a range of indices in the list of parents.
         *
         * @param startParentPosition The index at which to to start collapsing parents
         * @param parentCount The number of parents to collapse
         */
        @UiThread
        public void collapseParentRange(int startParentPosition, int parentCount) {
            int endParentPosition = startParentPosition + parentCount;
            for (int i = startParentPosition; i < endParentPosition; i++) {
                collapseParent(i);
            }
        }

        /**
         * Collapses all parents in the list.
         */
        @UiThread
        public void collapseAllParents() {
            for (P parent : mParentList) {
                collapseParent(parent);
            }
        }

        /**
         * Stores the expanded state map across state loss.
         * <p>
         * Should be called from {@link Activity#onSaveInstanceState(Bundle)} in
         * the {@link Activity} that hosts the RecyclerView that this
         * <p>
         * This will make sure to add the expanded state map as an extra to the
         * instance state bundle to be used in {@link #onRestoreInstanceState(Bundle)}.
         *
         * @param savedInstanceState The {@code Bundle} into which to store the
         *                           expanded state map
         */
        @UiThread
        public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
            savedInstanceState.putSerializable(EXPANDED_STATE_MAP, generateExpandedStateMap());
        }

        /**
         * Fetches the expandable state map from the saved instance state {@link Bundle}
         * and restores the expanded states of all of the parents.
         * <p>
         * Should be called from {@link Activity#onRestoreInstanceState(Bundle)} in
         * the {@link Activity} that hosts the RecyclerView that this
         * <p>
         * Assumes that the list of parents is the same as when the saved
         * instance state was stored.
         *
         * @param savedInstanceState The {@code Bundle} from which the expanded
         *                           state map is loaded
         */
        @SuppressWarnings("unchecked")
        @UiThread
        public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
            if (savedInstanceState == null
                    || !savedInstanceState.containsKey(EXPANDED_STATE_MAP)) {
                return;
            }

            HashMap<Integer, Boolean> expandedStateMap = (HashMap<Integer, Boolean>) savedInstanceState.getSerializable(EXPANDED_STATE_MAP);
            if (expandedStateMap == null) {
                return;
            }

            List<ExpandableWrapper<P, C>> itemList = new ArrayList<>();
            int parentsCount = mParentList.size();
            for (int i = 0; i < parentsCount; i++) {
                ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(mParentList.get(i));
                itemList.add(parentWrapper);

                if (expandedStateMap.containsKey(i)) {
                    boolean expanded = expandedStateMap.get(i);
                    parentWrapper.setExpanded(expanded);

                    if (expanded) {
                        List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
                        int childrenCount = wrappedChildList.size();
                        for (int j = 0; j < childrenCount; j++) {
                            ExpandableWrapper<P, C> childWrapper = wrappedChildList.get(j);
                            itemList.add(childWrapper);
                        }
                    }
                }
            }

            mFlatItemList = itemList;

            notifyDataSetChanged();
        }

        /**
         * Calls through to the ParentViewHolder to expand views for each
         * RecyclerView the specified parent is a child of.
         * <p>
         * These calls to the ParentViewHolder are made so that animations can be
         * triggered at the ViewHolder level.
         *
         * @param flatParentPosition The index of the parent to expand
         */
        @SuppressWarnings("unchecked")
        @UiThread
        private void expandViews(@NonNull ExpandableWrapper<P, C> parentWrapper, int flatParentPosition) {
            PVH viewHolder;
            for (RecyclerView recyclerView : mAttachedRecyclerViewPool) {
                viewHolder = (PVH) recyclerView.findViewHolderForAdapterPosition(flatParentPosition);
                if (viewHolder != null && !viewHolder.isExpanded()) {
                    viewHolder.setExpanded(true);
                    viewHolder.onExpansionToggled(false);
                }
            }

            updateExpandedParent(parentWrapper, flatParentPosition, false);
        }

        /**
         * Calls through to the ParentViewHolder to collapse views for each
         * RecyclerView a specified parent is a child of.
         * <p>
         * These calls to the ParentViewHolder are made so that animations can be
         * triggered at the ViewHolder level.
         *
         * @param flatParentPosition The index of the parent to collapse
         */
        @SuppressWarnings("unchecked")
        @UiThread
        private void collapseViews(@NonNull ExpandableWrapper<P, C> parentWrapper, int flatParentPosition) {
            PVH viewHolder;
            for (RecyclerView recyclerView : mAttachedRecyclerViewPool) {
                viewHolder = (PVH) recyclerView.findViewHolderForAdapterPosition(flatParentPosition);
                if (viewHolder != null && viewHolder.isExpanded()) {
                    viewHolder.setExpanded(false);
                    viewHolder.onExpansionToggled(true);
                }
            }

            updateCollapsedParent(parentWrapper, flatParentPosition, false);
        }

        /**
         * Expands a specified parent. Calls through to the
         * ExpandCollapseListener and adds children of the specified parent to the
         * flat list of items.
         *
         * @param parentWrapper The ExpandableWrapper of the parent to expand
         * @param flatParentPosition The index of the parent to expand
         * @param expansionTriggeredByListItemClick true if expansion was triggered
         *                                          by a click event, false otherwise.
         */
        @UiThread
        private void updateExpandedParent(@NonNull ExpandableWrapper<P, C> parentWrapper, int flatParentPosition, boolean expansionTriggeredByListItemClick) {
            if (parentWrapper.isExpanded()) {
                return;
            }

            parentWrapper.setExpanded(true);
            mExpansionStateMap.put(parentWrapper.getParent(), true);

            List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
            if (wrappedChildList != null) {
                int childCount = wrappedChildList.size();
                for (int i = 0; i < childCount; i++) {
                    mFlatItemList.add(flatParentPosition + i + 1, wrappedChildList.get(i));
                }

                notifyItemRangeInserted(flatParentPosition + 1, childCount);
            }

            if (expansionTriggeredByListItemClick && mExpandCollapseListener != null) {
                mExpandCollapseListener.onParentExpanded(getNearestParentPosition(flatParentPosition));
            }
        }

        /**
         * Collapses a specified parent item. Calls through to the
         * ExpandCollapseListener and removes children of the specified parent from the
         * flat list of items.
         *
         * @param parentWrapper The ExpandableWrapper of the parent to collapse
         * @param flatParentPosition The index of the parent to collapse
         * @param collapseTriggeredByListItemClick true if expansion was triggered
         *                                         by a click event, false otherwise.
         */
        @UiThread
        private void updateCollapsedParent(@NonNull ExpandableWrapper<P, C> parentWrapper, int flatParentPosition, boolean collapseTriggeredByListItemClick) {
            if (!parentWrapper.isExpanded()) {
                return;
            }

            parentWrapper.setExpanded(false);
            mExpansionStateMap.put(parentWrapper.getParent(), false);

            List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
            if (wrappedChildList != null) {
                int childCount = wrappedChildList.size();
                for (int i = childCount - 1; i >= 0; i--) {
                    mFlatItemList.remove(flatParentPosition + i + 1);
                }

                notifyItemRangeRemoved(flatParentPosition + 1, childCount);
            }

            if (collapseTriggeredByListItemClick && mExpandCollapseListener != null) {
                mExpandCollapseListener.onParentCollapsed(getNearestParentPosition(flatParentPosition));
            }
        }

        /**
         * Given the index relative to the entire RecyclerView, returns the nearest
         * ParentPosition without going past the given index.
         * <p>
         * If it is the index of a parent, will return the corresponding parent position.
         * If it is the index of a child within the RV, will return the position of that child's parent.
         */
        @UiThread
        int getNearestParentPosition(int flatPosition) {
            if (flatPosition == 0) {
                return 0;
            }

            int parentCount = -1;
            for (int i = 0; i <= flatPosition; i++) {
                ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
                if (listItem.isParent()) {
                    parentCount++;
                }
            }
            return parentCount;
        }

        /**
         * Given the index relative to the entire RecyclerView for a child item,
         * returns the child position within the child list of the parent.
         */
        @UiThread
        int getChildPosition(int flatPosition) {
            if (flatPosition == 0) {
                return 0;
            }

            int childCount = 0;
            for (int i = 0; i < flatPosition; i++) {
                ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
                if (listItem.isParent()) {
                    childCount = 0;
                } else {
                    childCount++;
                }
            }
            return childCount;
        }

        // endregion

        // region Data Manipulation

        /**
         * Notify any registered observers that the data set has changed.
         * <p>
         * This event does not specify what about the data set has changed, forcing
         * any observers to assume that all existing items and structure may no longer be valid.
         * LayoutManagers will be forced to fully rebind and relayout all visible views.</p>
         * <p>
         * It will always be more efficient to use the more specific change events if you can.
         * Rely on {@code #notifyParentDataSetChanged(boolean)} as a last resort. There will be no animation
         * of changes, unlike the more specific change events listed below.
         *
         * @see #notifyParentInserted(int)
         * @see #notifyParentRemoved(int)
         * @see #notifyParentChanged(int)
         * @see #notifyParentRangeInserted(int, int)
         * @see #notifyChildInserted(int, int)
         * @see #notifyChildRemoved(int, int)
         * @see #notifyChildChanged(int, int)
         *
         * @param preserveExpansionState If true, the adapter will attempt to preserve your parent's last expanded
         *                               state. This depends on object equality for comparisons of
         *                               old parents to parents in the new list.
         *
         *                               If false, only {@link Parent#isInitiallyExpanded()}
         *                               will be used to determine expanded state.
         *
         */
        @UiThread
        public void notifyParentDataSetChanged(boolean preserveExpansionState) {
            if (preserveExpansionState) {
                mFlatItemList = generateFlattenedParentChildList(mParentList, mExpansionStateMap);
            } else {
                mFlatItemList = generateFlattenedParentChildList(mParentList);
            }
            notifyDataSetChanged();
        }

        /**
         * Notify any registered observers that the parent reflected at {@code parentPosition}
         * has been newly inserted. The parent previously at {@code parentPosition} is now at
         * position {@code parentPosition + 1}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their
         * positions may be altered.
         *
         * @param parentPosition Position of the newly inserted parent in the data set, relative
         *                       to the list of parents only.
         *
         * @see #notifyParentRangeInserted(int, int)
         */
        @UiThread
        public void notifyParentInserted(int parentPosition) {
            P parent = mParentList.get(parentPosition);

            int flatParentPosition;
            if (parentPosition < mParentList.size() - 1) {
                flatParentPosition = getFlatParentPosition(parentPosition);
            } else {
                flatParentPosition = mFlatItemList.size();
            }

            int sizeChanged = addParentWrapper(flatParentPosition, parent);
            notifyItemRangeInserted(flatParentPosition, sizeChanged);
        }

        /**
         * Notify any registered observers that the currently reflected {@code itemCount}
         * parents starting at {@code parentPositionStart} have been newly inserted.
         * The parents previously located at {@code parentPositionStart} and beyond
         * can now be found starting at position {@code parentPositionStart + itemCount}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their positions
         * may be altered.
         *
         * @param parentPositionStart Position of the first parent that was inserted, relative
         *                            to the list of parents only.
         * @param itemCount Number of items inserted
         *
         * @see #notifyParentInserted(int)
         */
        @UiThread
        public void notifyParentRangeInserted(int parentPositionStart, int itemCount) {
            int initialFlatParentPosition;
            if (parentPositionStart < mParentList.size() - itemCount) {
                initialFlatParentPosition = getFlatParentPosition(parentPositionStart);
            } else {
                initialFlatParentPosition = mFlatItemList.size();
            }

            int sizeChanged = 0;
            int flatParentPosition = initialFlatParentPosition;
            int changed;
            int parentPositionEnd = parentPositionStart + itemCount;
            for (int i = parentPositionStart; i < parentPositionEnd; i++) {
                P parent = mParentList.get(i);
                changed = addParentWrapper(flatParentPosition, parent);
                flatParentPosition += changed;
                sizeChanged += changed;
            }

            notifyItemRangeInserted(initialFlatParentPosition, sizeChanged);
        }

        @UiThread
        private int addParentWrapper(int flatParentPosition, P parent) {
            int sizeChanged = 1;
            ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);
            mFlatItemList.add(flatParentPosition, parentWrapper);
            if (parentWrapper.isParentInitiallyExpanded()) {
                parentWrapper.setExpanded(true);
                List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
                mFlatItemList.addAll(flatParentPosition + sizeChanged, wrappedChildList);
                sizeChanged += wrappedChildList.size();
            }
            return sizeChanged;
        }

        /**
         * Notify any registered observers that the parents previously located at {@code parentPosition}
         * has been removed from the data set. The parents previously located at and after
         * {@code parentPosition} may now be found at {@code oldPosition - 1}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their positions
         * may be altered.
         *
         * @param parentPosition Position of the parent that has now been removed, relative
         *                       to the list of parents only.
         */
        @UiThread
        public void notifyParentRemoved(int parentPosition) {
            int flatParentPosition = getFlatParentPosition(parentPosition);
            int sizeChanged = removeParentWrapper(flatParentPosition);

            notifyItemRangeRemoved(flatParentPosition, sizeChanged);
        }

        /**
         * Notify any registered observers that the {@code itemCount} parents previously
         * located at {@code parentPositionStart} have been removed from the data set. The parents
         * previously located at and after {@code parentPositionStart + itemCount} may now be
         * found at {@code oldPosition - itemCount}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their positions
         * may be altered.
         *
         * @param parentPositionStart The previous position of the first parent that was
         *                            removed, relative to list of parents only.
         * @param itemCount Number of parents removed from the data set
         */
        public void notifyParentRangeRemoved(int parentPositionStart, int itemCount) {
            int sizeChanged = 0;
            int flatParentPositionStart = getFlatParentPosition(parentPositionStart);
            for (int i = 0; i < itemCount; i++) {
                sizeChanged += removeParentWrapper(flatParentPositionStart);
            }

            notifyItemRangeRemoved(flatParentPositionStart, sizeChanged);
        }

        @UiThread
        private int removeParentWrapper(int flatParentPosition) {
            int sizeChanged = 1;
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.remove(flatParentPosition);
            if (parentWrapper.isExpanded()) {
                int childListSize = parentWrapper.getWrappedChildList().size();
                for (int i = 0; i < childListSize; i++) {
                    mFlatItemList.remove(flatParentPosition);
                    sizeChanged++;
                }
            }
            return sizeChanged;
        }

        /**
         * Notify any registered observers that the parent at {@code parentPosition} has changed.
         * This will also trigger an item changed for children of the parent list specified.
         * <p>
         * This is an item change event, not a structural change event. It indicates that any
         * reflection of the data at {@code parentPosition} is out of date and should be updated.
         * The parent at {@code parentPosition} retains the same identity. This means
         * the number of children must stay the same.
         *
         * @param parentPosition Position of the item that has changed
         */
        @UiThread
        public void notifyParentChanged(int parentPosition) {
            P parent = mParentList.get(parentPosition);
            int flatParentPositionStart = getFlatParentPosition(parentPosition);
            int sizeChanged = changeParentWrapper(flatParentPositionStart, parent);

            notifyItemRangeChanged(flatParentPositionStart, sizeChanged);
        }

        /**
         * Notify any registered observers that the {@code itemCount} parents starting
         * at {@code parentPositionStart} have changed. This will also trigger an item changed
         * for children of the parent list specified.
         * <p>
         * This is an item change event, not a structural change event. It indicates that any
         * reflection of the data in the given position range is out of date and should be updated.
         * The parents in the given range retain the same identity. This means that the number of
         * children must stay the same.
         *
         * @param parentPositionStart Position of the item that has changed
         * @param itemCount Number of parents changed in the data set
         */
        @UiThread
        public void notifyParentRangeChanged(int parentPositionStart, int itemCount) {
            int flatParentPositionStart = getFlatParentPosition(parentPositionStart);

            int flatParentPosition = flatParentPositionStart;
            int sizeChanged = 0;
            int changed;
            P parent;
            for (int j = 0; j < itemCount; j++) {
                parent = mParentList.get(parentPositionStart);
                changed = changeParentWrapper(flatParentPosition, parent);
                sizeChanged += changed;
                flatParentPosition += changed;
                parentPositionStart++;
            }
            notifyItemRangeChanged(flatParentPositionStart, sizeChanged);
        }

        private int changeParentWrapper(int flatParentPosition, P parent) {
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            parentWrapper.setParent(parent);
            int sizeChanged = 1;
            if (parentWrapper.isExpanded()) {
                List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
                int childSize = wrappedChildList.size();
                for (int i = 0; i < childSize; i++) {
                    mFlatItemList.set(flatParentPosition + i + 1, wrappedChildList.get(i));
                    sizeChanged++;
                }
            }

            return sizeChanged;
        }

        /**
         * Notify any registered observers that the parent and its children reflected at
         * {@code fromParentPosition} has been moved to {@code toParentPosition}.
         * <p>
         * <p>This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their
         * positions may be altered.</p>
         *
         * @param fromParentPosition Previous position of the parent, relative to the list of
         *                           parents only.
         * @param toParentPosition New position of the parent, relative to the list of parents only.
         */
        @UiThread
        public void notifyParentMoved(int fromParentPosition, int toParentPosition) {
            int fromFlatParentPosition = getFlatParentPosition(fromParentPosition);
            ExpandableWrapper<P, C> fromParentWrapper = mFlatItemList.get(fromFlatParentPosition);

            // If the parent is collapsed we can take advantage of notifyItemMoved otherwise
            // we are forced to do a "manual" move by removing and then adding the parent + children
            // (no notifyItemRangeMovedAvailable)
            boolean isCollapsed = !fromParentWrapper.isExpanded();
            boolean isExpandedNoChildren = !isCollapsed && (fromParentWrapper.getWrappedChildList().size() == 0);
            if (isCollapsed || isExpandedNoChildren) {
                int toFlatParentPosition = getFlatParentPosition(toParentPosition);
                ExpandableWrapper<P, C> toParentWrapper = mFlatItemList.get(toFlatParentPosition);
                mFlatItemList.remove(fromFlatParentPosition);
                int childOffset = 0;
                if (toParentWrapper.isExpanded()) {
                    childOffset = toParentWrapper.getWrappedChildList().size();
                }
                mFlatItemList.add(toFlatParentPosition + childOffset, fromParentWrapper);

                notifyItemMoved(fromFlatParentPosition, toFlatParentPosition + childOffset);
            } else {
                // Remove the parent and children
                int sizeChanged = 0;
                int childListSize = fromParentWrapper.getWrappedChildList().size();
                for (int i = 0; i < childListSize + 1; i++) {
                    mFlatItemList.remove(fromFlatParentPosition);
                    sizeChanged++;
                }
                notifyItemRangeRemoved(fromFlatParentPosition, sizeChanged);


                // Add the parent and children at new position
                int toFlatParentPosition = getFlatParentPosition(toParentPosition);
                int childOffset = 0;
                if (toFlatParentPosition != INVALID_FLAT_POSITION) {
                    ExpandableWrapper<P, C> toParentWrapper = mFlatItemList.get(toFlatParentPosition);
                    if (toParentWrapper.isExpanded()) {
                        childOffset = toParentWrapper.getWrappedChildList().size();
                    }
                } else {
                    toFlatParentPosition = mFlatItemList.size();
                }

                mFlatItemList.add(toFlatParentPosition + childOffset, fromParentWrapper);
                List<ExpandableWrapper<P, C>> wrappedChildList = fromParentWrapper.getWrappedChildList();
                sizeChanged = wrappedChildList.size() + 1;

                mFlatItemList.addAll(toFlatParentPosition + childOffset + 1, wrappedChildList);

                notifyItemRangeInserted(toFlatParentPosition + childOffset, sizeChanged);
            }
        }

        /**
         * Notify any registered observers that the parent reflected at {@code parentPosition}
         * has a child list item that has been newly inserted at {@code childPosition}.
         * The child list item previously at {@code childPosition} is now at
         * position {@code childPosition + 1}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their
         * positions may be altered.
         *
         * @param parentPosition Position of the parent which has been added a child, relative
         *                       to the list of parents only.
         * @param childPosition Position of the child that has been inserted, relative to children
         *                      of the parent specified by {@code parentPosition} only.
         *
         */
        @UiThread
        public void notifyChildInserted(int parentPosition, int childPosition) {
            int flatParentPosition = getFlatParentPosition(parentPosition);
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);

            parentWrapper.setParent(mParentList.get(parentPosition));
            if (parentWrapper.isExpanded()) {
                ExpandableWrapper<P, C> child = parentWrapper.getWrappedChildList().get(childPosition);
                mFlatItemList.add(flatParentPosition + childPosition + 1, child);
                notifyItemInserted(flatParentPosition + childPosition + 1);
            }
        }

        /**
         * Notify any registered observers that the parent reflected at {@code parentPosition}
         * has {@code itemCount} child list items that have been newly inserted at {@code childPositionStart}.
         * The child list item previously at {@code childPositionStart} and beyond are now at
         * position {@code childPositionStart + itemCount}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their
         * positions may be altered.
         *
         * @param parentPosition Position of the parent which has been added a child, relative
         *                       to the list of parents only.
         * @param childPositionStart Position of the first child that has been inserted,
         *                           relative to children of the parent specified by
         *                           {@code parentPosition} only.
         * @param itemCount          number of children inserted
         */
        @UiThread
        public void notifyChildRangeInserted(int parentPosition, int childPositionStart, int itemCount) {
            int flatParentPosition = getFlatParentPosition(parentPosition);
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);

            parentWrapper.setParent(mParentList.get(parentPosition));
            if (parentWrapper.isExpanded()) {
                List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
                for (int i = 0; i < itemCount; i++) {
                    ExpandableWrapper<P, C> child = wrappedChildList.get(childPositionStart + i);
                    mFlatItemList.add(flatParentPosition + childPositionStart + i + 1, child);
                }
                notifyItemRangeInserted(flatParentPosition + childPositionStart + 1, itemCount);
            }
        }

        /**
         * Notify any registered observers that the parent located at {@code parentPosition}
         * has a child that has been removed from the data set, previously located at {@code childPosition}.
         * The child list item previously located at and after {@code childPosition} may
         * now be found at {@code childPosition - 1}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their positions
         * may be altered.
         *
         * @param parentPosition Position of the parent which has a child removed from, relative
         *                       to the list of parents only.
         * @param childPosition Position of the child that has been removed, relative to children
         *                      of the parent specified by {@code parentPosition} only.
         */
        @UiThread
        public void notifyChildRemoved(int parentPosition, int childPosition) {
            int flatParentPosition = getFlatParentPosition(parentPosition);
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            parentWrapper.setParent(mParentList.get(parentPosition));

            if (parentWrapper.isExpanded()) {
                mFlatItemList.remove(flatParentPosition + childPosition + 1);
                notifyItemRemoved(flatParentPosition + childPosition + 1);
            }
        }

        /**
         * Notify any registered observers that the parent located at {@code parentPosition}
         * has {@code itemCount} children that have been removed from the data set, previously
         * located at {@code childPositionStart} onwards. The child previously located at and
         * after {@code childPositionStart} may now be found at {@code childPositionStart - itemCount}.
         * <p>
         * This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their positions
         * may be altered.
         *
         * @param parentPosition Position of the parent which has a child removed from, relative
         *                       to the list of parents only.
         * @param childPositionStart Position of the first child that has been removed, relative
         *                           to children of the parent specified by {@code parentPosition} only.
         * @param itemCount number of children removed
         */
        @UiThread
        public void notifyChildRangeRemoved(int parentPosition, int childPositionStart, int itemCount) {
            int flatParentPosition = getFlatParentPosition(parentPosition);
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            parentWrapper.setParent(mParentList.get(parentPosition));

            if (parentWrapper.isExpanded()) {
                for (int i = 0; i < itemCount; i++) {
                    mFlatItemList.remove(flatParentPosition + childPositionStart + 1);
                }
                notifyItemRangeRemoved(flatParentPosition + childPositionStart + 1, itemCount);
            }
        }

        /**
         * Notify any registered observers that the parent at {@code parentPosition} has
         * a child located at {@code childPosition} that has changed.
         * <p>
         * This is an item change event, not a structural change event. It indicates that any
         * reflection of the data at {@code childPosition} is out of date and should be updated.
         * The parent at {@code childPosition} retains the same identity.
         *
         * @param parentPosition Position of the parent which has a child that has changed
         * @param childPosition Position of the child that has changed
         */
        @UiThread
        public void notifyChildChanged(int parentPosition, int childPosition) {
            P parent = mParentList.get(parentPosition);
            int flatParentPosition = getFlatParentPosition(parentPosition);
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            parentWrapper.setParent(parent);
            if (parentWrapper.isExpanded()) {
                int flatChildPosition = flatParentPosition + childPosition + 1;
                ExpandableWrapper<P, C> child = parentWrapper.getWrappedChildList().get(childPosition);
                mFlatItemList.set(flatChildPosition, child);
                notifyItemChanged(flatChildPosition);
            }
        }

        /**
         * Notify any registered observers that the parent at {@code parentPosition} has
         * {@code itemCount} children starting at {@code childPositionStart} that have changed.
         * <p>
         * This is an item change event, not a structural change event. It indicates that any
         * The parent at {@code childPositionStart} retains the same identity.
         * reflection of the set of {@code itemCount} children starting at {@code childPositionStart}
         * are out of date and should be updated.
         *
         * @param parentPosition Position of the parent who has a child that has changed
         * @param childPositionStart Position of the first child that has changed
         * @param itemCount number of children changed
         */
        @UiThread
        public void notifyChildRangeChanged(int parentPosition, int childPositionStart, int itemCount) {
            P parent = mParentList.get(parentPosition);
            int flatParentPosition = getFlatParentPosition(parentPosition);
            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            parentWrapper.setParent(parent);
            if (parentWrapper.isExpanded()) {
                int flatChildPosition = flatParentPosition + childPositionStart + 1;
                for (int i = 0; i < itemCount; i++) {
                    ExpandableWrapper<P, C> child
                            = parentWrapper.getWrappedChildList().get(childPositionStart + i);
                    mFlatItemList.set(flatChildPosition + i, child);
                }
                notifyItemRangeChanged(flatChildPosition, itemCount);
            }
        }

        /**
         * Notify any registered observers that the child list item contained within the parent
         * at {@code parentPosition} has moved from {@code fromChildPosition} to {@code toChildPosition}.
         * <p>
         * <p>This is a structural change event. Representations of other existing items in the
         * data set are still considered up to date and will not be rebound, though their
         * positions may be altered.</p>
         *
         * @param parentPosition Position of the parent which has a child that has moved
         * @param fromChildPosition Previous position of the child
         * @param toChildPosition New position of the child
         */
        @UiThread
        public void notifyChildMoved(int parentPosition, int fromChildPosition, int toChildPosition) {
            P parent = mParentList.get(parentPosition);
            int flatParentPosition = getFlatParentPosition(parentPosition);

            ExpandableWrapper<P, C> parentWrapper = mFlatItemList.get(flatParentPosition);
            parentWrapper.setParent(parent);
            if (parentWrapper.isExpanded()) {
                ExpandableWrapper<P, C> fromChild = mFlatItemList.remove(flatParentPosition + 1 + fromChildPosition);
                mFlatItemList.add(flatParentPosition + 1 + toChildPosition, fromChild);
                notifyItemMoved(flatParentPosition + 1 + fromChildPosition, flatParentPosition + 1 + toChildPosition);
            }
        }

        // endregion

        /**
         * Generates a full list of all parents and their children, in order.
         *
         * @param parentList A list of the parents from
         * @return A list of all parents and their children, expanded
         */
        private List<ExpandableWrapper<P, C>> generateFlattenedParentChildList(List<P> parentList) {
            List<ExpandableWrapper<P, C>> flatItemList = new ArrayList<>();

            int parentCount = parentList.size();
            for (int i = 0; i < parentCount; i++) {
                P parent = parentList.get(i);
                generateParentWrapper(flatItemList, parent, parent.isInitiallyExpanded());
            }

            return flatItemList;
        }

        /**
         * Generates a full list of all parents and their children, in order. Uses Map to preserve
         * last expanded state.
         *
         * @param parentList A list of the parents from
         * @param savedLastExpansionState A map of the last expanded state for a given parent key.
         * @return A list of all parents and their children, expanded accordingly
         */
        private List<ExpandableWrapper<P, C>> generateFlattenedParentChildList(List<P> parentList, Map<P, Boolean> savedLastExpansionState) {
            List<ExpandableWrapper<P, C>> flatItemList = new ArrayList<>();

            int parentCount = parentList.size();
            for (int i = 0; i < parentCount; i++) {
                P parent = parentList.get(i);
                Boolean lastExpandedState = savedLastExpansionState.get(parent);
                boolean shouldExpand = lastExpandedState == null ? parent.isInitiallyExpanded() : lastExpandedState;

                generateParentWrapper(flatItemList, parent, shouldExpand);
            }

            return flatItemList;
        }

        private void generateParentWrapper(List<ExpandableWrapper<P, C>> flatItemList, P parent, boolean shouldExpand) {
            ExpandableWrapper<P, C> parentWrapper = new ExpandableWrapper<>(parent);
            flatItemList.add(parentWrapper);
            if (shouldExpand) {
                generateExpandedChildren(flatItemList, parentWrapper);
            }
        }

        private void generateExpandedChildren(List<ExpandableWrapper<P, C>> flatItemList, ExpandableWrapper<P, C> parentWrapper) {
            parentWrapper.setExpanded(true);

            List<ExpandableWrapper<P, C>> wrappedChildList = parentWrapper.getWrappedChildList();
            int childCount = wrappedChildList.size();
            for (int j = 0; j < childCount; j++) {
                ExpandableWrapper<P, C> childWrapper = wrappedChildList.get(j);
                flatItemList.add(childWrapper);
            }
        }

        /**
         * Generates a HashMap used to store expanded state for items in the list
         * on configuration change or whenever onResume is called.
         *
         * @return A HashMap containing the expanded state of all parents
         */
        @NonNull
        @UiThread
        private HashMap<Integer, Boolean> generateExpandedStateMap() {
            HashMap<Integer, Boolean> parentHashMap = new HashMap<>();
            int childCount = 0;

            int listItemCount = mFlatItemList.size();
            for (int i = 0; i < listItemCount; i++) {
                if (mFlatItemList.get(i) != null) {
                    ExpandableWrapper<P, C> listItem = mFlatItemList.get(i);
                    if (listItem.isParent()) {
                        parentHashMap.put(i - childCount, listItem.isExpanded());
                    } else {
                        childCount++;
                    }
                }
            }

            return parentHashMap;
        }

        /**
         * Gets the index of a ExpandableWrapper within the helper item list based on
         * the index of the ExpandableWrapper.
         *
         * @param parentPosition The index of the parent in the list of parents
         * @return The index of the parent in the merged list of children and parents
         */
        @UiThread
        private int getFlatParentPosition(int parentPosition) {
            int parentCount = 0;
            int listItemCount = mFlatItemList.size();
            for (int i = 0; i < listItemCount; i++) {
                if (mFlatItemList.get(i).isParent()) {
                    parentCount++;

                    if (parentCount > parentPosition) {
                        return i;
                    }
                }
            }

            return INVALID_FLAT_POSITION;
        }
    }
}
