package kr.co.hs.widget.recyclerview.app;

import android.view.View;

import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * 생성된 시간 2017-01-05, Bae 에 의해 생성됨
 * 프로젝트 이름 : HsRecyclerView
 * 패키지명 : kr.co.hs.widget.recyclerview.app
 */

public interface IHsRecyclerViewContext {

    void onItemClick(HsRecyclerView recyclerView, View itemView, int position, boolean isCurrentChecked);
    boolean onItemLongClick(HsRecyclerView recyclerView, View itemView, int position, boolean isCurrentChecked);

    HsRecyclerView getRecyclerView();
    void setRecyclerView(HsRecyclerView recyclerView);

    boolean isMultiChoiceMode();
    void setChecked(int position, boolean check);
    boolean isChecked(int position);
}
