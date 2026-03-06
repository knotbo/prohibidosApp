package es.romaydili.prohibidosApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import javax.crypto.Cipher;


public class anticipoActivity extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue requestQueue;
    public static final int REQUEST_CODE = 1;

    Button backBtn,imprimirBtn;
    ImageButton buscarDniSolicitante, buscarDniEncargado;

    EditText editNombreSolicitante, editApellidosSolicitante, editDniSolicitante, editDniEncargado, editNombreEncargado, editApellidosEncargado, editImporte;
    View datosSolicitante, dniEncargado, datosEncargado, datosImporte;

    TextView txtDniEncargado;

    int importe=0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anticipo);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imprimirBtn = findViewById(R.id.imprimir_btn);
        backBtn = findViewById(R.id.back_btn);

        editNombreSolicitante = findViewById(R.id.edit_nombreSolicitante);
        editApellidosSolicitante = findViewById(R.id.edit_apellidosSolicitante);
        editDniSolicitante = findViewById(R.id.edit_dniSolicitante);

        editNombreEncargado = findViewById(R.id.edit_nombreEncargado);
        editApellidosEncargado = findViewById(R.id.edit_apellidosEncargado);
        editDniEncargado = findViewById(R.id.edit_dniEncargado);

        datosImporte = findViewById(R.id.linearImporte);
        editImporte = findViewById(R.id.edit_importe);

        buscarDniSolicitante = findViewById(R.id.search_dniSolicitante);
        txtDniEncargado = findViewById(R.id.txt_dniEncargado);
        dniEncargado = findViewById(R.id.linear_dniEncargado);
        buscarDniEncargado = findViewById(R.id.search_dniEncargado);
        datosSolicitante = findViewById(R.id.linearDatosSolicitante);
        datosEncargado = findViewById(R.id.linearDatosEncargado);

        backBtn.setOnClickListener(this);
        imprimirBtn.setOnClickListener(this);

        buscarDniSolicitante.setOnClickListener(this);
        buscarDniEncargado.setOnClickListener(this);

        Spinner spinnerImporte = findViewById(R.id.spinnerImporte);
        String[] importeOptions = new String[] {"Seleccione Importe", "100", "150", "200", "250", "300"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, importeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImporte.setAdapter(adapter);

        spinnerImporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String importeString = parent.getItemAtPosition(position).toString();
                try {
                    importe = Integer.parseInt(importeString);
                }catch(NumberFormatException e) {
                    importe = 0;
                }
                checkForm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se ha seleccionado ninguna opción
                importe = 0;
                checkForm();
            }
        });

        editDniSolicitante.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (editDniSolicitante.getText().length() > 5){
                    buscarDniSolicitante.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        editDniEncargado.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (editDniEncargado.getText().length() > 5){
                    buscarDniEncargado.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        editImporte.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                checkForm();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        editDniSolicitante.requestFocus();
    }


    private void checkForm(){
        if (editNombreSolicitante.getText().length() > 0
            && editApellidosSolicitante.getText().length() > 3
            && editDniSolicitante.getText().length() > 3
            && editNombreEncargado.getText().length() > 0
            && editApellidosEncargado.getText().length() > 3
            && editDniEncargado.getText().length() > 3
            && importe >= 100
            && importe <= 300
            && !editDniSolicitante.getText().toString().equals(editDniEncargado.getText().toString())
        ){
            imprimirBtn.setEnabled(true);
        }else{
            imprimirBtn.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        if (view==backBtn){
            MainActivity.setScannerInicial(false);
            finish();
        }
        if (view==imprimirBtn){
            //startActivity(new Intent(this,ProceedToCheckInActivity.class));
            Submit();
        }
        if (view==buscarDniSolicitante){
           getSolicitante();
        }
        if (view==buscarDniEncargado){
            getEncargado();
        }

    }
//
//    private void startImageScan() {
//        Intent intent = new Intent(getApplicationContext(),ScannerActivity.class);
//        intent.putExtra("imagekey",REQUEST_IMAGE_SCAN);
//        startActivity(intent);
//    }

    public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String reg = "^[a-zA-Z0-9]*$";
            if(!source.toString().matches(reg)) {
                return "";
            }

            return null;
        }
    };

    private void getSolicitante(){
        Log.d("buscarDniAntcipo", "Búsquemos el dni " + editDniSolicitante.getText().toString());

        //URL of the request we are sending
        StringRequest postRequest = new StringRequest(Request.Method.POST, MainActivity.getUrlDatosDni(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Context context = getApplicationContext();
                        //CharSequence text = response.substring(1);



                        boolean resultado_correcto=false;

                        // Creo un array con los datos JSON que he obtenido
                        //ArrayList listaArray = new ArrayList<>();

                        // Solicito los datos al archivo JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray arrayResultado=jsonObject.getJSONArray("resultado");
                                JSONObject infoSolicitante=arrayResultado.getJSONObject(0);

                                editDniSolicitante.setText(editDniSolicitante.getText().toString().toUpperCase());
                                editNombreSolicitante.setText(infoSolicitante.getString( "nombre"));
                                editApellidosSolicitante.setText(infoSolicitante.getString("apellidos"));

                                buscarDniSolicitante.setVisibility(View.GONE);
                                editDniSolicitante.setClickable(false);
                                editDniSolicitante.setFocusable(false);
                                datosSolicitante.setVisibility(View.VISIBLE);

                                txtDniEncargado.setVisibility(View.VISIBLE);
                                dniEncargado.setVisibility(View.VISIBLE);

                                editDniEncargado.requestFocus();

                                resultado_correcto=false;

                                //Nuevo SnackBar 28/12/2022
                                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), "Anticipo para " + editNombreSolicitante.getText().toString() + ' ' +  editApellidosSolicitante.getText().toString(), Snackbar.LENGTH_LONG);
                                View snackbarView = mySnackbar.getView();
                                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                textView.setMaxLines(40);  // show multiple line
                                mySnackbar.show();
                                //Fin Nuevo SnackBar 28/12/2022

                            }else{
                                resultado_correcto=false;
                                //Nuevo SnackBar 28/12/2022
                                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), "No hay resultados para el DNI " + editDniSolicitante.getText().toString(), Snackbar.LENGTH_INDEFINITE);
                                View snackbarView = mySnackbar.getView();
                                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                textView.setMaxLines(40);  // show multiple line
                                mySnackbar.show();
                                //Fin Nuevo SnackBar 28/12/2022
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if(MainActivity.getDebugMode() == true) {
                            Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), response, Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }//else{
                            //Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                        //}

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence textToast = "";

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the reuest has either time out or there is no connection
                            if (error.toString().contains(("javax.net.ssl.SSLHandshakeException"))){
                                textToast = "La conexión con el servidor no es segura (SSL)";
                            }else {
                                textToast = "Servidor no Responde o No Hay Conexión a Internet";
                            }
                        } else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                            textToast="Fallo de autenticación del Servidor";
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            textToast="Error en la Respuesta del Servidor";
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            textToast="Error de Red";
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                            textToast="No se puede interpretar la Respuesta del Servidor";
                        }else{
                            textToast="Error desconocido";
                        }

                        Toast.makeText(context, textToast, Toast.LENGTH_LONG).show();
                        if (MainActivity.getDebugMode()){
                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), error.toString(), Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("usuario", MainActivity.getUsuario());
                params.put("dni", editDniSolicitante.getText().toString());

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);


    }

    private void getEncargado(){
        Log.d("buscarDniAntcipo", "Búsquemos el dni " + editDniSolicitante.getText().toString());

        //URL of the request we are sending
        StringRequest postRequest = new StringRequest(Request.Method.POST, MainActivity.getUrlDatosDni(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Context context = getApplicationContext();
                        //CharSequence text = response.substring(1);



                        boolean resultado_correcto=false;

                        // Creo un array con los datos JSON que he obtenido
                        //ArrayList listaArray = new ArrayList<>();

                        // Solicito los datos al archivo JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray arrayResultado=jsonObject.getJSONArray("resultado");
                                JSONObject infoSolicitante=arrayResultado.getJSONObject(0);

                                editDniEncargado.setText(editDniEncargado.getText().toString().toUpperCase());
                                editNombreEncargado.setText(infoSolicitante.getString( "nombre"));
                                editApellidosEncargado.setText(infoSolicitante.getString("apellidos"));

                                buscarDniEncargado.setVisibility(View.GONE);
                                editDniEncargado.setClickable(false);
                                editDniEncargado.setFocusable(false);
                                datosEncargado.setVisibility(View.VISIBLE);

                                datosImporte.setVisibility(View.VISIBLE);

                                // Llevo el scroll al final de la actividad
                                ScrollView scrollView = findViewById(R.id.scrollAnticipos);
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                                // Oculta el teclado virtual
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(scrollView.getWindowToken(), 0);

                                //Nuevo SnackBar 28/12/2022
                                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), "Encargado: " + editNombreEncargado.getText().toString() + ' ' + editApellidosEncargado.getText().toString(), Snackbar.LENGTH_LONG);
                                View snackbarView = mySnackbar.getView();
                                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                textView.setMaxLines(40);  // show multiple line
                                mySnackbar.show();
                                //Fin Nuevo SnackBar 28/12/2022

                            }else{
                                resultado_correcto=false;
                                //Nuevo SnackBar 28/12/2022
                                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), "No hay resultados para el DNI " + editDniEncargado.getText().toString(), Snackbar.LENGTH_INDEFINITE);
                                View snackbarView = mySnackbar.getView();
                                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                textView.setMaxLines(40);  // show multiple line
                                mySnackbar.show();
                                //Fin Nuevo SnackBar 28/12/2022
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if(MainActivity.getDebugMode() == true) {
                            Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), response, Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }else{
                            //Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence textToast = "";

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the reuest has either time out or there is no connection
                            if (error.toString().contains(("javax.net.ssl.SSLHandshakeException"))){
                                textToast = "La conexión con el servidor no es segura (SSL)";
                            }else {
                                textToast = "Servidor no Responde o No Hay Conexión a Internet";
                            }
                        } else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                            textToast="Fallo de autenticación del Servidor";
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            textToast="Error en la Respuesta del Servidor";
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            textToast="Error de Red";
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                            textToast="No se puede interpretar la Respuesta del Servidor";
                        }else{
                            textToast="Error desconocido";
                        }

                        Toast.makeText(context, textToast, Toast.LENGTH_LONG).show();
                        if (MainActivity.getDebugMode()){
                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), error.toString(), Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("usuario", MainActivity.getUsuario());
                params.put("dni", editDniEncargado.getText().toString());

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);


    }


    private void Submit(){

        Log.d("solicitudAnticipo", "Vamos a imprimir la solicitud de Anticipo");
        imprimirBtn.setEnabled(false);
        imprimirSolicitudAnticipo(
                getApplicationContext(),
                editDniSolicitante.getText().toString(),
                editNombreSolicitante.getText().toString(),
                editApellidosSolicitante.getText().toString(),
                editDniEncargado.getText().toString(),
                editNombreEncargado.getText().toString(),
                editApellidosEncargado.getText().toString(),
                importe
        );
    }

    public void imprimirSolicitudAnticipo(final Context context, final String dniSolicitante, final String nombreSolicitante, final String apellidosSolicitante, final String dniEncargado, final String nombreEncargado, final String apellidosEncargado, final int importe){


        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try (Socket socket = new Socket(MainActivity.getIpServidor(), 9090)) {
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    //Console console = System.console();
                    writer.println("solicitudAnticipo," + dniSolicitante + ',' + nombreSolicitante + ',' + apellidosSolicitante + ',' + dniEncargado + ',' + nombreEncargado + ',' + apellidosEncargado + ',' + importe);

                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    final String respuesta = reader.readLine();

                    //Nuevo SnackBar 28/12/2022
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.anticipoLayout), respuesta, 5000);
                    View snackbarView = mySnackbar.getView();
                    TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setTextSize(18);
                    textView.setMaxLines(40);  // show multiple line
                    mySnackbar.show();
                    //Fin Nuevo SnackBar 28/12/2022

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, respuesta, Toast.LENGTH_LONG).show();
                        }
                    });

                    //System.out.println("Acabo el Thread " + Thread.currentThread().getId());
                    //socket.close();
                } catch (final UnknownHostException ex) {

                    System.out.println("Servidor no Encontrado: " + ex.getMessage());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "Servidor no Encontrado: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (final IOException ex) {
                    System.out.println("I/O error: " + ex.getMessage());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "I/O Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        thread.start();
    }

}