package com.gabmus.mdedit;

import android.animation.Animator;
import android.app.Activity;
import com.github.rjeschke.txtmark.Processor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends Activity {

    TextView compiledTxtView;
    EditText editorView;
    public static final int REQUEST_OPEN_MARKDOWN=1;
    public static Spanned compiledText= Html.fromHtml("");
    public static String openedFileString;
    public static boolean isCompiledShown=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compiledTxtView = (TextView) findViewById(R.id.compiledTxtView);
        editorView = (EditText) findViewById(R.id.editorView);

        compiledTxtView.setVisibility(View.GONE);
        //editorView.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //when screen rotates it has to keep text, so i re-set it overriding onResume :3
        compiledTxtView.setText(compiledText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void hideEdit() {

    }

    private void showEdit() {

    }

    private void hideText() {
        compiledTxtView.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {}
                    @Override public void onAnimationCancel(Animator animator) {}
                    @Override public void onAnimationRepeat(Animator animator) {}
                    @Override public void onAnimationEnd(Animator animator) {
                        compiledTxtView.setVisibility(View.GONE);
                    }
                });
    }

    private void showText() {
        compiledTxtView.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {
                        compiledTxtView.setVisibility(View.VISIBLE);
                    }
                    @Override public void onAnimationCancel(Animator animator) {}
                    @Override public void onAnimationRepeat(Animator animator) {}
                    @Override public void onAnimationEnd(Animator animator) {}
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_compile) {

            if (isCompiledShown) compiledTxtView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new Animator.AnimatorListener() {
                        @Override public void onAnimationStart(Animator animator) {}
                        @Override public void onAnimationCancel(Animator animator) {}
                        @Override public void onAnimationRepeat(Animator animator) {}
                        @Override public void onAnimationEnd(Animator animator) {
                            compiledTxtView.setVisibility(View.GONE);
                            isCompiledShown=false;
                        }
                    });
            else editorView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            String unformattedText = editorView.getText().toString();
                            compiledText = Html.fromHtml(Processor.process(unformattedText));
                            compiledTxtView.setText(compiledText);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            editorView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

            return true;
        }

        if (id == R.id.action_open) {
            Intent intentChooseUpdate = new Intent(Intent.ACTION_GET_CONTENT);
            intentChooseUpdate.setType("text/x-markdown");
            startActivityForResult(Intent.createChooser(intentChooseUpdate, "Select a file"), REQUEST_OPEN_MARKDOWN);
            return true;
        }

        if (id == R.id.action_save) {

            return true;
        }
        /*
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_MARKDOWN && resultCode == RESULT_OK) {


            Uri mdPath = data.getData();
            File markdownFile = new File(mdPath.getPath());
            try {
                FileInputStream fstream = new FileInputStream(markdownFile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                openedFileString="";
                while ((strLine = br.readLine()) != null)   {
                    // Print the content on the console
                    openedFileString+=strLine+"\n";
                }
                //Close the input stream
                in.close();
            }
            catch (Exception e){//Catch exception if any
                Log.e("Error: ", e.getMessage());
            }

            editorView.setText(openedFileString);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
