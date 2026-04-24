package es.romaydili.prohibidosApp;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CancellationSignal;
//import android.os.Handler;
//import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
//import android.print.PrintManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.scansolutions.mrzscannerlib.MRZScanner;

import org.json.JSONException;
import org.json.JSONObject;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.print.PrintDocumentAdapter;
import android.print.PrintAttributes;

//import javax.crypto.Cipher;



public class ScannedActivity extends AppCompatActivity implements View.OnClickListener {
    private RequestQueue requestQueue;
    public static final int REQUEST_CODE = 1;
    public final String resultado ="";
    private String tipo_documento="";
    private int clicks = 0;

    Button scanBtn,backBtn,submitBtn,manualBtn;

    EditText editGivenName, surName, editDocNum, editIssuingCount, editNationallity, editDateOfBirth, editAge, editSex, editExporationDate, editOptionalVal,editIssuingDate,editRawMrz,editSDK;
    TextView datosCliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        datosCliente = findViewById(R.id.activity_description_txt);
        scanBtn = findViewById(R.id.scan_button);
        manualBtn = findViewById(R.id.manual_button);
        submitBtn = findViewById(R.id.submit_btn);
        backBtn = findViewById(R.id.back_btn);
        editGivenName = findViewById(R.id.edit_given_name);
        surName = findViewById(R.id.edit_surname);
        editDocNum = findViewById(R.id.edit_document_number);
        editIssuingCount = findViewById(R.id.edit_issuing_country);
        editNationallity = findViewById(R.id.edit_nationality);
        editDateOfBirth = findViewById(R.id.edit_date_of_birth);
        editAge = findViewById(R.id.edit_age);
        editSex = findViewById(R.id.edit_sex);
        editExporationDate = findViewById(R.id.edit_expiration_date);
        editOptionalVal = findViewById(R.id.edit_optional_values);
        editIssuingDate = findViewById(R.id.edit_issuing_date);
        editRawMrz = findViewById(R.id.edit_raw_mrz);
        editSDK = findViewById(R.id.edit_sdk);
//      imgBtnFront=findViewById(R.id.imageButton_scanImage_front);
//      imgBtnBack=findViewById(R.id.imageButton_scanImage_back);

        scanBtn.setOnClickListener(this);
        manualBtn.setOnClickListener(this);
//        imgBtnBack.setOnClickListener(this);
//        imgBtnFront.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        datosCliente.setOnClickListener(this);

