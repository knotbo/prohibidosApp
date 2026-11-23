package es.romaydili.prohibidosApp;

import static android.content.ContentValues.TAG;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
//import com.scansolutions.mrzscannerlib.MRZScanner;

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


public class ScannedBarkoderSolicitudEfectivoActivity extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue requestQueue;
    public static final int REQUEST_CODE = 1;
    public final String resultado ="";
    private String tipo_documento="";
    private int clicks = 0;

    Button backBtn,imprimirBtn;

    EditText editNombre, editApellidos, editDocNum, editOtroBanco, editImporte;
    TextView datosCliente, txtOtroBanco, txtUltSolicitudEfectivo;
    Spinner spinnerBanco,  spinnerUltSolicitudes;

    JSONObject JsonFinalUnificado = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_solicitud_efectivo);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        datosCliente = findViewById(R.id.activity_description_txt);

        imprimirBtn = findViewById(R.id.imprimir_btn);
        backBtn = findViewById(R.id.back_btn);
        editNombre = findViewById(R.id.edit_given_name);
        editApellidos = findViewById(R.id.edit_surname);
        editDocNum = findViewById(R.id.edit_document_number);
        txtUltSolicitudEfectivo = findViewById(R.id.txt_ultimasSolicitudes);
        txtOtroBanco = findViewById(R.id.txt_otro_banco);
        editOtroBanco = findViewById(R.id.edit_otro_banco);
        editImporte = findViewById(R.id.edit_importe);

        spinnerBanco = findViewById(R.id.spinner_banco);
        spinnerUltSolicitudes = findViewById(R.id.spinner_ultimasSolicitudes);

        backBtn.setOnClickListener(this);
        imprimirBtn.setOnClickListener(this);
        datosCliente.setOnClickListener(this);

        //String[] lista_bancos={"Seleccione Banco", "SANTANDER", "LA CAIXA", "BBVA", "SABADELL", "UNICAJA", "BANKINTER", "CAJA RURAL", "ABANCA", "KUTXABANK", "CAJAMAR", "IBERCAJA", "BANCA MARCH", "Otro..."};
        String[] lista_bancos={"Seleccione Banco", "LA CAIXA", "BBVA", "ING", "CAJA RURAL", "SANTANDER", "CAJASUR", "DEUTSCHE BANK", "BANKIA", "UNICAJA", "BARCLAYS", "WIZINK", "KUTXABANK", "CAJAMAR", "CORREOS", "Otro..."};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, lista_bancos);
        spinnerBanco.setAdapter(adapter);

        spinnerBanco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item == "Otro...") {
                    txtOtroBanco.setVisibility(View.VISIBLE);
                    editOtroBanco.setVisibility(View.VISIBLE);
                    //Toast.makeText(ScannedSolicitudEfectivoActivity.this, item.toString(), Toast.LENGTH_SHORT).show();

                }else{
                    txtOtroBanco.setVisibility(View.GONE);
                    editOtroBanco.setVisibility(View.GONE);
                }

                checkForm();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });

        spinnerUltSolicitudes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if ( !item.toString().contains("Últimas Solicitudes") ){
                    String[] parts = item.toString().split(",");

                    if(parts.length == 3) {
                        editDocNum.setText(parts[0]);
                        editApellidos.setText(parts[1]);
                        editNombre.setText(parts[2]);
                    }

                }

                checkForm();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });

        editNombre.addTextChangedListener(new TextWatcher() {
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

        editApellidos.addTextChangedListener(new TextWatcher() {
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

        editDocNum.addTextChangedListener(new TextWatcher() {
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

        editOtroBanco.addTextChangedListener(new TextWatcher() {
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


        startScanner();

    }

    private void addResultToEditText(JSONObject successfulMrzScan) throws JSONException {

        editNombre.setText(successfulMrzScan.getString("nombre"));
        editApellidos.setText(successfulMrzScan.getString("apellidos"));
        editDocNum.setText(successfulMrzScan.getString("dni"));

        //Submit();
    }


    private void checkForm(){
        if (editNombre.getText().length() > 0
            && editApellidos.getText().length() > 0
            && editDocNum.getText().length() > 3
            && editImporte.getText().length() > 0
            && !spinnerBanco.getSelectedItem().toString().equals("Seleccione Banco")
            && !(spinnerBanco.getSelectedItem().toString().equals("Otro...") && editOtroBanco.getText().length() <= 2)
        ){
            imprimirBtn.setEnabled(true);
        }else{
            imprimirBtn.setEnabled(false);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK) {
                String requiredValue = data.getStringExtra("key");
                JSONObject jsonObj = new JSONObject(requiredValue);

                addResultToEditText(jsonObj);
            }else{
                getUltimasSolicitudes();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Fallo en el resultado de la actividad\r\n" + MRZScanner.sdkVersion(), Toast.LENGTH_SHORT).show();
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                JSONObject jsonObj = new JSONObject(data.getStringExtra("key"));
                String tipo = jsonObj.optString("type", "");

                if (tipo.equals("QR")) {
                    procesarLecturaQR();
                } else {
                    procesarLecturaMRZ(jsonObj);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en onActivityResult: " + e.getMessage());
            }
        }else{
            getUltimasSolicitudes();
        }
    }

    private void procesarLecturaQR() {
        new Thread(() -> {
            try {
                Log.i(TAG, "Procesando QR...");

                // 1. RECUPERAMOS los bytes del "túnel" (Holder)
                byte[] rawBytes = ScanDataHolder.rawBarcodeData;

                // 2. LIMPIAMOS EL HOLDER YA
                ScanDataHolder.rawBarcodeData = null;

                if (rawBytes != null) {
                    // 3. PARSEAMOS el binario puro para obtener los campos del DNI
                    // Esta es la función que creamos para entender el formato ASN.1/DER
                    JSONObject result = MiDniParser.parsear(rawBytes);

                    Log.d(TAG, "Resultado de Parsear MiDni: " + result);

                    // 4. ADAPTAMOS al formato estándar que entiende tu UI
                    //JSONObject jsonUnificado = new JSONObject();
                    JsonFinalUnificado = new JSONObject();

                    // Mapeamos lo que sale del Parser a los nombres de tu "contrato unificado"
                    JsonFinalUnificado.put("nombre", result.optString("first_name", ""));
                    JsonFinalUnificado.put("apellidos", result.optString("last_name", ""));
                    JsonFinalUnificado.put("dni", result.optString("document_number", ""));

                    // 4. VOLCAMOS a la UI en el hilo principal
                    runOnUiThread(() -> {
                        try {
                            // Log para verificar qué estamos enviando a la pantalla
                            Log.i(TAG, "Enviando datos unificados a la UI: " + JsonFinalUnificado.toString());

                            addResultToEditText(JsonFinalUnificado);

                        } catch (JSONException e) {
                            // Este catch es vital para que compile y para capturar errores de formato de último minuto
                            Log.e(TAG, "Error de JSON al volcar a la UI: " + e.getMessage());
                            Toast.makeText(this, "Error visualizando los datos", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Log.e(TAG, "Error: ScanDataHolder.rawBarcodeData es nulo");
                }
            } catch (Exception e) {
                Log.e(TAG, "Fallo crítico procesando QR: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al procesar el código QR", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private void procesarLecturaMRZ(JSONObject mrzRaw) {
        Log.i(TAG, "Procesando MRZ: " + mrzRaw);
        try {
            JSONObject details = mrzRaw.getJSONObject("details");
            JSONObject inner = new JSONObject(details.getString("JSON"));
            JSONObject normal = inner.getJSONObject("normal");
            JSONObject formatted = inner.getJSONObject("formatted");

            //JSONObject jsonUnificado = new JSONObject();
            JsonFinalUnificado = new JSONObject();
            JsonFinalUnificado.put("nombre", normal.optString("first_name", ""));
            JsonFinalUnificado.put("apellidos", normal.optString("last_name", ""));

            // Lógica de Swap DNI/Soporte que ya tenías
            String nroDoc = normal.optString("document_number", "");
            String optData = normal.optString("optional_data", "");
            if (!optData.isEmpty()) {
                JsonFinalUnificado.put("dni", optData); // El DNI real suele ir aquí en MRZ
            } else {
                JsonFinalUnificado.put("dni", nroDoc);
            }

            addResultToEditText(JsonFinalUnificado);
        } catch (Exception e) {
            Log.e(TAG, "Error procesando MRZ: " + e.getMessage());
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


    private void startScanner() {
        startActivityForResult(new Intent(getApplicationContext(),BarkoderActivity.class),REQUEST_CODE);
    }

    private void getUltimasSolicitudes(){


        //URL of the request we are sending
        StringRequest postRequest = new StringRequest(Request.Method.POST, MainActivity.getUrlEfectivo(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Context context = getApplicationContext();
                        CharSequence text = response.substring(1);



                        boolean resultado_correcto=false;

                        // Creo un array con los datos JSON que he obtenido
                        ArrayList listaArray = new ArrayList<>();

                        // Solicito los datos al archivo JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray arrayResultado=jsonObject.getJSONArray("resultado");
                                ArrayList<String> lista_ultimasSolicitudes=new ArrayList<String>();

                                lista_ultimasSolicitudes.add("Últimas Solicitudes " + "(" + arrayResultado.length() + ")");

                                for(int i=0;i<arrayResultado.length();i++) {
                                    JSONObject object1=arrayResultado.getJSONObject(i);
                                    lista_ultimasSolicitudes.add(object1.getString( "dni") +  ", " + object1.getString("apellidos") + ", " + object1.getString("nombre"));
                                    //textView.setText(object1.toString());
                                }
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ScannedBarkoderSolicitudEfectivoActivity.this, R.layout.support_simple_spinner_dropdown_item, lista_ultimasSolicitudes);
                                spinnerUltSolicitudes.setAdapter(adapter2);
                            }else{
                                if(jsonObject.getString("status").equals("true")) {
                                    resultado_correcto=false;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if(MainActivity.getDebugMode() == true) {
                            Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.solicitudEfectivoLayout), response, Snackbar.LENGTH_INDEFINITE);
                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }else{
                            //Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                        }

                        if(editDocNum.getText().length() == 0) {
                            txtUltSolicitudEfectivo.setVisibility(View.VISIBLE);
                            spinnerUltSolicitudes.setVisibility(View.VISIBLE);
                        }else{
                            txtUltSolicitudEfectivo.setVisibility(View.GONE);
                            spinnerUltSolicitudes.setVisibility(View.GONE);
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
                            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.solicitudEfectivoLayout), error.toString(), Snackbar.LENGTH_INDEFINITE);
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

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);


    }


    private void Submit(){
        String banco="";
        if (spinnerBanco.getSelectedItem().toString() != "Otro..." ){
            banco = spinnerBanco.getSelectedItem().toString();
        }else{
            banco = editOtroBanco.getText().toString();
        }

        imprimirSolicitudEfectivo(getApplicationContext(), editDocNum.getText().toString(),  editNombre.getText().toString(),  editApellidos.getText().toString(),  banco, editImporte.getText().toString() );
        imprimirBtn.setEnabled(false);
    }

    public void imprimirSolicitudEfectivo(final Context context, final String dni, final String nombre, final String apellidos, final String banco, final String importe){


        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try (Socket socket = new Socket(MainActivity.getIpServidor(), 9090)) {
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    //Console console = System.console();
                    writer.println("solicitudEfectivo," + dni + ',' + nombre + ',' + apellidos + ',' + banco + ',' + importe);

                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    final String respuesta = reader.readLine();

                    //Nuevo SnackBar 28/12/2022
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.solicitudEfectivoLayout), respuesta, 5000);
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