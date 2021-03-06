package net.martp.mihail.mumhelper;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetupFragment extends Fragment {

    SharedPreferences sPref;
    //  EditText editText2_studentID;
    EditText editTextStudentID;
    private String image_URL = "";
    public String imageFileName = "";
    static Bitmap bm;
    View getVievSetupFragment;
    String studentID;
    CheckBox checkBoxCache;

    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
//get status Cache function
        checkBoxCache = (CheckBox) getVievSetupFragment.findViewById(R.id.checkBoxCasheData);
        if (sPref.getString(MainActivity.CACHE_DATA, "").equals("yes")) {
            checkBoxCache.setChecked(true);
        } else {
            checkBoxCache.setChecked(false);
        }

//get studentID from preferences
        editTextStudentID = (EditText) getVievSetupFragment.findViewById(R.id.inputStudentID);
        editTextStudentID.setText(sPref.getString(MainActivity.SAVED_STUDENT_ID, ""));

// saveStudentID
        Button btnSaveID = (Button) getVievSetupFragment.findViewById(R.id.buttonSaveStudentID);

        // editText2_studentID = (EditText) getVievSetupFragment.findViewById(R.id.inputStudentID);

        View.OnClickListener oclBtnSaveID = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextStudentID.getText().length() == 14) {
                    //hidden keyboard
                    hideKeyboard();

                    ParseDataInfoAsyncTask parseDataInfoAsyncTask = new ParseDataInfoAsyncTask();
                    parseDataInfoAsyncTask.execute();
                } else {
                    Toast.makeText(getActivity(), "Ошибка!\nID должен состоять из 14 цифр.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnSaveID.setOnClickListener(oclBtnSaveID);

        Button btnDeleteID = (Button) getVievSetupFragment.findViewById(R.id.buttonDeleteStudentID);
        View.OnClickListener oclBtnDeleteID = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(MainActivity.SAVED_STUDENT_ID, "");
                ed.apply();
                editTextStudentID.setText("");
            }
        };
        btnDeleteID.setOnClickListener(oclBtnDeleteID);

        Button btnUpdateTeachers = (Button) getVievSetupFragment.findViewById(R.id.buttonUpdateTeachers);
        View.OnClickListener oclBtnUpadateTeacher = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Обновление списка\nпреподавателей университета", Toast.LENGTH_SHORT).show();
                UpadateTeachersListDataInfoAsyncTask upadateTeachersListDataInfoAsyncTask = new UpadateTeachersListDataInfoAsyncTask();
                upadateTeachersListDataInfoAsyncTask.execute();
            }
        };
        btnUpdateTeachers.setOnClickListener(oclBtnUpadateTeacher);

        checkBoxCache = (CheckBox) getVievSetupFragment.findViewById(R.id.checkBoxCasheData);
        final View.OnClickListener oclCheckBoxCache = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxCache.isChecked()) {
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(MainActivity.CACHE_DATA, "yes");
                    ed.apply();
                } else {
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(MainActivity.CACHE_DATA, "no");
                    ed.apply();
                }
            }
        };
        checkBoxCache.setOnClickListener(oclCheckBoxCache);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getVievSetupFragment = inflater.inflate(R.layout.fragment_setup3, container, false);
    }

    private class ParseDataInfoAsyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            EditText editStudentID = (EditText) getVievSetupFragment.findViewById(R.id.inputStudentID);
            studentID = editStudentID.getText().toString();

            dialog = new ProgressDialog(getVievSetupFragment.getContext());
            dialog.setMessage("Загрузка...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document doc;
            Connection.Response res;

            try {
                res = Jsoup.connect("http://student.miu.by/learning-card.html")
                        .data("act", "regnum", "id", "id", "regnum", studentID)
                        .method(Connection.Method.POST)
                        .execute();
            } catch (IOException e) {
                error1 = "IO Error";
                return null;
            }

            String sessionId = res.cookie("PHPSESSID");

            try {
                doc = Jsoup.connect("http://student.miu.by/learning-card.html")
                        .cookie("PHPSESSID", sessionId)
                        .get();
            } catch (IOException e) {
                error1 = "IO Error";
                return null;
            }

            Element table;
            try {
                table = doc.select("table").first();
            } catch (Exception e) {
                error1 = "Other error";
                return null;
            }

            try {
                //search image photo url
                String link = table.select("tr").get(0).select("td").get(0).select("img").first().attr("src");

                imageFileName = link;
                image_URL = "http://student.miu.by" + link;
            } catch (NullPointerException e) {
                error1 = "No photo in doc";
            }

            try {
                Elements rows = doc.select("table").first().select("tr");
                SharedPreferences.Editor ed = sPref.edit();

                if (error1.equals("No photo in doc")) {
                    ed.putString(MainActivity.SAVED_NUMBER_GROUP, rows.get(0).select("td").get(1).text());
                } else {
                    ed.putString(MainActivity.SAVED_NUMBER_GROUP, rows.get(0).select("td").get(2).text());
                }
                ed.putString(MainActivity.SAVED_SURNAME_STUDENT, rows.get(1).select("td").get(1).text());
                ed.putString(MainActivity.SAVED_NAME_STUDENT, rows.get(2).select("td").get(1).text());
                ed.putString(MainActivity.SAVED_MIDNAME_STUDENT, rows.get(3).select("td").get(1).text());
                ed.putString(MainActivity.SAVED_FACULTY, rows.get(4).select("td").get(1).text());
                ed.putString(MainActivity.SAVED_SPECIALTY, rows.get(5).select("td").get(1).text());
                ed.putString(MainActivity.SAVED_AVARAGE_SCORE, rows.get(6).select("td").get(1).text());
                //   ed.putString(MainActivity.SAVED_STUDENT_ID, editTextStudentID.getText().toString());
                ed.putString(MainActivity.SAVED_STUDENT_ID, studentID);
                ed.apply();

                if (!error1.equals("No photo in doc")) {
                    try {
                        getPhotoFromURL();
                        return null;
                    } catch (IOException e) {
                        error1 = "Photo not load";
                    }
                } else {
                    try {
                        InputStream bitmap = getActivity().getAssets().open("no_foto2.png");
                        bm = BitmapFactory.decodeStream(bitmap);
                    } catch (IOException e) {
                        error1 = "Photo not load";
                    }
                }

            } catch (NullPointerException e) {
                error1 = "ID not found";
            }
            return null;
        }

        private String error1 = "";

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            if (error1.equals("ID not found")) {
                Toast.makeText(getActivity(), "Ошибка!\nСтудент с таким ID не найден.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (error1.equals("IO Error")) {
                Toast.makeText(getActivity(), "Ошибка подключения к сети!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (error1.equals("Other error")) {
                Toast.makeText(getActivity(), "Произошла какая-то ошибка #10", Toast.LENGTH_SHORT).show();
                return;
            }
            ImageView bmImage = (ImageView) getVievSetupFragment.findViewById(R.id.imageView3);
            bmImage.setImageBitmap(bm);

            //save photo to sdcard
            getStudentPhoto((ImageView) getVievSetupFragment.findViewById(R.id.imageView3));
        }
    }


    private class UpadateTeachersListDataInfoAsyncTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog dialog;
        String error1 = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            EditText editStudentID = (EditText) getVievSetupFragment.findViewById(R.id.inputStudentID);
            studentID = editStudentID.getText().toString();

            dialog = new ProgressDialog(getVievSetupFragment.getContext());
            dialog.setMessage("Загрузка...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dialog.dismiss();

            if (error1.equals("IO Error")) {
                Toast.makeText(getActivity(), "Ошибка подключения к сети!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (error1.equals("Save file error")) {
                Toast.makeText(getActivity(), "Ошибка записи файла #21", Toast.LENGTH_SHORT).show();
                return;
            }

            if (error1.equals("Other error")) {
                Toast.makeText(getActivity(), "Произошла какая-то ошибка #20", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getActivity(), "Список преподавателей университета успешно обновлен", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Document doc;

            try {
                doc = Jsoup.connect("http://martp.net/miuby.info/parse_miuby_schedule.php?allteachers=1").get();
            } catch (IOException e) {
                error1 = "IO Error";
                return null;
            }
            Elements listTeacher = doc.select("body");

            if (listTeacher == null) {
                error1 = "Other error";
                return null;
            }

            String teachersListTemp = listTeacher.text().substring(4, listTeacher.text().length());
            String[] arrayTeacherList = teachersListTemp.split("\\. ");
            String[] arrayTeachersListNormal = new String[arrayTeacherList.length];
            for (int i = 0; i < arrayTeacherList.length; i++) {
                arrayTeachersListNormal[i] = arrayTeacherList[i] + ".";
            }

    //        System.out.println(Arrays.toString(arrayTeachersListNormal));

            //save listTeacher to file

            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/student.miu.by");
  /*          if (sdPath.mkdirs()) {
                Toast.makeText(getActivity(), "Приложение создало каталог\n" + sdPath.toString(), Toast.LENGTH_SHORT).show();
            }
*/
            String fileName = sdPath + "/teacherslist.txt";
 //           System.out.println("LOG8 " + fileName);

            BufferedWriter writer = null;
            try {

                writer = new BufferedWriter(new FileWriter(fileName));
                for (String anArrayTeachersListNormal : arrayTeachersListNormal) {
                    writer.write(anArrayTeachersListNormal);
                    writer.newLine();
                    writer.flush();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                error1 = "Save file error";
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        error1 = "Save file error";
                        e.printStackTrace();
                    }
                }
            }

            return null;
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

    public void getPhotoFromURL() throws IOException {
        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;
        bm = LoadImage(image_URL, bmOptions);
    }

    public Bitmap LoadImage(String URL, BitmapFactory.Options options) throws IOException {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in, null, options);
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Ошибка подключения к сети!", Toast.LENGTH_SHORT).show();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return bitmap;
    }

    private InputStream OpenHttpConnection(String strURL) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Произошла какая-то ошибка #9", Toast.LENGTH_SHORT).show();
        }
        return inputStream;
    }

    public void getStudentPhoto(ImageView getImageViewFrom) {
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/student.miu.by");
        if (sdPath.mkdirs()) {
            Toast.makeText(getActivity(), "Приложение создало каталог\n" + sdPath.toString(), Toast.LENGTH_SHORT).show();
        }

        OutputStream fOut;
        SharedPreferences.Editor ed = sPref.edit();
        try {
            File file = new File(sdPath, "photoStudent.jpg");
            fOut = new FileOutputStream(file);

            Bitmap bitmap = ((BitmapDrawable) getImageViewFrom.getDrawable()).getBitmap();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            ed.putString(MainActivity.SAVED_PHOTO, "yes").apply();
        } catch (IOException e) {
            ed.putString(MainActivity.SAVED_PHOTO, "no").apply();
            Toast.makeText(getActivity(), "Произошла какая-то ошибка #8", Toast.LENGTH_SHORT).show();
        }
    }
}
