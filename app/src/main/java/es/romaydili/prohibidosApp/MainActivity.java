package es.romaydili.prohibidosApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

//import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
//import android.bluetooth.BluetoothClass;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
//import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextDirectionHeuristic;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.BuildConfig;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
//import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String m_Text = "";
    private String ultActualizacion = "";
    private static String version = "";
    private static String versionAndroid = Build.VERSION.RELEASE;
    private int clicks = 0;
    private static String ipServidor = "192.168.1.2"; //TPV;
    private static final int myRequestCode = 0xe110; // Or whatever number you want
    // ensure it's unique compared to other activity request codes you use

    Button button_solicitudEfectivo,button_anticipo,button_scanner, button_comprobar, button_configuracion;
    TextView textVersion, textProvincia, textUltimaActualizacion;
    ImageView logoGEHD;
    ProgressBar spinner;
    ProgressDialog progressBar;

    private static String androidID, usuario;
    private String androidID_codificado;
    private static String fechaUltActualizacion;
    private static String ficheroUltActualizacion;
    private static String numProhibidosUltActualizacion;
    private static String mensaje = "";
    private static String versionApp = "";
    private static boolean esSalon = true;
    private static String provincia = "";
    private static boolean scanner_inicial;
    private static boolean ocultarInfo;
    private static boolean imprimir_vales;
    private static Boolean debugMode = false;
    private static Boolean beep = false;
    private static Boolean activado = false;
    private String url_base = "https://";
    private String url_servidor = "ce-juegos.es/";
    private static String url_documento;
    private static String url_mrz;
    private static String url_efectivo;

    private static String url_datosDni;
    private static String url_jugador;
    private Boolean actualizacion_disponible = false;
    private Boolean actualizacion_permitir = false;
    private String url_actualizacion, mensaje_actualizacion;

    public static String getIdentificadorAndroid() {
        return androidID;
    }

    public static String getFechaUltActualizacion() {
        return fechaUltActualizacion;
    }

    public static String getFicheroUltActualizacion() {
        return ficheroUltActualizacion;
    }

    public static String getNumProhibidosUltActualizacion() {
        return numProhibidosUltActualizacion;
    }

    public static boolean getScannerInicial() {
        return scanner_inicial;
    }

    public static void setScannerInicial(boolean nuevoEstado) {
        scanner_inicial = nuevoEstado;
    }

    public static boolean getBeep() {
        return beep;
    }

    public static void setBeep(boolean nuevoEstado) {
        beep = nuevoEstado;
    }

    public static boolean getDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean nuevoEstado) {
        debugMode = nuevoEstado;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getProvincia() {
        return provincia;
    }

    public static void setUsuario(String nuevoUsuario) {
        usuario = nuevoUsuario;
    }

    public static Boolean getActivado() {
        return activado;
    }

    public static String getUrlMrz() {
        return url_mrz;
    }

    public static String getUrlEfectivo() {
        return url_efectivo;
    }

    public static String getUrlDatosDni() {
        return url_datosDni;
    }

    public static String getUrlDocumento() {
        return url_documento;
    }

    public static String getUrlJugador() {
        return url_jugador;
    }

    public static boolean getImprimirVales() {
        return imprimir_vales;
    }

    public static void setImprimirVales(boolean nuevoEstado) {
        imprimir_vales = nuevoEstado;
    }

    public static boolean getOcultarInfo() {
        return ocultarInfo;
    }

    public static void setOcultarInfo(boolean nuevoEstado) {
        ocultarInfo = nuevoEstado;
    }

    public static String getVersion() {
        return versionApp;
    }

    public static String getIpServidor() {
        return ipServidor;
    }

    public static void guardarJugador(final Context contexto, final String documento,final String nivelJuego) {
        //URL of the request we are sending

        StringRequest postRequest = new StringRequest(Request.Method.POST, MainActivity.getUrlJugador(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        boolean resultado_correcto = false;
                        String mensaje = "";

                        // Creo un array con los datos JSON que he obtenido
                        ArrayList listaArray = new ArrayList<>();

                        // Solicito los datos al archivo JSON
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
                            if (jsonObject.getString("status").equals("true")) {
                                resultado_correcto = jsonObject.getBoolean("resultado_correcto");
                                mensaje = jsonObject.getString("mensaje");
                            } else {
                                if (jsonObject.getString("status").equals("true")) {
                                    resultado_correcto = false;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(contexto, "Error procesando JSON", Toast.LENGTH_LONG).show();
                        }

                        if (MainActivity.getDebugMode() == true) {
                            Toast.makeText(contexto, response, Toast.LENGTH_LONG).show();

                        }else{
                            Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Context context = contexto;
                        CharSequence text = "Compruebe su Conexión a Internet:\r\n\r\n" + error.toString();

                        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("documento", documento);
                params.put("dispositivo", MainActivity.getIdentificadorAndroid());
                params.put("usuario", MainActivity.getUsuario());
                params.put("version", MainActivity.getVersion());
                params.put("es_jugador", nivelJuego);

                return params;
            }
        };
        Volley.newRequestQueue( contexto ).add(postRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_solicitudEfectivo = findViewById(R.id.btn_solicitud);
        button_anticipo = findViewById(R.id.btn_anticipo);
        button_scanner = findViewById(R.id.btn_scanner);
        button_comprobar = findViewById(R.id.btn_comprobar);
        button_configuracion = findViewById(R.id.btn_configuracion);
        textVersion = findViewById(R.id.textView_Version);
        textProvincia = findViewById(R.id.textView_Provincia);
        textUltimaActualizacion = findViewById(R.id.textView_Actualizacion);
        logoGEHD = findViewById(R.id.logoGEHD);
        spinner = findViewById(R.id.spinner);

        button_solicitudEfectivo.setOnClickListener(this);
        button_anticipo.setOnClickListener(this);
        button_scanner.setOnClickListener(this);
        button_comprobar.setOnClickListener(this);
        button_configuracion.setOnClickListener(this);
        textUltimaActualizacion.setOnClickListener(this);

        spinner.setVisibility(View.VISIBLE);

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        scanner_inicial = myPreferences.getBoolean("scanner_inicial", false);
        ocultarInfo = myPreferences.getBoolean("ocultar_info", true);
        beep = myPreferences.getBoolean("beep", true);
        imprimir_vales = myPreferences.getBoolean("imprimir_vales", false);


/*
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PRIVILEGED_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PRIVILEGED_PHONE_STATE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
*/
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            textVersion.setText("v" + version);
            int versionCode = pInfo.versionCode;
            Log.d("MyApp", "Version Name : " + version + "\n Version Code : " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d("MyApp", "PackageManager Catch : " + e.toString());
        }
        //androidID=Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID) + "_" + ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        //androidID = UUID.randomUUID().toString();
        //androidID = Build.getSerial();

        androidID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            String androidID = MainActivity.getIdentificadorAndroid();

            MasterKey masterKey = new
                    MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS).
                    setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

            SharedPreferences msharedPreferences = EncryptedSharedPreferences.create(
                    this, "id", masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            // use the shared preferences and editor as you normally would
            //SharedPreferences.Editor editor = mSharedPreferences.edit();
            //msharedPreferences.edit().putString("androidID",androidID).apply();
            androidID_codificado = msharedPreferences.getString("androidID", "unknown");
            usuario = msharedPreferences.getString("usuario", "unknown");

            if (usuario.contains("fernando_")){
                ipServidor = "192.168.1.23";
                Toast.makeText(getApplicationContext(), "Servidor de impresión (NANDO-PC): " + ipServidor, Toast.LENGTH_SHORT).show();

                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.mainLayout), "Servidor de impresión (NANDO-PC): " + ipServidor, Snackbar.LENGTH_LONG);
                //mySnackbar.setAction(R.string.undo_string, new MyUndoListener());
                View snackbarView = mySnackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextSize(25);
                textView.setMaxLines(3);  // show multiple line
                mySnackbar.show();

            }

        } catch (Exception e) {
            //
            e.printStackTrace();
        }
        if (ultActualizacion.equals("")) {
            ultima_actualizacion();

        }


        borrarUpdate borraUpdate = new borrarUpdate();
        borraUpdate.setContext(getApplicationContext());
        borraUpdate.setActivity(MainActivity.this);
        borraUpdate.execute();

        //FadeOut del Spinner
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);//fade from 1 to 0 alpha
        fadeOutAnimation.setDuration(1500);
        fadeOutAnimation.setFillAfter(true);//to keep it at 0 when animation ends
        spinner.startAnimation(fadeOutAnimation);
        //spinner.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View view) {
        if (view == button_anticipo) {
            spinner.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getApplicationContext(), anticipoActivity.class));
        }

        if (view == button_solicitudEfectivo) {
            spinner.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getApplicationContext(), ScannedSolicitudEfectivoActivity.class));
        }

        if (view == button_comprobar) {
            spinner.setVisibility(View.INVISIBLE);
            solicitar_dni(this, findViewById(R.id.mainLayout));
        }

        if (view == button_scanner) {
            spinner.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getApplicationContext(), ScannedActivity.class));
        }
        if (view == button_configuracion) {
            spinner.setVisibility(View.INVISIBLE);
            startActivityForResult(new Intent(new Intent(getApplicationContext(), ConfiguracionActivity.class)), myRequestCode );
        }

        if (view == textUltimaActualizacion) {
            clicks++;
            if (clicks >= 10) {
                clicks = 0;
                Toast.makeText(getApplicationContext(), "Reiniciando la App", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                        .FLAG_ACTIVITY_CLEAR_TOP));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
//                Toast.makeText(getApplicationContext(), data.getData().toString(), Toast.LENGTH_SHORT).show();

                //Nuevo SnackBar 28/12/2022
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.mainLayout), data.getData().toString(), Snackbar.LENGTH_LONG);
                View snackbarView = mySnackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setMaxLines(40);  // show multiple line
                mySnackbar.show();
                //Fin Nuevo SnackBar 28/12/2022

                if (data.getData().toString().contains("Reiniciando la App")){
                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                            .FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), "Sin Mensaje", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String reg = "^[a-zA-Z0-9]*$";
            if (!source.toString().matches(reg)) {
                return "";
            }

            return null;
        }
    };

    public static void solicitar_dni( final Context contexto, final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setIcon(android.R.drawable.ic_menu_search);
        builder.setTitle("Comprobar Identificación");
        builder.setMessage("Introduzca DNI, NIE o Pasaporte\r\n(Sin espacios ni guiones)");
        // Set `EditText` to `dialog`. You can add `EditText` from `xml` too.
        final EditText input = new EditText(contexto);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        input.setFilters(new InputFilter[]{filter});
        builder.setPositiveButton("Comprobar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        String m_Text = input.getText().toString().toUpperCase();

                        InputMethodManager imm = (InputMethodManager) contexto.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        String reg = "^[a-zA-Z0-9]*$";
                        if (m_Text.matches(reg) && m_Text.length() >= 4 && m_Text.length() <= 12) {
                            comprobar_dni(m_Text, contexto, view);
                            //Toast.makeText(getApplicationContext(), "Comprobando " + m_Text, Toast.LENGTH_SHORT).show();
                        } else {
                            if (m_Text.length() < 4) {
                                Toast.makeText(contexto, "Introduzca al menos 4 caracteres (" + m_Text + ")", Toast.LENGTH_SHORT).show();
                            }
                            if (m_Text.length() > 12) {
                                Toast.makeText(contexto, "Introduzca Menos de 12 caracteres (" + m_Text + ")", Toast.LENGTH_SHORT).show();
                            }
                            if (!m_Text.matches(reg)) {
                                Toast.makeText(contexto, "Introduzca Sólo dígitos y caracteres (" + m_Text + ")", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // DO TASK
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
// Initially disable the button
        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) contexto.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
// OR you can use here setOnShowListener to disable button at first
// time.

// Now set the textchange listener for edittext
        input.addTextChangedListener(new TextWatcher() {
            String reg = "^[a-zA-Z0-9]*$";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (!s.toString().matches(reg)) {
                    Toast.makeText(contexto, "Introduzca Sólo dígitos y caracteres", Toast.LENGTH_SHORT).show();
                }

                if (s.length() > 12) {
                    Toast.makeText(contexto, "Introduzca Menos de 12 caracteres", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if edittext is empty
                if (s.toString().matches(reg) && s.length() >= 4 && s.length() <= 12) {
                    // Enable ok button
                    ((AlertDialog) dialog).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    // Disable the button.
                    ((AlertDialog) dialog).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }

            }
        });
    }

    public static void tonoAviso(Context contexto, int tipoAviso){
        //vibrar
        Vibrator v = (Vibrator) contexto.getSystemService(VIBRATOR_SERVICE);
        //long [] patron = {0, 500, 300, 1000, 500, 300, 1000, 500, 300, 1000, 500, 300, 1000, 500, 300, 1000, 500, 300, 1000};
        //long [] patron = {0, 1000, 100, 500, 100, 1000, 100, 500, 100, 1000, 100, 500, 100, 1000, 100, 500, 100, 1000, 100, 500, 100};
        //long [] patron = {0, 2000, 100, 2000, 100, 2000, 100, 1500, 100, 1500, 100, 1000, 100, 1000, 100, 500, 100, 500, 100};

        final long[] patronCorto = {0, 500, 100, 500, 100, 500}; //Vibración Corta (por defecto)
        final long[] patronMedio = {0, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500}; //Vibración Media
        final long[] patronLargo = {0, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 2000, 100, 2000, 100, 1000, 100, 1000, 100, 500, 100, 500, 100}; //Vibración larga

        if (tipoAviso == 1) {
            v.vibrate(patronMedio,-1);
        }else if (tipoAviso == 2){ //Vibración Larga
            v.vibrate(patronLargo,-1);
        }else{
            v.vibrate(patronCorto,-1);
        }

        //tono de aviso
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        //toneGen1.startTone(ToneGenerator.TONE_PROP_ACK, 150); //Bip Leido
        //toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 200); //Sonido error 1
        //toneGen1.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200); //Sonido error 2
        toneGen1.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 600); //Sonido error 3

        synchronized (toneGen1){
            try {
                toneGen1.wait(1000);
            } catch (Exception ex) {}
            //toneGen1.startTone(ToneGenerator.TONE_PROP_ACK, 150); //Bip Leido de nuevo
            //toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 200); //Sonido error 1
            //toneGen1.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200); //Sonido error 2
            toneGen1.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 600); //Sonido error 3
        }
    }


    private static void comprobar_dni(final String documento, final Context contexto, final View view) {
        //URL of the request we are sending

        StringRequest postRequest = new StringRequest(Request.Method.POST, url_documento,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        boolean permitido = false, resultado_correcto = false;
                        //boolean certificadoCovid = false; //Certificado Covid
                        boolean enMiLista = false; //Está en la lista personal
                        String mensaje = "";
                        String mensajeMiLista = "";
                        Boolean solicitarJugador = false; //Encuesta si el cliente es Jugador
                        //int numAccesosHoy = -1;
                        boolean imprimirPromocion=false;

                        // Creo un array con los datos JSON que he obtenido
                        ArrayList listaArray = new ArrayList<>();

                        // Solicito los datos al archivo JSON
                        String msgErrorExcepcion="";
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
                            if (jsonObject.getString("status").equals("true")) {
                                resultado_correcto = jsonObject.getBoolean("resultado_correcto");
                                permitido = jsonObject.getBoolean("acceso_permitido");
                                mensaje = jsonObject.getString("mensaje");
                                //numAccesosHoy = jsonObject.getInt("num_accesos_hoy");
                                //certificadoCovid = jsonObject.getBoolean("certificadoCovid"); //Certificado Covid
                                enMiLista = jsonObject.getBoolean("enMiLista"); //Está en la lista personal
                                mensajeMiLista = jsonObject.getString("mensajeMiLista");
                                if ( MainActivity.getVersion().equals("SerOnuba") ) {
                                    solicitarJugador = jsonObject.getBoolean("solicitarJugador");
                                    imprimirPromocion = jsonObject.getBoolean("promocion");
                                }
                            } else {
                                if (jsonObject.getString("status").equals("true")) {
                                    resultado_correcto = false;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(contexto, "Error procesando JSON", Toast.LENGTH_LONG).show();

                            if(MainActivity.getDebugMode() == true) {
                                msgErrorExcepcion=e.toString();
                            }
                        }

                        if(MainActivity.getDebugMode() == true) {
                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar;
                            if (msgErrorExcepcion.equals("")) {
                                mySnackbar = Snackbar.make(view, "JSON Procesado:\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            } else {
                                mySnackbar = Snackbar.make(view, "Error Procesando JSON:\n\n" + msgErrorExcepcion + "\n\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            }

                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }


                        ///////////////////////////////
                        int estilo = R.style.MyDialogThemeProhibido;

                        String titulo;
                        int icono;

                        if (resultado_correcto == true && permitido == true) {
                            estilo = R.style.MyDialogThemePermitido;
                            titulo = "Accceso Permitido";
                            icono = R.drawable.ic_baseline_check_circle_24;
                            if ( imprimir_vales == true && imprimirPromocion == true ) {
                                imprimir(documento, contexto, view);
                                titulo = "Accceso Permitido + PROMO";
                                mensaje = mensaje + "<br><br>IMPRIMIENDO PROMO";
                            }
                            if (enMiLista == true){
                                tonoAviso(contexto, 2); //Aviso Largo
                            }
                        } else {
                            if (resultado_correcto == true && permitido == false) {
                                estilo = R.style.MyDialogThemeProhibido;
                                titulo = "Acceso Denegado";
                                icono = R.drawable.ic_baseline_not_interested_24;

                                tonoAviso(contexto, 2); //Aviso Largo

                            } else { //El resultado no es correcto
                                estilo = R.style.MyDialogThemeNeutral;
                                titulo = "Resultado";
                                icono = R.drawable.ic_baseline_info_24;

                                tonoAviso(contexto, 0); //Aviso Corto
                            }

                        }

                        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(contexto, estilo);

                        alertOpciones.setTitle(titulo);
                        alertOpciones.setIcon(icono);

                        alertOpciones.setMessage(Html.fromHtml(mensaje));

                        if (enMiLista == true) {
                            LinearLayout diagLayout = new LinearLayout(contexto);
                            diagLayout.setOrientation(LinearLayout.VERTICAL);
                            TextView textoMensaje = new TextView(contexto);
                            textoMensaje.setTextColor(Color.BLACK);
                            textoMensaje.setText(Html.fromHtml(mensajeMiLista));
                            textoMensaje.setPadding(30, 30, 10, 30);
                            textoMensaje.setBackgroundColor(ContextCompat.getColor(contexto, R.color.peligro));
                            textoMensaje.setGravity(Gravity.CENTER);
                            textoMensaje.setTextSize(30);
                            diagLayout.addView(textoMensaje);
                            alertOpciones.setView(diagLayout);
                        }


                        alertOpciones.setCancelable(false);
                        alertOpciones.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

/*                        if(certificadoCovid == false) {
                            alertOpciones.setNeutralButton("Certificar", new DialogInterface.OnClickListener() {
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
                        //alertOpciones.show();
                        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                        messageText.setGravity(Gravity.CENTER);
                        messageText.setTextSize(18);
                        dialog.show();
                        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());


                        //Alert opciones Jugador 30/08/22
                        if ( MainActivity.getVersion().equals("SerOnuba") && resultado_correcto == true && permitido == true && solicitarJugador == true) {
                            final CharSequence[] OPCIONES_ALERTA = {"Jugador Fuerte", "Jugador Frecuente", "Jugador Ocasional", "No Juega", "No lo sé"};

                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(contexto, R.style.Theme_AppCompat_Dialog);
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
                                    guardarJugador(contexto, documento, eleccion);
                                    // En el click se descarta el diálogo
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                        //Fin Alert opciones 30/08/22
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //Context context = getApplicationContext();
                        CharSequence text = "Compruebe su Conexión a Internet:\r\n\r\n" + error.toString();

                        Toast.makeText(contexto, text, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("documento", documento);
                params.put("dispositivo", androidID);
                params.put("usuario", usuario);
                params.put("provincia", provincia);
                params.put("version", versionApp);

                return params;
            }
        };
        Volley.newRequestQueue(contexto).add(postRequest);
    }

    private void ultima_actualizacion() {
        //URL of the request we are sending
        //String url = "https://www.ce-juegos.es/App/v0.4.0/ult_actualizacion.php";


        String url2 = "App";
        String url_Version = "/v" + version.substring(0,5); //"/v0.4.2";
        String url_script = "/ult_actualizacion";
        String url_lenguaje = ".php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url_base + "www." + url_servidor + url2 + url_Version + url_script + url_lenguaje,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //((TextView)findViewById(R.id.TextResult)).setText(response);
                        //editOptionalVal.setText(response);

                        Context context = getApplicationContext();
                        CharSequence text = response.substring(1);


                        //procesa_JSON(response);
                        // Creo un array con los datos JSON que he obtenido
                        ArrayList listaArray = new ArrayList<>();

                        // Solicito los datos al archivo JSON
                        String msgErrorExcepcion="";
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
                            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON

                            //Log.i("Json Recibido", jsonObject.toString());
                            if (jsonObject.getString("status").equals("true")) {
                                activado = jsonObject.getBoolean("activado");
                                if ( activado == true){
                                    versionApp = jsonObject.getString("version");

                                    url_documento = jsonObject.getString("url_documento");
                                    url_mrz = jsonObject.getString("url_mrz");

                                    fechaUltActualizacion = jsonObject.getString("ultActualizacion");
                                    provincia = jsonObject.getString("provincia");
                                    ficheroUltActualizacion = jsonObject.getString("fichero");
                                    numProhibidosUltActualizacion = jsonObject.getString("numProhibidos");
                                    mensaje = jsonObject.getString("mensaje");

                                    actualizacion_permitir = jsonObject.getBoolean("actualizacion_permitir");
                                    actualizacion_disponible = jsonObject.getBoolean("actualizacion_disponible");
                                    mensaje_actualizacion = jsonObject.getString("actualizacion_mensaje");
                                    url_actualizacion = jsonObject.getString("actualizacion_url");

                                    if ( versionApp.equals("SerOnuba") ){
                                        url_efectivo = jsonObject.getString("url_efectivo");
                                        url_datosDni = jsonObject.getString("url_datosDni");
                                        url_jugador = jsonObject.getString("url_jugador");
                                        esSalon = jsonObject.getBoolean("esSalon");
                                    }
                                }

                                /*// Accedo a la fila 'postres' del archivo JSON
                                JSONArray dataArray = jsonObject.getJSONArray("configuracion");
                                // Recorro los datos que hay en la fila 'postres' del archivo JSON
                                for (int i = 0; i < dataArray.length(); i++) {
                                    // Creo la  variable 'objetos' y recupero los valores
                                    JSONObject objetos = dataArray.getJSONObject(i);
                                    // Selecciono dato por dato
                                fechaUltActualizacion=objetos.getString("ultActualizacion");
                                }*/

                            }else{
                                Toast.makeText(getApplicationContext(), "Error resultado JSON", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            versionApp = "";
                            e.printStackTrace();

                            Toast.makeText(getApplicationContext(), "Error procesando JSON", Toast.LENGTH_LONG).show();

                            msgErrorExcepcion=e.toString();
                        }

                        if(MainActivity.getDebugMode() == true) {
                            //Nuevo SnackBar 28/12/2022
                            Snackbar mySnackbar;
                            if (msgErrorExcepcion.equals("")) {
                                mySnackbar = Snackbar.make(findViewById(R.id.mainLayout), "JSON Procesado:\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            } else {
                                mySnackbar = Snackbar.make(findViewById(R.id.mainLayout), "Error Procesando JSON:\n\n" + msgErrorExcepcion + "\n\n\n" + response, Snackbar.LENGTH_INDEFINITE);
                            }

                            View snackbarView = mySnackbar.getView();
                            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setMaxLines(40);  // show multiple line
                            mySnackbar.show();
                            //Fin Nuevo SnackBar 28/12/2022
                        }

                        if (activado == true && androidID.equals(androidID_codificado)) {
                            if (versionApp.equals("SerOnuba")) {
                                button_anticipo.setVisibility(View.VISIBLE);
                                button_solicitudEfectivo.setVisibility(View.VISIBLE);
                                logoGEHD.setVisibility(View.VISIBLE);
                            }

                            button_comprobar.setVisibility(View.VISIBLE);
                            button_scanner.setVisibility(View.VISIBLE);

                            if (esSalon == false){
                                button_solicitudEfectivo.setVisibility(View.GONE);
                                button_comprobar.setVisibility(View.GONE);
                                button_scanner.setVisibility(View.GONE);

                            }

                            Toast.makeText(getApplicationContext(), fechaUltActualizacion, Toast.LENGTH_LONG).show();

                            if ( MainActivity.getDebugMode() == false && !mensaje.equals("null") ) {
                                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.mainLayout), mensaje, Snackbar.LENGTH_LONG);
                                //mySnackbar.setAction(R.string.undo_string, new MyUndoListener());
                                View snackbarView = mySnackbar.getView();
                                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                textView.setMaxLines(3);  // show multiple line
                                mySnackbar.show();
                            }

                            textUltimaActualizacion.setText(fechaUltActualizacion);
                            textProvincia.setText(provincia);

                            if (actualizacion_permitir == true && actualizacion_disponible == true) { //Hay una actualización disponible

                                //Mostramos el mensaje de actualización disponible
                                final androidx.appcompat.app.AlertDialog.Builder alertOpciones = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this, R.style.MyDialogThemeNeutral);
                                alertOpciones.setTitle("Hay una actualización disponible");
                                alertOpciones.setMessage(Html.fromHtml(mensaje_actualizacion)); //"<a href=\"http://www.google.com\">Check this link out</a>"
                                alertOpciones.setCancelable(false);
                                alertOpciones.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                alertOpciones.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_actualizacion));
                                        //startActivity(browserIntent);

                                        UpdateApp actualizaApp = new UpdateApp();
                                        actualizaApp.setContext(getApplicationContext());
                                        actualizaApp.setActivity(MainActivity.this);
                                        actualizaApp.execute(url_actualizacion);
                                        //Toast.makeText(getApplicationContext(), "Descargando Actualización", Toast.LENGTH_LONG).show();
                                    }
                                });

                                androidx.appcompat.app.AlertDialog dialog = alertOpciones.show();
                                //alertOpciones.show();
                                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                                messageText.setGravity(Gravity.CENTER);
                                //dialog.show();
                                ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

                            } else { //No hay actualización disponible
                                if (scanner_inicial == true && activado) {
                                    spinner.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(getApplicationContext(), ScannedActivity.class));
                                }
                            }


                        } else {
                            button_anticipo.setVisibility(View.GONE);
                            button_solicitudEfectivo.setVisibility(View.GONE);
                            button_comprobar.setVisibility(View.INVISIBLE);
                            button_scanner.setVisibility(View.INVISIBLE);
                            //textUltimaActualizacion.setGravity(Gravity.CENTER);
                            textUltimaActualizacion.setText("Dispositivo NO Activado");
                            logoGEHD.setVisibility(View.INVISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence textToast="";
                        CharSequence textError="";


                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            //This indicates that the reuest has either time out or there is no connection
                            textToast = "Servidor no Responde o No Hay Conexión a Internet";
                            textError="Servidor no Responde o No Hay Conexión a Internet";


                        } else if (error instanceof AuthFailureError) {
                            // Error indicating that there was an Authentication Failure while performing the request
                            textToast="Fallo de autenticación del Servidor";
                            textError="Fallo de autenticación del Servidor";

                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            textToast="Error en la Respuesta del Servidor";
                            textError="Error en la Respuesta del Servidor";
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            textToast="Error de Red";
                            textError="Error de Red";
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                            textToast="No se puede interpretar la Respuesta del Servidor";
                            textError="No se puede interpretar la Respuesta del Servidor";
                        }

                        textUltimaActualizacion.setGravity(Gravity.CENTER);
                        textUltimaActualizacion.setText(textError);

                        Toast.makeText(context, textToast, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:

                params.put("dispositivo", androidID);
                params.put("usuario", usuario);
                params.put("modelo", Build.MANUFACTURER + " " + Build.MODEL);
                params.put("versionApp", version);
                params.put("versionAndroid", versionAndroid);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);

    }

    private void procesa_JSON(String cadena) {
        // Creo un array con los datos JSON que he obtenido
        ArrayList listaArray = new ArrayList<>();

        // Solicito los datos al archivo JSON
        String msgErrorExcepcion="";
        try {
            JSONObject jsonObject = new JSONObject(cadena);

            // En los datos que recibo verifico si obtengo el estado o 'status' con el valor 'true'
            // El dato 'status' con el valor 'true' se encuentra dentro del archivo JSON
            if (jsonObject.getString("status").equals("true")) {
                activado = jsonObject.getBoolean("activado");
                if ( activado == true){
                    versionApp = jsonObject.getString("version");

                    url_documento = jsonObject.getString("url_documento");
                    url_mrz = jsonObject.getString("url_mrz");

                    fechaUltActualizacion = jsonObject.getString("ultActualizacion");
                    provincia = jsonObject.getString("provincia");
                    ficheroUltActualizacion = jsonObject.getString("fichero");
                    numProhibidosUltActualizacion = jsonObject.getString("numProhibidos");
                    mensaje = jsonObject.getString("mensaje");

                    actualizacion_permitir = jsonObject.getBoolean("actualizacion_permitir");
                    actualizacion_disponible = jsonObject.getBoolean("actualizacion_disponible");
                    mensaje_actualizacion = jsonObject.getString("actualizacion_mensaje");
                    url_actualizacion = jsonObject.getString("actualizacion_url");

                    if ( versionApp.equals("SerOnuba") ){
                        url_efectivo = jsonObject.getString("url_efectivo");
                        url_jugador = jsonObject.getString("url_jugador");
                    }
                }

                /*// Accedo a la fila 'postres' del archivo JSON
                JSONArray dataArray = jsonObject.getJSONArray("configuracion");
                // Recorro los datos que hay en la fila 'postres' del archivo JSON
                for (int i = 0; i < dataArray.length(); i++) {
                    // Creo la  variable 'objetos' y recupero los valores
                    JSONObject objetos = dataArray.getJSONObject(i);
                    // Selecciono dato por dato
                    fechaUltActualizacion=objetos.getString("ultActualizacion");
                }*/

            }else{
                Toast.makeText(getApplicationContext(), "Error resultado JSON", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            versionApp = "";
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Error procesando JSON", Toast.LENGTH_LONG).show();

            msgErrorExcepcion=e.toString();
        }

        if(MainActivity.getDebugMode() == true) {
            //Nuevo SnackBar 28/12/2022
            Snackbar mySnackbar;
            if (msgErrorExcepcion.equals("")) {
                mySnackbar = Snackbar.make(findViewById(R.id.mainLayout), "JSON Procesado:\n\n" + cadena, Snackbar.LENGTH_INDEFINITE);
            } else {
                mySnackbar = Snackbar.make(findViewById(R.id.mainLayout), "Error Procesando JSON:\n\n" + msgErrorExcepcion + "\n\n\n" + cadena, Snackbar.LENGTH_INDEFINITE);
            }

            View snackbarView = mySnackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setMaxLines(40);  // show multiple line
            mySnackbar.show();
            //Fin Nuevo SnackBar 28/12/2022
        }


    }


    public static void imprimir(final String msg, final Context contexto, final View view) {

        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {


                try (Socket socket = new Socket(ipServidor, 9090)) {
                    Log.i("Thread Impresión", "Thread Impresión Iniciado");
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    //Console console = System.console();
                    writer.println(msg);

                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

                    final String respuesta = reader.readLine();

                    Log.i("Thread Impresión", "Respuesta Servidor: " + respuesta);

                    //Nuevo SnackBar 28/12/2022
                    Snackbar mySnackbar = Snackbar.make(view, respuesta, 5000);
                    View snackbarView = mySnackbar.getView();
                    TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setTextSize(18);
                    textView.setMaxLines(40);  // show multiple line
                    mySnackbar.show();
                    //Fin Nuevo SnackBar 28/12/2022

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // write your code here
                            Toast.makeText(contexto, respuesta, Toast.LENGTH_LONG).show();
                        }
                    });

                    socket.close();
                    Log.i("Thread Impresión", "Thread Impresión finalizado");
                } catch (final UnknownHostException ex) {
                    final String resultado="Servidor no Encontrado: " + ex.getMessage();
                    Log.e("Thread Impresión", resultado);

                    //Nuevo SnackBar 28/12/2022
                    Snackbar mySnackbar = Snackbar.make(view, "Servidor no encontrado:\n\n" + resultado, 5000);
                    View snackbarView = mySnackbar.getView();
                    TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setMaxLines(40);  // show multiple line
                    mySnackbar.show();
                    //Fin Nuevo SnackBar 28/12/2022

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(contexto, resultado, Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (final IOException ex) {
                    final String resultado="I/O error: " + ex.getMessage();
                    Log.e("Thread Impresión", resultado);

                    //Nuevo SnackBar 28/12/2022
                    Snackbar mySnackbar = Snackbar.make(view, "Error al Imprimir Promoción:\n\n" + resultado, 5000);
                    View snackbarView = mySnackbar.getView();
                    TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setMaxLines(40);  // show multiple line
                    mySnackbar.show();
                    //Fin Nuevo SnackBar 28/12/2022

//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(contexto, resultado , Toast.LENGTH_LONG).show();
//                        }
//                    });

                }
            }
        });

        thread.start();

    }


    class UpdateApp extends AsyncTask<String, Integer, Boolean> {
        private Context context;
        private Activity activity;
        private Intent intent;
        private static final int PERMISSION_REQUEST = 255;

        public void setContext(Context contextf) {
            context = contextf;
        }

        public void setActivity(Activity activityf) {
            activity = activityf;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar = new ProgressDialog(MainActivity.this);
            progressBar.setCancelable(false);
            progressBar.setMessage("Descargando Actualización ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.setCanceledOnTouchOutside(false);

        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            String PATH = "/sdcard/Download/";
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(context, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQUEST);
                    } else {
                        //WRITE_EXTERNAL_STORAGE permission granted
                        checkFile();
                    }
                } else {
                    //WRITE_EXTERNAL_STORAGE permission not required
                    checkFile();
                }

                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                int fileLength = c.getContentLength();
                if(fileLength <0){
                    fileLength=41577262;
                    fileLength=41500000;
                }

                //String RutaInterna = getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS).toString();

                //String PATH = context.getFilesDir() + "/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "update_prohibidos.apk");
                if(file.exists()){
                    //Toast.makeText(context, "Ruta Existe", Toast.LENGTH_LONG).show();
                }else{
                    //Toast.makeText(context, "Ruta NO Existe", Toast.LENGTH_LONG).show();
                }
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;

                    //if(fileLength > 0) {
                        publishProgress((int) (total*100/fileLength));
                    //}

                    fos.write(buffer, 0, len1);
                }

                fos.close();
                is.close();
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Actualización Descargada", Toast.LENGTH_LONG).show();
                    }
                });




            } catch (final Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Error al Descargar la Actualización:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            try{
                intent = new Intent(Intent.ACTION_VIEW);

                File file = new File(PATH + "update_prohibidos.apk");
                Uri data = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                intent.setDataAndType(data, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                Log.i("UpdateAPP", "Actualización Completada! ");
                progressBar.dismiss();
            } catch (final Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Error al Actualizar:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            return true;


        }

        @Override
        protected void onPostExecute(final Boolean response) {
            //this.activity.startActivity( new Intent( this.activity.getBaseContext(), MainActivity.class ) );
            progressBar.hide();
            progressBar.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);
            //progressBar.setMessage(values[0].toString());
            progressBar.show();
        }

        private void checkFile() {

            //Path directory of the file we want to load.
            String PATH = "/sdcard/Download/";
            File documentsPath = new File(PATH);
            //If documentsPath doesn´t exists, then create
            if (!documentsPath.exists()) {
                Log.i(TAG, "create path: " + documentsPath);
                documentsPath.mkdir();
            } else {
                Log.i(TAG, "path: " + documentsPath + " exists!");
            }
            File file = new File(documentsPath, "update_prohibidos.apk");

            if (file.exists()) {
                Log.i(TAG, "The file exists!, share file.");
                //shareFile(file);
            } else {
                Log.e(TAG, "The file doesn´t exists!");
            }


        }

        private boolean hasPermissions(Context context, String... permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
            return true;
        }

    }
}
class borrarUpdate extends AsyncTask<String, String, Boolean> {
    private Context context;
    private Activity activity;
    private Intent intent;
    private static final int PERMISSION_REQUEST = 255;

