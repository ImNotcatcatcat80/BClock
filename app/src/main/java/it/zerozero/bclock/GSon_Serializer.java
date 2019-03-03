package it.zerozero.bclock;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by David on 26/08/2017.
 */

public class GSon_Serializer {

    private Context context;
    private String filename;
    private Gson gson;


    public GSon_Serializer(Context c, String fn) {
        this.context = c;
        this.filename = fn;
        this.gson = new Gson();
    }

    public void save (ArrayList<String> readingList) throws IOException {

        String fileStr = gson.toJson(readingList);

        Writer writer = null;
        try {
            OutputStream outputStream = this.context.openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            writer.write(fileStr);
        }
        finally {
            if(writer != null){
                writer.close();
            }
        }

    }

    public ArrayList<String> load () throws IOException {
        String JSonStr = null;
        ArrayList<String> outArray = new ArrayList<>();  //Si si, di' quello che vuoi...

        BufferedReader reader = null;
        try{
            InputStream inputStream = this.context.openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSonStr = stringBuilder.toString();
            Log.i("JSonStr", JSonStr);

        }
        finally {
            if(reader != null){
                reader.close();
            }
        }

        outArray = gson.fromJson(JSonStr, ArrayList.class);

        return outArray;
    }

}