        startScanner();

    }

    private void addResultToEditText(JSONObject successfulMrzScan) throws JSONException {

        int edad = calcularEdad(successfulMrzScan.getString("dob_readable"));

        editGivenName.setText(successfulMrzScan.getString("given_names_readable"));
        surName.setText(successfulMrzScan.getString("surname"));
        editDocNum.setText(successfulMrzScan.getString("document_number"));
        editIssuingCount.setText(successfulMrzScan.getString("issuing_country"));
        editIssuingDate.setText(successfulMrzScan.getString("est_issuing_date_readable"));
        editNationallity.setText(successfulMrzScan.getString("nationality"));
        editDateOfBirth.setText(successfulMrzScan.getString("dob_readable"));
        editAge.setText(String.valueOf(edad));
        editSex.setText(successfulMrzScan.getString("sex"));
        editExporationDate.setText(successfulMrzScan.getString("expiration_date_readable"));
        editOptionalVal.setText(successfulMrzScan.getString("optionals"));
        editRawMrz.setText(successfulMrzScan.getString("raw_mrz"));
        tipo_documento=successfulMrzScan.getString("document_type_raw");
        editSDK.setText(MRZScanner.sdkVersion());

        if(MainActivity.getDebugMode() == true){
            editRawMrz.setVisibility(View.VISIBLE);
            editSDK.setVisibility(View.VISIBLE);
            findViewById(R.id.label_mrz).setVisibility(View.VISIBLE);
            findViewById(R.id.label_sdk).setVisibility(View.VISIBLE);
            findViewById(R.id.txt_given_name).setVisibility(View.VISIBLE);

            editGivenName.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_surname).setVisibility(View.VISIBLE);
            surName.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_country).setVisibility(View.VISIBLE);
            editIssuingCount.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_date).setVisibility(View.VISIBLE);
            editIssuingDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_nationality).setVisibility(View.VISIBLE);
            editNationallity.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_sex).setVisibility(View.VISIBLE);
            editSex.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_expiration_date).setVisibility(View.VISIBLE);
            editExporationDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_optional_values).setVisibility(View.VISIBLE);
            editOptionalVal.setVisibility(View.VISIBLE);
        }else{
            editRawMrz.setVisibility(View.GONE);
            editSDK.setVisibility(View.GONE);
            findViewById(R.id.label_mrz).setVisibility(View.GONE);
            findViewById(R.id.label_sdk).setVisibility(View.GONE);
        }
        if(MainActivity.getOcultarInfo() == true){
            findViewById(R.id.txt_given_name).setVisibility(View.GONE);
            editGivenName.setVisibility(View.GONE);
            findViewById(R.id.txt_surname).setVisibility(View.GONE);
            surName.setVisibility(View.GONE);
            findViewById(R.id.txt_issuing_country).setVisibility(View.GONE);
            editIssuingCount.setVisibility(View.GONE);
            findViewById(R.id.txt_issuing_date).setVisibility(View.GONE);
            editIssuingDate.setVisibility(View.GONE);
            findViewById(R.id.txt_nationality).setVisibility(View.GONE);
            editNationallity.setVisibility(View.GONE);
            findViewById(R.id.txt_sex).setVisibility(View.GONE);
            editSex.setVisibility(View.GONE);
            findViewById(R.id.txt_expiration_date).setVisibility(View.GONE);
            editExporationDate.setVisibility(View.GONE);
            findViewById(R.id.txt_optional_values).setVisibility(View.GONE);
            editOptionalVal.setVisibility(View.GONE);
        }else{
            findViewById(R.id.txt_given_name).setVisibility(View.VISIBLE);
            editGivenName.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_surname).setVisibility(View.VISIBLE);
            surName.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_country).setVisibility(View.VISIBLE);
            editIssuingCount.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_issuing_date).setVisibility(View.VISIBLE);
            editIssuingDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_nationality).setVisibility(View.VISIBLE);
            editNationallity.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_sex).setVisibility(View.VISIBLE);
            editSex.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_expiration_date).setVisibility(View.VISIBLE);
            editExporationDate.setVisibility(View.VISIBLE);
            findViewById(R.id.txt_optional_values).setVisibility(View.VISIBLE);
            editOptionalVal.setVisibility(View.VISIBLE);
        }

        submitBtn.setEnabled(true);
        Submit();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK) {
                String requiredValue = data.getStringExtra("key");
                JSONObject jsonObj = new JSONObject(requiredValue);

                addResultToEditText(jsonObj);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Fallo en el resultado de la actividad\r\n" + MRZScanner.sdkVersion(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == scanBtn) {
            startScanner();
        }

        if (view==manualBtn){
            MainActivity.solicitar_dni(this, findViewById(R.id.scannedLayout));
        }
