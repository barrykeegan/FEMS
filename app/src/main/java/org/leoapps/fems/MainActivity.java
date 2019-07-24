package org.leoapps.fems;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
//import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity-CaseList:";

    private RecyclerView rv;
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Utils.database == null)
        {
            Utils.initialiseUtilsProperties(getApplicationContext());
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_case_list);

        //set for backgroundTint backwards compatibility as per:
        //https://stackoverflow.com/questions/27735890/lollipops-backgroundtint-has-no-effect-on-a-button/29756195#29756195
        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]}, new int[]{R.color.colorSecondary}));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCase.class);
                MainActivity.this.startActivity(intent);
            }
        });

        rv = findViewById(R.id.rv_case_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayCaseList();
    }

    private void displayCaseList()
    {
        //Log.i(TAG, "In display Case List");
        List<Case> cases = Utils.database.caseDAO().getAllCases();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);

        if (cases.size() > 0)
        {
            RecyclerView.Adapter myAdapter = new CaseListAdapter(cases);
            rv.setAdapter(myAdapter);
        }
        else
        {
            List<NoContent> noContent = new ArrayList<>();
            noContent.add(new NoContent(
                    "There are no cases added yet",
                    "Click on + button below to begin adding a case."
            ));
            RecyclerView.Adapter myAdapter = new NoContentAdapter(noContent);
            rv.setAdapter(myAdapter);
        }
    }
}
