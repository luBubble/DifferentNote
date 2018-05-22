package dn.differentnote.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import dn.differentnote.R;
import dn.differentnote.fragment.MyFragmentCalendar;
import dn.differentnote.fragment.MyFragmentList;
import dn.differentnote.fragment.MyFragmentTomato;

import static android.content.ContentValues.TAG;

/**
 * Created by Coder-pig on 2015/8/28 0028.
 */
public class FragmentActivity extends AppCompatActivity implements View.OnClickListener{

    //UI Object
    private TextView txt_list;
    private TextView txt_calendar;
    private TextView txt_tomato;
    private TextView txt_mine;
    private FrameLayout ly_content;

    //Fragment Object
    private MyFragmentList fg1,fg4;
    private MyFragmentCalendar fg2;
    private MyFragmentTomato fg3;
    private FragmentManager fManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fg_navigationbar);
        fManager = getSupportFragmentManager();
        bindViews();
        txt_list.performClick();   //模拟一次点击，既进去后选择第一项
    }

    //UI组件初始化与事件绑定
    private void bindViews() {
        txt_list = (TextView) findViewById(R.id.txt_list);
        txt_calendar = (TextView) findViewById(R.id.txt_calendar);
        txt_tomato = (TextView) findViewById(R.id.txt_tomato);
        txt_mine = (TextView) findViewById(R.id.txt_mine);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        txt_list.setOnClickListener(this);
        txt_calendar.setOnClickListener(this);
        txt_tomato.setOnClickListener(this);
        txt_mine.setOnClickListener(this);
    }

    //重置所有文本的选中状态
    private void setSelected(){
        txt_list.setSelected(false);
        txt_calendar.setSelected(false);
        txt_tomato.setSelected(false);
        txt_mine.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fg1 != null)fragmentTransaction.hide(fg1);
        if(fg2 != null)fragmentTransaction.hide(fg2);
        if(fg3 != null)fragmentTransaction.hide(fg3);
        if(fg4 != null)fragmentTransaction.hide(fg4);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        Log.d(TAG, "activity content change: 哈哈哈");
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = fManager.beginTransaction();;
        hideAllFragment(fTransaction);
        switch (v.getId()){
            case R.id.txt_list:
                setSelected();
                txt_list.setSelected(true);
                if(fg1 == null){
                    fg1 = new MyFragmentList();
                    fg1.setArguments(getIntent().getExtras());//传递用户id
                    fTransaction.add(R.id.ly_content,fg1);
                }else{
                    fTransaction.show(fg1);
                }
                break;
            case R.id.txt_calendar:
                setSelected();
                txt_calendar.setSelected(true);
                if(fg2 == null){
                    fg2 = new MyFragmentCalendar("");
                    fTransaction.add(R.id.ly_content,fg2);
                }else{
                    fTransaction.show(fg2);
                }
                break;
            case R.id.txt_tomato:
                setSelected();
                txt_tomato.setSelected(true);
                if(fg3 == null){
                    fg3 = new MyFragmentTomato("");
                    fTransaction.add(R.id.ly_content,fg3);
                }else{
                    fTransaction.show(fg3);
                }
                break;
            case R.id.txt_mine:
                setSelected();
                txt_mine.setSelected(true);
                if(fg4 == null){
//                    fg4 = new MyFragmentList("第四个Fragment");
//                    fTransaction.add(R.id.ly_content,fg4);
                }else{
                    fTransaction.show(fg4);
                }
                break;
        }
        fTransaction.commit();
    }


}
