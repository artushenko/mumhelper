package net.martp.mihail.mumhelper;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetupFragment extends Fragment {

    SharedPreferences sPref;
    EditText editText2_studentID;
    private String image_URL="";

    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//get studentID from preferences
        EditText editStudentID = (EditText) getView().findViewById(R.id.editStudentID);
        sPref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        editStudentID.setText(sPref.getString(MainActivity.SAVED_STUDENT_ID, ""));

// saveStudentID
        Button btnSaveID = (Button) getView().findViewById(R.id.buttonSaveID);
        editText2_studentID = (EditText) getView().findViewById(R.id.editStudentID);
   //     sPref = getActivity().getPreferences(getActivity().MODE_PRIVATE);

        View.OnClickListener oclBtnSaveID = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


              //hidden keyboard
                hideKeyboard();

                Toast.makeText(getActivity(), "press SaveID", Toast.LENGTH_SHORT).show();

//save studentID in preferences
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(MainActivity.SAVED_STUDENT_ID, editText2_studentID.getText().toString());
                ed.commit();
                ParseDataInfoAsyncTask parseDataInfoAsyncTask = new ParseDataInfoAsyncTask();
                parseDataInfoAsyncTask.execute();
            }
        };
        btnSaveID.setOnClickListener(oclBtnSaveID);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viev = inflater.inflate(R.layout.fragment_setup, container, false);
        return viev;
    }


    private class ParseDataInfoAsyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(getView().getContext());
            dialog.setMessage("Загрузка...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document doc = null;
            Connection.Response res = null;

            SharedPreferences sPref = getActivity().getPreferences(getActivity().MODE_PRIVATE);
            String studentID = sPref.getString(MainActivity.SAVED_STUDENT_ID, "");

            try {
                res = Jsoup.connect("http://student.miu.by/learning-card.html")
                    /*
                    .data("act", "regnum")
                    .data("id", "id")
                    .data("regnum", "20090312012423")
                    */
                      //  .data("act", "regnum", "id", "id", "regnum", "20090312012423")
                        .data("act", "regnum", "id", "id", "regnum", studentID)
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                //   e.printStackTrace();
                System.out.println("Ошибка подключени к сети " + getClass().getSimpleName());

            //    return;
            }

            String sessionId = res.cookie("PHPSESSID");

            try {
                doc = Jsoup.connect("http://student.miu.by/learning-card.html")
                        .cookie("PHPSESSID", sessionId)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //  System.out.print(doc);

            Element table = doc.select("table").first();
            Elements rows = table.select("tr");
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                System.out.print(cols.get(0).text());
                System.out.print(" ");
                System.out.print(cols.get(1).text());
                System.out.println();
            }

            System.out.println("-------------------------------------------");

            String link = table.select("tr").get(0).select("td").get(0).select("img").first().attr("src");

            System.out.println("http://student.miu.by"+link);

            image_URL="http://student.miu.by"+link;

            System.out.println("-------------------------------------------");

            rows = doc.select("table").first().select("tr");

            /*
    final static public String SAVED_NAME_STUDENT = "nameStudent";
    final static public String SAVED_SURNAME_STUDENT = "surnameStudent";
    final static public String SAVED_MIDNAME_STUDENT = "midnameStudent";
    final static public String SAVED_FACULTY = "faculty";
    final static public String SAVED_SPECIALTY = "specialty";
    final static public String SAVED_AVARAGE_SCORE = "avscoreStudent";
    final static public String SAVED_NUMBER_GROUP = "numberGroup";
             */

            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(MainActivity.SAVED_NUMBER_GROUP, rows.get(0).select("td").get(2).text());
            ed.commit();

            ed = sPref.edit();
            ed.putString(MainActivity.SAVED_SURNAME_STUDENT, rows.get(1).select("td").get(1).text());
            ed.commit();

            ed = sPref.edit();
            ed.putString(MainActivity.SAVED_NAME_STUDENT, rows.get(2).select("td").get(1).text());
            ed.commit();

            ed = sPref.edit();
            ed.putString(MainActivity.SAVED_MIDNAME_STUDENT, rows.get(3).select("td").get(1).text());
            ed.commit();

            ed = sPref.edit();
            ed.putString(MainActivity.SAVED_FACULTY, rows.get(4).select("td").get(1).text());
            ed.commit();

            ed = sPref.edit();
            ed.putString(MainActivity.SAVED_SPECIALTY,rows.get(5).select("td").get(1).text());
            ed.commit();

            ed = sPref.edit();
            ed.putString(MainActivity.SAVED_AVARAGE_SCORE, rows.get(6).select("td").get(1).text());
            ed.commit();

            System.out.print(rows.get(1).select("td").get(1).text()); //surName
            System.out.println();

            System.out.print(rows.get(2).select("td").get(1).text()); //surName
            System.out.println();

            System.out.print(rows.get(3).select("td").get(1).text()); //surName
            System.out.println();

            System.out.print(rows.get(4).select("td").get(1).text()); //surName
            System.out.println();

            System.out.print(rows.get(5).select("td").get(1).text()); //surName
            System.out.println();

            System.out.print(rows.get(6).select("td").get(1).text()); //surName
            System.out.println();

            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
