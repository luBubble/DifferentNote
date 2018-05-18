package dn.differentnote.list;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import dn.differentnote.R;

public class ListkindActivity extends Activity {
 private  ListView listViewlist;
 private List<String> data=new ArrayList<String>();

 public  void onCreat(Bundle savedInstanceState){
  super.onCreate(savedInstanceState);

  listViewlist=new ListView(this);
  /*
  Cursor cursor=getContentResolver().query(Listkind.CONTENT_URL,null,null,null,null);
  startManagingCursor(cursor);

  ListAdapter listAdapter=new SimpleCursorAdapter(this,android.R.layout.simple_expandable_list_item_1,
          cursor,new String[]{listkind.lk_listname},new int[]{android.R.id.text1});

  listViewlist.setAdapter(listAdapter);
  */
  listViewlist.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,getData()));
  setContentView(listViewlist);
 }
private List<String> getData(){
     List<String> data =new ArrayList<String>();
     data.add("清单");
    data.add("购物");
    data.add("观影");

    return data;
}

}
