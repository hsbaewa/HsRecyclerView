package kr.co.hs.widget.recyclerview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import kr.co.hs.widget.recyclerview.HsRecyclerView;
import kr.co.hs.widget.recyclerview.app.HsRecyclerViewActivity;

/**
 * Created by Bae on 2016-12-30.
 */
public class SampleActivity extends HsRecyclerViewActivity {

//    HsRecyclerView mHsRecyclerView;
//    SampleAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        HsRecyclerView temp = (HsRecyclerView) findViewById(R.id.RecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        temp.setLayoutManager(llm);
        setRecyclerView(temp);

        setRecyclerAdapter(new SampleAdapter());
//        getRecyclerView().setAdapter(getRecyclerAdapter());
    }

    @Override
    public void onItemClick(HsRecyclerView recyclerView, View itemView, int position, boolean isCurrentChecked) {
        Snackbar.make(getRecyclerView(), ""+position+" 클릭함", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onItemLongClick(HsRecyclerView recyclerView, View itemView, int position, boolean isCurrentChecked) {
        Snackbar.make(getRecyclerView(), ""+position+" 롱 클릭함", Snackbar.LENGTH_LONG).show();
        return false;
    }




    class SampleAdapter extends HsRecyclerView.HsAdapter<SampleAdapter.SampleHolder>{
        @Override
        public SampleHolder onCreateHsViewHolder(ViewGroup parent, int viewType) {
            return new SampleHolder(LayoutInflater.from(getContext()).inflate(R.layout.viewholder_sample, parent, false));
        }

        @Override
        public void onBindHsViewHolder(SampleHolder holder, int position, boolean isChecked) {
            if(!isMultiChoiceMode()) {
                holder.mCheckBox.setVisibility(View.GONE);
            }
            else{
                holder.mCheckBox.setVisibility(View.VISIBLE);
                holder.mCheckBox.setChecked(isChecked);
            }

            String text = "Position %d";
            holder.mTextView.setText(String.format(text, position));

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class SampleHolder extends HsRecyclerView.HsViewHolder{
            CheckBox mCheckBox;
            TextView mTextView;
            public SampleHolder(View itemView) {
                super(itemView);
                mCheckBox = (CheckBox) findViewById(R.id.CheckBox);
                mTextView = (TextView) findViewById(R.id.TextView);
            }
        }

    }

}
