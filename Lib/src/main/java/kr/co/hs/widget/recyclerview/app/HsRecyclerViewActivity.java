package kr.co.hs.widget.recyclerview.app;

import android.view.View;

import kr.co.hs.app.HsActivity;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2017-01-02.
 */
public abstract class HsRecyclerViewActivity extends HsActivity implements HsRecyclerView.OnItemClickListener, HsRecyclerView.OnItemLongClickListener {

    private HsRecyclerView mRecyclerView;
    private HsRecyclerView.HsAdapter mAdapter;


    @Override
    public void onItemClick(HsRecyclerView adapterView, View itemView, int position) {
        if(isMultiChoiceMode()){
            //멀티선택모드이다. 선택한 아이템을 체크해주자
            adapterView.setChecked(position, !adapterView.isChecked(position));
            getRecyclerAdapter().notifyItemChanged(position);
        }
        onItemClick(adapterView, itemView, position, adapterView.isChecked(position));
    }

    @Override
    public boolean onItemLongClick(HsRecyclerView adapterView, View itemView, int position) {
        if(!isMultiChoiceMode()){
            //롱터치하여 멀티선택 모드로 변환 시키면서 선택된 아이템 체크함.
            adapterView.setChoiceMode(HsRecyclerView.CHOICE_MODE_MULTIPLE);
            adapterView.setChecked(position, !adapterView.isChecked(position));
            getRecyclerAdapter().notifyDataSetChanged();
        }
        //멀티선택 모드임
        return onItemLongClick(adapterView, itemView, position, adapterView.isChecked(position));
    }

    @Override
    public void onBackPressed() {
        if(isMultiChoiceMode()){
            getRecyclerView().setChoiceMode(HsRecyclerView.CHOICE_MODE_NONE);
            getRecyclerAdapter().notifyDataSetChanged();
        }else{
            super.onBackPressed();
        }
    }

    public abstract void onItemClick(HsRecyclerView recyclerView, View itemView, int position, boolean isCurrentChecked);
    public abstract boolean onItemLongClick(HsRecyclerView recyclerView, View itemView, int position, boolean isCurrentChecked);


    protected HsRecyclerView getRecyclerView(){
        if(mRecyclerView == null){
            mRecyclerView = onCreateRecyclerView();
            mRecyclerView.setOnItemClickListener(this);
            mRecyclerView.setOnItemLongClickListener(this);
        }
        return mRecyclerView;
    }

    protected HsRecyclerView.HsAdapter getRecyclerAdapter(){
        if(mAdapter == null)
            mAdapter = onCreateRecyclerAdapter();
        return mAdapter;
    }

    protected boolean isMultiChoiceMode(){
        return getRecyclerView().getChoiceMode() == HsRecyclerView.CHOICE_MODE_MULTIPLE || getRecyclerView().getChoiceMode() == HsRecyclerView.CHOICE_MODE_MULTIPLE_MODAL;
    }

    protected abstract HsRecyclerView onCreateRecyclerView();
    protected abstract HsRecyclerView.HsAdapter onCreateRecyclerAdapter();
}
