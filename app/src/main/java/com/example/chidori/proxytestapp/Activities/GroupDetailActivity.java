package com.example.chidori.proxytestapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chidori.proxytestapp.Activities.entity.GroupMember;
import com.example.chidori.proxytestapp.Activities.adapter.GroupMemberCardRecyclerAdapter;
import com.example.chidori.proxytestapp.Activities.util.StaticTool;
import com.example.chidori.proxytestapp.Config;
import com.example.chidori.proxytestapp.Contract.Contract;
import com.example.chidori.proxytestapp.Presenter.GroupDetailPresenterImpl;
import com.example.chidori.proxytestapp.R;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity implements Contract.IGroupDetailView {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private View view;
    private Button btn;
    private String id;
    private boolean state = false;

    private List<GroupMember> cardList = new ArrayList<>();
    private GroupMemberCardRecyclerAdapter recyclerAdapter = new GroupMemberCardRecyclerAdapter(cardList);

    private GroupDetailPresenterImpl presenter = new GroupDetailPresenterImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this,R.layout.activity_group_detail,null);
        setContentView(view);

        id = getIntent().getStringExtra("id");
        TextView groupName = (TextView) findViewById(R.id.group_detail_name);
        groupName.setText(getIntent().getStringExtra("title"));

        btn = (Button)findViewById(R.id.group_delete);
        btn.setClickable(false);

        presenter.attachView(this);
        presenter.doGetGroupMembers(id);

        setToolbar();
        toolbarTitle.setText("小组详情");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state) {
                    btn.setClickable(false);
                    presenter.doQuitGroup(id);
                }
                else {
                    btn.setClickable(false);
                    presenter.doEnterGroup(id);
                }
            }
        });

        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbarTitle = (TextView) findViewById(R.id.toolbar_txt);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 绑定toolbar跟menu
        getMenuInflater().inflate(R.menu.toolbar, menu);
        toolbar.getMenu().findItem(R.id.add).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            finish();
            return true;
        }
        else return false;
    }

    @Override
    public void onGroupEntered(String status) {
        if(status.equals("success")){
            Toast.makeText(GroupDetailActivity.this, "加入小组成功", Toast.LENGTH_SHORT).show();
            cardList.add(new GroupMember(Config.userId,Config.username,Config.sex,Config.phone,Config.email,null));
            btn.setText("退出小组");
            state = true;
            recyclerAdapter.resetCardList(cardList);
            btn.setClickable(true);
        }
        else{
            Toast.makeText(GroupDetailActivity.this, "加入小组失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGroupQuit(String status) {
        if(status.equals("success")){
            Toast.makeText(GroupDetailActivity.this, "退出小组成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(GroupDetailActivity.this, "退出小组失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGroupMembersResult(String status) {
        if(status.equals("success")){
            cardList = presenter.getMembers();
            if(cardList==null) cardList = new ArrayList<>();
            recyclerAdapter.resetCardList(cardList);

            for(GroupMember m:cardList){
                if(m.getUserId().equals(Config.userId)) state = true;
            }
            if(state) btn.setText("退出小组");
            else btn.setText("加入小组");
            btn.setClickable(true);
        }
        else {
            Toast.makeText(GroupDetailActivity.this, "获取小组成员失败", Toast.LENGTH_SHORT).show();
            cardList = new ArrayList<>();
            recyclerAdapter.resetCardList(cardList);
        }
    }
}