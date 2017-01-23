package kr.co.hs.widget.recyclerview.app;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import kr.co.hs.app.HsFragment;
import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * 생성된 시간 2017-01-09, Bae 에 의해 생성됨
 * 프로젝트 이름 : MobileFilter4
 * 패키지명 : kr.co.hs.widget.recyclerview.app
 */

public abstract class HsRecyclerViewFragment extends HsFragment implements IHsRecyclerViewContext, HsRecyclerView.OnItemClickListener, HsRecyclerView.OnItemLongClickListener {

    private HsRecyclerView mRecyclerView;

    @Override
    public HsRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void setRecyclerView(HsRecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mRecyclerView.setOnItemClickListener(this);
        this.mRecyclerView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean isMultiChoiceMode() {
        if(getRecyclerView() == null)
            return false;
        return getRecyclerView().getChoiceMode() == HsRecyclerView.CHOICE_MODE_MULTIPLE || getRecyclerView().getChoiceMode() == HsRecyclerView.CHOICE_MODE_MULTIPLE_MODAL;
    }

    @Override
    public void setChecked(int position, boolean check) {
        if(getRecyclerView() != null)
            getRecyclerView().setChecked(position, check);
    }

    @Override
    public boolean isChecked(int position) {
        return getRecyclerView().isChecked(position);
    }

    @Override
    public void setChoiceMode(int mode) {
        if(getRecyclerView() != null)
            getRecyclerView().setChoiceMode(HsRecyclerView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public void onItemClick(HsRecyclerView adapterView, RecyclerView.ViewHolder viewHolder, View itemView, int position) {
        if(isMultiChoiceMode()){
            //멀티선택모드이다. 선택한 아이템을 체크해주자
            adapterView.setChecked(position, !adapterView.isChecked(position));
            HsRecyclerView.HsAdapter adapter = (HsRecyclerView.HsAdapter) adapterView.getAdapter();
            if(adapter != null)
                adapter.notifyItemChanged(position);
        }
        onItemClick(adapterView, itemView, position, adapterView.isChecked(position));
    }

    @Override
    public boolean onItemLongClick(HsRecyclerView adapterView, RecyclerView.ViewHolder viewHolder, View itemView, int position) {
        if(!isMultiChoiceMode()){
            //롱터치하여 멀티선택 모드로 변환 시키면서 선택된 아이템 체크함.
            adapterView.setChoiceMode(HsRecyclerView.CHOICE_MODE_MULTIPLE);
            adapterView.setChecked(position, !adapterView.isChecked(position));
            HsRecyclerView.HsAdapter adapter = (HsRecyclerView.HsAdapter) adapterView.getAdapter();
            if(adapter != null){
                adapter.notifyDataSetChanged();
            }
        }
        //멀티선택 모드임
        return onItemLongClick(adapterView, itemView, position, adapterView.isChecked(position));
    }
}