    public void setContext(Context contextf){
        context = contextf;
    }
    public void setActivity(Activity activityf){
        activity = activityf;
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!hasPermissions(context, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQUEST );
                } else {
                    //WRITE_EXTERNAL_STORAGE permission granted
                    checkFile();
                }
            } else {
                //WRITE_EXTERNAL_STORAGE permission not required
                checkFile();
            }

            String PATH = "/sdcard/Download/update_prohibidos.apk";
            File documentsPath = new File(PATH);
            if (documentsPath.exists()) {
                if(documentsPath.delete()){
                    Log.i(TAG, "Update Borrada");
                }else{
                    Log.i(TAG, "La Update no ha podido ser Borrada");
                }
            }else{
                Log.i(TAG, "No hay Update para borrar");
            }

            /*
            String PATH = "/mnt/sdcard/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, "update.apk");
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();


            intent = new Intent(Intent.ACTION_VIEW);
            file = new File("/mnt/sdcard/Download/update.apk");
            Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider" , file );
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
*/
            Log.i("BorrarUpdate", "Borrar Update Completado! ");

        } catch (Exception e) {
            Log.e("BorrarUpdate", "Borrar Update Error! " + e.getMessage());
        }
        return true;


    }
    @Override
    protected void onPostExecute( final Boolean response ) {

        //this.activity.startActivity( new Intent( this.activity.getBaseContext(), MainActivity.class ) );
    }

    private void checkFile(){
        String PATH = "/sdcard/Download/";
        //Path directory of the file we want to load.
        File documentsPath = new File(PATH);
        //If documentsPath doesn´t exists, then create
        if (!documentsPath.exists()) {
            Log.i(TAG, "create path: " + documentsPath);
            documentsPath.mkdir();
        }else{
            Log.i(TAG, "path: " + documentsPath + " exists!");
        }
        File file = new File(documentsPath, "update_prohibidos.apk");

        if(file.exists()) {
            Log.i(TAG, "The file exists!, share file.");
            //shareFile(file);
        }else{
            Log.e(TAG, "The file doesn´t exists!");
        }


    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