//        if (view==imgBtnFront){
//            REQUEST_IMAGE_SCAN=2;
//            startImageScan();
//        }
//        if ( view==imgBtnBack)
//        {
//            REQUEST_IMAGE_SCAN=3;
//            startImageScan();
//        }
        if (view==backBtn){
            //MainActivity.setScannerInicial(false);
            finish();
        }
        if (view==submitBtn){
            //startActivity(new Intent(this,ProceedToCheckInActivity.class));
            Submit();
        }

        if (view == datosCliente){
            clicks++;
            if(clicks >= 10) {
                MainActivity.setDebugMode(true);
                editRawMrz.setVisibility(View.VISIBLE);
                editSDK.setVisibility(View.VISIBLE);
                findViewById(R.id.label_mrz).setVisibility(View.VISIBLE);
                findViewById(R.id.label_sdk).setVisibility(View.VISIBLE);

                findViewById(R.id.txt_given_name).setVisibility(View.VISIBLE);
                editGivenName.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_surname).setVisibility(View.VISIBLE);
                surName.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_issuing_country).setVisibility(View.VISIBLE);
                editIssuingCount.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_issuing_date).setVisibility(View.VISIBLE);
                editIssuingDate.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_nationality).setVisibility(View.VISIBLE);
                editNationallity.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_sex).setVisibility(View.VISIBLE);
                editSex.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_expiration_date).setVisibility(View.VISIBLE);
                editExporationDate.setVisibility(View.VISIBLE);
                findViewById(R.id.txt_optional_values).setVisibility(View.VISIBLE);
                editOptionalVal.setVisibility(View.VISIBLE);
            }
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
        startActivityForResult(new Intent(getApplicationContext(),ScannerActivity.class),REQUEST_CODE);
    }

    private void Submit(){
        //URL of the request we are sending
        StringRequest postRequest = new StringRequest(Request.Method.POST, MainActivity.getUrlMrz(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Context context = getApplicationContext();
                        CharSequence text = response.substring(1);



                        boolean permitido=false,resultado_correcto=false;
                        //boolean certificadoCovid = false; //Certificado Covid
                        boolean enMiLista = false; //Está en la lista personal
                        String mensaje="";
                        String mensajeMiLista="";
                        boolean solicitarJugador = false; //Encuesta si el cliente es Jugador
                        //int numAccesosHoy=-1;
                        boolean imprimirPromocion=false;

                        // Creo un array con los datos JSON que he obtenido
                        ArrayList listaArray = new ArrayList<>();

                        String msgErrorExcepcion="";
                        // Solicito los datos al archivo JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
                            if (jsonObject.getString("status").equals("true")) {
                                resultado_correcto=jsonObject.getBoolean("resultado_correcto");
                                permitido=jsonObject.getBoolean("acceso_permitido");
                                mensaje=jsonObject.getString("mensaje");
                                //certificadoCovid = jsonObject.getBoolean("certificadoCovid"); //Certificado Covid
                                enMiLista = jsonObject.getBoolean("enMiLista"); //Está en la lista personal
                                mensajeMiLista = jsonObject.getString("mensajeMiLista");

                                if ( MainActivity.getVersion().equals("SerOnuba")) {
                                    solicitarJugador = jsonObject.getBoolean("solicitarJugador");
                                    imprimirPromocion = jsonObject.getBoolean("promocion");
                                }
                            }else{
                                if(jsonObject.getString("status").equals("true")) {
                                    resultado_correcto=false;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(getApplicationContext(), "Error procesando JSON", Toast.LENGTH_LONG).show();

                            if(MainActivity.getDebugMode() == true) {
                                msgErrorExcepcion=e.toString();
                            }
                        }


                        if(MainActivity.getDebugMode() == true) {

                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar;
                            if (msgErrorExcepcion.equals("")) {
                                mySnackbar = Snackbar.make(findViewById(R.id.scannedLayout), "JSON Procesado:\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            }else{
                                mySnackbar = Snackbar.make(findViewById(R.id.scannedLayout), "Error Procesando JSON:\n\n" + msgErrorExcepcion + "\n\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            }
//                            mySnackbar.setAction("Ver", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            final AlertDialog.Builder alertDebug = new AlertDialog.Builder(ScannedActivity.this, R.style.MyDialogThemeNeutral );
//
//                                            alertDebug.setTitle("Repuesta del Servidor (Debug):");
//                                            alertDebug.setIcon(R.drawable.ic_baseline_info_24);
//                                            alertDebug.setMessage(response);
//                                            alertDebug.setCancelable(false);
//
//                                            alertDebug.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
//                                                }
//                                            });
//
//                                            AlertDialog dialog = alertDebug.show();
//
//                                            TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
//                                            messageText.setTextSize(18);
//                                            messageText.setGravity(Gravity.CENTER);
//                                            dialog.show();
//                                            ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
//                                        }
//                                    });

                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }


                        int estilo = R.style.MyDialogThemeProhibido;

                        String titulo;
                        int icono;
                        if(resultado_correcto == true && permitido == true) {
                            estilo = R.style.MyDialogThemePermitido;
                            titulo="Accceso Permitido";
                            icono=R.drawable.ic_baseline_check_circle_24;
                            if( MainActivity.getImprimirVales() == true && imprimirPromocion == true ) {
                                MainActivity.imprimir(editDocNum.getText().toString(), ScannedActivity.this, findViewById(R.id.scannedLayout));
                                titulo="Accceso Permitido + PROMO";
                                mensaje=mensaje+"<br><br> IMPRIMIENDO PROMO";
                            }

                            //Tono de Aviso
                            if (enMiLista == true){
                                MainActivity.tonoAviso(context, 2); //Aviso Largo
                            }
                        }else{
                            if (resultado_correcto == true && permitido == false) {
                                estilo = R.style.MyDialogThemeProhibido;
                                titulo="Acceso Denegado";
                                icono=R.drawable.ic_baseline_not_interested_24;

                                MainActivity.tonoAviso(context, 2); //Aviso Largo
                            }else{ //el Resultado no es correcto
                                estilo = R.style.MyDialogThemeNeutral;
                                titulo="Resultado";
                                icono=R.drawable.ic_baseline_info_24;

                                MainActivity.tonoAviso(context, 0); //Aviso Corto
                            }


                        }


                        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(ScannedActivity.this, estilo );

                        alertOpciones.setTitle(titulo);
                        alertOpciones.setIcon(icono);
                        alertOpciones.setMessage(Html.fromHtml(mensaje));
                        alertOpciones.setCancelable(false);

                        if (enMiLista == true) {
                            LinearLayout diagLayout = new LinearLayout(ScannedActivity.this);
                            diagLayout.setOrientation(LinearLayout.VERTICAL);
                            TextView textoMensaje = new TextView(ScannedActivity.this);
                            textoMensaje.setTextColor(Color.BLACK);
                            textoMensaje.setText(Html.fromHtml(mensajeMiLista));
                            textoMensaje.setPadding(30, 30, 10, 30);
                            textoMensaje.setBackgroundColor(ContextCompat.getColor(ScannedActivity.this, R.color.peligro));
                            textoMensaje.setGravity(Gravity.CENTER);
                            textoMensaje.setTextSize(30);
                            diagLayout.addView(textoMensaje);
                            alertOpciones.setView(diagLayout);
                        }

                        alertOpciones.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        alertOpciones.setNeutralButton("LEER DE NUEVO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startScanner();
                            }
                        });
/*
                        if(certificadoCovid == false) {
                            alertOpciones.setNegativeButton("Certificar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i;
                                    PackageManager manager = getPackageManager();
                                    try {
                                        i = manager.getLaunchIntentForPackage("ch.admin.bag.covidcertificate.verifier");
                                        if (i == null)
                                            throw new PackageManager.NameNotFoundException();
                                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                                        startActivity(i);
                                    } catch (PackageManager.NameNotFoundException e) {

                                    }
                                    alertOpciones.show();
                                    ((TextView) alertOpciones.show().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                                }
                            });
                        }
*/
                        AlertDialog dialog = alertOpciones.show();

                        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
                        messageText.setTextSize(18);
                        messageText.setGravity(Gravity.CENTER);
                        dialog.show();
                        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

                        //Alert opciones Jugador 30/08/22
                        if ( MainActivity.getVersion().equals("SerOnuba") && resultado_correcto == true && permitido == true && solicitarJugador == true) {
                            final CharSequence[] OPCIONES_ALERTA = {"Jugador Fuerte", "Jugador Frecuente", "Jugador Ocasional", "No Juega", "No lo sé"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(ScannedActivity.this, R.style.Theme_AppCompat_Dialog);
                            //builder.setTitle(titulo);
                            builder.setIcon(R.drawable.ic_baseline_question_answer_24);
                            //builder.setMessage("Seleccione una de las siguientes opciones:");
                            builder.setCancelable(false);
                            builder.setTitle("¿El Cliente es Jugador de Máquinas?");
                            builder.setItems(OPCIONES_ALERTA, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // which representa el índice del arreglo de opciones
                                    String eleccion = OPCIONES_ALERTA[which].toString();
                                    // Aquí puedes mostrar la elección o hacer lo que sea con ella.
                                    //Toast.makeText(ScannedActivity.this, "Elegiste: " + eleccion + ' ' + MainActivity.getVersion(), Toast.LENGTH_SHORT).show();
                                    MainActivity.guardarJugador(getApplicationContext(), editDocNum.getText().toString(), eleccion);
                                    // En el click se descarta el diálogo
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                        //Fin Alert opciones 30/08/22

                        //Custom AlertDialog DESACTIVADO
                        /*
                        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
                        ViewGroup viewGroup = findViewById(android.R.id.content);

                        //then we will inflate the custom alert dialog xml that we created
                        final View dialogView = LayoutInflater.from(context).inflate(R.layout.my_dialog_scanned_ok, viewGroup, false);

                        //Modificamos el mensaje
                        TextView textView = (TextView) dialogView.findViewById(R.id.mensaje);
                        textView.setText(mensaje);

                        //Now we need an AlertDialog.Builder object
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ScannedActivity.this);

                        //setting the view of the builder to our custom view that we already inflated
                        builder.setView(dialogView);

                        //finally creating the alert dialog and displaying it
                        final AlertDialog alertDialog = builder.create();

                        Button ok_item = (Button)dialogView.findViewById(R.id.ok_btn);
                        ok_item.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                alertDialog.dismiss();

                            }
                        });
                        Button scanear_item = (Button)dialogView.findViewById(R.id.escanear_btn);
                        scanear_item.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                startScanner();

                            }
                        });
                        alertDialog.show();

                         */
                    //Fin de Custom AlertDialog









///////////////////////////////

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "Compruebe su Conexión a Internet:\r\n\r\n" + error.toString();

                        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put( "dispositivo", MainActivity.getIdentificadorAndroid());
                params.put( "usuario", MainActivity.getUsuario());
                params.put( "provincia", MainActivity.getProvincia());
                params.put( "version", MainActivity.getVersion());

                params.put( "nacionalidad", editNationallity.getText().toString() );
                params.put( "fecha_nacimiento", editDateOfBirth.getText().toString());
                params.put( "dni", editDocNum.getText().toString());
                params.put( "nombre", editGivenName.getText().toString());
                params.put( "apellidos", surName.getText().toString());
                params.put( "sexo", editSex.getText().toString());
                params.put( "caducidad_dni", editExporationDate.getText().toString());
                params.put( "opcional", editOptionalVal.getText().toString());
                params.put( "pais_origen", editIssuingCount.getText().toString());
                params.put( "mrz_completo", editRawMrz.getText().toString());
                params.put( "tipo_documento", tipo_documento);
                params.put( "fecha_expedicion", editIssuingDate.getText().toString());

                Log.d(TAG, "Enviando al Servidor:\n" + MainActivity.getUrlMrz() + "\n" + params.toString());

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    private int calcularEdad(String fechaNacimiento) {
        if (fechaNacimiento == null) return -1;

        // Quitamos guiones, barras, puntos o espacios: "2026-04-10" -> "20260410"
        String limpia = fechaNacimiento.replaceAll("[^0-9]", "");
        if (limpia.length() < 8) return -1;

        try {
            int anio, mes, dia;

            // DETECCIÓN AUTOMÁTICA DE FORMATO
            // Si el año está al principio (Formato ISO: YYYYMMDD)
            if (Integer.parseInt(limpia.substring(0, 4)) > 1900) {
                anio = Integer.parseInt(limpia.substring(0, 4));
                mes = Integer.parseInt(limpia.substring(4, 6));
                dia = Integer.parseInt(limpia.substring(6, 8));
            }
            // Si el año está al final (Formato Europeo: DDMMYYYY)
            else {
                dia = Integer.parseInt(limpia.substring(0, 2));
                mes = Integer.parseInt(limpia.substring(2, 4));
                anio = Integer.parseInt(limpia.substring(4, 8));
            }

            java.util.Calendar nac = java.util.Calendar.getInstance();
            nac.set(anio, mes - 1, dia);

            java.util.Calendar hoy = java.util.Calendar.getInstance();

            int edad = hoy.get(java.util.Calendar.YEAR) - nac.get(java.util.Calendar.YEAR);

            // Ajuste de precisión (si aún no ha llegado su cumple este año)
            if (hoy.get(java.util.Calendar.MONTH) < nac.get(java.util.Calendar.MONTH) ||
                    (hoy.get(java.util.Calendar.MONTH) == nac.get(java.util.Calendar.MONTH) &&
                            hoy.get(java.util.Calendar.DAY_OF_MONTH) < nac.get(java.util.Calendar.DAY_OF_MONTH))) {
                edad--;
            }

            return edad;
        } catch (Exception e) {
            Log.e("EDAD", "Error al calcular con fecha: " + fechaNacimiento);
            return -1;
        }
    }

}




/*
class MyPrintDocumentAdapter extends PrintDocumentAdapter
    {
        Context context;

        public MyPrintDocumentAdapter(Context context)
        {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {
        }


        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal
                                    cancellationSignal,
                            final WriteResultCallback callback) {
        }

    }

*/